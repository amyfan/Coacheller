package com.ratethisfest.data;

public enum FestData {
  INSTANCE;

  public static final String FEST_NAME = "FEST_NAME";
  public static final String FEST_WEEK = "FEST_WEEK";
  public static final String FEST_DATE = "FEST_DATE";
  //public boolean initialized;
  //private static final ImmutableTable<Integer, String, String> festTable;
  
  static {
//    LogController.MULTIWEEK.logMessage("FestData initializing - should only happen once");
//    ImmutableTable.Builder<Integer, String, String> tableBuilder = new ImmutableTable.Builder<Integer, String, String>();
//    
//    int row = 0;
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Coachella");
//    tableBuilder.put(row, "FEST_WEEK", "1");
//    tableBuilder.put(row, "FEST_DATE", "2013-04-12");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Coachella");
//    tableBuilder.put(row, "FEST_WEEK", "1");
//    tableBuilder.put(row, "FEST_DATE", "2013-04-13");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Coachella");
//    tableBuilder.put(row, "FEST_WEEK", "1");
//    tableBuilder.put(row, "FEST_DATE", "2013-04-14");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Coachella");
//    tableBuilder.put(row, "FEST_WEEK", "2");
//    tableBuilder.put(row, "FEST_DATE", "2013-04-19");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Coachella");
//    tableBuilder.put(row, "FEST_WEEK", "2");
//    tableBuilder.put(row, "FEST_DATE", "2013-04-20");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Coachella");
//    tableBuilder.put(row, "FEST_WEEK", "2");
//    tableBuilder.put(row, "FEST_DATE", "2013-04-21");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Lollapalooza");
//    tableBuilder.put(row, "FEST_WEEK", "1");
//    tableBuilder.put(row, "FEST_DATE", "2013-08-02");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Lollapalooza");
//    tableBuilder.put(row, "FEST_WEEK", "1");
//    tableBuilder.put(row, "FEST_DATE", "2013-08-03");
//    
//    row++;
//    tableBuilder.put(row, "FEST_NAME", "Lollapalooza");
//    tableBuilder.put(row, "FEST_WEEK", "1");
//    tableBuilder.put(row, "FEST_DATE", "2013-08-04");
//    
//    festTable = tableBuilder.build();
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

//  public static ImmutableTable<Integer, String, String> getTable() {
//    return FestData.festTable;
//  }
}
