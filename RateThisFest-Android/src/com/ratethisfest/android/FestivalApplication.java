package com.ratethisfest.android;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.ratethisfest.R;
import com.ratethisfest.android.alert.AlertManager;
import com.ratethisfest.android.auth.AppControllerInt;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.FakeDataSource;
import com.ratethisfest.android.data.JSONArrayHashMap;
import com.ratethisfest.android.data.LoginData;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.android.ui.ChooseLoginActivity;
import com.ratethisfest.android.ui.FestivalActivity;
import com.ratethisfest.data.AndroidConstants;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.data.HttpConstants;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.CalendarUtils;
import com.ratethisfest.shared.LoginType;

public class FestivalApplication extends Application implements AppControllerInt {

  // An alternate solution has been implemented
  // public static final String FACEBOOK_APP_ID =
  // AuthConstants.COACH_FACEBOOK_APP_ID;
  // public static final String FACEBOOK_APP_SECRET =
  // AuthConstants.COACH_FACEBOOK_APP_SECRET;

  protected AuthModel authModel;
  private AlertManager alertManager = new AlertManager(this);
  private ChooseLoginActivity activityChooseLogin = null;
  private FestivalActivity activityFestival = null;

  private boolean dataFirstUse = true;
  private StorageManager storageManager;

  // CRITICAL that the keys are listed in this order
  private JSONArrayHashMap userRatingsJAHM = new JSONArrayHashMap(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
      AndroidConstants.JSON_KEY_RATINGS__WEEK);
  private boolean _networkErrors;

  private int queryYear;
  private int queryWeek;
  private String queryDay;

  public FestivalApplication() {
    System.out.println("Application Object Instantiated"); // Keep this
    // first

    // Set logging options here
    // LogController.LIFECYCLE_ACTIVITY.disable();
    // LogController.LIFECYCLE_THREAD.disable();
    // LogController.USER_ACTION_UI.disable();
    // LogController.MULTIWEEK.disable();
    LogController.allCategoriesOn();

    LogController.addLogInterface(new AndroidLogCatLogger("ratethisfest"));
  }

  public void registerChooseLoginActivity(ChooseLoginActivity act) {
    if (activityChooseLogin != null) {
      if (activityChooseLogin == act) {
        LogController.LIFECYCLE_ACTIVITY.logMessage("Identical ChooseLoginActivity was registered with Application");
      } else {
        System.out.println("Warning: Different ChooseLoginActivity was registered with Application");
      }
    }
    activityChooseLogin = act;
  }

  @Override
  public ChooseLoginActivity getChooseLoginActivity() {
    return activityChooseLogin;
  }

  // public void unregisterChooseLoginActivity() {
  // activityChooseLogin = null;
  // }

  public void registerActivity(FestivalActivity act) {
    if (activityFestival != null) {
      if (activityFestival == act) {
        LogController.LIFECYCLE_ACTIVITY.logMessage("Identical FestivalActivity was registered with Application");
      } else {
        LogController.LIFECYCLE_ACTIVITY
            .logMessage("Warning: Different FestivalActivity was registered with Application");
      }
    }
    activityFestival = act;

    // this.queryWeek = 1;
    // this.queryDay = "Friday";
    this.queryWeek = CalendarUtils.suggestWeekToQuery(getFestival());
    this.queryDay = CalendarUtils.suggestDayToQueryString(getFestival());
    this.queryYear = CalendarUtils.currentYear();

    storageManager = new StorageManager(this, getString(R.string.save_file_name));

    userRatingsJAHM = new JSONArrayHashMap(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
        AndroidConstants.JSON_KEY_RATINGS__WEEK);
  }

  public FestivalActivity getFestivalActivity() {
    return activityFestival;
  }

  public void unregisterFestivalActivity() {
    activityFestival = null;
  }

  public AuthModel getAuthModel() {
    return authModel;
  }

  public void setLastActivity(Activity lastActivity) {
    if (lastActivity instanceof ChooseLoginActivity) {
      authModel.setLastAuthRelatedActivity((ChooseLoginActivity) lastActivity);
    }
  }

  // Not used
  // public AuthActivityInt getLastAuthActivity() {
  // return authModel.getLastAuthActivity();
  // }

  public String getDayToQuery() {
    return queryDay;
  }

  public void setDayToQuery(String dayToQuery) {
    this.queryDay = dayToQuery;
  }

  public int getWeekToQuery() {
    return queryWeek;
  }

  public void setWeekToQuery(int weekToQuery) {
    this.queryWeek = weekToQuery;
  }

  public int getYearToQuery() {
    return queryYear;
  }

  public void setYearToQuery(int yearToQuery) {
    this.queryYear = yearToQuery;
  }

  // TODO: Create Interface for Overridden Methods
  public FestivalEnum getFestival() {
    // return getTestFestival(); // Testing only...
    return null;
  }

  private FestivalEnum getTestFestival() {
    FestivalEnum.TESTFEST.announceTestMessage();
    return FestivalEnum.TESTFEST;
  }

