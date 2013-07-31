package com.ratethisfest.android.auth;

import java.util.ArrayList;
import java.util.HashMap;

import org.scribe.model.Response;
import org.scribe.model.Verb;

import android.app.Activity;
import android.content.Intent;

import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.android.ui.TwitterAuthWebpageActivity;
import com.ratethisfest.auth.TwitterAuthProviderOAuth;
import com.ratethisfest.auth.verify.TwitterVerifier;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.Helper;

public class TwitterAuthProvider implements AuthProviderInt {
  // private ChooseLoginActivity _activity;
  private AuthModel _model;
  private final String LOGIN_TYPE = AuthConstants.LOGIN_TYPE_TWITTER;
  private final String CONSUMER_KEY;
  private final String CONSUMER_SECRET;
  private final String OAUTH_CALLBACK_URL;

  private TwitterAuthProviderOAuth _oAuthProvider;
  private HashMap<String, String> _twitterAccountProperties = new HashMap<String, String>();
  private ArrayList<String> _twitterAccountPropertyNames;

  // Default constructor disallowed
  @SuppressWarnings("unused")
  private TwitterAuthProvider() {
    CONSUMER_KEY = "";
    CONSUMER_SECRET = "";
    OAUTH_CALLBACK_URL = "";
  }

  public TwitterAuthProvider(AuthModel model) {
    // _activity = activity;
    _model = model;
    CONSUMER_KEY = _model.getAppConstant(AuthConstants.TWITTER_CONSUMER_KEY);
    CONSUMER_SECRET = _model.getAppConstant(AuthConstants.TWITTER_CONSUMER_SECRET);
    OAUTH_CALLBACK_URL = _model.getAppConstant(AuthConstants.TWITTER_OAUTH_CALLBACK_URL);

    _oAuthProvider = new TwitterAuthProviderOAuth(CONSUMER_KEY, CONSUMER_SECRET, OAUTH_CALLBACK_URL);

    _twitterAccountPropertyNames = new ArrayList<String>();
    _twitterAccountPropertyNames.add(TwitterVerifier.ACCOUNT_PROPERTY_ID);
    _twitterAccountPropertyNames.add(TwitterVerifier.ACCOUNT_PROPERTY_NAME);
    _twitterAccountPropertyNames.add(TwitterVerifier.ACCOUNT_PROPERTY_HANDLE);
  }

  @Override
  public boolean isLoggedIn() {

    if (_twitterAccountProperties.get(TwitterVerifier.ACCOUNT_PROPERTY_HANDLE) == null) {
      return false;
    }

    TwitterVerifier verifier = new TwitterVerifier(CONSUMER_KEY, CONSUMER_SECRET, OAUTH_CALLBACK_URL);
    return verifier.verify(_oAuthProvider.getAccessToken(),
        _twitterAccountProperties.get(TwitterVerifier.ACCOUNT_PROPERTY_HANDLE));

  }

  @Override
  public void login() {
    LogController.AUTH_TWITTER.logMessage("Acquiring Twitter permissions");
    String authReqTokenUrl = _oAuthProvider.getRequestTokenUrl();
    Activity lastAuthActivity = _model.getLastAuthActivity();

    Intent twitterAuthIntent = new Intent(lastAuthActivity, TwitterAuthWebpageActivity.class);
    twitterAuthIntent.putExtra(AuthConstants.INTENT_EXTRA_AUTH_URL, authReqTokenUrl);
    lastAuthActivity.startActivityForResult(twitterAuthIntent, AuthConstants.INTENT_TWITTER_LOGIN);
  }

  @Override
  public void logout() {
    _twitterAccountProperties.clear();
  }

  @Override
  public String getAccountType() {
    return "twitter (custom implementation)";
  }

  @Override
  public String getLocalAccountName() {
    return _twitterAccountProperties.get(TwitterVerifier.ACCOUNT_PROPERTY_HANDLE) + "("
        + _twitterAccountProperties.get(TwitterVerifier.ACCOUNT_PROPERTY_NAME) + ")";
  }

  @Override
  public String getVerifiedAccountIdentifier() {
    // Not available through twitter
    return _twitterAccountProperties.get(TwitterVerifier.ACCOUNT_PROPERTY_ID);
  }

  @Override
  public String getAuthToken() {
    return _oAuthProvider.getAccessToken();
  }

  @Override
  public void extendAccess() {
    // TODO Auto-generated method stub

  }

  public void requestTokenCallback(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      String token = data.getStringExtra(AuthConstants.INTENT_EXTRA_OAUTH1_RETURN_TOKEN);
      String verifier = data.getStringExtra(AuthConstants.INTENT_EXTRA_OAUTH1_RETURN_VERIFIER);
      _oAuthProvider.requestTokenResult(token, verifier);
    }

    Response response = _oAuthProvider.accessResource(Verb.GET,
        "http://api.twitter.com/1/account/verify_credentials.xml");
    String responseBody = response.getBody();
    // System.out.println(response.getBody());

    _twitterAccountProperties.clear();
    for (String s : _twitterAccountPropertyNames) {
      _twitterAccountProperties.put(s, Helper.readXmlProperty(s, responseBody));
    }

    // If data is meaningful, set logged in flag;
    if (getLocalAccountName() != null && getVerifiedAccountIdentifier() != null) {

      System.out.println("Twitter Authentication Successful");
      _model.loginSuccess(LOGIN_TYPE);
      // _model.getAppController().getChooseLoginActivity().modelChanged();
    } else {
      System.out.println("Twitter Authentication was not successful");
    }
  }

  public String tweet(SocialNetworkPost post) {
    String message = "I saw the set by " + post.artistName + " and rated it " + post.rating + " (out of "
        + AuthConstants.RATING_MAXIMUM + ").";
    if (post.note != null && !post.note.equals("")) {
      message += "\r\nNotes: " + post.note;
    }

    HashMap<String, String> bodyParameters = new HashMap<String, String>();
    bodyParameters.put("status", message);

    Response response = _oAuthProvider.accessResource(Verb.POST, "https://api.twitter.com/1/statuses/update.json",
        bodyParameters);
    return response.getBody();
  }

}
