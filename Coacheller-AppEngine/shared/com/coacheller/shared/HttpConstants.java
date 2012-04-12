package com.coacheller.shared;

public class HttpConstants {
  // TODO ideally placed in resource file
  public static final String SERVER_URL = "http://ratethisfest.appspot.com/coachellerServlet?";

  public static final String PARAM_ACTION = "action";
  public static final String PARAM_EMAIL = "email";
  public static final String PARAM_DAY = "day";
  public static final String PARAM_YEAR = "year";
  public static final String PARAM_ARTIST = "artist";
  public static final String PARAM_SCORE = "score";
  public static final String PARAM_WEEKEND = "weekend";

  public static final String ACTION_GET_SETS = "get_sets";
  public static final String ACTION_GET_RATINGS = "get_ratings";
  public static final String ACTION_ADD_RATING = "add_rating";
}
