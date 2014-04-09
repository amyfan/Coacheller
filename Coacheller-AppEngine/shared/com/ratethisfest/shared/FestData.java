package com.ratethisfest.shared;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.data.DaysHashMap;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.CalendarUtils;

/**
 * This class could REALLY use some comments -AF
 * 
 */
public enum FestData {
  INSTANCE; // Singleton magic

  public static final String FEST_NAME = "FEST_NAME";
  public static final String FEST_WEEK = "FEST_WEEK";
  public static final String FEST_YEAR = "FEST_YEAR";
  public static final String FEST_MONTH = "FEST_MONTH";
  public static final String FEST_DAYNAME = "FEST_DAYNAME";
  public static final String FEST_DAYOFMONTH = "FEST_DAYOFMONTH";

  // public boolean initialized;
  private static final ImmutableTable<Integer, String, String> festTable;

  static {
    // LogController.MULTIWEEK.logMessage("FestData initializing - should only happen once");
    ImmutableTable.Builder<Integer, String, String> tableBuilder = new ImmutableTable.Builder<Integer, String, String>();

    String festName;
    int festYear;
    int festWeek;
    int row = 0;

    festName = "Coachella";
    festWeek = 1;

    festYear = 2014;

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, festYear, 4, 11);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, festYear, 4, 12);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, festYear, 4, 13);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 4, 12);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 4, 13);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 4, 14);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2012, 4, 13);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2012, 4, 14);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2012, 4, 15);

    festWeek = 2;

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, festYear, 4, 18);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, festYear, 4, 19);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, festYear, 4, 20);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 4, 19);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 4, 20);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 4, 21);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2012, 4, 20);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2012, 4, 21);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2012, 4, 22);

    festName = "Lollapalooza";
    festWeek = 1;

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 8, 2);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 8, 3);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, 2013, 8, 4);

    festName = "TestFest";
    festWeek = 1;
    // //Fake stuff starts here
    Calendar cal = Calendar.getInstance();
    int currentYear = CalendarUtils.currentYear();
    int currentMonth = CalendarUtils.currentNMonth();
    int currentDayOfMonth = CalendarUtils.currentDayOfMonth();

    // default java calendar setting of LENIENT should mean that this should not blow things up too badly
    // when this code results in days of the month greater than 31...

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 1);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 2);

    festWeek = 2;
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 8);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 9);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 10);

    festWeek = 3;
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 16);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 17);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek + "");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth + 18);

    festTable = tableBuilder.build();
  }

  private static void addFestTableRow(ImmutableTable.Builder<Integer, String, String> tableBuilder, int row,
      int festYear, int festNMonth, int festDayOfMonth) {

    int festJMonth = festNMonth - 1; // Java calendar uses zero based months

    tableBuilder.put(row, FEST_YEAR, festYear + "");
    tableBuilder.put(row, FEST_MONTH, festNMonth + ""); // Our data structure, Normal Month
    tableBuilder.put(row, FEST_DAYOFMONTH, festDayOfMonth + "");

    Calendar cal = Calendar.getInstance();
    cal.set(festYear, festJMonth, festDayOfMonth); // Java Calendar Class, J Month
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // Use java calendar to calculate day of week
    tableBuilder.put(row, FEST_DAYNAME, CalendarUtils.getDayName(dayOfWeek));
  }

  // Not used
  private FestData() {
  }

  public static ImmutableTable<Integer, String, String> getTable() {
    return FestData.festTable;
  }

  public static Map<Integer, Map<String, String>> rowsMatchingAll(HashMap<String, String> criteria) {
    // Set<Integer> rowsToReturn = Collections.<Integer> emptySet();
    // rowsToReturn.addAll(festTable.rowKeySet());
    TreeSet<Integer> rowsToReturn = new TreeSet<Integer>(festTable.rowKeySet());

    int size = rowsToReturn.size();
    String debugSearchParams = "";
    for (Map.Entry<String, String> entry : criteria.entrySet()) {
      String columnName = entry.getKey();
      String valueToMatch = entry.getValue();
      // LogController.OTHER.logMessage("Processing match criterion " + columnName + " == " + valueToMatch);
      debugSearchParams += columnName + "=" + valueToMatch + " ";
      Set<Integer> matchingRows = searchForRows(columnName, valueToMatch).keySet();
      rowsToReturn.retainAll(matchingRows);
      size = rowsToReturn.size();
    }
    // LogController.OTHER.logMessage("FestData Search: " + debugSearchParams);

    Predicate<Integer> returnPredicate = Predicates.in(rowsToReturn);
    return Maps.filterKeys(festTable.rowMap(), returnPredicate);
  }

  // Returns rows that have a value of matchValue in the column specified by columnName
  // e.g. searchForRows(FestData.FEST_NAME, "Lollapalooza")
  public static Map<Integer, Map<String, String>> searchForRows(String columnName, String matchValue) {
    // All row keys paired to the column we are matching against
    ImmutableMap<Integer, String> selectedColumnKeys = festTable.column(columnName);
    Predicate<String> matchSearchValue = Predicates.equalTo(matchValue);

    // All row keys paired to values in the selected column that match our search
    final Map<Integer, String> filteredRowKeys = Maps.filterValues(selectedColumnKeys, matchSearchValue);

    // System.out.println("filteredRowKeys:");
    // for (Integer key : filteredRowKeys.keySet()) {
    // System.out.println(key + ":" + filteredRowKeys.get(key));
    // }

    Predicate<Integer> containedInMatchedRowKeys = new Predicate<Integer>() {
      @Override
      public boolean apply(Integer input) {
        return filteredRowKeys.containsKey(input);

      }
    };

    Map<Integer, Map<String, String>> filteredRows = Maps.filterKeys(festTable.rowMap(), containedInMatchedRowKeys);
    return filteredRows;

  }

  // Return primitive string array representing the years that we have data for the specified fest
  // Sorted in descending order!
  public static String[] getArrayYears(FestivalEnum fest) {
    HashMap<String, String> criteria = new HashMap<String, String>();
    criteria.put(FestData.FEST_NAME, fest.getName());
    Map<Integer, Map<String, String>> rowsMatchingAll = FestData.rowsMatchingAll(criteria);
    Iterator<Map<String, String>> rowIterator = rowsMatchingAll.values().iterator();

    // Iterate rows and collect in an ArrayList to retain only unique items
    ArrayList<String> years = new ArrayList<String>();
    while (rowIterator.hasNext()) {
      Map<String, String> latestRowFound = rowIterator.next();
      String festYear = latestRowFound.get(FestData.FEST_YEAR);
      if (!years.contains(festYear)) {
        years.add(festYear);
      }
    }
    Collections.sort(years, Collections.reverseOrder());

    // Now build and return a primitive array
    String[] yearsPrimitive = new String[years.size()];
    for (int i = 0; i < years.size(); i++) {
      yearsPrimitive[i] = years.get(i);
    }
    return yearsPrimitive;
  }

  // Return a String array with the human-readable values describing the weeks in a fest
  // i.e. for Coachella, return {1, 2}
  public static String[] getArrayWeeks(FestivalEnum fest) {
    int maxNumberOfWeeks = CalendarUtils.getFestivalMaxNumberOfWeeks(fest);
    String[] arrayWeeks = new String[maxNumberOfWeeks];

    for (int i = 0; i < maxNumberOfWeeks; i++) {
      int weekNumber = i + 1;
      arrayWeeks[i] = weekNumber + "";
    }
    return arrayWeeks;
  }

  public static String[] getArrayDays(FestivalEnum fest) {
    LogController.OTHER.logMessage("Searching for sets matching fest: " + fest.getName());
    HashMap<String, String> criteria = new HashMap<String, String>();
    criteria.put(FestData.FEST_NAME, fest.getName());
    Map<Integer, Map<String, String>> rowsMatchingAll = FestData.rowsMatchingAll(criteria);
    Iterator<Map<String, String>> rowIterator = rowsMatchingAll.values().iterator();

    ArrayList<Integer> days = new ArrayList<Integer>();
    while (rowIterator.hasNext()) {
      Map<String, String> latestRowFound = rowIterator.next();
      String festDayString = latestRowFound.get(FestData.FEST_DAYNAME);
      int festDayInt = DaysHashMap.DayStringToJavaCalendar(festDayString);

      // If day is Sunday, add 7 so it gets sorted to after saturday
      if (festDayInt == Calendar.SUNDAY) {
        festDayInt += 7;
      }

      if (!days.contains(festDayInt)) {
        days.add(festDayInt);
      }
    }
    Collections.sort(days);

    String[] arrayDayNames = new String[days.size()];
    for (int i = 0; i < days.size(); i++) {
      arrayDayNames[i] = CalendarUtils.getDayName(days.get(i));
    }
    return (String[]) arrayDayNames;
  }

}
