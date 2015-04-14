package com.ratethisfest.shared;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ratethisfest.android.log.LogController;
import com.ratethisfest.data.AndroidConstants;
import com.ratethisfest.data.DaysHashMap;
import com.ratethisfest.data.FestivalEnum;

public class CalendarUtils {
  // public static HashMap<Integer, String> days; //Not used?

  /** @category TimeNow */
  // Return the current time as 3 or 4 digits in 24-hour format
  public static Integer currentTime24hr() {
    SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
    String timeValueStr = sdf.format(new Date());
    Integer timeValueInteger = Integer.valueOf(timeValueStr);

    return timeValueInteger;
  }

  /** @category TimeNow */
  // Allows operations to be simplified in code
  // i.e. comparing the order of days
  public static int currentDayOfWeek() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.DAY_OF_WEEK);
  }

  /** @category TimeNow */
  public static int currentDayOfMonth() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  /** @category TimeNow */
  // App and Database use strings in English so this time it is better to do it
  // this way:
  public static String currentDayName() {
    int todayInt = currentDayOfWeek();
    return getDayName(todayInt);
  }

  public static String getDayName(int dayInt) {
    int dayToLookup = dayInt;
    if (dayInt == 8) {
      dayToLookup -= 7;
    }
    return DaysHashMap.DayJavaCalendarToString(dayToLookup);
  }

  /** @category TimeNow */
  public static int currentNMonth() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.MONTH) + 1; // Months are zero-based, 0-11
  }

  /** @category TimeNow */
  public static int currentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  /** @category TimeNow */
  public static Date getCurrentDateTime() {
    Calendar cal = Calendar.getInstance();
    return cal.getTime();
  }

  // Use this when suggesting a day to search on and the user's preference is not yet known.
  // If a day of the "suggested" fest week has the same name as today, pick that day
  // Otherwise, pick the first day of the "suggested" fest week
  public static String suggestDayToQueryString(FestivalEnum fest) {
    HashMap<String, String> criteria = new HashMap<String, String>();
    criteria.put(FestData.FEST_NAME, fest.getName());
    criteria.put(FestData.FEST_YEAR, currentYear() + "");
    criteria.put(FestData.FEST_WEEK, suggestWeekToQuery(fest) + "");

    Map<Integer, Map<String, String>> rowsMatchingAll = FestData.rowsMatchingAll(criteria);

    Iterator<Map<String, String>> rowIterator = rowsMatchingAll.values().iterator();

    ArrayList<Integer> days = new ArrayList<Integer>();
    while (rowIterator.hasNext()) {
      Map<String, String> latestRowFound = rowIterator.next();
      String dayName = latestRowFound.get(FestData.FEST_DAYNAME);
      int dayInt = DaysHashMap.DayStringToJavaCalendar(dayName);
      days.add(dayInt);

      if (currentDayOfWeek() == dayInt) {
        return getDayName(dayInt);
      }
    }

    // Today does not match any of the days queried from the fest week
    Collections.sort(days);
    Integer firstDay = days.get(0);
    if (firstDay == null) {
      return getDayName(Calendar.FRIDAY);
    }

    return getDayName(firstDay);
  }

  // Before fest starts, return 1
  // During fest, returns the current fest week
  // After fest, returns the last fest week
  public static int suggestWeekToQuery(FestivalEnum fest) {
    final int maximumWeeks = getFestivalMaxNumberOfWeeks(fest);
    final int lastWeekExpired = getlastFestWeekExpired(fest);

    if (lastWeekExpired + 1 > maximumWeeks) {
      // After week 1 is over and there is no week 2 (return 1)
      // After week 2 is over and there is no week 3 (return 2)
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

    Date currentDateTime = getCurrentDateTime();
    Date setDateTime = getSetDateTime(lastSetSelected, weekToQuery, fest);
    LogController.SET_TIME_OPERATIONS.logMessage("Current Time: " + currentDateTime + " Selected Set Time: "
        + setDateTime);
    return currentDateTime.before(setDateTime);
  }

  // Old implementation
  // public static boolean isSetInTheFuture(JSONObject lastSetSelected, int weekToQuery, FestivalEnum fest)
  // throws JSONException {
  // int currentYear = CalendarUtils.currentYear();
  //
  // int currentDay = CalendarUtils.currentDayOfWeek();
  // String currentDayName = CalendarUtils.currentDayName();
  //
  // int selectedYear = (Integer) lastSetSelected.get(AndroidConstants.JSON_KEY_SETS__YEAR);
  // int selectedWeek = weekToQuery;
  // String selectedDayName = (String) lastSetSelected.get(AndroidConstants.JSON_KEY_SETS__DAY);
  //
  // // Get the numeric date of the selected set using year, week, dayname
  // HashMap<String, String> searchCriteria = new HashMap<String, String>();
  // searchCriteria.put(FestData.FEST_NAME, fest.getName());
  // searchCriteria.put(FestData.FEST_YEAR, selectedYear + "");
  // searchCriteria.put(FestData.FEST_WEEK, selectedWeek + "");
  // searchCriteria.put(FestData.FEST_DAYNAME, selectedDayName);
  // Map<Integer, Map<String, String>> rowsMatchingMap = FestData.rowsMatchingAll(searchCriteria);
  //
  // int numberOfResults = rowsMatchingMap.values().size();
  // if (numberOfResults > 1) {
  // LogController.ERROR.logMessage("CalendarUtils - Error, more than one fest day returned");
  // return false;
  // }
  //
  // Integer rowIndex = rowsMatchingMap.keySet().iterator().next();
  // Map<String, String> rowMatched = rowsMatchingMap.get(rowIndex);
  // String selectedFestDayOfMonth = rowMatched.get(FestData.FEST_DAYOFMONTH);
  // String selectedFestMonth = rowMatched.get(FestData.FEST_MONTH);
  //
  // //get set time
  // Integer selectedSetTime = getSetTime(lastSetSelected, weekToQuery);
  //
  // String currentDateTime = "" + currentYear + padStringZero(currentMonth(), 2)
  // + padStringZero(currentDayOfMonth(), 2) + padStringZero(currentTime24hr(), 4);
  // String selectedDateTime = "" + selectedYear + padStringZero(selectedFestMonth, 2)
  // + padStringZero(selectedFestDayOfMonth, 2) + padStringZero(selectedSetTime, 4);
  // LogController.SET_TIME_OPERATIONS.logMessage("Current:" + currentDateTime+ " Selected:" + selectedDateTime);
  //
  // if (currentDateTime.compareTo(selectedDateTime) < 0) {
  // return true;
  // } else {
  // return false;
  // }
  // }

  // Fix: Why does this require fest?
  public static Integer getSetTime(JSONObject setData, int week) throws JSONException {
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

  public static Date getSetDateTime(JSONObject setData, int week, FestivalEnum fest) throws JSONException {
    final int setYear = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__YEAR);
    final int setWeek = week;
    final String setDayName = (String) setData.get(AndroidConstants.JSON_KEY_SETS__DAY);
    final Integer setTime24Format = getSetTime(setData, week);
    final Integer setTimeHours = getHourFromSetTime(setTime24Format);
    final Integer setTimeMinutes = getMinutesFromSetTime(setTime24Format);

    // Get the numeric date of the selected set using year, week, dayname
    // Search is required to lookup set fest/year/week/dayname and find month and dayOfMonth
    HashMap<String, String> searchCriteria = new HashMap<String, String>();
    searchCriteria.put(FestData.FEST_NAME, fest.getName());
    searchCriteria.put(FestData.FEST_YEAR, setYear + "");
    searchCriteria.put(FestData.FEST_WEEK, setWeek + "");
    searchCriteria.put(FestData.FEST_DAYNAME, setDayName);
    Map<Integer, Map<String, String>> rowsMatchingMap = FestData.rowsMatchingAll(searchCriteria);

    int numberOfResults = rowsMatchingMap.values().size();
    if (numberOfResults > 1) {
      LogController.ERROR.logMessage("CalendarUtils - Error, more than one fest day returned");
      return null;
    }

    Integer rowIndex = rowsMatchingMap.keySet().iterator().next();
    Map<String, String> rowMatched = rowsMatchingMap.get(rowIndex);
    final Integer setDayOfMonth = Integer.valueOf(rowMatched.get(FestData.FEST_DAYOFMONTH));
    final Integer setMonth = Integer.valueOf(rowMatched.get(FestData.FEST_MONTH));

    String selectedDateTime = "Set Data Year:" + setYear + " Month:" + padStringZero(setMonth, 2) + " Day:"
        + padStringZero(setDayOfMonth, 2) + " Time:" + padStringZero(setTime24Format, 4) + " (as " + setTimeHours + ":"
        + setTimeMinutes + ")";
    LogController.SET_TIME_OPERATIONS.logMessage(selectedDateTime);

    Calendar returnCal = Calendar.getInstance();
    returnCal.set(Calendar.YEAR, setYear);
    returnCal.set(Calendar.MONTH, setMonth - 1); // Java calendar months are zero based.
    returnCal.set(Calendar.DAY_OF_MONTH, setDayOfMonth);
    returnCal.set(Calendar.HOUR_OF_DAY, setTimeHours); // HOUR_OF_DAY for 24hr format
    returnCal.set(Calendar.MINUTE, setTimeMinutes);

    return returnCal.getTime();
  }

  // Extract the hour from an Integer representation of 24-hour time
  // 0 -> 0, 45 -> 0, 145 -> 1, 2300 -> 23, 2345 -> 23
  public static Integer getHourFromSetTime(Integer setTime) {
    return setTime / 100;
  }

  // Extract the minutes from an Integer representation of 24-hour time
  // 0 -> 0, 45 -> 45, 145 -> 45, 2300 -> 0, 2345 -> 45
  public static Integer getMinutesFromSetTime(Integer setTime) {
    return setTime % 100;
  }

  // For a given fest, see what weeks of that fest are already over
  // If the fest has not started this year or week 1 is still in progress, returns 0
  // During week 2 of a fest, returns 1
  // When a fest with 2 weeks is completely over, returns 2
  public static int getlastFestWeekExpired(FestivalEnum fest) {
    final int numWeeks = getFestivalMaxNumberOfWeeks(fest);
    final int currentYear = CalendarUtils.currentYear();

    int lastWeekExpired = 0;
    Calendar today = Calendar.getInstance();

    for (int checkWeek = 1; checkWeek <= numWeeks; checkWeek++) {
      Map<String, String> lastDayOfWeek = getLastDayOfFestWeek(fest, "" + currentYear, "" + checkWeek);
      String festMonth = lastDayOfWeek.get(FestData.FEST_MONTH);
      String festDay = lastDayOfWeek.get(FestData.FEST_DAYOFMONTH);

      Calendar endOfThatWeek = Calendar.getInstance();
      endOfThatWeek.set(Calendar.MONTH, Integer.valueOf(festMonth) - 1); // Java Calendar months are ZERO BASED
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
    Map<String, String> latestRowFound = rowIterator.next();

    while (rowIterator.hasNext()) {
      Map<String, String> currentRow = rowIterator.next();

      String biggestRowMonthDay = "" + latestRowFound.get(FestData.FEST_MONTH)
          + latestRowFound.get(FestData.FEST_DAYOFMONTH);
      String currentRowMonthDay = "" + currentRow.get(FestData.FEST_MONTH) + currentRow.get(FestData.FEST_DAYOFMONTH);

      // Might have this backwards...
      if (biggestRowMonthDay.compareTo(currentRowMonthDay) < 0) {
        latestRowFound = currentRow;
      }
    }

    return latestRowFound;
  }

  // What is the largest number of weeks that the named festival has ever run?
  // Returns the greatest number of weeks that a fest ever ran in a year
  // Even if it only happened in that one year
  // For Coachella we expect the answer to be 2
  // For lollapalooza we expect the answer to be 1
  // If Coachella ran for 3 weeks in 1972 AND it was in the database, the answer would be 3
  public static int getFestivalMaxNumberOfWeeks(FestivalEnum festival) {
    // Get every single entry for the stated fest
    HashMap<String, String> criteria = new HashMap<String, String>();
    criteria.put(FestData.FEST_NAME, festival.getName());
    Map<Integer, Map<String, String>> rowsMatchingAll = FestData.rowsMatchingAll(criteria);

    int largestNumberOfWeeks = 0;
    for (Map<String, String> festDayData : rowsMatchingAll.values()) {
      int weekNumber = Integer.valueOf(festDayData.get(FestData.FEST_WEEK));
      if (weekNumber > largestNumberOfWeeks) {
        largestNumberOfWeeks = weekNumber;
      }
    }

    LogController.MULTIWEEK.logMessage("Calculated festival" + festival + " to have up to " + largestNumberOfWeeks
        + "weeks");
    return largestNumberOfWeeks;
  }

  /** @category String formatting */
  public static String padStringZero(Integer intInput, int numChars) {
    String input = intInput + "";
    return padStringZero(input, numChars);
  }

  /** @category String formatting */
  public static String padStringZero(String input, int numChars) {
    return String.format("%" + numChars + "s", input).replace(" ", "0");
  }

  /** @category String formatting */
  public static String formatInterval(final long l) {
    long longTimeMillis = Math.abs(l);

    int maxValues = 2;
    long days = longTimeMillis / 86400000; // d
    long hours = longTimeMillis % 86400000 / 3600000; // h
    long minutes = longTimeMillis % 3600000 / 60000; // m
    // long seconds = l%60000/1000; //s
    // long millis = l%1000; //ms

    StringBuffer returnBuffer = new StringBuffer();
    int values = 0;

    if (days > 0 && values < maxValues) {
      returnBuffer.append(days);
      returnBuffer.append(" day");
      if (days > 0) {
        returnBuffer.append("s");
      }
      values++;
    }

    if (hours > 0 && values < maxValues) {
      if (values > 0) {
        returnBuffer.append(" ");
      }
      returnBuffer.append(hours);
      returnBuffer.append(" hour");
      if (hours > 0) {
        returnBuffer.append("s");
      }
      values++;
    }

    if (minutes > 0 && values < maxValues) {
      if (values > 0) {
        returnBuffer.append(" ");
      }
      returnBuffer.append(minutes);
      returnBuffer.append(" minute");
      if (minutes > 0) {
        returnBuffer.append("s");
      }
      values++;
    }

    if (l < 0) {
      returnBuffer.append(" ago");
    }

    return returnBuffer.toString();
  }

}
