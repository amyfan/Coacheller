package com.ratethisfest.android;

import com.coacheller.CoachellerApplication;
import com.ratethisfest.android.log.LogController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlertReceiver extends BroadcastReceiver {

  // Supposedly called by Android AlarmManager
  public AlertReceiver() {
    LogController.ALERTS.logMessage("Class AlertReceiver - Default Constructor called");
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    LogController.ALERTS.logMessage("Alert - Received alert broadcast");
    // here you can get the extras you passed in when creating the alarm
    Bundle bundleExtras = intent.getBundleExtra(Alert.REMINDER_BUNDLE);
    String hashKey = (String) bundleExtras.get(Alert.HASH_KEY);
    LogController.ALERTS.logMessage("Alarm went off for hashKey:" + hashKey);
    // Toast.makeText(context, "Alarm went off for hashKey:"+ hashKey, Toast.LENGTH_SHORT).show();

    CoachellerApplication appContext = (CoachellerApplication) context.getApplicationContext();
    appContext.getAlertManager().alertWentOff(hashKey);
  }
}
