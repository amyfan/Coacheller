package com.ratethisfest.android;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Application;

public class CalendarUtils extends Application {
  public static HashMap<Integer, String> days;

  
  
  public static boolean isFestDayAfter(int comparedTo, int thisDay) {
    int comparedToFestValue = comparedTo;
    int thisDayFestValue = thisDay;
    
    //This gives Sunday a value of 8 within this function
    //Making it the last day of the week after Saturday(7)
    if (comparedToFestValue == Calendar.SUNDAY) {
      comparedToFestValue += 7;
    }
    if (thisDayFestValue == Calendar.SUNDAY) {
      thisDayFestValue += 7;
    }

    if (thisDayFestValue > comparedToFestValue) {
      return true; // AFTER
    } else {
      return false; // Not After
    }
  }

  public static int whatYearIsToday() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  // Allows operations to be simplified in code
  // i.e. comparing the order of days
  public static int whatDayIsTodayInt() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.DAY_OF_WEEK);
  }

  // App and Database use strings in English so this time it is better to do it
  // this way:
  public static String whatDayIsTodayString() {
    int todayInt = whatDayIsTodayInt();
    return DaysHashMap.DayJavaCalendarToString(todayInt);
  }

  // Use this when suggesting a day to search on and the user's preference is not yet known.
  // No reason to try to display set data from a Monday
  public static String suggestDayToQueryString() {
    Calendar cal = Calendar.getInstance();
    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      return DaysHashMap.DayJavaCalendarToString(Calendar.SUNDAY);
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
      return DaysHashMap.DayJavaCalendarToString(Calendar.SATURDAY);
    } else {
      return whatDayIsTodayString();
    }
  }

  // Use this to compare days WITHIN A COACHELLA WEEK
  // In a COACHELLA WEEK, SATURDAY comes before SUNDAY
  // The last day of a COACHELLA WEEK is SUNDAY

  /**
   * TODO: refine
   * 
   * @return
   */
  // The last day of a COACHELLA WEEK is SUNDAY
  public static int whatWeekIsToday() {
    Calendar cal = Calendar.getInstance();
    if (cal.get(Calendar.DAY_OF_MONTH) < 15) {
      return 1;
    } else {
      return 2;
    }
  }

}
