package com.ratethisfest.client.auth;

public class FacebookAuthUtils {

  public static String getRequestUrl(String clientId, String redirectUri) {
    StringBuilder urlStringBuilder = new StringBuilder();
    urlStringBuilder.append("https://www.facebook.com/dialog/oauth/?");
    urlStringBuilder.append("client_id=");
    urlStringBuilder.append(clientId);
    urlStringBuilder.append("&redirect_uri=");
    urlStringBuilder.append(redirectUri);
    // to prevent cross site forgery
    // urlStringBuilder.append("&state=");
    // urlStringBuilder.append("YOUR_STATE_VALUE");
    // TODO
    // urlStringBuilder.append("&scope=");
    // urlStringBuilder.append("COMMA_SEPARATED_LIST_OF_PERMISSION_NAMES");
    return urlStringBuilder.toString();
  }

  public static String buildTokenRequestUrl(String code, String clientId, String clientSecret,
      String redirectUri) {
    StringBuilder urlStringBuilder = new StringBuilder();
    urlStringBuilder.append("https://graph.facebook.com/oauth/access_token?");
    urlStringBuilder.append("client_id=");
    urlStringBuilder.append(clientId);
    urlStringBuilder.append("&client_secret=");
    urlStringBuilder.append(clientSecret);
    urlStringBuilder.append("&code=");
    urlStringBuilder.append(code);
    urlStringBuilder.append("&redirect_uri=");
    urlStringBuilder.append(redirectUri);
    return urlStringBuilder.toString();
  }

  public static String buildGraphApiRequestUrl(String token) {
    StringBuilder urlStringBuilder = new StringBuilder();
    urlStringBuilder.append("https://graph.facebook.com/me?");
    urlStringBuilder.append("access_token=");
    urlStringBuilder.append(token);
    return urlStringBuilder.toString();
  }
}
