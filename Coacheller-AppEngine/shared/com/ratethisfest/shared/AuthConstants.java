package com.ratethisfest.shared;

public class AuthConstants {

  // Data
  public static final int DATA_NOTE_VISIBLE_MAX_LENGTH = 140;
  public static final int RATING_MAXIMUM = 5;

  // Android UI String
  public static final String UI_STR_LOGIN_STATUS = "Login Status: ";

  public static final String MSG_EMAIL_FORMAT = "Please enter your real email address.";
  public static final String MSG_SIGNIN_REQUIRED = "Sign in so we can remember this for you.";

  public static final String DIALOG_TITLE_GET_EMAIL = "Keep Track of Everything";
  // public static final String DIALOG_TITLE_FIRST_USE = "Welcome, Fellow Lollapaloozer";

  // Not used?
  // public static final int DEFAULT_PORT_HTTP = 80;
  // public static final int DEFAULT_PORT_HTTPS = 443;
  // public static final String OAUTH_REQUEST_CONTENT_TYPE =
  // "application/x-www-form-urlencoded";

  // Derived from "FBOOK"
  public static final int INTENT_FACEBOOK_LOGIN = 32665;
  public static final int INTENT_CHOOSE_LOGIN_TYPE = 2;
  public static final int INTENT_TWITTER_LOGIN = 1;

  public static final String INTENT_EXTRA_AUTH_URL = "intent.extra.twitter.auth";
  public static final String INTENT_EXTRA_OAUTH1_RETURN_TOKEN = "intent.extra.oauth1.token";
  public static final String INTENT_EXTRA_OAUTH1_RETURN_VERIFIER = "intent.extra.oauth1.verifier";
  public static final String INTENT_EXTRA_LOGIN_TYPE = "intent.extra.login.type";
  public static final String INTENT_EXTRA_ACCOUNT_IDENTIFIER = "intent.extra.account.identifier";
  public static final String INTENT_EXTRA_LOGIN_TOKEN = "intent.extra.login.token";

  public static final String LOGIN_TYPE_GOOGLE = "LOGIN_TYPE_GOOGLE";
  public static final String LOGIN_TYPE_FACEBOOK = "LOGIN_TYPE_FACEBOOK";
  public static final String LOGIN_TYPE_TWITTER = "LOGIN_TYPE_TWITTER";
  public static final String LOGIN_TYPE_FACEBOOK_BROWSER = "LOGIN_TYPE_FACEBOOK_BROWSER";

  // TODO: FILL OUT!

  // Used for login with google account, These map to the application registered for google APIs
  // Managed at https://code.google.com/apis/console under account malex.sd@gmail.com
  public static final String GOOGLE_MOBILE_CLIENT_ID = "GOOGLE_MOBILE_CLIENT_ID";
  public static final String GOOGLE_MOBILE_CLIENT_SECRET = "GOOGLE_MOBILE_CLIENT_SECRET";
  public static final String COACH_GOOGLE_MOBILE_CLIENT_ID = "287333849695.apps.googleusercontent.com";
  public static final String COACH_GOOGLE_MOBILE_CLIENT_SECRET = "A18Bx6V9FvOjB1IdIg5mKuaN";
  public static final String LOLLA_GOOGLE_MOBILE_CLIENT_ID = "253259340939.apps.googleusercontent.com";
  public static final String LOLLA_GOOGLE_MOBILE_CLIENT_SECRET = "3HqdJ51XXYc6Px83sZuJlfmI";

  // Facebook Apps managed at http://developer.facebook.com under malex.sd@gmail.com
  public static final String FACEBOOK_APP_ID = "FACEBOOK_APP_ID"; // Property Identifier
  public static final String FACEBOOK_APP_SECRET = "FACEBOOK_APP_SECRET"; // Property Identifier
  public static final String COACH_FACEBOOK_APP_ID = "275753515892849";
  public static final String COACH_FACEBOOK_APP_SECRET = "e7fa740e453771b378640ea9c61ffc7a";
  public static final String LOLLA_FACEBOOK_APP_ID = "186287061500005";
  public static final String LOLLA_FACEBOOK_APP_SECRET = "87eed9f76050ebd82c10a0ef161ec8a6";

  // Referenced in Coacheller-AppEngine. Not sure if they are registered with
  // google...
  public static final String LOLLA_GOOGLE_WEB_CLIENT_ID = "620188116680.apps.googleusercontent.com";
  public static final String LOLLA_GOOGLE_WEB_CLIENT_SECRET = "lbpYR2zYAl22gH6wf69lZKvj";

  // User Authentication, appears to all be used by Twitter and incomplete FacebookWeb
  // Managed at https://dev.twitter.com/apps under Twitter account @malexsd
  // TODO consider updating 'dummy' URL, should name twitter
  public static final String OAUTH_CALLBACK_PARAM_TOKEN = "oauth_token"; // Does not vary
  public static final String OAUTH_CALLBACK_PARAM_VERIFIER = "oauth_verifier"; // Does not vary
  public static final String OAUTH_CALLBACK_SCHEME = "http"; // Does not vary

  public static final String TWITTER_CONSUMER_KEY = "TWITTER_CONSUMER_KEY"; // Property Identifier
  public static final String TWITTER_CONSUMER_SECRET = "TWITTER_CONSUMER_SECRET"; // Property Identifier
  public static final String TWITTER_OAUTH_CALLBACK_URL = "TWITTER_OAUTH_CALLBACK_URL"; // Property Identifier

  public static final String COACH_TWITTER_CONSUMER_KEY = "Azi4kYhNXWTcXO4TekMbjw";
  public static final String COACH_TWITTER_CONSUMER_SECRET = "8AKZh88PYpexN8V5rf9cfkCtp5IEhjNg4jGXdAmW0";
  public static final String COACH_TWITTER_OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
      + "://coacheller.appspot.com/dummy"; // this

  public static final String LOLLA_TWITTER_CONSUMER_KEY = "yit4Mu71Mj93eNILUo3uCw";
  public static final String LOLLA_TWITTER_CONSUMER_SECRET = "rdYvdK4g3ckWVdnvzmAj6JXmj9RoI05rIb4nVYQsoI";
  public static final String LOLLA_TWITTER_OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
      + "://lollapaloozer.appspot.com/dummy";

  // public static final String REQUEST_URL_TWITTER =
  // "https://api.twitter.com/oauth/request_token";
  // public static final String AUTHORIZE_URL_TWITTER =
  // "https://api.twitter.com/oauth/authorize";
  // public static final String ACCESS_URL_TWITTER =
  // "https://api.twitter.com/oauth/access_token";

  // public static final String REQUEST_URL_FACEBOOK =
  // "http://www.facebook.com/dialog/oauth/";
  // public static final String AUTHORIZE_URL_TWITTER =
  // "https://api.twitter.com/oauth/authorize";
  // public static final String ACCESS_URL_TWITTER =
  // "https://api.twitter.com/oauth/access_token";

}