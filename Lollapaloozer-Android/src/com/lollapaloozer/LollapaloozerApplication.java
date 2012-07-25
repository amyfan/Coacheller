package com.lollapaloozer;

import android.app.Application;

import com.lollapaloozer.auth.client.AuthModel;
import com.lollapaloozer.ui.ChooseLoginActivity;
import com.lollapaloozer.ui.LollapaloozerActivity;

public class LollapaloozerApplication extends Application {
  private AuthModel _authModel;
  private ChooseLoginActivity _activityChooseLogin = null;
  private LollapaloozerActivity _activityLollapaloozer = null;

  public LollapaloozerApplication() {
    System.out.println("Application Object Instantiated");

    _authModel = new AuthModel(this);
  }

  public void registerChooseLoginActivity(ChooseLoginActivity act) {
    if (_activityChooseLogin != null) {
      System.out.println("Warning: Duplicate ChooseLoginActivity registered with Application");
    }
    _activityChooseLogin = act;
  }

  public ChooseLoginActivity getChooseLoginActivity() {
    return _activityChooseLogin;
  }

  public void unregisterChooseLoginActivity() {
    _activityChooseLogin = null;
  }

  public void registerLollapaloozerActivity(LollapaloozerActivity act) {
    if (_activityLollapaloozer != null) {
      System.out.println("Warning: Duplicate LollapaloozerActivity registered with Application");
    }
    _activityLollapaloozer = act;
  }

  public LollapaloozerActivity getLollapaloozerActivity() {
    return _activityLollapaloozer;
  }

  public void unregisterLollapaloozerActivity() {
    _activityLollapaloozer = null;
  }

  public AuthModel getAuthModel() {
    return _authModel;
  }

  private void _checkNull(Object obj) {
    if (obj == null) {
      System.out.println("Application object was destroyed, state has been lost");
      System.exit(0);
    }
  }

}
