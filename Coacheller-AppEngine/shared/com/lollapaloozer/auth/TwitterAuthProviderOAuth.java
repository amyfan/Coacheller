package com.lollapaloozer.auth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class TwitterAuthProviderOAuth {

	private OAuthService _service;
	private Token _requestToken;
	private Token _accessToken;
	private String _consumerKey;
	private String _consumerSecret;
	private String _callbackUrl;

	public TwitterAuthProviderOAuth(String consumerKey, String consumerSecret,
			String callbackUrl) {
		_consumerKey = consumerKey;
		_consumerSecret = consumerSecret;
		_callbackUrl = callbackUrl;
		
		_service = getService();
	}

	public String getRequestTokenUrl() {
		
		_requestToken = _service.getRequestToken();

		String authUrl = _service.getAuthorizationUrl(_requestToken);
		System.out.println("Twitter OAuth request token URL:" + authUrl);
		return authUrl;
	}
	
	public OAuthService getService() {
		return new ServiceBuilder().provider(TwitterApi.class)
		.apiKey(_consumerKey).apiSecret(_consumerSecret)
		.callback(_callbackUrl).build();
	}

	public void requestTokenResult(String token, String tokenVerify) {
		Verifier verifier = new Verifier(tokenVerify);
		_accessToken = _service.getAccessToken(_requestToken, verifier); // the
																			// requestToken
																			// you
																			// had
																			// from
																			// step
																			// 2
																			// OAuthRequest
																			// request

	}

	public Response accessResource(Verb httpVerb, String resourceUrl) {
		
		OAuthRequest request = new OAuthRequest(httpVerb, resourceUrl);
		
		_service.signRequest(_accessToken, request); // the access // token from
														// // step 4 Response
														// response =
		return request.send();
	}

	public String getAccessToken() {
		return _accessToken.getToken();
	}
	
	public String getTokenSecret() {
		return _accessToken.getSecret();
	}
	
	public void setAccessTokenObject(String token, String secret) {
		_accessToken = new Token(token, secret);
	}
}
