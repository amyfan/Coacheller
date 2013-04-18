package com.ratethisfest.android;

public class AndroidConstants {
  public static final String SORT_TIME = "time";
  public static final String SORT_ARTIST = "artist";
  public static final String SORT_STAGE = "stage";
  
  // Local Storage
  public static final String DATA_SETS = "DATA_SETS";
  public static final String DATA_RATINGS = "DATA_RATINGS";
  
  // Alerts feature
  //Minutes before a set to trigger an alert, default setting
  public static final Integer ALERT_DEFAULT_REMINDERTIME = 15;
  
  public static final Integer FESTIVAL_WEEKS_LOLLAPALOOZER = 1;
  public static final Integer FESTIVAL_WEEKS_COACHELLER = 2;

  // JSON Hashmap Keys
  public static final String JSON_KEY_SETS__SET_ID = "id";
  public static final String JSON_KEY_SETS__ARTIST = "artist";
  public static final String JSON_KEY_SETS__DAY = "day";
  public static final String JSON_KEY_SETS__TIME_ONE = "time_one";
  public static final String JSON_KEY_SETS__TIME_TWO = "time_two";
  public static final String JSON_KEY_SETS__STAGE_ONE = "stage_one";
  public static final String JSON_KEY_SETS__STAGE_TWO = "stage_two";
  public static final String JSON_KEY_SETS__AVG_SCORE_ONE = "avg_score_one";
  public static final String JSON_KEY_SETS__AVG_SCORE_TWO = "avg_score_two";

  public static final String JSON_KEY_RATINGS__SET_ID = "set_id";
  public static final String JSON_KEY_RATINGS__WEEK = "weekend";
  public static final String JSON_KEY_RATINGS__SCORE = "score";
  public static final String JSON_KEY_RATINGS__NOTES = "notes";

  public static final int THREAD_UPDATE_UI = 1;
  public static final int THREAD_SUBMIT_RATING = 2;

  public static final int DIALOG_RATE = 1;
  public static final int DIALOG_GETEMAIL = 2;
  public static final int DIALOG_NETWORK_ERROR = 3;
  public static final int DIALOG_FIRST_USE = 4;
  public static final int DIALOG_ALERTS = 5;
}
