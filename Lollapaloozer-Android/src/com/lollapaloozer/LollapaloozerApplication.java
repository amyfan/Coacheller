package com.lollapaloozer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.lollapaloozer.ui.ChooseLoginActivity;
import com.lollapaloozer.ui.LollapaloozerActivity;
import com.ratethisfest.android.AndroidConstants;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.CalendarUtils;
import com.ratethisfest.android.ServiceUtils;
import com.ratethisfest.android.StorageManager;
import com.ratethisfest.android.auth.AppControllerInt;
import com.ratethisfest.android.auth.AuthActivityInt;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.JSONArrayHashMap;
import com.ratethisfest.android.data.LoginData;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.HttpConstants;

/**
 * Class to maintain global application state. Serves as controller & model.
 * 
 */
public class LollapaloozerApplication extends Application implements AppControllerInt {


  private AuthModel authModel;
  private ChooseLoginActivity _activityChooseLogin = null;
  private LollapaloozerActivity _activityLollapaloozer = null;

  private boolean dataFirstUse = true;
  private StorageManager storageManager;

  private JSONArrayHashMap userRatingsJAHM;
  private boolean _networkErrors = false;

  private int yearToQuery;
  private String dayToQuery;

  public LollapaloozerApplication() {
    System.out.println("Application Object Instantiated");
    
    //Initialize app constant hashmap for this application (Lollapaloozer)
    HashMap<String, String> appConstants = new HashMap<String, String>();
    
    appConstants.put(AuthConstants.GOOGLE_MOBILE_CLIENT_ID,  AuthConstants.LOLLA_GOOGLE_MOBILE_CLIENT_ID);
    appConstants.put(AuthConstants.GOOGLE_MOBILE_CLIENT_SECRET, AuthConstants.LOLLA_GOOGLE_MOBILE_CLIENT_SECRET);
    
    appConstants.put(AuthConstants.FACEBOOK_APP_ID, AuthConstants.LOLLA_FACEBOOK_APP_ID);
    appConstants.put(AuthConstants.FACEBOOK_APP_SECRET, AuthConstants.LOLLA_FACEBOOK_APP_SECRET);
    
    appConstants.put(AuthConstants.TWITTER_CONSUMER_KEY, AuthConstants.LOLLA_TWITTER_CONSUMER_KEY);
    appConstants.put(AuthConstants.TWITTER_CONSUMER_SECRET,  AuthConstants.LOLLA_TWITTER_CONSUMER_SECRET);
    appConstants.put(AuthConstants.TWITTER_OAUTH_CALLBACK_URL, AuthConstants.LOLLA_TWITTER_OAUTH_CALLBACK_URL);

    authModel = new AuthModel(this, appConstants);
  }

  public void registerChooseLoginActivity(ChooseLoginActivity act) {
    if (_activityChooseLogin != null) {
      if (_activityChooseLogin == act) {
        System.out.println("Identical ChooseLoginActivity was registered with Application");
      } else {
        System.out
            .println("Warning: Different ChooseLoginActivity was registered with Application");
      }
    }
    _activityChooseLogin = act;
  }

  @Override
  public ChooseLoginActivity getChooseLoginActivity() {
    return _activityChooseLogin;
  }

  public void unregisterChooseLoginActivity() {
    _activityChooseLogin = null;
  }

  public void registerLollapaloozerActivity(LollapaloozerActivity act) {
    if (_activityLollapaloozer != null) {
      if (_activityLollapaloozer == act) {
        System.out.println("Identical LollapaloozerActivity was registered with Application");
      } else {
        System.out
            .println("Warning: Different LollapaloozerActivity was registered with Application");
      }
    }
    _activityLollapaloozer = act;

    yearToQuery = CalendarUtils.whatYearIsToday();
    dayToQuery = CalendarUtils.whatDayIsTodayString();

    storageManager = new StorageManager(this, getString(R.string.save_file_name));

    // CRITICAL that the keys are listed in this order
    userRatingsJAHM = new JSONArrayHashMap(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
        AndroidConstants.JSON_KEY_RATINGS__WEEK);
  }

