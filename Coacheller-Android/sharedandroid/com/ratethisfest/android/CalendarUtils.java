package com.ratethisfest.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ratethisfest.android.log.LogController;
import com.ratethisfest.data.FestData;
import com.ratethisfest.shared.FestivalEnum;

import android.app.Application;

public class CalendarUtils extends Application {
  public static HashMap<Integer, String> days;

  public static Integer currentTime24hr() {
    SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
    String timeValueStr = sdf.format(new Date());
    Integer timeValueInteger = Integer.valueOf(timeValueStr);

    return timeValueInteger;
  }

  // Allows operations to be simplified in code
  // i.e. comparing the order of days
  public static int currentDayOfWeek() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.DAY_OF_WEEK);
  }

  public static int currentDayOfMonth() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  // App and Database use strings in English so this time it is better to do it
  // this way:
  public static String currentDayName() {
    int todayInt = currentDayOfWeek();
    return DaysHashMap.DayJavaCalendarToString(todayInt);
  }

  public static int currentMonth() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.MONTH) + 1; // Months are zero-based, 0-11
  }

  public static int currentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
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
      return DaysHashMap.DayJavaCalendarToString(Calendar.FRIDAY);
    }
  }

  public static int suggestWeekToQuery(FestivalEnum fest) {
    final int maximumWeeks = fest.getNumberOfWeeks();
    final int lastWeekExpired = CalendarUtils.getlastFestWeekExpired(fest);

    if (lastWeekExpired + 1 > maximumWeeks) {
      // After week 1 is over and there is no week 2
      // After week 2 is over and there is no week 3
      return lastWeekExpired;
    } else {
      // Before week 1 starts (return 0+1)
      // During week 1 before it ends (return 0+1)
      // After week 1 is over and during week 2 (return 1+1)
      return lastWeekExpired + 1;
    }

  }

  public static boolean isSetInTheFuture(JSONObject lastSetSelected, int weekToQuery, FestivalEnum fest)
      throws JSONException {
    int currentYear = CalendarUtils.currentYear();

    int currentDay = CalendarUtils.currentDayOfWeek();
    String currentDayName = CalendarUtils.currentDayName();

    int selectedYear = (Integer) lastSetSelected.get(AndroidConstants.JSON_KEY_SETS__YEAR);
    int selectedWeek = weekToQuery;
    String selectedDayName = (String) lastSetSelected.get(AndroidConstants.JSON_KEY_SETS__DAY);

    // Get the numeric date of the selected set using year, week, dayname
    HashMap<String, String> searchCriteria = new HashMap<String, String>();
    searchCriteria.put(FestData.FEST_NAME, fest.getName());
    searchCriteria.put(FestData.FEST_YEAR, selectedYear + "");
    searchCriteria.put(FestData.FEST_WEEK, selectedWeek + "");
    searchCriteria.put(FestData.FEST_DAYNAME, selectedDayName);
    Map<Integer, Map<String, String>> rowsMatchingMap = FestData.rowsMatchingAll(searchCriteria);

    int numberOfResults = rowsMatchingMap.values().size();
    if (numberOfResults > 1) {
      LogController.ERROR.logMessage("CalendarUtils - Error, more than one fest day returned");
      return false;
    }

    Integer rowIndex = rowsMatchingMap.keySet().iterator().next();
    Map<String, String> rowMatched = rowsMatchingMap.get(rowIndex);
    String selectedFestDayOfMonth = rowMatched.get(FestData.FEST_DAYOFMONTH);
    String selectedFestMonth = rowMatched.get(FestData.FEST_MONTH);

    //get set time
    Integer selectedSetTime = getSetTime(lastSetSelected, weekToQuery, fest);

    String currentDateTime = "" + currentYear + padStringZero(currentMonth(), 2)
        + padStringZero(currentDayOfMonth(), 2) + padStringZero(currentTime24hr(), 4);
    String selectedDateTime = "" + selectedYear + padStringZero(selectedFestMonth, 2)
        + padStringZero(selectedFestDayOfMonth, 2) + padStringZero(selectedSetTime, 4);
    LogController.SET_TIME_OPERATIONS.logMessage("Current:" + currentDateTime+ " Selected:" + selectedDateTime);

    if (currentDateTime.compareTo(selectedDateTime) < 0) {
      return true;
    } else {
      return false;
    }
  }
  
  public static Integer getSetTime(JSONObject setData, int week, FestivalEnum fest) throws JSONException {
    int selectedYear = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__YEAR);
    
 // Determine if we should read the first or second week's set time
    String selectedSetTimeKey;
    if (week == 1) {
      selectedSetTimeKey = AndroidConstants.JSON_KEY_SETS__TIME_ONE;
    } else {
      selectedSetTimeKey = AndroidConstants.JSON_KEY_SETS__TIME_TWO;
    }
    LogController.SET_TIME_OPERATIONS.logMessage("Using key:" + selectedSetTimeKey + " to retrieve set time");

    // Read the chosen set time
    Integer selectedSetTime = (Integer) setData.get(selectedSetTimeKey);
    return selectedSetTime;
  }
  
  public void getSetDatetime(JSONObject setData, int week, FestivalEnum fest) throws JSONException {
    Integer setTime = getSetTime(setData, week, fest);
    
    //USE ISSETINTHEFUTURE() code
    //year
    //month
    //day of month
    
    //set calendar object, return datetime in some form
  }

  // For a given fest, see what weeks of that fest are already over
  // If the fest has not started this year or week 1 is still in progress, returns 0
  // During week 2 of a fest, returns 1
  // When a fest with 2 weeks is completely over, returns 2
  public static int getlastFestWeekExpired(FestivalEnum fest) {
    final int numWeeks = fest.getNumberOfWeeks();
    final int currentYear = CalendarUtils.currentYear();

    int lastWeekExpired = 0;
    Calendar today = Calendar.getInstance();

    for (int checkWeek = 1; checkWeek <= numWeeks; checkWeek++) {
      Map<String, String> lastDayOfWeek = getLastDayOfFestWeek(fest, "" + currentYear, "" + checkWeek);
      String festMonth = lastDayOfWeek.get(FestData.FEST_MONTH);
      String festDay = lastDayOfWeek.get(FestData.FEST_DAYOFMONTH);

      Calendar endOfThatWeek = Calendar.getInstance();
      endOfThatWeek.set(Calendar.MONTH, Integer.valueOf(festMonth)-1); //Java Calendar months are ZERO BASED
      endOfThatWeek.set(Calendar.DAY_OF_MONTH, Integer.valueOf(festDay));
      if (today.after(endOfThatWeek)) {
        lastWeekExpired = checkWeek;
      }
    }

    return lastWeekExpired;
  }

  public static Map<String, String> getLastDayOfFestWeek(FestivalEnum fest, String year, String week) {
    HashMap<String, String> criteria = new HashMap<String, String>();
    criteria.put(FestData.FEST_NAME, fest.getName());
    criteria.put(FestData.FEST_YEAR, year);
    criteria.put(FestData.FEST_WEEK, week);

    Map<Integer, Map<String, String>> rowsMatchingAll = FestData.rowsMatchingAll(criteria);

    Iterator<Map<String, String>> rowIterator = rowsMatchingAll.values().iterator();
    Map<String, String> biggestRow = rowIterator.next();

    while (rowIterator.hasNext()) {
      Map<String, String> currentRow = rowIterator.next();

      String biggestRowMonthDay = "" + biggestRow.get(FestData.FEST_MONTH) + biggestRow.get(FestData.FEST_DAYOFMONTH);
      String currentRowMonthDay = "" + currentRow.get(FestData.FEST_MONTH) + currentRow.get(FestData.FEST_DAYOFMONTH);

      // Might have this backwards...
      if (biggestRowMonthDay.compareTo(currentRowMonthDay) < 0) {
        biggestRow = currentRow;
      }
    }

    return biggestRow;
  }

  public static String padStringZero(Integer intInput, int numChars) {
    String input = intInput + "";
    return padStringZero(input, numChars);
  }

  public static String padStringZero(String input, int numChars) {
    return String.format("%" + numChars + "s", input).replace(" ", "0");
  }

}
