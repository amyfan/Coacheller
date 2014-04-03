package com.lollapaloozer;

import java.util.HashMap;

import com.lollapaloozer.ui.SearchSetsActivity;
import com.ratethisfest.android.FestivalApplication;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.AuthConstants;

/**
 * Class to maintain global application state. Serves as controller & model.
 * 
 */
public class LollapaloozerApplication extends FestivalApplication {

  private SearchSetsActivity activitySearchSets = null;

  public LollapaloozerApplication() {
    super();

    HashMap<String, String> appConstants = new HashMap<String, String>();

    // Initialize app constant hashmap for this application (Lollapaloozer)
    appConstants.put(AuthConstants.GOOGLE_MOBILE_CLIENT_ID, AuthConstants.LOLLA_GOOGLE_MOBILE_CLIENT_ID);
    appConstants.put(AuthConstants.GOOGLE_MOBILE_CLIENT_SECRET, AuthConstants.LOLLA_GOOGLE_MOBILE_CLIENT_SECRET);
    appConstants.put(AuthConstants.FACEBOOK_APP_ID, AuthConstants.LOLLA_FACEBOOK_APP_ID);
    appConstants.put(AuthConstants.FACEBOOK_APP_SECRET, AuthConstants.LOLLA_FACEBOOK_APP_SECRET);
    appConstants.put(AuthConstants.TWITTER_CONSUMER_KEY, AuthConstants.LOLLA_TWITTER_CONSUMER_KEY);
    appConstants.put(AuthConstants.TWITTER_CONSUMER_SECRET, AuthConstants.LOLLA_TWITTER_CONSUMER_SECRET);
    appConstants.put(AuthConstants.TWITTER_OAUTH_CALLBACK_URL, AuthConstants.LOLLA_TWITTER_OAUTH_CALLBACK_URL);

    authModel = new AuthModel(this, appConstants);
  }

  @Override
  public FestivalEnum getFestival() {
    return FestivalEnum.LOLLAPALOOZA;
  }

  public void registerSearchSetsActivity(SearchSetsActivity act) {
    if (activitySearchSets != null) {
      if (activitySearchSets == act) {
        LogController.LIFECYCLE_ACTIVITY.logMessage("Identical SetsSearchActivity was registered with Application");
      } else {
        LogController.LIFECYCLE_ACTIVITY
            .logMessage("Warning: Different SetsSearchActivity was registered with Application");
      }
    }
    activitySearchSets = act;
  }

  public SearchSetsActivity getSearchSetsActivity() {
    return activitySearchSets;
  }

  public void unregisterSearchSetsActivity() {
    activitySearchSets = null;
  }

}
