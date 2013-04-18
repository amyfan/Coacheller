package com.ratethisfest.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.ratethisfest.android.log.LogController;

import android.app.Application;

public class CalendarUtils extends Application {
  public static HashMap<Integer, String> days;
  
  public static Integer whatTimeisNow24() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String timeValueStr = sdf.format(new Date());
    Integer timeValueInteger = Integer.valueOf(timeValueStr);
    return timeValueInteger;
  }

  
  
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



  public static boolean isSetInTheFuture(JSONObject lastSetSelected, int weekToQuery, String dayToQuery) {
    int currentWeek = CalendarUtils.whatWeekIsToday();
    int selectedWeek = weekToQuery;
    int currentDay = CalendarUtils.whatDayIsTodayInt();
    int selectedDay = DaysHashMap.DayStringToJavaCalendar(dayToQuery);

    // Determine if we should read the first or second week's set time
    String selectedSetTimeKey;
    if (selectedWeek == 1) {
      selectedSetTimeKey = AndroidConstants.JSON_KEY_SETS__TIME_ONE;
    } else {
      selectedSetTimeKey = AndroidConstants.JSON_KEY_SETS__TIME_TWO;
    }
    LogController.SET_TIME_OPERATIONS.logMessage("Using key:" + selectedSetTimeKey + " to retrieve set time");

    // Read the chosen set time
    Integer selectedSetTime = 9999;
    try {
      selectedSetTime = (Integer) lastSetSelected.get(selectedSetTimeKey);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      LogController.ERROR.logMessage(e.getClass().getSimpleName() + " parsing selected set time");
      e.printStackTrace();
    }

    LogController.SET_TIME_OPERATIONS.logMessage("Current week:" + currentWeek + " selected set week:" + selectedWeek);
    if (currentWeek < selectedWeek) {
      // CurrentWeek < SelectedWeek -> Selected set must be in the future
      LogController.SET_TIME_OPERATIONS.logMessage("Selected week in the future?: YES");
      return true;

    } else if (currentWeek > selectedWeek) {
      // CurrentWeek > SelectedWeek -> Selected set must be in the past
      LogController.SET_TIME_OPERATIONS.logMessage("Selected week in the future?: NO");
      return false;

    }

    // Week is the same -> Selected set may or may not be in the future, do more checks
    LogController.SET_TIME_OPERATIONS.logMessage("Current day:" + currentDay + " selected set day:" + selectedDay);
    if (CalendarUtils.isFestDayAfter(currentDay, selectedDay)) {
      // Selected Day is after today, selected set must be in the future
      LogController.SET_TIME_OPERATIONS.logMessage("Selected day in the future?: YES");
      return true;

    } else if (CalendarUtils.isFestDayAfter(selectedDay, currentDay)) {
      // Selected day is before today, selected set must be in the past
      LogController.SET_TIME_OPERATIONS.logMessage("Selected day in the future?: NO");
      return false;
    }

    // Day is the same, compare times
    LogController.SET_TIME_OPERATIONS.logMessage("Current time:" + CalendarUtils.whatTimeisNow24()
        + " selected set time:" + selectedSetTime);
    if (CalendarUtils.whatTimeisNow24() < selectedSetTime) {
      LogController.SET_TIME_OPERATIONS.logMessage("Selected time in the future?: YES");
      return true;
    } else if (CalendarUtils.whatTimeisNow24() >= selectedSetTime) {
      LogController.SET_TIME_OPERATIONS.logMessage("Selected time in the future?: NO");
      return false;
    }

    // If currentTime < OR == selectedTime Before
    LogController.ERROR.logMessage("Could not determine if selected set is in the future");
    return false;
  }

}
