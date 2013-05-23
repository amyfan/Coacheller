package com.ratethisfest.android;

import java.io.IOException;
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
  private static final long serialVersionUID = 1L;
  public static final String REMINDER_BUNDLE = "RateThisFestAlertBundle";
  public static final String HASH_KEY = "HASH_KEY";
  public static final String ACTION_ALERT = "com.ratethisfest.Alert:ACTION";
  private String hashKey;
  private String stageName;
  private String artistName;
  private Date performanceDateTime;
  private int minutesBeforeSetTime;

  // Should not be members, we don't have context when we deserialize so cannot re-create
  // private transient Context context; // Do not serialize, not relevant unless alarm is being set
  // private PendingIntent createdPendingIntent; // Not sure about serializing

  public Alert() {

    LogController.ALERTS.logMessage("Class Alert - Default Constructor called");
  }

  /** @category Properties */
  public String getStage() {
    return this.stageName;
  }

  /** @category Properties */
  public String getArtist() {
    return this.artistName;
  }

  /** @category Properties */
  public String getHashKey() {
    return this.hashKey;
  }

  /** @category Properties */
  public Date getSetTime() {
    return this.performanceDateTime;
  }

  // you can use this constructor to create the alarm.
  // Just pass in the main activity as the context,
  // any extras you'd like to get later when triggered
  // and the timeout
  public Alert(FestivalEnum fest, int week, JSONObject setData, String hashKey, int timeoutInSeconds)
      throws JSONException {

    this.hashKey = hashKey;
    this.minutesBeforeSetTime = timeoutInSeconds;
    this.performanceDateTime = CalendarUtils.getSetDateTime(setData, week, fest);
    this.artistName = setData.getString(AndroidConstants.JSON_KEY_SETS__ARTIST);

    String stageKey = AndroidConstants.JSON_KEY_SETS__STAGE_ONE;
    if (week == 2) {
      stageKey = AndroidConstants.JSON_KEY_SETS__STAGE_TWO;
    }
    this.stageName = setData.getString(stageKey);
  }

  public void setAlarm(Context context) {
    LogController.ALERTS.logMessage("Class Alert - Setting alert " + hashKey);

    Bundle extras = createBundle();

    // Create pendingIntent to be broadcast later
    PendingIntent builtPendingIntent = buildPendingIntent(context, extras);

    // // Set intent to fire after X millis
    // Calendar time = Calendar.getInstance();
    // time.setTimeInMillis(System.currentTimeMillis()); // Might not be needed
    // time.add(Calendar.SECOND, this.minutesBeforeSetTime);
    Calendar time = Calendar.getInstance();
    time.setTime(getAlertDateTime());

    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), builtPendingIntent);
  }

  public void cancel(Context context) {
    LogController.ALERTS.logMessage("Class Alert - Cancelling alert" + hashKey);
    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    // Extras bundle does not matter for purpose of matching PendingIntent 's
    alarmMgr.cancel(buildPendingIntent(context, createBundle()));
  }

  /** @category Intent */
  private Bundle createBundle() {
    Bundle extras = new Bundle();
    extras.putString(HASH_KEY, this.hashKey);
    return extras;
  }

  /** @category Intent */
  private PendingIntent buildPendingIntent(Context context, Bundle extras) {
    Intent intent = new Intent(context, AlertReceiver.class);
    intent.setAction(ACTION_ALERT);
    intent.putExtra(REMINDER_BUNDLE, extras);
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  public String getDayDateAsString() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(performanceDateTime);
    // use simpledateformat here
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d");
    // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy"); //Includes year
    return simpleDateFormat.format(performanceDateTime);
  }

  public String getSetTimeAsString() {
    int milTime = performanceDateTime.getHours() * 100 + performanceDateTime.getMinutes();
    return DateTimeUtils.militaryToCivilianTime(milTime);
  }

  // Should check if alert time has passed before using this
  public String getTextIntervalUntilAlert() {
    long currentTimeMillis = System.currentTimeMillis();
    Date alertDateTime = this.getAlertDateTime();
    long alertTimeMillis = alertDateTime.getTime();

    return CalendarUtils.formatInterval(alertTimeMillis - currentTimeMillis);
  }

  public Date getAlertDateTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(performanceDateTime);
    cal.add(Calendar.MINUTE, -1 * minutesBeforeSetTime);
    return cal.getTime();
  }

  public boolean getIsAlertInFuture() {
    Date alertDateTime = this.getAlertDateTime();
    long alertTimeMillis = alertDateTime.getTime();
    return System.currentTimeMillis() < alertTimeMillis;
  }

  // private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
  // stream.defaultReadObject();
  //
  // }

}
