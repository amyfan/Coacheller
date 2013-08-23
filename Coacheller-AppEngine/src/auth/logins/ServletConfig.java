package auth.logins;

import java.util.logging.Logger;

public class ServletConfig {

  //Configured for Facebook app "RateThisFest"
  public static final String FACEBOOK_ID = "186287061500005";
  public static final String FACEBOOK_SECRET = "87eed9f76050ebd82c10a0ef161ec8a6";

  public static final String FACEBOOK_USER_AUTH_SCRIBE_START_URL = "http://ratethisfest.appspot.com/sessionsTest?response_type=code&scope=email"
      + "&RTFAction=UserReqFacebookAuthScribe";
  public static final String FACEBOOK_USER_AUTH_SCRIBE_CALLBACK_URL = "http://ratethisfest.appspot.com/sessionsTest?RTFAction=CallbackFacebookUserAuth";

  public static final String GOOGLE_USER_AUTH_START_URL = "http://ratethisfest.appspot.com/sessionsTest?RTFAction=UserReqGoogleAuth";
  public static final String GOOGLE_USER_AUTH_CALLBACK_URL = "http://ratethisfest.appspot.com/sessionsTest?RTFAction=CallbackGoogleUserAuth";

  public static final String GOOGLE_SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
  public static final String GOOGLE_SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
  public static final String GOOGLE_OAUTH_REQ_SCOPE = GOOGLE_SCOPE_EMAIL + " " + GOOGLE_SCOPE_PROFILE;

  public static final String GOOGLE_REQ_TOKEN_URL = "https://accounts.google.com/o/oauth2/auth";
  public static final String GOOGLE_API_KEY = "253259340939.apps.googleusercontent.com";
  public static final String GOOGLE_API_SECRET = "3HqdJ51XXYc6Px83sZuJlfmI";
  
  public static final String TWITTER_KEY = "yit4Mu71Mj93eNILUo3uCw"; // Server applications ONLY
  public static final String TWITTER_SECRET = "rdYvdK4g3ckWVdnvzmAj6JXmj9RoI05rIb4nVYQsoI"; // Server applications ONLY
  public static final String TWITTER_REQ_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
  public static final String TWITTER_REDIRECT_URL = "http://ratethisfest.appspot.com/sessionsTest?RTFAction=CallbackTwitterUserAuth";
  
  public static final String PATH_HOME = "/sessionsTest";

}
