package com.ratethisfest.android.auth;

import android.app.Activity;

public interface AuthActivityInt {

  public void doFacebookPost();

  public void doTwitterPost();

  public void modelChanged();

  public Activity getLastActivity();
}
