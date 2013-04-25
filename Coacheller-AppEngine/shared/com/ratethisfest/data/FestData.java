package com.ratethisfest.data;

import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.ratethisfest.android.log.LogController;

public enum FestData {
  INSTANCE;

  public static final String FEST_NAME = "FEST_NAME";
  public static final String FEST_WEEK = "FEST_WEEK";
  public static final String FEST_YEAR = "FEST_YEAR";
  public static final String FEST_MONTH = "FEST_MONTH";
  public static final String FEST_DAYOFMONTH = "FEST_DAYOFMONTH";

  // public boolean initialized;
  private static final ImmutableTable<Integer, String, String> festTable;

  static {
    LogController.MULTIWEEK.logMessage("FestData initializing - should only happen once");
    ImmutableTable.Builder<Integer, String, String> tableBuilder = new ImmutableTable.Builder<Integer, String, String>();

    int row = 0;
    row++;
    tableBuilder.put(row, FEST_NAME, "Coachella");
    tableBuilder.put(row, FEST_WEEK, "1");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "4");
    tableBuilder.put(row, FEST_DAYOFMONTH, "12");

    row++;
    tableBuilder.put(row, FEST_NAME, "Coachella");
    tableBuilder.put(row, FEST_WEEK, "1");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "4");
    tableBuilder.put(row, FEST_DAYOFMONTH, "13");

    row++;
    tableBuilder.put(row, FEST_NAME, "Coachella");
    tableBuilder.put(row, FEST_WEEK, "1");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "4");
    tableBuilder.put(row, FEST_DAYOFMONTH, "14");

    row++;
    tableBuilder.put(row, FEST_NAME, "Coachella");
    tableBuilder.put(row, FEST_WEEK, "2");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "4");
    tableBuilder.put(row, FEST_DAYOFMONTH, "19");

    row++;
    tableBuilder.put(row, FEST_NAME, "Coachella");
    tableBuilder.put(row, FEST_WEEK, "2");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "4");
    tableBuilder.put(row, FEST_DAYOFMONTH, "20");

    row++;
    tableBuilder.put(row, FEST_NAME, "Coachella");
    tableBuilder.put(row, FEST_WEEK, "2");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "4");
    tableBuilder.put(row, FEST_DAYOFMONTH, "21");

    row++;
    tableBuilder.put(row, FEST_NAME, "Lollapalooza");
    tableBuilder.put(row, FEST_WEEK, "1");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "8");
    tableBuilder.put(row, FEST_DAYOFMONTH, "2");

    row++;
    tableBuilder.put(row, FEST_NAME, "Lollapalooza");
    tableBuilder.put(row, FEST_WEEK, "1");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "8");
    tableBuilder.put(row, FEST_DAYOFMONTH, "3");

    row++;
    tableBuilder.put(row, FEST_NAME, "Lollapalooza");
    tableBuilder.put(row, FEST_WEEK, "1");
    tableBuilder.put(row, FEST_YEAR, "2013");
    tableBuilder.put(row, FEST_MONTH, "8");
    tableBuilder.put(row, FEST_DAYOFMONTH, "4");

    festTable = tableBuilder.build();
  }

  // Not used
  private FestData() {
  }

  public static ImmutableTable<Integer, String, String> getTable() {
    return FestData.festTable;
  }

  //Returns rows that have a value of matchValue in the column specified by columnName
  // e.g. searchForRows(FestData.FEST_NAME, "Lollapalooza")
  public static Map<Integer, Map<String, String>> searchForRows(String columnName, String matchValue) {
    // All row keys paired to the column we are matching against
    ImmutableMap<Integer, String> selectedColumnKeys = festTable.column(columnName);
    Predicate<String> matchSearchValue = Predicates.equalTo(matchValue);

    // All row keys paired to values in the selected column that match our search
    final Map<Integer, String> filteredRowKeys = Maps.filterValues(selectedColumnKeys, matchSearchValue);

    System.out.println("filteredRowKeys:");
    for (Integer key : filteredRowKeys.keySet()) {
      System.out.println(key + ":" + filteredRowKeys.get(key));
    }

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