  public void refreshDataFromStorage() {
    // Below here is stuff to be done each refresh
    _networkErrors = false;

    // TODO: enumerate
    if (queryDay == null) {
      queryDay = CalendarUtils.suggestDayToQueryString(getFestival());
    }

    // CalendarUtils should handle the defaulting

    // int dayToQueryInt = DaysHashMap.DayStringToJavaCalendar(queryDay);
    // String defaultDayToQueryString =
    // DaysHashMap.DayJavaCalendarToString(Calendar.FRIDAY);
    // if (!(dayToQueryInt == Calendar.FRIDAY) && !(dayToQueryInt ==
    // Calendar.SATURDAY)
    // && !(dayToQueryInt == Calendar.SUNDAY)) {
    // queryDay = defaultDayToQueryString;
    // }
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

    if (LoginType.GOOGLE.getName().equals(loginData.loginType)
        || LoginType.FACEBOOK.getName().equals(loginData.loginType)) {
      loginData.emailAddress = loginData.accountIdentifier;
    } else {
      loginData.emailAddress = null;
    }

    System.out.println("Saving login data timeIssued=" + loginData.timeLoginIssued + " loginType="
        + loginData.loginType + " accountIdentifier=" + loginData.accountIdentifier + " accountToken="
        + loginData.accountToken + " emailAddress=" + loginData.emailAddress);

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

  public JSONArray getDataFromServer() throws JSONException {
    // Get my ratings
    if (getIsLoggedIn()) {

      JSONArray myRatings = null;
      try {
        List<NameValuePair> params = AndroidUtils.createGetQueryParamsArrayList(queryYear + "", queryDay,
            getLoginData());

        myRatings = ServiceUtils.getRatings(params, this, getFestival().getServerUrl());

        storageManager.putJSONArray(AndroidConstants.DATA_RATINGS, myRatings);
      } catch (Exception e1) {
        _networkErrors = true;
        LogController.OTHER.logMessage("Exception getting Ratings data, loading from storage if available");
        try {
          myRatings = storageManager.getJSONArray(AndroidConstants.DATA_RATINGS);
        } catch (JSONException e) {
          e.printStackTrace();
          LogController.OTHER.logMessage("JSONException loading ratings from storage");
        }
      }

      try {
        if (myRatings == null) {
          LogController.OTHER.logMessage("Had to initialize ratings data JSONArray");
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

    // Get sets
    JSONArray setData = null;
    try {
      // TODO: pass proper values (year can remain hard-coded for now)
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair(HttpConstants.PARAM_YEAR, queryYear + ""));
      params.add(new BasicNameValuePair(HttpConstants.PARAM_DAY, queryDay));

      // If festival equals testFest, do something wacky
      if (getFestival().equals(FestivalEnum.TESTFEST)) {
        setData = FakeDataSource.getData(params);
      } else {
        setData = ServiceUtils.getSets(params, this, getFestival().getServerUrl());
      }
      storageManager.putJSONArray(AndroidConstants.DATA_SETS, setData);
    } catch (Exception e) {
      _networkErrors = true;
      LogController.OTHER.logMessage("Exception getting Set data, loading from storage if available");
      setData = storageManager.getJSONArray(AndroidConstants.DATA_SETS);
    }

    if (setData == null) {
      LogController.OTHER.logMessage("Had to initialize set data JSONArray");
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
      String weekNumber = rating.get(AndroidConstants.JSON_KEY_RATINGS__WEEK).toString();
      String scoreSelectedValue = rating.get(AndroidConstants.JSON_KEY_RATINGS__SCORE).toString();
      String notes = "";
      if (rating.has(AndroidConstants.JSON_KEY_RATINGS__NOTES)) {
        notes = rating.getString(AndroidConstants.JSON_KEY_RATINGS__NOTES);
      }
      List<NameValuePair> nameValuePairs = AndroidUtils.createSubmitRatingParamsArrayList(queryYear + "", setId,
          scoreSelectedValue, notes, getLoginData(), weekNumber + "");
      ServiceUtils.addRating(nameValuePairs, this, getFestival().getServerUrl());

      // Need this in order to make the new rating appear in real time

      try {
        userRatingsJAHM.addValues(rating);

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } catch (Exception e1) {
      LogController.OTHER.logMessage("Error submitting rating");
      e1.printStackTrace();
      throw e1;
    }
  }

  public void updateSearchFields(String year, String week, String day) {
    LogController.OTHER.logMessage("Searching year[" + year + "] week[" + week + "] day[" + day + "]");
    setYearToQuery(Integer.valueOf(year));
    setDayToQuery(day);
    setWeekToQuery(Integer.valueOf(week));

    getFestivalActivity().refreshData();
  }

  @Override
  public void showErrorDialog(String title, String problem, String details) {
    String errorString = problem + "\r\n\r\nDetails:\r\n" + details;
    System.out.println(errorString);

    AlertDialog.Builder builder = new AlertDialog.Builder(authModel.getLastAuthActivity());
    builder.setTitle(title);
    builder.setMessage(errorString).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
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

  public AlertManager getAlertManager() {
    return this.alertManager;
  }

  public void redrawSetList() {
    if (this.activityFestival != null) {
      this.activityFestival.redrawUI();
    }
  }

}