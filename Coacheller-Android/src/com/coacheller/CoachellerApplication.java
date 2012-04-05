package com.coacheller;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class CoachellerApplication extends Application {

  public static void debug(Context context, String out) {
    Log.v(context.getString(R.string.app_name), out);
  }

}
