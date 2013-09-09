package auth.logins;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import auth.logins.data.AuthProviderAccount;
import auth.logins.other.LoginManager;
import auth.logins.other.LoginType;
import auth.logins.other.RTFAccountException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ServletInterface {
  private static final Logger log = Logger.getLogger(new Object() {
  }.getClass().getEnclosingClass().getName());

  public static final String PARAM_NAME_RTFACTION = "RTFAction";
  public static final String PARAM_NAME_RETURNHOST = "RTFReturnUrl";

  public static final String GOOGLE_PARAM_OAUTH_VERIFIER = "oauth_verifier";
  public static final String GOOGLE_PARAM_OAUTH_TOKEN = "oauth_token";
  public static final String GOOGLE_REDIRECT_URL_BASE = "https://www.google.com/accounts/OAuthAuthorizeToken";
  public static final String GOOGLE_REDIRECT_REQUEST_TOKEN_SECRET = "requestTokenSecret";
  public static final String GOOGLE_REDIRECT_REQUEST_TOKEN = "requestToken";

  public static final String FACEBOOK_PARAM_CODE = "code";

  public static final String TWITTER_PARAM_OAUTH_VERIFIER = "oauth_verifier";
  public static final String TWITTER_PARAM_OAUTH_TOKEN = "oauth_token";

  public static final String ACTION_DESTROY_ACCOUNT = "destroyAccount";
  public static final String ACTION_LOGOUT = "logout";

  public static final String ACTION_GOOGLE_AUTH = "UserReqGoogleAuth";
  public static final String ACTION_FACEBOOK_AUTH_SCRIBE = "UserReqFacebookAuthScribe";
  public static final String ACTION_FACEBOOK_AUTH = "UserReqFacebookAuth";
  public static final String ACTION_TWITTER_AUTH = "UserReqTwitterAuth";

  public static final String ACTION_CALLBACK_GOOGLE_AUTH = "CallbackGoogleUserAuth";
  public static final String ACTION_CALLBACK_FACEBOOK_AUTH = "CallbackFacebookUserAuth";
  public static final String ACTION_CALLBACK_TWITTER_AUTH = "CallbackTwitterUserAuth";

  public static final String TWITTER_PROTECTED_URL_USERINFO = "https://api.twitter.com/1.1/account/verify_credentials.json";
  public static final String FACEBOOK_PROTECTED_URL_USERINFO = "https://graph.facebook.com/me";
  public static final String GOOGLE_PROTECTED_URL_USERINFO = "https://www.googleapis.com/oauth2/v2/userinfo";

  // Input is null checked
  public static String getFirstParameter(Map<String, String[]> parameterMap, String parameterName) {
    if (parameterMap == null) {
      return null;
    }

    String[] strings = parameterMap.get(parameterName);
    if (strings == null) {
      return null;
    }
    return strings[0];
  }

  // TODO reduce the complexity of this part....
  // reqHost = host name that client sent the request to, useful for determining target fest
  public static String libraryHandleRTFAction(HttpSession session, HttpServletRequest request) throws IOException,
      JsonProcessingException, RTFAccountException {
    String reqHost = request.getServerName(); // Identify the hostname that the client sent the request to
    // Map<String, String[]> parametersMap = request.getParameterMap();
    String paramRTFAction = request.getParameter(PARAM_NAME_RTFACTION);
    String redirectHostName = request.getParameter(PARAM_NAME_RETURNHOST);

    if (redirectHostName != null) { // Return redirect URL
      log.info("Routing request for RTF Action: " + paramRTFAction + " redirect host: " + redirectHostName);
      String redirectTargetUrl = getRedirectTargetUrl(request, redirectHostName);
      return redirectTargetUrl;
    }

    log.info("Routing request for RTF Action: " + paramRTFAction);

    if (ACTION_GOOGLE_AUTH.equals(paramRTFAction)) {
      return actionUserGoogleAuthStart(session, reqHost);
    }

    if (ACTION_FACEBOOK_AUTH.equals(paramRTFAction)) {
      // actionUserFacebookCustomAuthStart(req);
      return null; // This should not be called. It should break, if this gets called :P
    }

    if (ACTION_FACEBOOK_AUTH_SCRIBE.equals(paramRTFAction)) {
      return actionUserFacebookScribeAuthStart(reqHost);
    }

    if (ACTION_TWITTER_AUTH.equals(paramRTFAction)) {
      return actionUserTwitterAuthStart(reqHost);
    }

    if (ACTION_CALLBACK_GOOGLE_AUTH.equals(paramRTFAction)) {
      return actionCallbackGoogleAuthScribe(session, request);
    }

    if (ACTION_CALLBACK_FACEBOOK_AUTH.equals(paramRTFAction)) {
      return actionCallbackFacebookAuthScribe(session, request);
    }

    if (ACTION_CALLBACK_TWITTER_AUTH.equals(paramRTFAction)) {
      return actionCallbackTwitterAuth(session, request);
    }

    if (ACTION_LOGOUT.equals(paramRTFAction)) {
      LoginManager.logOutUser(session);
      return ServletConfig.HTTP + request.getServerName();
    }

    if (ACTION_DESTROY_ACCOUNT.equals(paramRTFAction)) {
      LoginManager.destroyRTFAccount(session);
      LoginManager.logOutUser(session);
      return ServletConfig.HTTP + request.getServerName();
    }

    log.info("Unexpectedly did not find an action to handle");
    return null;
  }

  private static String getRedirectTargetUrl(HttpServletRequest request, String redirectHostName) {
    String redirectTargetUrl = request.getScheme() + "://" + redirectHostName + request.getRequestURI();

    if (request.getQueryString() != null) {
      boolean addedQuestionMark = false;

      Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String parameterName = parameterNames.nextElement();
        String parameterValue = request.getParameter(parameterName);
        if (!parameterName.equals(PARAM_NAME_RETURNHOST)) { // Discard returnhost parameter
          if (addedQuestionMark) {
            redirectTargetUrl += "&";
          } else {
            redirectTargetUrl += "?";
            addedQuestionMark = true;
          }
          redirectTargetUrl += parameterName + "=" + parameterValue;
        }
      }
    }

    log.info("Redirecting request to: " + redirectTargetUrl);
    return redirectTargetUrl;
  }

  private static String buildGoogleCallbackUrl(String reqHost) {
    StringBuilder callbackUrl = new StringBuilder();
    callbackUrl.append(ServletConfig.HTTP).append(ServletConfig.HOSTNAME_RATETHISFEST)
        .append(ServletConfig.GOOGLE_USER_AUTH_CALLBACK_PATH);
    callbackUrl.append("&").append(PARAM_NAME_RETURNHOST).append("=").append(reqHost);
    return callbackUrl.toString();
  }

  private static String buildFacebookCallbackUrl(String reqHost) {
    StringBuilder callbackUrl = new StringBuilder(); // This must be set here, google will redirect to whatever is
                                                     // specified
    callbackUrl.append(ServletConfig.HTTP).append(ServletConfig.HOSTNAME_RATETHISFEST)
        .append(ServletConfig.FACEBOOK_USER_AUTH_SCRIBE_CALLBACK_PATH);
    callbackUrl.append("&").append(PARAM_NAME_RETURNHOST).append("=").append(reqHost);
    return callbackUrl.toString();
  }

  private static String buildTwitterCallbackUrl(String reqHost) {
    StringBuilder callbackUrl = new StringBuilder(); // This must be set here, google will redirect to whatever is
                                                     // specified
    callbackUrl.append(ServletConfig.HTTP).append(ServletConfig.HOSTNAME_RATETHISFEST)
        .append(ServletConfig.TWITTER_REDIRECT_PATH);
    callbackUrl.append("&").append(PARAM_NAME_RETURNHOST).append("=").append(reqHost);
    return callbackUrl.toString();
  }

  private static String actionUserGoogleAuthStart(HttpSession session, String reqHost) throws IOException {
    String callbackUrl = buildGoogleCallbackUrl(reqHost); // This must be set here, google will redirect to whatever is
                                                          // specified
    log.info("Setting Google callback URL: " + callbackUrl.toString());

    OAuthService service = new ServiceBuilder().provider(GoogleApi.class).apiKey(ServletConfig.GOOGLE_API_KEY)
        .apiSecret(ServletConfig.GOOGLE_API_SECRET).callback(callbackUrl.toString())
        .scope(ServletConfig.GOOGLE_OAUTH_REQ_SCOPE).build();

    // Obtain the Request Token
    Token requestToken = service.getRequestToken();
    session.setAttribute(GOOGLE_REDIRECT_REQUEST_TOKEN, requestToken.getToken());
    session.setAttribute(GOOGLE_REDIRECT_REQUEST_TOKEN_SECRET, requestToken.getSecret());

    StringBuilder redirectUrl = new StringBuilder(GOOGLE_REDIRECT_URL_BASE + "?" + GOOGLE_PARAM_OAUTH_TOKEN + "="
        + requestToken.getToken());
    redirectUrl.append("&").append(PARAM_NAME_RETURNHOST).append("=").append(reqHost);

    return redirectUrl.toString();
  }

  private static String actionUserFacebookScribeAuthStart(String reqHost) throws IOException {
    String facebookCallbackUrl = buildFacebookCallbackUrl(reqHost);
    log.info("Setting Facebook callback URL: " + facebookCallbackUrl.toString());
    // Attempt to implement Facebook with scribe
    OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(ServletConfig.FACEBOOK_ID)
        .apiSecret(ServletConfig.FACEBOOK_SECRET).callback(facebookCallbackUrl).scope("email").build();

    // Obtain the Authorization URL
    System.out.println("Fetching the Authorization URL...");
    StringBuilder authorizationUrl = new StringBuilder(service.getAuthorizationUrl(null));
    authorizationUrl.append("&").append(PARAM_NAME_RETURNHOST).append("=").append(reqHost);

    log.info("Facebook User Auth URL: " + authorizationUrl.toString());
    return authorizationUrl.toString();
  }

  // Done with no OAuth library code, following Facebook's developer instructions 8/1/2013
  // private static void actionUserFacebookCustomAuthStart(HttpServletRequest req) {
  // if ("code".equals(req.getParameter("response_type"))) {
  // String code = req.getParameter("code");
  // // String token = getFacebookAccessToken(code);
  // // doc.body().appendText("Got token: " + token).appendElement("br");
  // // getFacebookUserData(doc, token);
  // }
  // }

  private static String actionUserTwitterAuthStart(String reqHost) throws IOException {
    String twitterRedirectUrl = buildTwitterCallbackUrl(reqHost);
    log.info("Setting Twitter callback URL: " + twitterRedirectUrl.toString());
    OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(ServletConfig.TWITTER_KEY)
        .apiSecret(ServletConfig.TWITTER_SECRET).callback(twitterRedirectUrl).build();

    Token requestToken = service.getRequestToken();
    StringBuilder authorizationUrl = new StringBuilder(service.getAuthorizationUrl(requestToken));
    authorizationUrl.append("&").append(PARAM_NAME_RETURNHOST).append("=").append(reqHost);

    // doc.appendText("Twitter Auth URL: " + authUrl);
    log.info("Twitter Auth URL: " + authorizationUrl.toString());
    return authorizationUrl.toString();
  }

  private static String actionCallbackGoogleAuthScribe(HttpSession session, HttpServletRequest hsRequest)
      throws JsonProcessingException, IOException, RTFAccountException {
    Map parametersMap = hsRequest.getParameterMap();
    String paramToken = getFirstParameter(parametersMap, GOOGLE_PARAM_OAUTH_TOKEN);
    String paramVerifier = getFirstParameter(parametersMap, GOOGLE_PARAM_OAUTH_VERIFIER);
    String callbackUrl = buildGoogleCallbackUrl(hsRequest.getServerName());

    String reqToken = (String) session.getAttribute(GOOGLE_REDIRECT_REQUEST_TOKEN);
    String reqTokenSecret = (String) session.getAttribute(GOOGLE_REDIRECT_REQUEST_TOKEN_SECRET);
    Token requestToken = new Token(reqToken, reqTokenSecret);

    Verifier verifier = new Verifier(paramVerifier);

    OAuthRequest request = new OAuthRequest(Verb.GET, GOOGLE_PROTECTED_URL_USERINFO);
    OAuthService service = new ServiceBuilder().provider(GoogleApi.class).apiKey(ServletConfig.GOOGLE_API_KEY)
        .apiSecret(ServletConfig.GOOGLE_API_SECRET).callback(callbackUrl).scope(ServletConfig.GOOGLE_OAUTH_REQ_SCOPE)
        .build();

    Token accessToken = service.getAccessToken(requestToken, verifier);
    service.signRequest(accessToken, request);
    // request.addHeader("GData-Version", "3.0"); //TODO is this needed? Not sure. Seems like it's not.
    Response response = request.send();
    // TODO need to handle if user refuses the app
    // TODO need to handle if user fails auth process

    log.info(response.getCode() + "\r\n" + response.getBody());

    LoginType loginType = LoginType.GOOGLE;
    AuthProviderAccount newProviderAcct = new AuthProviderAccount(response.getBody(), loginType);

    LoginManager.authProviderLoginAccomplished(session, loginType, newProviderAcct);
    log.info("Login Success with:" + loginType.getName());
    return ServletConfig.HTTP + hsRequest.getServerName();
  }

  private static String actionCallbackFacebookAuthScribe(HttpSession session, HttpServletRequest hsRequest)
      throws JsonProcessingException, IOException, RTFAccountException {

    // TODO handle user refused auth
    // TODO handle user failed auth process
    Map parametersMap = hsRequest.getParameterMap();
    String callbackUrl = buildFacebookCallbackUrl(hsRequest.getServerName());

    // Facebook user auth has returned
    OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(ServletConfig.FACEBOOK_ID)
        .apiSecret(ServletConfig.FACEBOOK_SECRET).callback(callbackUrl).scope("email").build();
//    OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(ServletConfig.FACEBOOK_ID)
//        .apiSecret(ServletConfig.FACEBOOK_SECRET).callback(callbackUrl).build();
    // OAuthService service = (OAuthService)req.getSession().getAttribute("scribeservice");
    // String authorizationUrl = service.getAuthorizationUrl(null);

    String facebookCodeParam = getFirstParameter(parametersMap, FACEBOOK_PARAM_CODE);
    Verifier verifier = new Verifier(facebookCodeParam);

    // Trade the Request Token and Verfier for the Access Token
    log.info("Trading the Request Token for an Access Token...");

    Token nullToken = null;
    Token accessToken = service.getAccessToken(nullToken, verifier);

    // Now let's go and ask for a protected resource!
    OAuthRequest request = new OAuthRequest(Verb.GET, FACEBOOK_PROTECTED_URL_USERINFO);
    service.signRequest(accessToken, request);
    Response response = request.send();
    int responseCode = response.getCode();
    log.info("Response Body: " + response.getBody());

    LoginType loginType = LoginType.FACEBOOK;
    AuthProviderAccount newProviderAcct = new AuthProviderAccount(response.getBody(), loginType);

    LoginManager.authProviderLoginAccomplished(session, loginType, newProviderAcct);
    // Can log something here with this, descriptive info should go here once we stop dumping APAccount to log
    String id = newProviderAcct.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);
    String name = newProviderAcct.getProperty(AuthProviderAccount.LOGIN_PERSON_NAME);
    String email = newProviderAcct.getProperty(AuthProviderAccount.LOGIN_EMAIL);

    log.info("Got Facebook ID: " + id + " name: " + name + " email: " + email);
    log.info("Got Facebook ID: " + id + " name: " + name + " email: " + email);
    return ServletConfig.HTTP + hsRequest.getServerName();
  }

  private static String actionCallbackTwitterAuth(HttpSession session, HttpServletRequest hsRequest)
      throws IOException, JsonProcessingException, RTFAccountException {
    // TODO handle user refused auth
    // TODO handle user failed auth process
    Map parametersMap = hsRequest.getParameterMap();
    String callbackUrl = buildTwitterCallbackUrl(hsRequest.getServerName());

    String tokenString = getFirstParameter(parametersMap, TWITTER_PARAM_OAUTH_TOKEN);
    String verifierString = getFirstParameter(parametersMap, TWITTER_PARAM_OAUTH_VERIFIER);

    Token token = new Token(tokenString, verifierString);
    Verifier verifier = new Verifier(verifierString);

    OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(ServletConfig.TWITTER_KEY)
        .apiSecret(ServletConfig.TWITTER_SECRET).callback(callbackUrl).build();
    Token accessToken = service.getAccessToken(token, verifier);

    OAuthRequest request = new OAuthRequest(Verb.GET, TWITTER_PROTECTED_URL_USERINFO);
    service.signRequest(accessToken, request); // the access token from step 4
    Response response = request.send();
    log.info(response.getBody());

    LoginType loginType = LoginType.TWITTER;
    AuthProviderAccount newProviderAcct = new AuthProviderAccount(response.getBody(), loginType);
    LoginManager.authProviderLoginAccomplished(session, loginType, newProviderAcct);
    // Can log something here with this, descriptive info should go here once we stop dumping APAccount to log
    String id = newProviderAcct.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);
    String name = newProviderAcct.getProperty(AuthProviderAccount.LOGIN_PERSON_NAME);
    String twitterName = newProviderAcct.getProperty(AuthProviderAccount.LOGIN_SCREEN_NAME);
    return ServletConfig.HTTP + hsRequest.getServerName();
  }
}
