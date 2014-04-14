package auth.logins;

import java.util.logging.Logger;

public class ServletConfig {

  // Configured for Facebook app "RateThisFest"

  public static final String HTTP = "http://";
  public static final String HTTPS = "https://";
  public static final String HOSTNAME_RATETHISFEST = "ratethisfest.appspot.com";
  public static final String SERVLET_BASEPATH = "/sessionsTest";

  public static final String FACEBOOK_ID = "186287061500005";
  // public static final String FACEBOOK_SECRET = "87eed9f76050ebd82c10a0ef161ec8a6";
  public static final String FACEBOOK_SECRET = "6f3c9b5d8f31932a79c06724b8bf486a";

  public static final String FACEBOOK_USER_AUTH_SCRIBE_START_URL = SERVLET_BASEPATH + "?response_type=code&scope=email"
      + "&" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_FACEBOOK_AUTH_SCRIBE;
  // public static final String FACEBOOK_USER_AUTH_SCRIBE_CALLBACK_URL =
  // "http://ratethisfest.appspot.com/sessionsTest?RTFAction=CallbackFacebookUserAuth";
  public static final String FACEBOOK_USER_AUTH_SCRIBE_CALLBACK_PATH = SERVLET_BASEPATH + "?"
      + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_CALLBACK_FACEBOOK_AUTH;

  public static final String GOOGLE_USER_AUTH_START_PATH = SERVLET_BASEPATH + "?"
      + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_GOOGLE_AUTH;
  public static final String GOOGLE_USER_AUTH_CALLBACK_PATH = SERVLET_BASEPATH + "?"
      + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_CALLBACK_GOOGLE_AUTH;
  // public static final String GOOGLE_USER_AUTH_CALLBACK_URL_OLD =
  // "http://ratethisfest.appspot.com/sessionsTest?RTFAction=CallbackGoogleUserAuth";

  public static final String GOOGLE_SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
  public static final String GOOGLE_SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
  public static final String GOOGLE_OAUTH_REQ_SCOPE = GOOGLE_SCOPE_EMAIL + " " + GOOGLE_SCOPE_PROFILE;

  public static final String GOOGLE_REQ_TOKEN_URL = "https://accounts.google.com/o/oauth2/auth";
  public static final String GOOGLE_API_KEY = "253259340939-s0i5mo2av3tkte0fre46l334u1kh96tl.apps.googleusercontent.com";
  public static final String GOOGLE_API_SECRET = "Xz9eWQ3KsW2h9_2FZninmxHj";

  public static final String TWITTER_KEY = "yit4Mu71Mj93eNILUo3uCw"; // Server applications ONLY
  public static final String TWITTER_SECRET = "rdYvdK4g3ckWVdnvzmAj6JXmj9RoI05rIb4nVYQsoI"; // Server applications ONLY
  public static final String TWITTER_REQ_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
  // public static final String TWITTER_REDIRECT_URL =
  // "http://ratethisfest.appspot.com/sessionsTest?RTFAction=CallbackTwitterUserAuth";
  public static final String TWITTER_REDIRECT_PATH = SERVLET_BASEPATH + "?" + ServletInterface.PARAM_NAME_RTFACTION
      + "=" + ServletInterface.ACTION_CALLBACK_TWITTER_AUTH;

}
