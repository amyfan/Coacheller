package com.lollapaloozer.auth.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.scribe.model.Response;
import org.scribe.model.Verb;

import android.app.Activity;
import android.content.Intent;

import com.lollapaloozer.auth.TwitterAuthProviderOAuth;
import com.lollapaloozer.auth.verify.TwitterVerifier;
import com.lollapaloozer.ui.TwitterAuthWebpageActivity;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.Helper;

public class FacebookWebAuthProvider implements AuthProvider {

  // private ChooseLoginActivity _activity;
  private final String LOGIN_TYPE = AuthConstants.LOGIN_TYPE_FACEBOOK_BROWSER;
  private AuthModel _model;
  private TwitterAuthProviderOAuth _oAuthProvider;
  private HashMap<String, String> _twitterAccountProperties = new HashMap<String, String>();
  private ArrayList<String> _twitterAccountPropertyNames;

  // Default constructor disallowed
  private FacebookWebAuthProvider() {
  }

  // private String _key;
  // private String _secret;
  // Consumer key yit4Mu71Mj93eNILUo3uCw
  // Consumer secret rdYvdK4g3ckWVdnvzmAj6JXmj9RoI05rIb4nVYQsoI

  public FacebookWebAuthProvider(AuthModel model) {
    // _activity = activity;
    _model = model;

    _oAuthProvider = new TwitterAuthProviderOAuth(AuthConstants.CONSUMER_KEY,
        AuthConstants.CONSUMER_SECRET, AuthConstants.OAUTH_CALLBACK_URL);
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

    TwitterVerifier verifier = new TwitterVerifier();
    return verifier.verify(_oAuthProvider.getAccessToken(),
        _twitterAccountProperties.get(TwitterVerifier.ACCOUNT_PROPERTY_HANDLE));

  }

  @Override
  public void login() {
    String authReqTokenUrl = _oAuthProvider.getRequestTokenUrl();
    Intent twitterAuthIntent = new Intent(_model.getApp().getChooseLoginActivity(),
        TwitterAuthWebpageActivity.class);
    twitterAuthIntent.putExtra(AuthConstants.INTENT_EXTRA_AUTH_URL, authReqTokenUrl);
    _model.getApp().getChooseLoginActivity()
        .startActivityForResult(twitterAuthIntent, AuthConstants.INTENT_TWITTER_LOGIN);
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
      // _model.getApp().getChooseLoginActivity().modelChanged();
    } else {
      System.out.println("Twitter Authentication was not successful");
    }
  }

}