  public LollapaloozerActivity getLollapaloozerActivity() {
    return _activityLollapaloozer;
  }

  public void unregisterLollapaloozerActivity() {
    _activityLollapaloozer = null;
  }

  public AuthModel getAuthModel() {
    return authModel;
  }

  public void setLastAuthActivity(AuthActivityInt lastActivity) {
    authModel.setLastAuthActivity(lastActivity);
  }

  public AuthActivityInt getLastAuthActivity() {
    return authModel.getLastAuthActivity();
  }

  public String getDayToQuery() {
    return dayToQuery;
  }

  public void setDayToQuery(String dayToQuery) {
    this.dayToQuery = dayToQuery;
  }

  public int getYearToQuery() {
    return yearToQuery;
  }

  public void setYearToQuery(int yearToQuery) {
    this.yearToQuery = yearToQuery;
  }

  public void refreshDataFromStorage() {
    // Below here is stuff to be done each refresh
    _networkErrors = false;

    // TODO: enumerate
    if (!dayToQuery.equals("Friday") && !dayToQuery.equals("Saturday")
        && !dayToQuery.equals("Sunday")) {
      dayToQuery = "Friday";
    }
  }

  public boolean isDataFirstUse() {
    return dataFirstUse;
  }

  public void setDataFirstUse(boolean dataFirstUse) {
    this.dataFirstUse = dataFirstUse;
  }

  public LoginData getLoginData() {
    if (storageManager.getObject(LoginData.DATA_LOGIN_INFO) != null) {
      return (LoginData) storageManager.getObject(LoginData.DATA_LOGIN_INFO);
    }
    return null;
  }

  public void clearLoginData() {
    saveDataLoginInfo(null);
  }

  @Deprecated
  public void setLoginEmail(String email) {
  }

  public boolean getIsLoggedIn() {
    if (getLoginData() == null) {
      return false;
    } else {
      return true;
    }
  }

  public void processLoginData(Bundle results) {
    LoginData loginData = new LoginData();
    loginData.timeLoginIssued = System.currentTimeMillis();
    loginData.loginType = results.getString(AuthConstants.INTENT_EXTRA_LOGIN_TYPE);
    loginData.accountIdentifier = results.getString(AuthConstants.INTENT_EXTRA_ACCOUNT_IDENTIFIER);
    loginData.accountToken = results.getString(AuthConstants.INTENT_EXTRA_LOGIN_TOKEN);

    if (loginData.loginType.equals(AuthConstants.LOGIN_TYPE_GOOGLE)
        || loginData.loginType.equals(AuthConstants.LOGIN_TYPE_FACEBOOK)) {
      loginData.emailAddress = loginData.accountIdentifier;
    } else {
      loginData.emailAddress = null;
    }

    System.out.println("Saving login data timeIssued=" + loginData.timeLoginIssued + " loginType="
        + loginData.loginType + " accountIdentifier=" + loginData.accountIdentifier
        + " accountToken=" + loginData.accountToken + " emailAddress=" + loginData.emailAddress);

    saveDataLoginInfo(loginData);
  }

  private void saveDataLoginInfo(LoginData loginData) {
    storageManager.putObject(LoginData.DATA_LOGIN_INFO, loginData);
    storageManager.save();
  }

  public boolean saveData() {
    if (!_networkErrors) {
      // TODO only if both sets and ratings were retrieved
      storageManager.save();
    }
    return !_networkErrors;
  }

  public JSONArrayHashMap getUserRatingsJAHM() {
    return userRatingsJAHM;
  }

  public void setUserRatingsJAHM(JSONArrayHashMap userRatingsJAHM) {
    this.userRatingsJAHM = userRatingsJAHM;
  }

