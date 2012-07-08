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
import com.lollapaloozer.ui.AuthChooseAccountActivity;

public class GoogleAuthProvider implements AuthProvider {

  private final String ACCOUNT_TYPE_REQUESTED = "com.google";
  private final int TOKEN_RETRIES = 2;

  private AuthChooseAccountActivity _activity;
  private Bundle _currentGoogleLoginTokenBundle = null;
  private JSONObject _currentAuthResult;
  private boolean _confirmedAuthorizedGoogle;
  private String _verifiedAccountName = null;
  private int _tokenRetries;

  private GoogleAuthProvider() {
    // Default constructor disallowed
  }

  public GoogleAuthProvider(AuthChooseAccountActivity activity) {
    _activity = activity;
    GoogleAuthVerifier googleVerifier = new GoogleAuthVerifier();
    // googleVerifier.simulateFailure(1); // DEBUG ONLY.
  }

  @Override
  public boolean isLoggedIn() {
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

      AccountManagerFuture<Bundle> bundleFuture = AccountManager.get(_activity)
          .getAuthTokenByFeatures(ACCOUNT_TYPE_REQUESTED,
              "oauth2:https://www.googleapis.com/auth/userinfo.email", null, _activity, null, null,
              new GoogleAuthAccountManagerCallback(), null);
      System.out.println("Done with AccountManager call.  Auth proceeds asynchronously");
    } else {
      Account accountObj = new Account(accountName, ACCOUNT_TYPE_REQUESTED);
      AccountManager.get(_activity).getAuthToken(accountObj,
          "oauth2:https://www.googleapis.com/auth/userinfo.email", null, _activity,
          new GoogleAuthAccountManagerCallback(), null);
      System.out.println("Done with AccountManager retry.  Auth proceeds asynchronously");
    }
  }

  @Override
  public void logout() {
    System.out.println("GoogleAuthProvider.logout()");
    if (_currentGoogleLoginTokenBundle != null) {
      AccountManager aMgr = AccountManager.get(_activity);
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

    System.out
        .println("Activity resumed.  Not sure if google auth token is automatically extended.");
  }

  private final class GoogleAuthAccountManagerCallback implements AccountManagerCallback<Bundle> {
    public void run(AccountManagerFuture<Bundle> future) {
      System.out.println("AccountManagerCallback executing");
      try {
        _currentGoogleLoginTokenBundle = future.getResult();
        System.out.println("Received token bundle from AccountManagerCallback");
      } catch (OperationCanceledException e) {
        Log.e("e", e.getMessage(), e);
        System.out.println("User appears to have denied auth request");
      } catch (AuthenticatorException e) {
        Log.e("e", e.getMessage(), e);
      } catch (IOException e) {
        Log.e("e", e.getMessage(), e);
      }

      for (String s : _currentGoogleLoginTokenBundle.keySet()) {
        System.out.println("Received key/value: " + s + "="
            + _currentGoogleLoginTokenBundle.getString(s));
      }

      if (_currentGoogleLoginTokenBundle.containsKey(AccountManager.KEY_INTENT)) {
        throw new RuntimeException("Unexpected Code Path");
        // TODO return to account selection or something...
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
        _activity.modelChanged();

      } else {

        String errorString = _currentGoogleLoginTokenBundle.getString("error");
        System.out.println("Error received: " + errorString);
        System.out.println("Retrying " + _tokenRetries + " more times");
        if (_tokenRetries > 0) {
          _tokenRetries--;
          logout();
          _getToken(accountName);
        }
      }
    }
  }

}
