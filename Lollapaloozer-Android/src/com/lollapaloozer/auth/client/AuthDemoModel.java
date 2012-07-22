package com.lollapaloozer.auth.client;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.Intent;

import com.facebook.android.Facebook;
import com.lollapaloozer.LollapaloozerApplication;
import com.ratethisfest.shared.Constants;

public class AuthDemoModel {

  private LollapaloozerApplication _app;
  private GoogleAuthProvider _authProviderGoogle;
  private FacebookAuthProvider _authProviderFacebook;
  private TwitterAuthProvider _authProviderTwitter;
  private FacebookWebAuthProvider _authProviderFacebookWeb;
  // private ChooseLoginActivity _activity;

  private String _verifiedAccountName = null;
  private ArrayList<Integer> _permissions = new ArrayList<Integer>();

  public AuthDemoModel(LollapaloozerApplication app) {
    _app = app;
    _authProviderGoogle = new GoogleAuthProvider(AuthDemoModel.this);
    _authProviderFacebook = new FacebookAuthProvider(AuthDemoModel.this);
    _authProviderFacebookWeb = new FacebookWebAuthProvider(AuthDemoModel.this);
    _authProviderTwitter = new TwitterAuthProvider(AuthDemoModel.this);
  }

  public LollapaloozerApplication getApp() {
    return _app;
  }

  public boolean isLoggedIn() {
    int numLogins = 0;
    if (_authProviderGoogle.isLoggedIn()) {
      numLogins++;
    }

    if (_authProviderFacebook.isLoggedIn()) {
      numLogins++;
    }

    if (_authProviderFacebookWeb.isLoggedIn()) {
      numLogins++;
    }

    if (_authProviderTwitter.isLoggedIn()) {
      numLogins++;
    }

    if (numLogins > 1) {
      System.out.println("WARNING: Multiple concurrent login types detected");
    }

    if (numLogins > 0) {
      return true;
    } else {
      return false;
    }
  }

  public boolean havePermission(int permission) {
    return _permissions.contains(permission);
  }

  public void getPermission(int permission) {
    switch (permission) {
    case Constants.PERMISSION_FACEBOOK_POSTWALL:
      System.out.println("Placeholder to obtain Facebook permission");
      if (true) {
        _addPermission(permission);
      }
      break;

    case Constants.PERMISSION_TWITTER_TWEET:
      System.out.println("Placeholder to obtain Twitter permission");
      if (true) {
        _addPermission(permission);
      }
      break;

    default:
      throw new RuntimeException("Invalid permission, unexpected code path");
    }
  }

  private void _addPermission(int permission) {
    if (!havePermission(permission)) {
      _permissions.add(permission);
    }
  }

  public boolean ensurePermission(int permission) {
    if (!havePermission(permission)) {
      getPermission(permission);
    }

    return havePermission(permission);
  }

  public AuthProvider getCurrentAuthProvider() {
    if (_authProviderGoogle.isLoggedIn()) {
      return _authProviderGoogle;
    }

    if (_authProviderFacebook.isLoggedIn()) {
      return _authProviderFacebook;
    }

    if (_authProviderFacebookWeb.isLoggedIn()) {
      return _authProviderFacebookWeb;
    }

    if (_authProviderTwitter.isLoggedIn()) {
      return _authProviderTwitter;
    }

    return null;
  }

  public int getCurrentAuthProviderType() {
    if (_authProviderGoogle.isLoggedIn()) {
      return Constants.LOGIN_TYPE_GOOGLE;
    }

    if (_authProviderFacebook.isLoggedIn()) {
      return Constants.LOGIN_TYPE_FACEBOOK;
    }

    if (_authProviderFacebookWeb.isLoggedIn()) {
      return Constants.LOGIN_TYPE_FACEBOOK_BROWSER;
    }

    if (_authProviderTwitter.isLoggedIn()) {
      return Constants.LOGIN_TYPE_TWITTER;
    }

    return 0;

  }

  public void checkAccounts() {
    AccountManager aMgr = AccountManager.get(getApp().getChooseLoginActivity());

    System.out.println("Warning: The following requires permission GET_ACCOUNTS");
    for (Account a : aMgr.getAccounts()) {
      System.out.println(a.name + " " + a.type + " " + a.toString());
    }

    System.out.println("Installed Authenticators: ");
    for (AuthenticatorDescription d : aMgr.getAuthenticatorTypes()) {
      System.out.print(d.type);
      System.out.print("/ ");
    }
    System.out.println();
  }

  public Facebook getFacebookObject() {
    return _authProviderFacebook.getFacebookObject();
  }

  public void invalidateTokens() {
    _authProviderGoogle.logout();
    _authProviderFacebook.logout();
    _authProviderFacebookWeb.logout();
    _authProviderTwitter.logout();
    _permissions.clear();
  }

  public void loginToGoogle() {
    _authProviderGoogle.login();
  }

  public void loginToFacebook() {
    _authProviderFacebook.login();
  }

  public void loginToFacebookBrowser() {
    _authProviderFacebookWeb.login();
  }

  public void loginToTwitter() {
    _authProviderTwitter.login();
  }

  public void twitterAuthCallback(int requestCode, int resultCode, Intent data) {
    _authProviderTwitter.requestTokenCallback(requestCode, resultCode, data);
  }

  public Context getAuthActivity() {
    // TODO Auto-generated method stub
    return null;
  }

}
