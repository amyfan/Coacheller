package com.lollapaloozer.auth.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.lollapaloozer.auth.verify.FacebookVerifier;
import com.ratethisfest.shared.Constants;

public class FacebookAuthProvider implements AuthProvider {

  // private ChooseLoginActivity _activity;
  private final String LOGIN_TYPE = Constants.LOGIN_TYPE_FACEBOOK;
  private AuthModel _model;
  private Facebook _facebook = new Facebook("186287061500005");
  private AsyncFacebookRunner _AsyncRunner = new AsyncFacebookRunner(_facebook);

  private JSONObject _userInfo;

  // Default constructor disallowed
  private FacebookAuthProvider() {
  }

  public FacebookAuthProvider(AuthModel model) {
    // _activity = activity;
    _model = model;
  }

  private void _showError(String problem, String details) {
    _model.getApp().showErrorDialog("Facebook Login Error", problem, details);
  }

  @Override
  public void login() {
    // Proceeds Asynchronously
    System.out.println("Starting Facebook Login");
    _facebook.authorize(_model.getApp().getLastActivity(),
        new String[] { "email", "publish_stream" }, new AuthListener());
  }

  @Override
  public void logout() {
    try {
      _facebook.logout(_model.getApp().getChooseLoginActivity());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isLoggedIn() {
    if (_userInfo == null) {
      return false;
    }

    FacebookVerifier verifier = new FacebookVerifier();
    return verifier.verify(getAuthToken(), getVerifiedAccountIdentifier());
  }

  public Facebook getFacebookObject() {
    return _facebook;
  }

  @Override
  public String getAccountType() {
    return _facebook.getClass().toString();
  }

  @Override
  public String getLocalAccountName() {
    try {
      return _userInfo.getString("name");
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getVerifiedAccountIdentifier() {
    try {
      return _userInfo.getString("email");
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getAuthToken() {
    return _facebook.getAccessToken();
  }

  @Override
  public void extendAccess() {
    _facebook.extendAccessTokenIfNeeded(_model.getApp().getChooseLoginActivity(), null);
  }

  public String postToWall(String message) {
    Bundle parameters = new Bundle();
    parameters.putString("message", message);
    parameters.putString("description", "topic share");
    try {
      _facebook.request("me");
      String response = _facebook.request("me/feed", parameters, "POST");
      Log.d("Tests", "got response: " + response);
      if (response == null || response.equals("") || response.equals("false")) {
        return "Blank response from Facebook.";
      } else {
        return "Message posted to your facebook wall! " + response;
      }

    } catch (Exception e) {

      e.printStackTrace();
      return "Failed to post to wall!";
    }
  }

  // Called after onActivityResult calls the appropriate method
  private final class AuthListener implements DialogListener {
    @Override
    public void onComplete(Bundle values) {
      // Auth is completed, now we can actually request user data
      System.out.println("Facebook Authorization Complete, bundle: "
          + _model.getApp().bundleValues(values));

      int hours = ((int) (_facebook.getAccessExpires() - System.currentTimeMillis()) / 1000) / 60 / 60;
      System.out.println("Token valid for " + hours + " hours: " + _facebook.getAccessToken());

      // get user info from facebook
      _AsyncRunner.request("me", new IDRequestListener());
      // TODO should lock UI here
    }

    @Override
    public void onFacebookError(FacebookError error) {
      System.out.println("Facebook platform error during authorization");
      _showError("Facebook Authorization Platform Error",
          error.getErrorType() + ": " + error.getMessage());
      // error.getStackTrace()
    }

    @Override
    public void onError(DialogError e) {
      System.out.println("Error with facebook authorization dialog");
      _showError("Facebook Authorization Dialog Error", "DialogError: " + e.getMessage());
      // e.getStackTrace();
    }

    @Override
    public void onCancel() {
      System.out.println("Facebook authorization dialog cancelled by user");
      _showError(
          "User Cancelled Authorization",
          "You should only be seeing this message if you declined to authorize this application for your Facebook account.");
    }
  }

  private class IDRequestListener implements RequestListener {

    private String TAG = "fbDemo";

    @Override
    public void onComplete(String response, Object state) {
      try {
        System.out.println("Facebook async request has returned");

        Log.d(TAG, "IDRequestONComplete");
        Log.d(TAG, "Response: " + response.toString());
        JSONObject json = Util.parseJson(response);
        String userID = json.getString("id");
        String userName = json.getString("name");
        // fbEmail = json.getString("email");

        System.out.println("Retrieved from Facebook userID[" + userID + "] username [" + userName
            + "]");
        _userInfo = json;

        _model.loginSuccess(LOGIN_TYPE);
        // _model.getApp().getChooseLoginActivity().modelChanged();

      } catch (JSONException ex) {
        _showError("Facebook Response Error", "JSONException reading response: " + ex.getMessage());
      } catch (FacebookError e) {
        _showError("Facebook Response Platform Error", e.getErrorType() + ": " + e.getMessage()
            + " " + state.toString());
      }
      // TODO Unlock UI here
    }

    @Override
    public void onIOException(IOException e, Object state) {
      _showError("Facebook Request Error", "IOException while verifying account: " + e.getMessage());
    }

    @Override
    public void onFileNotFoundException(FileNotFoundException e, Object state) {
      _showError("Facebook Request Error",
          "FileNotFoundException while verifying account: " + e.getMessage());
    }

    @Override
    public void onMalformedURLException(MalformedURLException e, Object state) {
      _showError("Facebook Request Error",
          "MalformedURLException while verifying account: " + e.getMessage());
    }

    @Override
    public void onFacebookError(FacebookError e, Object state) {
      _showError("Facebook Request Platform Error", e.getErrorType() + ": " + e.getMessage() + " "
          + state.toString());
    }

  }

}
