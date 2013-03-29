package com.ratethisfest.android.auth;

public interface AuthProviderInt {

  public boolean isLoggedIn();

  public void login();

  public void logout();

  public String getAccountType();

  public String getLocalAccountName();

  public String getVerifiedAccountIdentifier();

  public String getAuthToken();

  public void extendAccess();

}
