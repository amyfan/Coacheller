package com.lollapaloozer.auth.client;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Intent;

import com.facebook.android.Facebook;
import com.lollapaloozer.LollapaloozerApplication;
import com.lollapaloozer.ui.ChooseLoginActivity;
import com.lollapaloozer.ui.LollapaloozerActivity;
import com.ratethisfest.shared.Constants;

public class AuthModel {

  public static final String PERMISSION_FACEBOOK_POSTWALL = "PERMISSION_FACEBOOK_POSTWALL";
  public static final String PERMISSION_TWITTER_TWEET = "PERMISSION_TWITTER_TWEET";

  private LollapaloozerApplication _app;
  private GoogleAuthProvider _authProviderGoogle;
  private FacebookAuthProvider _authProviderFacebook;
  private TwitterAuthProvider _authProviderTwitter;
  private FacebookWebAuthProvider _authProviderFacebookWeb;
  private String _primaryLoginType;
  // private ChooseLoginActivity _activity;

  private String _verifiedAccountName = null;
  private ArrayList<String> _permissions = new ArrayList<String>();

  public AuthModel(LollapaloozerApplication app) {
    _app = app;
    _authProviderGoogle = new GoogleAuthProvider(AuthModel.this);
    _authProviderFacebook = new FacebookAuthProvider(AuthModel.this);
    _authProviderFacebookWeb = new FacebookWebAuthProvider(AuthModel.this);
    _authProviderTwitter = new TwitterAuthProvider(AuthModel.this);
  }

  public LollapaloozerApplication getApp() {
    return _app;
  }

  public boolean isLoggedInPrimary() {
    if (_primaryLoginType != null) {
      return true;
    } else {
      return false;
    }
  }

  public void loginSuccess(String loginType) {
    if (_primaryLoginType == null) {
      _primaryLoginType = loginType;
    }

    if (loginType.equals(Constants.LOGIN_TYPE_FACEBOOK)) {
      _addPermission(PERMISSION_FACEBOOK_POSTWALL);
      if (_app.getLastActivity() instanceof LollapaloozerActivity) {
        _app.getLollapaloozerActivity().doFacebookPost();
      }
    }

    if (loginType.equals(Constants.LOGIN_TYPE_TWITTER)) {
      _addPermission(PERMISSION_TWITTER_TWEET);
      if (_app.getLastActivity() instanceof LollapaloozerActivity) {
        _app.getLollapaloozerActivity().doTwitterPost();
      }
    }

    if (_app.getLastActivity() instanceof ChooseLoginActivity) {
      _app.getChooseLoginActivity().modelChanged();
    }

  }

  // public boolean isLoggedIn() {
  // int numLogins = 0;
  // if (_authProviderGoogle.isLoggedIn()) {
  // numLogins++;
  // }
  //
  // if (_authProviderFacebook.isLoggedIn()) {
  // numLogins++;
  // }
  //
  // if (_authProviderFacebookWeb.isLoggedIn()) {
  // numLogins++;
  // }
  //
  // if (_authProviderTwitter.isLoggedIn()) {
  // numLogins++;
  // }
  //
  // if (numLogins > 1) {
  // System.out.println("WARNING: Multiple concurrent login types detected");
  // }
  //
  // if (numLogins > 0) {
  // return true;
  // } else {
  // return false;
  // }
  // }

  public boolean havePermission(String permission) {
    return _permissions.contains(permission);
  }

  public void getPermission(String permission) {
    if (permission.equals(PERMISSION_FACEBOOK_POSTWALL)) {
      _authProviderFacebook.login();

    } else if (permission.equals(PERMISSION_TWITTER_TWEET)) {
      _authProviderTwitter.login();

    } else {

      throw new RuntimeException("Invalid permission, unexpected code path");
    }
  }

  private void _addPermission(String permission) {
    if (!havePermission(permission)) {
      _permissions.add(permission);
    }
  }

  // Return whether permission was successfully obtained
  public void ensurePermission(String permission) {
    if (!havePermission(permission)) {
      getPermission(permission);
    }
  }

  public AuthProvider getCurrentAuthProvider() {
    if (Constants.LOGIN_TYPE_GOOGLE.equals(_primaryLoginType)) {
      return _authProviderGoogle;
    }

    if (Constants.LOGIN_TYPE_FACEBOOK.equals(_primaryLoginType)) {
      return _authProviderFacebook;
    }

    if (Constants.LOGIN_TYPE_FACEBOOK_BROWSER.equals(_primaryLoginType)) {
      return _authProviderFacebookWeb;
    }

    if (Constants.LOGIN_TYPE_TWITTER.equals(_primaryLoginType)) {
      return _authProviderTwitter;
    }

    return null;
  }

  public String getCurrentAuthProviderType() {
    return _primaryLoginType;
  }

  // public int getCurrentAuthProviderType() {
  // if (_authProviderGoogle.isLoggedIn()) {
  // return Constants.LOGIN_TYPE_GOOGLE;
  // }
  //
  // if (_authProviderFacebook.isLoggedIn()) {
  // return Constants.LOGIN_TYPE_FACEBOOK;
  // }
  //
  // if (_authProviderFacebookWeb.isLoggedIn()) {
  // return Constants.LOGIN_TYPE_FACEBOOK_BROWSER;
  // }
  //
  // if (_authProviderTwitter.isLoggedIn()) {
  // return Constants.LOGIN_TYPE_TWITTER;
  // }
  //
  // return 0;
  //
  // }

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

    _primaryLoginType = null;
    _permissions.clear();
  }

  public void primaryLogin(String loginType) {
    if (Constants.LOGIN_TYPE_GOOGLE.equals(loginType)) {
      loginToGoogle();

    } else if (Constants.LOGIN_TYPE_FACEBOOK.equals(loginType)) {
      loginToFacebook();

    } else if (Constants.LOGIN_TYPE_FACEBOOK_BROWSER.equals(loginType)) {
      loginToFacebookBrowser();

    } else if (Constants.LOGIN_TYPE_TWITTER.equals(loginType)) {
      loginToTwitter();

    } else {
      throw new RuntimeException("Invalid login type, unexpected code path");
    }
  }

  void loginToGoogle() {
    _authProviderGoogle.login();
  }

  void loginToFacebook() {
    _authProviderFacebook.login();
  }

  void loginToFacebookBrowser() {
    _authProviderFacebookWeb.login();
  }

  void loginToTwitter() {
    _authProviderTwitter.login();
  }

  public void twitterAuthCallback(int requestCode, int resultCode, Intent data) {
    _authProviderTwitter.requestTokenCallback(requestCode, resultCode, data);
  }

  public String postToFacebookWall(String message) {
    return _authProviderFacebook.postToWall(message);
  }

}
