package com.ratethisfest.android;

import java.util.Calendar;

import android.app.Application;

public class CalendarUtils extends Application {

  public static int whatYearIsToday() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  // App and Database use strings in English so this time it is better to do it
  // this way:
  public static String whatDayIsToday() {
    Calendar cal = Calendar.getInstance();
    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      return "Sunday";
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
      return "Monday";
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
      return "Tuesday";
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
      return "Wednesday";
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
      return "Thursday";
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
      return "Friday";
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
      return "Saturday";
    }
    return "";
  }

  /**
   * TODO: refine
   * 
   * @return
   */
  public static int whichWeekIsToday() {
    Calendar cal = Calendar.getInstance();
    if (cal.get(Calendar.DAY_OF_MONTH) < 19) {
      return 1;
    } else {
      return 2;
    }
  }

}
