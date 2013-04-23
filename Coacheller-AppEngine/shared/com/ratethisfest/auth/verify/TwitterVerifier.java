package com.ratethisfest.auth.verify;

import java.util.StringTokenizer;

import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.ratethisfest.auth.TwitterAuthProviderOAuth;
import com.ratethisfest.shared.Helper;

public class TwitterVerifier implements AuthVerifier {

  public final static String ACCOUNT_PROPERTY_ID = "id";
  public final static String ACCOUNT_PROPERTY_NAME = "name";
  public final static String ACCOUNT_PROPERTY_HANDLE = "screen_name";
  private int _failuresToSimulate;

  private TwitterAuthProviderOAuth _oAuthProvider;

  // Can not instantiate without specifying application
  @SuppressWarnings("unused")
  private TwitterVerifier() {
  }

  public TwitterVerifier(String consumerKey, String consumerSecret, String callbackURL) {
    _oAuthProvider = new TwitterAuthProviderOAuth(consumerKey, consumerSecret, callbackURL);
  }

  @Override
  public boolean verify(String authToken, String identifier) {
    System.out.println("Verifying twitter token: " + authToken + " identifier: " + identifier);

    StringTokenizer tokenizer = new StringTokenizer(authToken, "|");

    String accessToken = tokenizer.nextToken();
    String accessTokenSecret = tokenizer.nextToken();

    _oAuthProvider.setAccessTokenObject(accessToken, accessTokenSecret);

    Response response = _oAuthProvider.accessResource(Verb.GET,
        "http://api.twitter.com/1/account/verify_credentials.xml");
    String responseBody = response.getBody();
    // System.out.println(response.getBody());

    String id = Helper.readXmlProperty(ACCOUNT_PROPERTY_ID, responseBody);
    String handle = Helper.readXmlProperty(ACCOUNT_PROPERTY_HANDLE, responseBody);
    String name = Helper.readXmlProperty(ACCOUNT_PROPERTY_NAME, responseBody);

    // If data is meaningful, set logged in flag;
    if (id != null && handle.equals(identifier)) {
      System.out.println("Twitter Verification Successful using verifier");
      return true;
    }

    System.out.println("Twitter Verification was not successful using verifier");
    return false;
  }

  @Override
  public void simulateFailure(int failures) {
    _failuresToSimulate = failures;
    throw new RuntimeException("Not Implemented for twitter");
  }

}
