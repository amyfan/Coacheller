package com.ratethisfest.android.log;

public enum LogController {
  LIFECYCLE_ACTIVITY("Activity Lifecycle"), LIFECYCLE_THREAD("Thread Lifecycle"), USER_ACTION_UI("User UI Action"), SET_DATA(
      "Set Data Operations"), AUTH_GOOGLE("Google Authorization"), AUTH_FACEBOOK("Facebook Authorization"), AUTH_TWITTER(
      "Twitter Authorization"), OTHER("Miscellaneous"), MULTIWEEK("Multi-Week Functionality");

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
      System.out.println(initMessage);
    }
  }

  public void enable() {
    _messagesEnabled = true;
  }
  
  public void disable() {
    _messagesEnabled = false;
  }
  
  public static void allCategoriesOn() {
    for (LogController category : LogController.values()) {
      category._messagesEnabled = true;
    }
    System.out.println("All log categories enabled");
  }

  public static void allCategoriesOff() {
    for (LogController category : LogController.values()) {
      category._messagesEnabled = false;
    }
    System.out.println("All log categories disabled");
  }

  public void logMessage(String message) {
    if (_messagesEnabled) {
      System.out.println(message);
    }
  }

}
