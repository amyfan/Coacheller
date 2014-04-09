package com.ratethisfest.android.auth;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;

import com.facebook.android.Facebook;
import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.android.ui.ChooseLoginActivity;
import com.ratethisfest.shared.LoginType;

public class AuthModel {

  public static final String PERMISSION_FACEBOOK_POSTWALL = "PERMISSION_FACEBOOK_POSTWALL";
  public static final String PERMISSION_TWITTER_TWEET = "PERMISSION_TWITTER_TWEET";

  private AppControllerInt appController;
  private HashMap<String, String> _appConstants;
  private Activity lastAuthActivity;
  private GoogleAuthProvider _authProviderGoogle;
  private FacebookAuthProvider _authProviderFacebook;
  private TwitterAuthProvider _authProviderTwitter;
  private FacebookWebAuthProvider _authProviderFacebookWeb;
  private LoginType _primaryLoginType;
  // private ChooseLoginActivity _activity;

  private String _verifiedAccountName = null;
  private ArrayList<String> _permissions = new ArrayList<String>();

  public AuthModel(AppControllerInt appController, HashMap<String, String> appConstants) {
    this.appController = appController;
    _appConstants = appConstants;
    _authProviderGoogle = new GoogleAuthProvider(AuthModel.this);
    _authProviderFacebook = new FacebookAuthProvider(AuthModel.this);
    // _authProviderFacebookWeb = new FacebookWebAuthProvider(AuthModel.this); //Never implemented
    _authProviderTwitter = new TwitterAuthProvider(AuthModel.this);
  }

  public String getAppConstant(String keyName) {
    return _appConstants.get(keyName);
  }

  public boolean isLoggedInPrimary() {
    if (_primaryLoginType != null) {
      return true;
    } else {
      return false;
    }
  }

  public void loginSuccess(LoginType loginType) {
    if (_primaryLoginType == null) {
      _primaryLoginType = loginType;
    }

    if (loginType.equals(LoginType.FACEBOOK)) {
      _addPermission(PERMISSION_FACEBOOK_POSTWALL);
    }

    if (loginType.equals(LoginType.TWITTER)) {
      _addPermission(PERMISSION_TWITTER_TWEET);
    }

    // If ChooseLoginActivity
    if (lastAuthActivity instanceof ChooseLoginActivity) {
      ((ChooseLoginActivity) lastAuthActivity).modelChanged();
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

  public AuthProviderInt getCurrentAuthProvider() {
    if (_primaryLoginType != null) {
      switch (_primaryLoginType) {
      case GOOGLE:
        return _authProviderGoogle;
      case FACEBOOK:
        return _authProviderFacebook;
      case FACEBOOK_BROWSER:
        return _authProviderFacebookWeb;
      case TWITTER:
        return _authProviderTwitter;
      }
    }

    return null;
  }

  public LoginType getCurrentAuthProviderType() {
    return _primaryLoginType;
  }

  // public int getCurrentAuthProviderType() {
  // if (_authProviderGoogle.isLoggedIn()) {
  // return AuthConstants.LOGIN_TYPE_GOOGLE;
  // }
  //
  // if (_authProviderFacebook.isLoggedIn()) {
  // return AuthConstants.LOGIN_TYPE_FACEBOOK;
  // }
  //
  // if (_authProviderFacebookWeb.isLoggedIn()) {
  // return AuthConstants.LOGIN_TYPE_FACEBOOK_BROWSER;
  // }
  //
  // if (_authProviderTwitter.isLoggedIn()) {
  // return AuthConstants.LOGIN_TYPE_TWITTER;
  // }
  //
  // return 0;
  //
  // }

  // TODO
  public void checkAccounts() {
    // AccountManager aMgr =
    // AccountManager.get(getApp().getChooseLoginActivity());
    //
    // System.out.println("Warning: The following requires permission GET_ACCOUNTS");
    // for (Account a : aMgr.getAccounts()) {
    // System.out.println(a.name + " " + a.type + " " + a.toString());
    // }
    //
    // System.out.println("Installed Authenticators: ");
    // for (AuthenticatorDescription d : aMgr.getAuthenticatorTypes()) {
    // System.out.print(d.type);
    // System.out.print("/ ");
    // }
    // System.out.println();
  }

  public Facebook getFacebookObject() {
    return _authProviderFacebook.getFacebookObject();
  }

  public void invalidateTokens() {
    _authProviderGoogle.logout();
    _authProviderFacebook.logout();
    // _authProviderFacebookWeb.logout();
    _authProviderTwitter.logout();

    _primaryLoginType = null;
    _permissions.clear();
  }

  public void primaryLogin(LoginType loginType) {
    switch (loginType) {
    case GOOGLE:
      loginToGoogle();
      break;
    case FACEBOOK:
      loginToFacebook();
      break;
    case FACEBOOK_BROWSER:
      loginToFacebookBrowser();
      break;
    case TWITTER:
      loginToTwitter();
      break;
    default:
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

  public String postToFacebookWall(SocialNetworkPost queuedFacebookPost) {
    return _authProviderFacebook.postToWall(queuedFacebookPost);
  }

  public String tweetToTwitter(SocialNetworkPost queuedTwitterPost) {
    return _authProviderTwitter.tweet(queuedTwitterPost);
  }

  public AppControllerInt getAppController() {
    return appController;
  }

  public void setLastAuthRelatedActivity(Activity lastAuthActivity) {
    this.lastAuthActivity = lastAuthActivity;
  }

  public Activity getLastAuthActivity() {
    return lastAuthActivity;
  }

}
