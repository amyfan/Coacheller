package com.ratethisfest.auth;

public class FacebookAuthProviderOAuth {
  //This code is not used and having it uncommented is causing confusion
  /*

  private OAuthService _service;
  private Token _requestToken;
  private Token _accessToken;
  private String _consumerKey;
  private String _consumerSecret;
  private String _callbackUrl;

  public FacebookAuthProviderOAuth(String consumerKey, String consumerSecret, String callbackUrl) {
    _consumerKey = consumerKey;
    _consumerSecret = consumerSecret;
    _callbackUrl = callbackUrl;

    _service = _getService();
  }

  private OAuthService _getService() {
    return new ServiceBuilder().provider(TwitterApi.class).apiKey(_consumerKey)
        .apiSecret(_consumerSecret).callback(_callbackUrl).build();
  }

  public String getRequestTokenUrl() {
    _requestToken = _service.getRequestToken();

    String authUrl = _service.getAuthorizationUrl(_requestToken);
    System.out.println("Twitter OAuth request token URL:" + authUrl);
    return authUrl;
  }

  public void requestTokenResult(String token, String tokenVerify) {
    Verifier verifier = new Verifier(tokenVerify);
    _accessToken = _service.getAccessToken(_requestToken, verifier);
  }

  public Response accessResource(Verb httpVerb, String resourceUrl) {
    OAuthRequest request = new OAuthRequest(httpVerb, resourceUrl);

    _service.signRequest(_accessToken, request); // the access // token from
    // // step 4 Response
    // response =
    return request.send();
  }

  public String getAccessToken() {
    return _accessToken.getToken() + "|" + _accessToken.getSecret();
  }

  public void setAccessTokenObject(String token, String secret) {
    _accessToken = new Token(token, secret);
  }
  */
}
