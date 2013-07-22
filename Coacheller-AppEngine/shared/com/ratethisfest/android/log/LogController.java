package com.ratethisfest.android.log;

import android.util.Log;

public enum LogController {
  LIFECYCLE_ACTIVITY("Activity Lifecycle"), LIFECYCLE_THREAD("Thread Lifecycle"), USER_ACTION_UI("User UI Action"), SET_DATA(
      "Set Data Operations"), SET_TIME_OPERATIONS("Comparing stored set time data to the current time"), AUTH_GOOGLE(
      "Google Authorization"), AUTH_FACEBOOK("Facebook Authorization"), AUTH_TWITTER("Twitter Authorization"), OTHER(
      "Miscellaneous"), MULTIWEEK("Multi-Week Functionality"), MODEL("Internal Representation of Data"), ERROR(
      "Things that we expect should not ever happen"), ALERTS("Alert Feature"), LIST_ADAPTER(
      "Implementation of ListAdapter interface"), LOG("Log Interface");

  public final String _readableDescription;
  private boolean _messagesEnabled;

  LogController(String description) {
    _readableDescription = description;
    _messagesEnabled = true;
  }

  public void printStatus() {
    for (LogController category : LogController.values()) {
      StringBuffer initMessage = new StringBuffer();
      initMessage.append("Log Category: ").append(category).append(" [");
      if (category._messagesEnabled) {
        initMessage.append("ON");
      } else {
        initMessage.append("OFF");
      }

      initMessage.append("] - ").append(category._readableDescription);
      systemLogMessage(initMessage.toString());
    }
  }

  public void enable() {
    _messagesEnabled = true;
  }

  public void disable() {
    if (!this.equals(ERROR)) {
      systemLogMessage("LogController - Cannot disable error messages");
      _messagesEnabled = false;
    }

  }

  public static void allCategoriesOn() {
    for (LogController category : LogController.values()) {
      category._messagesEnabled = true;
    }
    LOG.systemLogMessage("All log categories enabled");
  }

  public static void allCategoriesOff() {
    for (LogController category : LogController.values()) {
      category._messagesEnabled = false;
    }
    LOG.systemLogMessage("All log categories disabled");
  }

  public void logMessage(String message) {
    if (!_messagesEnabled) {
      return;
    }

    String projectName = "ratethisfest";
    if (this.equals(ERROR)) {
      Log.e(projectName, message);
    } else {
      Log.i(projectName, message);
    }
  }

  // Does System.out.println followed by regular log message
  private void systemLogMessage(String message) {
    System.out.println(message);
    logMessage(message);
  }

}