  public JSONArray getRatingsFromServer() throws JSONException {
    if (getIsLoggedIn()) { // Get my ratings

      JSONArray myRatings = null;
      try {
        // TODO: year can remain hardcoded for now (to force users to
        // update app in future)

        List<NameValuePair> params = AndroidUtils.createGetQueryParamsArrayList("2012", dayToQuery,
            getLoginData());

        myRatings = ServiceUtils.getRatings(params, this, HttpConstants.SERVER_URL_LOLLAPALOOZER);

        storageManager.putJSONArray(AndroidConstants.DATA_RATINGS, myRatings);
      } catch (Exception e1) {
        _networkErrors = true;
        debug(this, "Exception getting Ratings data, loading from storage if available");
        try {
          myRatings = storageManager.getJSONArray(AndroidConstants.DATA_RATINGS);
        } catch (JSONException e) {
          e.printStackTrace();
          debug(this, "JSONException loading ratings from storage");
        }
      }

      try {
        if (myRatings == null) {
          debug(this, "Had to initialize ratings data JSONArray");
          myRatings = new JSONArray();
        }

        userRatingsJAHM.rebuildDataWith(myRatings);

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        // Could not get my ratings :(
        e.printStackTrace();
      }
    } else {
      // Need to wipe out ratings if email was just deleted
      userRatingsJAHM.wipeData();
    }

    // New strategy does not re-instantiate this object, this line should
    // not be
    // needed
    // _setListAdapter.setNewJAHM(_myRatings_JAHM);

    JSONArray setData = null;
    try {
      // TODO: pass proper values (year can remain hard-coded for now)
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair(HttpConstants.PARAM_YEAR, "2012"));
      params.add(new BasicNameValuePair(HttpConstants.PARAM_DAY, dayToQuery));
      setData = ServiceUtils.getSets(params, this, HttpConstants.SERVER_URL_LOLLAPALOOZER);

      storageManager.putJSONArray(AndroidConstants.DATA_SETS, setData);
    } catch (Exception e) {
      _networkErrors = true;
      debug(this, "Exception getting Set data, loading from storage if available");
      setData = storageManager.getJSONArray(AndroidConstants.DATA_SETS);
    }

    if (setData == null) {
      debug(this, "Had to initialize set data JSONArray");
      setData = new JSONArray();
    }

    return setData;
  }

  /**
   * TODO: throw a more specific Exception
   * 
   * @param rating
   * @throws Exception
   */
  public void doSubmitRating(JSONObject rating) throws Exception {
    // submit rating
    // If Exception is thrown, do not store rating locally
    try {
      String setId = rating.get(AndroidConstants.JSON_KEY_RATINGS__SET_ID).toString();
      String scoreSelectedValue = rating.get(AndroidConstants.JSON_KEY_RATINGS__SCORE).toString();
      String notes = "";
      if (rating.has(AndroidConstants.JSON_KEY_RATINGS__NOTES)) {
        notes = rating.getString(AndroidConstants.JSON_KEY_RATINGS__NOTES);
      }

      List<NameValuePair> nameValuePairs = AndroidUtils.createSubmitRatingParamsArrayList("2012",
          setId, scoreSelectedValue, notes, getLoginData(), "1");
      ServiceUtils.addRating(nameValuePairs, this, HttpConstants.SERVER_URL_LOLLAPALOOZER);

      // Need this in order to make the new rating appear in real time

      try {
        userRatingsJAHM.addValues(rating);

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } catch (Exception e1) {
      debug(this, "Error submitting rating");
      e1.printStackTrace();
      throw e1;
    }
  }

  @Override
  public void showErrorDialog(String title, String problem, String details) {
    String errorString = problem + "\r\n\r\nDetails:\r\n" + details;
    System.out.println(errorString);

    AlertDialog.Builder builder = new AlertDialog.Builder(authModel.getLastAuthActivity()
        .getLastActivity());
    builder.setTitle(title);
    builder.setMessage(errorString).setCancelable(true)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
          }
        }

        )
    // todo .setIcon(R.)

    // .setNegativeButton("No", new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog, int id) {
    // }
    // })
    ;
    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void debug(Context context, String out) {
    Log.d(context.getString(R.string.app_name), out);
  }

}
