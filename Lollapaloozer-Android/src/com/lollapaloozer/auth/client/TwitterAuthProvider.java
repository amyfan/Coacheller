package com.lollapaloozer.auth.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.scribe.model.Response;
import org.scribe.model.Verb;

import android.app.Activity;
import android.content.Intent;

import com.lollapaloozer.auth.TwitterAuthProviderOAuth;
import com.lollapaloozer.auth.verify.TwitterVerifier;
import com.lollapaloozer.ui.AuthChooseAccountActivity;
import com.ratethisfest.shared.Constants;
import com.ratethisfest.shared.Helper;

public class TwitterAuthProvider implements AuthProvider {
  private AuthChooseAccountActivity _activity;

  private TwitterAuthProviderOAuth _oAuthProvider;
  private HashMap<String, String> _twitterAccountProperties = new HashMap<String, String>();
  private ArrayList<String> _twitterAccountPropertyNames;

  // private String _key;
  // private String _secret;
  // Consumer key yit4Mu71Mj93eNILUo3uCw
  // Consumer secret rdYvdK4g3ckWVdnvzmAj6JXmj9RoI05rIb4nVYQsoI

  public TwitterAuthProvider(AuthChooseAccountActivity activity) {
    _activity = activity;
    _oAuthProvider = new TwitterAuthProviderOAuth(Constants.CONSUMER_KEY,
        Constants.CONSUMER_SECRET, Constants.OAUTH_CALLBACK_URL);
    _twitterAccountPropertyNames = new ArrayList<String>();
    _twitterAccountPropertyNames.add(TwitterVerifier.ACCOUNT_PROPERTY_ID);
    _twitterAccountPropertyNames.add(TwitterVerifier.ACCOUNT_PROPERTY_NAME);
    _twitterAccountPropertyNames.add(TwitterVerifier.ACCOUNT_PROPERTY_HANDLE);
  }

  // //This does work, although it launches the device browser directly
  // @Override
  // public void login() {
  // try {
  // CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(_key,
  // _secret);
  // DefaultOAuthProvider provider = new
  // DefaultOAuthProvider("https://twitter.com/oauth/request_token",
  // "https://twitter.com/oauth/access_token",
  // "https://twitter.com/oauth/authorize");
  // String authUrl = provider.retrieveRequestToken(consumer,
  // "http://callback_url"); //todo fix this
  // Toast.makeText(_activity, "Please authorize this app!",
  // Toast.LENGTH_LONG).show();
  // //setConsumerProvider();
  // _activity.startActivity(new Intent(Intent.ACTION_VIEW,
  // Uri.parse(authUrl)));
  // } catch (Exception e) {
  // Toast.makeText(_activity, e.getMessage(), Toast.LENGTH_LONG).show();
  // }
  // }

  // //Copied from library example page, this could not be made to work
  // @Override
  // public void login() {
  // TwitterFactory factory = new TwitterFactory();
  // Twitter twitter = factory.getInstance();
  //
  // RequestToken requestToken;
  // try {
  // twitter.setOAuthConsumer("_key", "_secret");
  // requestToken = twitter.getOAuthRequestToken(); //throws TwitterException
  // System.out.println("Open the following URL and grant access to your account:");
  // System.out.println(requestToken.getAuthorizationURL());
  //
  //
  // //twitter.setOAuthAccessToken(accessToken);
  //
  //
  //
  // } catch (TwitterException e) {
  // // TODO Auto-generated catch block
  // int statusCode = e.getStatusCode();
  // String exceptionCode = e.getExceptionCode();
  // String exceptionMessage = e.getMessage();
  // e.printStackTrace();
  // }
  //
  // }

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
    TwitterAuthDialogPresenter twitterAuthDialog = new TwitterAuthDialogPresenter(_activity);
    twitterAuthDialog.showDialog(authReqTokenUrl);

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
      String token = data.getStringExtra(Constants.INTENT_EXTRA_OAUTH1_RETURN_TOKEN);
      String verifier = data.getStringExtra(Constants.INTENT_EXTRA_OAUTH1_RETURN_VERIFIER);
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
      _activity.modelChanged();
    } else {
      System.out.println("Twitter Authentication was not successful");
    }
  }

}
