package com.ratethisfest.android;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ratethisfest.android.log.LogController;
import com.ratethisfest.shared.DateTimeUtils;
import com.ratethisfest.shared.FestivalEnum;

public class Alert implements Serializable {

  public static final String REMINDER_BUNDLE = "RateThisFestAlertBundle";
  public static final String HASH_KEY = "HASH_KEY"; //Possibly move to Alert class
  private String hashKey;
  private String stageName;
  private String artistName;
  private Date setTime;
  private int minutesBeforeSetTime;
  private transient Context context; // Do not serialize, not relevant unless alarm is being set
  private PendingIntent createdPendingIntent; // Not sure about serializing

  public Alert() {
    LogController.ALERTS.logMessage("Class Alert - Default Constructor called");
  }
  
  public Date getSetTime() {
    return this.setTime;
  }

  // you can use this constructor to create the alarm.
  // Just pass in the main activity as the context,
  // any extras you'd like to get later when triggered
  // and the timeout
  public Alert(FestivalEnum fest, int week, JSONObject setData, String hashKey, int timeoutInSeconds)
      throws JSONException {
    
    this.hashKey = hashKey;
    this.minutesBeforeSetTime = timeoutInSeconds;
    this.setTime = CalendarUtils.getSetDateTime(setData, week, fest);
    this.artistName = setData.getString(AndroidConstants.JSON_KEY_SETS__ARTIST);
    
    String stageKey = AndroidConstants.JSON_KEY_SETS__STAGE_ONE;
    if (week ==2) {
      stageKey = AndroidConstants.JSON_KEY_SETS__STAGE_TWO;
    }
    this.stageName = setData.getString(stageKey);
  }

  public void setAlarm(Context context) {
    LogController.ALERTS.logMessage("Class Alert - Setting alert "+ hashKey);

    Bundle extras = new Bundle();
    extras.putString(HASH_KEY, this.hashKey);
    
    // Create pendingIntent to be broadcast later
    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(context, AlertReceiver.class);
    intent.putExtra(REMINDER_BUNDLE, extras);
    this.createdPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    // Set intent to fire after X millis
    Calendar time = Calendar.getInstance();
    time.setTimeInMillis(System.currentTimeMillis()); // Might not be needed
    time.add(Calendar.SECOND, this.minutesBeforeSetTime);
    alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), this.createdPendingIntent);
  }

  public void cancel() {
    LogController.ALERTS.logMessage("Class Alert - Cancelling alert"+ hashKey);
    AlarmManager alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
    alarmMgr.cancel(this.createdPendingIntent);
  }

  public String getDayDateAsString() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(setTime);
    //use simpledateformat here
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d");
    // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy"); //Includes year
    return simpleDateFormat.format(setTime);
  }

  public String getSetTimeAsString() {
    int milTime = setTime.getHours() *100 + setTime.getMinutes();
    return DateTimeUtils.militaryToCivilianTime(milTime);
  }

  public String getStage() {
    return this.stageName;
  }
  
  public String getArtist() {
    return this.artistName;
  }
  
  //Should check if alert time has passed before using this
  public String getTimeIntervalToAlert() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(setTime);
    cal.add(Calendar.MINUTE, -1 * minutesBeforeSetTime);
    
    long currentTimeMillis = System.currentTimeMillis();
    long alertTimeMillis = cal.getTimeInMillis();
    
    return CalendarUtils.formatInterval(alertTimeMillis - currentTimeMillis);
  }
  
  

}
