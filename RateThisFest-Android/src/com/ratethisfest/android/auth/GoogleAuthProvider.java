package com.ratethisfest.android.auth;

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

import com.ratethisfest.auth.verify.GoogleAuthVerifier;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.LoginType;

public class GoogleAuthProvider implements AuthProviderInt {

  private final LoginType LOGIN_TYPE = LoginType.GOOGLE;
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
  @SuppressWarnings("unused")
  private GoogleAuthProvider() {
  }

  public GoogleAuthProvider(AuthModel model) {
    // _activity = activity;
    _model = model;
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
          _model.getAppController().getChooseLoginActivity()).getAuthTokenByFeatures(ACCOUNT_TYPE_REQUESTED,
          "oauth2:https://www.googleapis.com/auth/userinfo.email", null,
          _model.getAppController().getChooseLoginActivity(), null, null,
          new GoogleAuthAccountManagerCallback(), null);
      System.out.println("Done with first AccountManager call.  Auth proceeds asynchronously");
    } else {
      Account accountObj = new Account(accountName, ACCOUNT_TYPE_REQUESTED);
      AccountManager.get(_model.getAppController().getChooseLoginActivity()).getAuthToken(accountObj,
          "oauth2:https://www.googleapis.com/auth/userinfo.email", null,
          _model.getAppController().getChooseLoginActivity(), new GoogleAuthAccountManagerCallback(), null);
      System.out.println("Done with AccountManager retry.  Auth proceeds asynchronously");
    }
  }

  @Override
  public void logout() {
    System.out.println("GoogleAuthProvider.logout()");
    if (_currentGoogleLoginTokenBundle != null) {
      AccountManager aMgr = AccountManager.get(_model.getAppController().getChooseLoginActivity());
      aMgr.invalidateAuthToken(getAccountType(), getAuthToken());
    }

    // Wipe login status
    _confirmedAuthorizedGoogle = false; // Not authenticated
    _currentGoogleLoginTokenBundle = null;
    _verifiedAccountName = null;
  }

  @Override
  public String getAuthToken() {
    return _currentGoogleLoginTokenBundle.getString(AccountManager.KEY_AUTHTOKEN);
  }

  @Override
  public String getAccountType() {
    return _currentGoogleLoginTokenBundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
  }

  @Override
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
    _model.getAppController().showErrorDialog("Google Login Error", problem, details);
  }

  private final class GoogleAuthAccountManagerCallback implements AccountManagerCallback<Bundle> {
    @Override
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

      GoogleAuthVerifier googleVerifier = new GoogleAuthVerifier(
          _model.getAppConstant(AuthConstants.GOOGLE_MOBILE_CLIENT_ID), 
          _model.getAppConstant(AuthConstants.GOOGLE_MOBILE_CLIENT_SECRET));
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
        // _model.getAppController().getChooseLoginActivity().modelChanged();

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
