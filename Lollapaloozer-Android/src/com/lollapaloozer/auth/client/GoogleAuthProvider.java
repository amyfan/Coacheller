package com.lollapaloozer.auth.client;

import java.io.IOException;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.util.Log;

import com.lollapaloozer.auth.verify.GoogleAuthVerifier;
import com.ratethisfest.shared.Constants;

public class GoogleAuthProvider implements AuthProvider {

  private final String LOGIN_TYPE = Constants.LOGIN_TYPE_GOOGLE;
  private final String ACCOUNT_TYPE_REQUESTED = "com.google";
  private final int TOKEN_RETRIES = 2;

  // private ChooseLoginActivity _activity;
  private AuthModel _model;
  private Bundle _currentGoogleLoginTokenBundle = null;
  private JSONObject _currentAuthResult;
  private boolean _confirmedAuthorizedGoogle;
  private String _verifiedAccountName = null;
  private int _tokenRetries;

  // Default constructor disallowed
  private GoogleAuthProvider() {
  }

  public GoogleAuthProvider(AuthModel model) {
    // _activity = activity;
    _model = model;
    GoogleAuthVerifier googleVerifier = new GoogleAuthVerifier();
    // googleVerifier.simulateFailure(1); // DEBUG ONLY.
  }

  @Override
  public boolean isLoggedIn() {
    // TODO this is supposed to use the google verifier
    return _confirmedAuthorizedGoogle;
  }

  @Override
  public void login() {
    _tokenRetries = TOKEN_RETRIES;

    System.out.println("Starting");
    _getToken(null);

  }

  private void _getToken(String accountName) {
    if (accountName == null) {

      AccountManagerFuture<Bundle> bundleFuture = AccountManager.get(
          _model.getApp().getChooseLoginActivity()).getAuthTokenByFeatures(ACCOUNT_TYPE_REQUESTED,
          "oauth2:https://www.googleapis.com/auth/userinfo.email", null,
          _model.getApp().getChooseLoginActivity(), null, null,
          new GoogleAuthAccountManagerCallback(), null);
      System.out.println("Done with first AccountManager call.  Auth proceeds asynchronously");
    } else {
      Account accountObj = new Account(accountName, ACCOUNT_TYPE_REQUESTED);
      AccountManager.get(_model.getApp().getChooseLoginActivity()).getAuthToken(accountObj,
          "oauth2:https://www.googleapis.com/auth/userinfo.email", null,
          _model.getApp().getChooseLoginActivity(), new GoogleAuthAccountManagerCallback(), null);
      System.out.println("Done with AccountManager retry.  Auth proceeds asynchronously");
    }
  }

  @Override
  public void logout() {
    System.out.println("GoogleAuthProvider.logout()");
    if (_currentGoogleLoginTokenBundle != null) {
      AccountManager aMgr = AccountManager.get(_model.getApp().getChooseLoginActivity());
      aMgr.invalidateAuthToken(getAccountType(), getAuthToken());
    }

    // Wipe login status
    _confirmedAuthorizedGoogle = false; // Not authenticated
    _currentGoogleLoginTokenBundle = null;
    _verifiedAccountName = null;
  }

  public String getAuthToken() {
    return _currentGoogleLoginTokenBundle.getString(AccountManager.KEY_AUTHTOKEN);
  }

  public String getAccountType() {
    return _currentGoogleLoginTokenBundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
  }

  public String getLocalAccountName() {
    return _currentGoogleLoginTokenBundle.getString(AccountManager.KEY_ACCOUNT_NAME);
  }

  @Override
  public String getVerifiedAccountIdentifier() {
    return _verifiedAccountName;
  }

  @Override
  public void extendAccess() {
    System.out.println("Google auth token is automatically extended?");
  }

  private void _errorDialog(String problem, String details) {
    _model.getApp().showErrorDialog("Google Login Error", problem, details);
  }

  private final class GoogleAuthAccountManagerCallback implements AccountManagerCallback<Bundle> {
    public void run(AccountManagerFuture<Bundle> future) {
      System.out.println("AccountManagerCallback executing");
      try {
        _currentGoogleLoginTokenBundle = future.getResult();
        System.out.println("Received token bundle from AccountManagerCallback");
      } catch (OperationCanceledException e) {
        Log.e("e", e.getMessage(), e);
        _errorDialog(
            "You should only see this message if you refused to authorize this app for your Google account.",
            "OperationCanceledException" + e.getMessage());
      } catch (AuthenticatorException e) {
        Log.e("e", e.getMessage(), e);
        _errorDialog("AuthenticatorException", e.getMessage());
      } catch (IOException e) {
        Log.e("e", e.getMessage(), e);
        _errorDialog("IOException", e.getMessage());
      }

      for (String s : _currentGoogleLoginTokenBundle.keySet()) {
        System.out.println("Received key/value: " + s + "="
            + _currentGoogleLoginTokenBundle.getString(s));
      }

      if (_currentGoogleLoginTokenBundle.containsKey(AccountManager.KEY_INTENT)) {
        _errorDialog("Unexpected response from Google",
            "Response contains Intent key.  Unexpected code path.");
        return;
      }

      GoogleAuthVerifier googleVerifier = new GoogleAuthVerifier();
      String currentToken = _currentGoogleLoginTokenBundle.getString(AccountManager.KEY_AUTHTOKEN);
      String accountName = _currentGoogleLoginTokenBundle
          .getString(AccountManager.KEY_ACCOUNT_NAME);

      _confirmedAuthorizedGoogle = googleVerifier.verify(currentToken, getLocalAccountName());
      // _confirmedAuthorizedGoogle = googleVerifier.verify("FAKE TOKEN",
      // getLocalAccountName());

      if (_confirmedAuthorizedGoogle) {

        // MUST BE CALLED HERE AS A CONSEQUENCE OF MULTI-THREADING
        _verifiedAccountName = getLocalAccountName();
        _model.loginSuccess(LOGIN_TYPE);
        // _model.getApp().getChooseLoginActivity().modelChanged();

      } else {

        String errorString = _currentGoogleLoginTokenBundle.getString("error");
        System.out.println("Error received: " + errorString);
        System.out.println("Retrying " + _tokenRetries + " more times");
        if (_tokenRetries > 0) {
          _tokenRetries--;
          logout();
          _getToken(accountName);
        } else {
          // No More Retries
          _errorDialog("Login failed after multiple attempts.", "Could not get access token after "
              + TOKEN_RETRIES + " attempts.");
        }
      }
    }
  }

}
