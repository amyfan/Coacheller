package com.ratethisfest.data;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.ratethisfest.android.CalendarUtils;
import com.ratethisfest.android.log.LogController;

public enum FestData {
  INSTANCE;  //Singleton magic

  public static final String FEST_NAME = "FEST_NAME";
  public static final String FEST_WEEK = "FEST_WEEK";
  public static final String FEST_YEAR = "FEST_YEAR";
  public static final String FEST_MONTH = "FEST_MONTH";
  public static final String FEST_DAYNAME = "FEST_DAYNAME";
  public static final String FEST_DAYOFMONTH = "FEST_DAYOFMONTH";

  // public boolean initialized;
  private static final ImmutableTable<Integer, String, String> festTable;

  static {
    LogController.MULTIWEEK.logMessage("FestData initializing - should only happen once");
    ImmutableTable.Builder<Integer, String, String> tableBuilder = new ImmutableTable.Builder<Integer, String, String>();

    String festName;
    int festWeek;
    int row = 0;
    
    
    festName = "Coachella";
    festWeek = 1;
    
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 4, 12);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 4, 13);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 4, 14);

    festWeek = 2;
    
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 4, 19);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 4, 20);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 4, 21);

    festName = "Lollapalooza";
    festWeek = 1;

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 8, 2);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 8, 3);

    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, 2013, 8, 4);
    

    festName = "TestFest";
    festWeek = 1;
        ////Fake stuff starts here
    Calendar cal = Calendar.getInstance();
    int currentYear = CalendarUtils.currentYear();
    int currentMonth = CalendarUtils.currentNMonth();
    int currentDayOfMonth = CalendarUtils.currentDayOfMonth();

    //default java calendar setting of LENIENT should mean that this should not blow things up too badly
    //when this code results in days of the month greater than 31...
    
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+1);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+2);
    
    festWeek = 2;
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+7);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+8);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+9);
    
    festWeek = 3;
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+10);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+11);
    row++;
    tableBuilder.put(row, FEST_NAME, festName);
    tableBuilder.put(row, FEST_WEEK, festWeek+"");
    addFestTableRow(tableBuilder, row, currentYear, currentMonth, currentDayOfMonth+12);

    festTable = tableBuilder.build();
  }

  private static void addFestTableRow(ImmutableTable.Builder<Integer, String, String> tableBuilder, int row,
      int festYear, int festNMonth, int festDayOfMonth) {

    int festJMonth = festNMonth-1;  //Java calendar uses zero based months
    
    tableBuilder.put(row, FEST_YEAR, festYear+"");
    tableBuilder.put(row, FEST_MONTH, festNMonth+"");  //Our data structure, Normal Month
    tableBuilder.put(row, FEST_DAYOFMONTH, festDayOfMonth+"");
    
    Calendar cal = Calendar.getInstance();
    cal.set(festYear, festJMonth, festDayOfMonth);  //Java Calendar Class, J Month
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);  //Use java calendar to calculate day of week
    tableBuilder.put(row, FEST_DAYNAME, CalendarUtils.getDayName(dayOfWeek));
  }

  // Not used
  private FestData() {
  }

  public static ImmutableTable<Integer, String, String> getTable() {
    return FestData.festTable;
  }

  public static Map<Integer, Map<String, String>> rowsMatchingAll(HashMap<String, String> criteria) {
    //Set<Integer> rowsToReturn = Collections.<Integer> emptySet();
     //rowsToReturn.addAll(festTable.rowKeySet());
    TreeSet<Integer> rowsToReturn = new TreeSet<Integer>(festTable.rowKeySet());
   

    for (Map.Entry<String, String> entry : criteria.entrySet()) {
      String columnName = entry.getKey();
      String valueToMatch = entry.getValue();

      Set<Integer> matchingRows = searchForRows(columnName, valueToMatch).keySet();
      rowsToReturn.retainAll(matchingRows);
    }

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

}
