package com.ratethisfest.data;


import com.google.common.collect.ImmutableTable;
import com.ratethisfest.android.log.LogController;

public enum FestData {
  INSTANCE;

  public static final String FEST_NAME = "FEST_NAME";
  public static final String FEST_WEEK = "FEST_WEEK";
  public static final String FEST_YEAR = "FEST_YEAR";
  public static final String FEST_MONTH = "FEST_MONTH";
  public static final String FEST_DAYOFMONTH = "FEST_DAYOFMONTH";

  //public boolean initialized;
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

  //Not used
  private FestData() {
  }
  
  //Use constructor not init(), guaranteed only one construction via enum
  //remove code
//  public void init() {
//    if (this.initialized) {
//      return;
//    }
//  }

  public static ImmutableTable<Integer, String, String> getTable() {
    return FestData.festTable;
  }
  
  
}
