package com.ratethisfest.data;

import java.util.Calendar;
import java.util.HashMap;
import com.ratethisfest.android.log.LogController;

//Meant to be used by static methods
public class DaysHashMap {
  private static HashMap<String, Integer> stringToJavaCalendarMap;
  private static HashMap<Integer, String> JavaCalendarToStringMap;
  private static boolean initialized = false;

  private DaysHashMap() {
  }

  // The database uses English strings
  // We want to specify the Calendar mappings to English
  // NOT the user's current locale
  private static void init() {
    if (initialized) {
      return;
    }

    DaysHashMap.stringToJavaCalendarMap = new HashMap<String, Integer>();
    DaysHashMap.JavaCalendarToStringMap = new HashMap<Integer, String>();
    Integer currentInitDayJavaCalendar;
    String currentInitDayString;

    currentInitDayJavaCalendar = Calendar.MONDAY;
    currentInitDayString = "Monday";
    initPutValues(currentInitDayJavaCalendar, currentInitDayString);

    currentInitDayJavaCalendar = Calendar.TUESDAY;
    currentInitDayString = "Tuesday";
    initPutValues(currentInitDayJavaCalendar, currentInitDayString);

    currentInitDayJavaCalendar = Calendar.WEDNESDAY;
    currentInitDayString = "Wednesday";
    initPutValues(currentInitDayJavaCalendar, currentInitDayString);

    currentInitDayJavaCalendar = Calendar.THURSDAY;
    currentInitDayString = "Thursday";
    initPutValues(currentInitDayJavaCalendar, currentInitDayString);

    currentInitDayJavaCalendar = Calendar.FRIDAY;
    currentInitDayString = "Friday";
    initPutValues(currentInitDayJavaCalendar, currentInitDayString);

    currentInitDayJavaCalendar = Calendar.SATURDAY;
    currentInitDayString = "Saturday";
    initPutValues(currentInitDayJavaCalendar, currentInitDayString);

    currentInitDayJavaCalendar = Calendar.SUNDAY;
    currentInitDayString = "Sunday";
    initPutValues(currentInitDayJavaCalendar, currentInitDayString);

    initialized = true;
  }

  private static void initPutValues(Integer currentInitDayJavaCalendar, String currentInitDayString) {
    DaysHashMap.stringToJavaCalendarMap.put(currentInitDayString, currentInitDayJavaCalendar);
    DaysHashMap.JavaCalendarToStringMap.put(currentInitDayJavaCalendar, currentInitDayString);
  }

  public static String DayJavaCalendarToString(int JavaCalendarValue) {
    DaysHashMap.init();
    return DaysHashMap.JavaCalendarToStringMap.get(JavaCalendarValue);
  }

  public static int DayStringToJavaCalendar(String stringValue) {
    DaysHashMap.init();
    LogController.SET_DATA.logMessage("Looking up " + stringValue);
    return DaysHashMap.stringToJavaCalendarMap.get(stringValue);
  }

}
