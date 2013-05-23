package com.ratethisfest.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;

import com.coacheller.CoachellerApplication;
import com.ratethisfest.android.log.LogController;

import android.R.anim;
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
    String action = intent.getAction();
    LogController.ALERTS.logMessage("Alert - Received alert broadcast, action: " + action);

    if (action.equals(Alert.ACTION_ALERT)) {
      handleAlertAction(context, intent);
    } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
      LogController.ALERTS.logMessage("Device was restarted, re-setting next alert");
      handleBootAction(context, intent);
    } else if (action.equals("android.intent.action.QUICKBOOT_POWERON")) {
      LogController.ALERTS.logMessage("Device received quickboot broadcast, re-setting next alert");
      handleBootAction(context, intent);
    } else {
      LogController.ERROR.logMessage("AlertReceiver received unknown alert!!!");
    }

  }

  // To receive the BOOT_COMPLETED broadcast, the application MUST be installed to internal storage
  // External storage is not yet attached when this broadcast is sent!
  private void handleAlertAction(Context context, Intent intent) {
    // here you can get the extras you passed in when creating the alarm
    Bundle bundleExtras = intent.getBundleExtra(Alert.REMINDER_BUNDLE);
    String hashKey = (String) bundleExtras.get(Alert.HASH_KEY);
    LogController.ALERTS.logMessage("Alarm went off for hashKey:" + hashKey);
    // Toast.makeText(context, "Alarm went off for hashKey:"+ hashKey, Toast.LENGTH_SHORT).show();

    CoachellerApplication appContext = (CoachellerApplication) context.getApplicationContext();
    appContext.getAlertManager().alertWentOff(hashKey);
  }

  private void handleBootAction(Context context, Intent intent) {
    CoachellerApplication appContext = (CoachellerApplication) context.getApplicationContext();
    try {
      appContext.getAlertManager().loadAlerts();
    } catch (StreamCorruptedException e) {
      appContext.getAlertManager().exceptionLoadingAlerts(e);
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      appContext.getAlertManager().exceptionLoadingAlerts(e);
      e.printStackTrace();
    } catch (IOException e) {
      appContext.getAlertManager().exceptionLoadingAlerts(e);
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      appContext.getAlertManager().exceptionLoadingAlerts(e);
      e.printStackTrace();
    }
  }
}
