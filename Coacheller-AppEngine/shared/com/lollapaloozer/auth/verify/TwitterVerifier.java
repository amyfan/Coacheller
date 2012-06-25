package com.lollapaloozer.auth.verify;

import java.util.HashMap;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.lollapaloozer.auth.client.TwitterAuthProviderOAuth;
import com.lollapaloozer.util.Constants;
import com.lollapaloozer.util.Helper;

public class TwitterVerifier implements AuthVerifier {
	
	public final static String ACCOUNT_PROPERTY_ID = "id";
	public final static String ACCOUNT_PROPERTY_NAME = "name";
	public final static String ACCOUNT_PROPERTY_HANDLE = "screen_name";
	
	
	private TwitterAuthProviderOAuth _oAuthProvider = new TwitterAuthProviderOAuth(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET, Constants.OAUTH_CALLBACK_URL);
	
	public TwitterVerifier(String accessToken, String accessTokenSecret) {
		_oAuthProvider.setAccessTokenObject(accessToken, accessTokenSecret);
	}

	@Override
	public boolean verify(String authToken, String identifier) {
		//authToken is not used!!! value set in constructor
		Response response = _oAuthProvider.accessResource(Verb.GET,
		"http://api.twitter.com/1/account/verify_credentials.xml");
		String responseBody = response.getBody();
		// System.out.println(response.getBody());


		String id = Helper.readXmlProperty(ACCOUNT_PROPERTY_ID, responseBody);
		String handle = Helper.readXmlProperty(ACCOUNT_PROPERTY_HANDLE, responseBody);
		String name = Helper.readXmlProperty(ACCOUNT_PROPERTY_NAME, responseBody);


		// If data is meaningful, set logged in flag;
		if (id != null  && handle.equals(identifier)) {
			
			System.out.println("Twitter Verification Successful using verifier");
			return true;
		} else {
			System.out.println("Twitter Verification was not successful using verifier");
		}
		return false;
	}
	
	


}
