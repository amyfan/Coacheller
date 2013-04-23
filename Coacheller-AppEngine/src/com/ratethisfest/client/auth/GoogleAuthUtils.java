package com.ratethisfest.client.auth;

public class GoogleAuthUtils {

  public static String getRequestUrl(String clientId, String redirectUri) {
    StringBuilder urlStringBuilder = new StringBuilder();
    urlStringBuilder.append("https://accounts.google.com/o/oauth2/auth?");
    urlStringBuilder.append("client_id=");
    urlStringBuilder.append(clientId);
    urlStringBuilder.append("&response_type=code");
    urlStringBuilder.append("&scope=");
    urlStringBuilder.append("https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
    urlStringBuilder.append("&redirect_uri=");
    urlStringBuilder.append(redirectUri);
    return urlStringBuilder.toString();
  }

  public static String buildTokenRequestUrl(String code, String clientId, String clientSecret, String redirectUri) {
    StringBuilder urlStringBuilder = new StringBuilder();
    urlStringBuilder.append("https://accounts.google.com/o/oauth2/token?");
    urlStringBuilder.append("client_id=");
    urlStringBuilder.append(clientId);
    urlStringBuilder.append("&client_secret=");
    urlStringBuilder.append(clientSecret);
    urlStringBuilder.append("&code=");
    urlStringBuilder.append(code);
    urlStringBuilder.append("&redirect_uri=");
    urlStringBuilder.append(redirectUri);
    urlStringBuilder.append("grant_type=authorization_code");
    return urlStringBuilder.toString();
  }
}
