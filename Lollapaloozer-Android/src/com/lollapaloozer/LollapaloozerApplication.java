package com.lollapaloozer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lollapaloozer.auth.client.AuthModel;
import com.lollapaloozer.data.JSONArrayHashMap;
import com.lollapaloozer.data.LoginData;
import com.lollapaloozer.ui.ChooseLoginActivity;
import com.lollapaloozer.ui.LollapaloozerActivity;
import com.lollapaloozer.util.LollapaloozerHelper;
import com.ratethisfest.shared.AndroidConstants;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.HttpConstants;

/**
 * Class to maintain global application state. Serves as controller.
 * 
 */
public class LollapaloozerApplication extends Application {

  private AuthModel _authModel;
  private ChooseLoginActivity _activityChooseLogin = null;
  private LollapaloozerActivity _activityLollapaloozer = null;
  private Activity _lastActivity;

  private boolean dataFirstUse = true;
  private LoginData loginData;
  private LollapaloozerStorageManager storageManager;

  private JSONArrayHashMap userRatingsJAHM;
  private boolean _networkErrors = false;

  private int yearToQuery;
  private String dayToQuery;

  public LollapaloozerApplication() {
    System.out.println("Application Object Instantiated");

    _authModel = new AuthModel(this);
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

    yearToQuery = LollapaloozerHelper.whatYearIsToday();
    dayToQuery = LollapaloozerHelper.whatDayIsToday();

    storageManager = new LollapaloozerStorageManager(this);
    storageManager.load();

    obtainLoginDataFromStorage();

    userRatingsJAHM = new JSONArrayHashMap(AndroidConstants.QUERY_RATINGS__SET_ID,
        AndroidConstants.QUERY_RATINGS__WEEK);
  }

  public LollapaloozerActivity getLollapaloozerActivity() {
    return _activityLollapaloozer;
  }

  public void unregisterLollapaloozerActivity() {
    _activityLollapaloozer = null;
  }

  public AuthModel getAuthModel() {
    return _authModel;
  }

  public void setLastActivity(Activity act) {
    _lastActivity = act;
  }

  public Activity getLastActivity() {
    return _lastActivity;

  }

  public void showErrorDialog(String title, String problem, String details) {
    String errorString = problem + "\r\n\r\nDetails:\r\n" + details;
    System.out.println(errorString);

    AlertDialog.Builder builder = new AlertDialog.Builder(getLastActivity());
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

  public String bundleValues(Bundle inputBundle) {
    StringBuilder returnString = new StringBuilder();
    int count = 0;
    for (String s : inputBundle.keySet()) {
      returnString.append(s + ": " + inputBundle.get(s));
      count++;
    }
    return "[" + count + "]: " + returnString.toString();
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

    obtainLoginDataFromStorage();
  }

  public boolean isDataFirstUse() {
    return dataFirstUse;
  }

  public void setDataFirstUse(boolean dataFirstUse) {
    this.dataFirstUse = dataFirstUse;
  }

  private void obtainLoginDataFromStorage() {
    loginData = (LoginData) storageManager.getObject(LoginData.DATA_LOGIN_INFO);
  }

  public LoginData getLoginData() {
    return loginData;
  }

  public void clearLoginData() {
    this.loginData = null;
    storageManager.putObject(LoginData.DATA_LOGIN_INFO, loginData);
    storageManager.save();
  }

  public void setLoginEmail(String email) {
    loginData.emailAddress = email;
  }

  public boolean getIsLoggedIn() {
    if (loginData == null) {
      return false;
    } else {
      return true;
    }
  }

  public void processLoginData(Bundle results) {
    loginData = new LoginData();
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

  public void saveDataLoginInfo(LoginData loginData) {
    // TODO: optimize?
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

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(HttpConstants.PARAM_YEAR, "2012"));
        params.add(new BasicNameValuePair(HttpConstants.PARAM_DAY, dayToQuery));
        params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TYPE, loginData.loginType + ""));
        params
            .add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_ID, loginData.accountIdentifier));
        params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TOKEN, loginData.accountToken));
        if (loginData.emailAddress != null) {
          params.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, loginData.emailAddress));
        }
        myRatings = LollapaloozerServiceUtils.getRatings(params, this);

        storageManager.putJSONArray(AndroidConstants.DATA_RATINGS, myRatings);
      } catch (Exception e1) {
        _networkErrors = true;
        LollapaloozerHelper.debug(this,
            "Exception getting Ratings data, loading from storage if available");
        try {
          myRatings = storageManager.getJSONArray(AndroidConstants.DATA_RATINGS);
        } catch (JSONException e) {
          e.printStackTrace();
          LollapaloozerHelper.debug(this, "JSONException loading ratings from storage");
        }
      }

      try {
        if (myRatings == null) {
          LollapaloozerHelper.debug(this, "Had to initialize ratings data JSONArray");
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
      setData = LollapaloozerServiceUtils.getSets(params, this);

      storageManager.putJSONArray(AndroidConstants.DATA_SETS, setData);
    } catch (Exception e) {
      _networkErrors = true;
      LollapaloozerHelper.debug(this,
          "Exception getting Set data, loading from storage if available");
      setData = storageManager.getJSONArray(AndroidConstants.DATA_SETS);
    }

    if (setData == null) {
      LollapaloozerHelper.debug(this, "Had to initialize set data JSONArray");
      setData = new JSONArray();
    }

    return setData;
  }

  /**
   * TODO: throw a more specific Exception
   * 
   * @param scoreSelectedValue
   * @param notes
   * @param lastItemSelected
   * @param lastRatings
   * @throws Exception
   */
  public void doSubmitRating(String scoreSelectedValue, String notes, JSONObject lastItemSelected,
      JSONObject lastRatings) throws Exception {
    // submit rating
    // If Exception is thrown, do not store rating locally
    try {
      String set_id = lastItemSelected.get(AndroidConstants.QUERY_SETS__SET_ID) + "";

      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_SET_ID, set_id));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_SCORE, scoreSelectedValue));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_NOTES, notes));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TYPE, loginData.loginType
          + ""));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_ID,
          loginData.accountIdentifier));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TOKEN,
          loginData.accountToken));
      if (loginData.emailAddress != null) {
        nameValuePairs
            .add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, loginData.emailAddress));
      }

      LollapaloozerServiceUtils.addRating(nameValuePairs, this);

      // Need this in order to make the new rating appear in real time

      try {
        // CRITICAL that the keys are listed in this order
        userRatingsJAHM.addValues(AndroidConstants.QUERY_RATINGS__SET_ID,
            AndroidConstants.QUERY_RATINGS__WEEK, lastRatings);

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } catch (Exception e1) {
      LollapaloozerHelper.debug(this, "Error submitting rating");
      e1.printStackTrace();
      throw e1;
    }
  }

}
