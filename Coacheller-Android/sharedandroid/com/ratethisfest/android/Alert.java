package com.ratethisfest.android;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.coacheller.R;
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

  // Initializes an object to represent the Alert for this set
  private Alert() {
    LogController.ERROR.logMessage("Class Alert - Default Constructor called");
  }

  // Initializes an object to represent the Alert for this set
  public Alert(FestivalEnum fest, int week, JSONObject setData, String hashKey, int minutesBeforeSet)
      throws JSONException {
    this.hashKey = hashKey;
    this.minutesBeforeSetTime = minutesBeforeSet;
    this.performanceDateTime = CalendarUtils.getSetDateTime(setData, week, fest);
    this.artistName = setData.getString(AndroidConstants.JSON_KEY_SETS__ARTIST);

    String stageKey = AndroidConstants.JSON_KEY_SETS__STAGE_ONE;
    if (week == 2) {
      stageKey = AndroidConstants.JSON_KEY_SETS__STAGE_TWO;
    }
    this.stageName = setData.getString(stageKey);
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
  public Date getSetDateTime() {
    return this.performanceDateTime;
  }

  public Date getAlertDateTime() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(performanceDateTime);
    cal.add(Calendar.MINUTE, -1 * minutesBeforeSetTime);
    return cal.getTime();
  }

  public String getSetTimeAsString() {
    int milTime = performanceDateTime.getHours() * 100 + performanceDateTime.getMinutes();
    return DateTimeUtils.militaryToCivilianTime(milTime);
  }

  public String getDayDateAsString() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(performanceDateTime);
    // use simpledateformat here
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d");
    // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy"); //Includes year
    return simpleDateFormat.format(performanceDateTime);
  }

  // Registers PendingIntent with Android AlarmManager service.
  // The app will be notified when the PendingIntent is later broadcast.
  public void setAlarm(Context context) {
    LogController.ALERTS.logMessage("Class Alert - Setting alert " + hashKey);

    // Create pendingIntent to be broadcast later
    Bundle extras = createBundle();
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

  // Cancels PendingIntent with Android AlarmManager service.
  // Must be called with the same Context that set the Alert
  public void cancelAlarm(Context context) {
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
    String alertMsg = "Go see " + this.getArtist() + " at " + this.getStage() + " for the set at "
        + this.getSetTimeAsString() + " starting in " + this.getTextIntervalUntilSet();

    Intent intent = new Intent(context, RTFIntentReceiver.class);
    intent.setAction(ACTION_ALERT);
    // intent.putExtra(REMINDER_BUNDLE, extras); // Dont think we need this
    intent.putExtra(HASH_KEY, getHashKey());

    intent.putExtra(AlertAlarmActivity.DIALOG_TITLE, context.getString(R.string.app_name));
    intent.putExtra(AlertAlarmActivity.DIALOG_MESSAGE, alertMsg);
    intent.putExtra(AlertAlarmActivity.LEFT_BUTTON_TEXT, "Show Schedule");
    intent.putExtra(AlertAlarmActivity.RIGHT_BUTTON_TEXT, "Dismiss");
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  // This seems to handle alerts in the past reasonably well
  public String getTextIntervalUntilAlert() {
    return CalendarUtils.formatInterval(this.getAlertDateTime().getTime() - System.currentTimeMillis());
  }

  public String getTextIntervalUntilSet() {
    return CalendarUtils.formatInterval(this.getSetDateTime().getTime() - System.currentTimeMillis());
  }

  public boolean getIsAlertInFuture() {
    Date alertDateTime = this.getAlertDateTime();
    long alertTimeMillis = alertDateTime.getTime();
    return System.currentTimeMillis() < alertTimeMillis;
  }

  public int getMinutesBeforeSet() {
    return this.minutesBeforeSetTime;
  }

  public void setMinutesBeforeSet(int minutesBefore) {
    this.minutesBeforeSetTime = minutesBefore;
  }

  // private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
  // stream.defaultReadObject();
  //
  // }

}
