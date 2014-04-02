package com.ratethisfest.android.auth;

import android.app.Activity;

public interface AppControllerInt {
  public Activity getChooseLoginActivity();

  public void showErrorDialog(String title, String problem, String details);
}
