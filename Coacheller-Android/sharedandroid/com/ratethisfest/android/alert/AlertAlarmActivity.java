package com.ratethisfest.android.alert;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.coacheller.R;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.android.ui.CoachellerActivity;

// <uses-permission android:name="android.permission.VIBRATE"/>
// <uses-permission android:name="android.permission.DISABLE_KEYGUARD"/>
// <uses-permission android:name="android.permission.WAKE_LOCK"/>
public class AlertAlarmActivity extends Activity implements OnClickListener {

  public static String LEFT_BUTTON_TEXT = "LEFT_BUTTON_TEXT";
  public static String RIGHT_BUTTON_TEXT = "RIGHT_BUTTON_TEXT";
  public static String DIALOG_TITLE = "DIALOG_TITLE";
  public static String DIALOG_MESSAGE = "DIALOG_MESSAGE";

  private Vibrator vibrator;
  private KeyguardLock keyguardLock;
  private static PowerManager.WakeLock wakeLock;
  private MediaPlayer mediaPlayer;
  private long timeActionsStarted;
  private static final long MIN_ALERT_TIME_MILLIS = 500; // externalActionsStop exception

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogController.LIFECYCLE_ACTIVITY.logMessage("AlertAlarmActivity: onCreate()");
    setupUI();

  }

  @Override
  protected void onPause() {
    super.onPause();
    LogController.LIFECYCLE_ACTIVITY.logMessage("AlertAlarmActivity: onPause()");
    externalActionsStop();
  }

  @Override
  protected void onStart() {
    super.onStart();
    LogController.LIFECYCLE_ACTIVITY.logMessage("AlertAlarmActivity: onStart()");
    externalActionsStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    LogController.LIFECYCLE_ACTIVITY.logMessage("AlertAlarmActivity: onResume()");
    // externalActionsStart();
  }

  private void setupUI() {
    setContentView(R.layout.activity_alarm);

    for (String key : getIntent().getExtras().keySet()) {
      // Ignore this key to keep a stack trace out of logcat
      if (key.equals("android.intent.extra.ALARM_COUNT")) {
        continue;
      }
      String value = getIntent().getExtras().getString(key); // Could generate handled exception

      if (key.equals(DIALOG_TITLE)) {
        TextView textView = (TextView) findViewById(R.id.textDialogTitle);
        // textView.setText(value);
        this.setTitle(value);
      } else if (key.equals(DIALOG_MESSAGE)) {
        TextView textView = (TextView) findViewById(R.id.textDialogMessage);
        textView.setText(value);
      } else if (key.equals(LEFT_BUTTON_TEXT)) {
        Button btn = (Button) findViewById(R.id.leftBigButton);
        btn.setText(value);
      } else if (key.equals(RIGHT_BUTTON_TEXT)) {
        Button btn = (Button) findViewById(R.id.rightSmallButton);
        btn.setText(value);
      }
    }

    Button btnLeft = (Button) findViewById(R.id.leftBigButton);
    btnLeft.setOnClickListener(this);
    Button btnRight = (Button) findViewById(R.id.rightSmallButton);
    btnRight.setOnClickListener(this);

  }

  private void externalActionsStart() {
    LogController.ALERTS.logMessage("AlertAlarmActivity: externalActionsStart()");
    this.timeActionsStarted = System.currentTimeMillis();
    // Wake lock should be first
    // must be done before intent is sent or device could sleep
    // PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    // this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
    // this.wakeLock.acquire();

    KeyguardManager myKeyGuard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    this.keyguardLock = myKeyGuard.newKeyguardLock("tagName");
    this.keyguardLock.disableKeyguard();

    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    // Start without a delay // Vibrate for x milliseconds // Sleep for y milliseconds
    long[] pattern = { 0, 200, 1000 };
    this.vibrator.vibrate(pattern, 0); // The '0' here means to repeat indefinitely '-1' would play the vibration once

    this.mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
    this.mediaPlayer.setLooping(true);
    this.mediaPlayer.start();

  }

  // Android sends onPause within 100ms of onStart+onResume for some reason, need to guard against that
  private void externalActionsStop() {
    LogController.ALERTS.logMessage("AlertAlarmActivity: externalActionsStop()");
    long timeActionsRunning = System.currentTimeMillis() - this.timeActionsStarted;
    if (timeActionsRunning < MIN_ALERT_TIME_MILLIS) {
      return;
    }

    // Things to do when activity is dismissed

    // FIGURE OUT HOW TO USE CLEANUP OBJECT

    // Stop vibrating
    this.vibrator.cancel();

    // DO NOT Re-enable keylock
    // this.keyguardLock.reenableKeyguard();

    // un-acquire wake lock
    if (wakeLock.isHeld()) {
      this.wakeLock.release();
    } else {
      LogController.ALERTS.logMessage("Alarm Dialog did not try to release Wake Lock, since it was not held");
    }

    // stop playing audio
    this.mediaPlayer.stop();
  }

  @Override
  public void onClick(View viewClicked) {

    if (viewClicked == findViewById(R.id.leftBigButton)) {
      externalActionsStop();
      finish();
      Intent intent = new Intent(this, CoachellerActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);

    } else if (viewClicked == findViewById(R.id.rightSmallButton)) {
      externalActionsStop();
      finish();
    }

  }

  // This static method can be called to bring up the Alarm dialog.
  public static void launch(Context context, Intent intent) {
    // Wake lock must be acquired now, other wake lock will be lost after BroadcastReceiver.onReceive() returns
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
    wakeLock.acquire();

    // Intent scheduledIntent = new Intent(context, AlertAlarmActivity.class);

    // This Intent already has the extras we want, just change the target class
    intent.setClass(context, AlertAlarmActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Asks to use a new task
    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK); // Supplement to NEW_TASK that asks to ALWAYS use a new task

    // scheduledIntent.putExtras(intent.getExtras()); // Put extras from other intent into this intent
    context.startActivity(intent);
  }
}