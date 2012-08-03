package com.lollapaloozer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lollapaloozer.auth.client.AuthModel;
import com.lollapaloozer.ui.ChooseLoginActivity;
import com.lollapaloozer.ui.LollapaloozerActivity;

public class LollapaloozerApplication extends Application {
  private AuthModel _authModel;
  private ChooseLoginActivity _activityChooseLogin = null;
  private LollapaloozerActivity _activityLollapaloozer = null;
  private Activity _lastActivity;

  public LollapaloozerApplication() {
    System.out.println("Application Object Instantiated");

    _authModel = new AuthModel(this);
  }

  public void registerChooseLoginActivity(ChooseLoginActivity act) {
    if (_activityChooseLogin != null) {
      if (_activityChooseLogin == act) {
        System.out.println("Identical ChooseLoginActivity was registered with Application");
      } else {
        System.out
            .println("Warning: Different ChooseLoginActivity was registered with Application");
      }
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
      if (_activityLollapaloozer == act) {
        System.out.println("Identical LollapaloozerActivity was registered with Application");
      } else {
        System.out
            .println("Warning: Different LollapaloozerActivity was registered with Application");
      }
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

  public void setLastActivity(Activity act) {
    _lastActivity = act;
  }

  public Activity getLastActivity() {
    return _lastActivity;

  }

  public void showErrorDialog(String title, String problem, String details) {
    String errorString = problem + "\r\n\r\nDetails:\r\n" + details;
    System.out.println(errorString);

    AlertDialog.Builder builder = new AlertDialog.Builder(getLastActivity());
    builder.setTitle(title);
    builder.setMessage(errorString).setCancelable(true)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
          }
        }

        )
    // todo .setIcon(R.)

    // .setNegativeButton("No", new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog, int id) {
    // }
    // })
    ;
    AlertDialog alert = builder.create();
    alert.show();
  }

  public String bundleValues(Bundle inputBundle) {
    StringBuilder returnString = new StringBuilder();
    int count = 0;
    for (String s : inputBundle.keySet()) {
      returnString.append(s + ": " + inputBundle.get(s));
      count++;
    }
    return "[" + count + "]: " + returnString.toString();
  }

}
