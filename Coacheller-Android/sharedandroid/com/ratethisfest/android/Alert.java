package com.ratethisfest.android;

import java.util.Calendar;

import com.ratethisfest.android.log.LogController;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Alert extends BroadcastReceiver {
  //NEED TO BE ABLE TO LIST ALL ALARMS SET BY THIS CLASS
  
  private final static String REMINDER_BUNDLE = "RateThisFestAlertBundle";
  private Context appContext;
  private PendingIntent createdPendingIntent;
  
//Supposedly called by Android AlarmManager
  public Alert() {
    LogController.ALERTS.logMessage("Class Alert - Default Constructor called");
  }

  // you can use this constructor to create the alarm.
  // Just pass in the main activity as the context,
  // any extras you'd like to get later when triggered
  // and the timeout
  public Alert(Context context, Bundle extras, int timeoutInSeconds) {
    this.appContext = context;
    LogController.ALERTS.logMessage("Class Alert - Setting alert");
    
    //Create pendingIntent to be broadcast later
    AlarmManager alarmMgr = (AlarmManager) this.appContext.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(this.appContext, AlertManager.class);
    intent.putExtra(REMINDER_BUNDLE, extras);
    this.createdPendingIntent = PendingIntent.getBroadcast(this.appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    
    //Set intent to fire after X millis
    Calendar time = Calendar.getInstance();
    time.setTimeInMillis(System.currentTimeMillis()); //Might not be needed
    time.add(Calendar.SECOND, timeoutInSeconds);
    alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), this.createdPendingIntent);
  }
  
  public void cancel() {
    LogController.ALERTS.logMessage("Class Alert - Cancelling alert");
    AlarmManager alarmMgr = (AlarmManager) this.appContext.getSystemService(Context.ALARM_SERVICE);
    alarmMgr.cancel(this.createdPendingIntent);
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
    LogController.ALERTS.logMessage("AlertManager - Received alert broadcast");
    // here you can get the extras you passed in when creating the alarm
    // intent.getBundleExtra(REMINDER_BUNDLE));

    Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show();
  }

}
