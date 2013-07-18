package com.ratethisfest.shared;

public class HttpConstants {
  // TODO ideally placed in resource file
  private static final String SERVER_URL_COACHELLER = "https://ratethisfest.appspot.com/coachellerServlet";
  public static final String SERVER_URL_LOLLAPALOOZER = "https://ratethisfest.appspot.com/lollapaloozerServlet";
  public static final String SERVER_URL_TEST = "https://www.google.com"; // Not really used

  public static final String PARAM_ACTION = "action";
  public static final String PARAM_AUTH_TYPE = "auth_type";
  public static final String PARAM_AUTH_ID = "auth_id";
  public static final String PARAM_AUTH_TOKEN = "auth_token";
  public static final String PARAM_EMAIL = "email";
  public static final String PARAM_SET_ID = "set_id";
  public static final String PARAM_DAY = "day";
  public static final String PARAM_YEAR = "year";
  public static final String PARAM_SCORE = "score";
  public static final String PARAM_WEEKEND = "weekend";
  public static final String PARAM_NOTES = "notes";

  public static final String ACTION_GET_SETS = "get_sets";
  public static final String ACTION_GET_RATINGS = "get_ratings";
  public static final String ACTION_ADD_RATING = "add_rating";
  public static final String ACTION_EMAIL_RATINGS = "email_ratings";

  // Only FestivalEnum should use this.
  // A class wanting the fest URL should be asking whatever instance of FestivalEnum it can reach
  public static String readCommentgetCoachellerServerUrlReadComment() {
    return SERVER_URL_COACHELLER;
  }
}
