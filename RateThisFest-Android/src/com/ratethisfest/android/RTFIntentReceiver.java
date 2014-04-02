package com.ratethisfest.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ratethisfest.android.alert.Alert;
import com.ratethisfest.android.log.LogController;

public class RTFIntentReceiver extends BroadcastReceiver {

  // Supposedly called by Android AlarmManager
  public RTFIntentReceiver() {
    LogController.ALERTS.logMessage("Class RTFIntentReceiver - Default Constructor called");
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (action == null) {
      LogController.ALERTS.logMessage("BroadcastReceiver received intent with null action, ignoring");
      return;
    }
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
      LogController.ERROR.logMessage("RTFIntentReceiver received unknown alert!!!");
    }

  }

  // To receive the BOOT_COMPLETED broadcast, the application MUST be installed to internal storage
  // External storage is not yet attached when this broadcast is sent!
  private void handleAlertAction(Context context, Intent intent) {
    // here you can get the extras you passed in when creating the alarm
    Bundle bundleExtras = intent.getBundleExtra(Alert.REMINDER_BUNDLE);
    String hashKey = (String) intent.getStringExtra(Alert.HASH_KEY);
    // String hashKey = (String) bundleExtras.get(Alert.HASH_KEY); //Changed how bundle is packaged
    LogController.ALERTS.logMessage("Alarm went off for hashKey:" + hashKey);
    // Toast.makeText(context, "Alarm went off for hashKey:"+ hashKey, Toast.LENGTH_SHORT).show();

    FestivalApplication appContext = (FestivalApplication) context.getApplicationContext();
    appContext.getAlertManager().alertWentOff(hashKey, context, intent);
    appContext.redrawSetList();
  }

  private void handleBootAction(Context context, Intent intent) {
    FestivalApplication appContext = (FestivalApplication) context.getApplicationContext();
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
