package com.coacheller;

import java.util.ArrayList;
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

import com.coacheller.ui.ChooseLoginActivity;
import com.coacheller.ui.CoachellerActivity;
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

public class CoachellerApplication extends Application implements AppControllerInt {

  private AuthModel authModel;
  private ChooseLoginActivity _activityChooseLogin = null;
  private CoachellerActivity _activityCoacheller = null;

  private boolean dataFirstUse = true;
  private LoginData loginData;
  private StorageManager storageManager;

  private JSONArrayHashMap userRatingsJAHM = new JSONArrayHashMap(
      AndroidConstants.JSON_KEY_RATINGS__SET_ID, AndroidConstants.JSON_KEY_RATINGS__WEEK);
  private boolean _networkErrors;

  private int yearToQuery;
  private int weekToQuery;
  private String dayToQuery;

  public CoachellerApplication() {
    System.out.println("Application Object Instantiated");

    authModel = new AuthModel(this);
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

  public void registerCoachellerActivity(CoachellerActivity act) {
    if (_activityCoacheller != null) {
      if (_activityCoacheller == act) {
        System.out.println("Identical CoachellerActivity was registered with Application");
      } else {
        System.out.println("Warning: Different CoachellerActivity was registered with Application");
      }
    }
    _activityCoacheller = act;

    yearToQuery = CalendarUtils.whatYearIsToday();
    weekToQuery = CalendarUtils.whichWeekIsToday();
    dayToQuery = CalendarUtils.whatDayIsToday();

    storageManager = new StorageManager(this, getString(R.string.save_file_name));
    storageManager.load();

    obtainLoginDataFromStorage();

    userRatingsJAHM = new JSONArrayHashMap(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
        AndroidConstants.JSON_KEY_RATINGS__WEEK);
  }

  public CoachellerActivity getCoachellerActivity() {
    return _activityCoacheller;
  }

  public void unregisterCoachellerActivity() {
    _activityCoacheller = null;
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

  public int getWeekToQuery() {
    return weekToQuery;
  }

  public void setWeekToQuery(int weekToQuery) {
    this.weekToQuery = weekToQuery;
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
            loginData);

        myRatings = ServiceUtils.getRatings(params, this, HttpConstants.SERVER_URL_COACHELLER);

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
      setData = ServiceUtils.getSets(params, this, HttpConstants.SERVER_URL_COACHELLER);

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

      String setId = rating.get(AndroidConstants.JSON_KEY_SETS__SET_ID).toString();
      String weekNumber = rating.get(AndroidConstants.JSON_KEY_RATINGS__WEEK).toString();
      String scoreSelectedValue = rating.get(AndroidConstants.JSON_KEY_RATINGS__RATING).toString();
      String notes = "";
      if (rating.has(AndroidConstants.JSON_KEY_RATINGS__NOTES)) {
        notes = rating.getString(AndroidConstants.JSON_KEY_RATINGS__NOTES);
      }
      List<NameValuePair> nameValuePairs = AndroidUtils.createSubmitParamsArrayList("2012", setId,
          scoreSelectedValue, notes, loginData, weekNumber + "");
      ServiceUtils.addRating(nameValuePairs, this, HttpConstants.SERVER_URL_COACHELLER);

      // Need this in order to make the new rating appear in real time

      try {
        // CRITICAL that the keys are listed in this order
        userRatingsJAHM.addValues(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
            AndroidConstants.JSON_KEY_RATINGS__WEEK, rating);

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
