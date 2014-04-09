package com.ratethisfest.android.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ratethisfest.R;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.FestivalApplication;
import com.ratethisfest.android.ServiceUtils;
import com.ratethisfest.android.alert.Alert;
import com.ratethisfest.android.alert.AlertsActivity;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.CustomPair;
import com.ratethisfest.android.data.CustomSetListAdapter;
import com.ratethisfest.android.data.LoginData;
import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.data.AndroidConstants;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.data.HttpConstants;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.CalendarUtils;
import com.ratethisfest.shared.FieldVerifier;

/**
 * Main Coacheller Activity. Serves as view controller.
 * 
 */
public abstract class FestivalActivity extends Activity implements View.OnClickListener, OnItemSelectedListener,
    OnItemClickListener {

  protected static final int REFRESH_INTERVAL__SECONDS = 15;

  private CustomSetListAdapter customSetListAdapter;
  protected String sortMode;
  protected long _lastRefresh = 0;

  protected Dialog dialogRate;
  protected Dialog dialogEmail;
  protected Dialog dialogAlerts;
  protected Dialog dialogNetworkError;
  protected Dialog dialogFirstUse;

  // contains set id, stored in setListAdapter
  protected JSONObject lastSetSelected;
  // contains actual rating, stored in userRatingsJAHM
  protected JSONObject lastRating;
  // contains both week's scores
  protected CustomPair<JSONObject, JSONObject> lastRatingPair = new CustomPair<JSONObject, JSONObject>(null, null);
  protected HashMap<Integer, Integer> _selectedIdToValue = new HashMap<Integer, Integer>();
  protected HashMap<String, Integer> _ratingSelectedValueToId = new HashMap<String, Integer>();

  // private int _ratingSelectedWeek;

  protected Handler _networkIOHandler;
  protected FestivalApplication _application;

  protected SocialNetworkPost _queuedTwitterPost;

  protected SocialNetworkPost _queuedFacebookPost;

  /** Called by Android Framework when the activity is first created. */

  /** Called by Android Framework when activity (re)gains foreground status */
  @Override
  public void onResume() {
    super.onResume();
    LogController.LIFECYCLE_ACTIVITY.logMessage(this + " onResume");
    _application.setLastActivity(this);
    _application.getAuthModel().setLastAuthRelatedActivity(this);

    // TODO: We'll reenable this if we have something significant to say in
    // the beginning
    // if (_appController.isDataFirstUse()) {
    // showDialog(DIALOG_FIRST_USE);
    // } else {
    _showClickToRate();
    // }

    long sinceLastRefresh = System.currentTimeMillis() - _lastRefresh;
    if (sinceLastRefresh / 1000 > REFRESH_INTERVAL__SECONDS) {
      LogController.OTHER.logMessage(sinceLastRefresh + "ms since last data refresh, calling refreshData");
      refreshData(); // TODO multi-thread this
    }

    // testFestData(); // Just prints some stuff
  }

  // What was this supposed to do?
  // private void testFestData() {
  // ImmutableTable<Integer, String, String> festTable = FestData.getTable();
  // ImmutableMap<Integer, String> rowToFestName =
  // festTable.column(FestData.FEST_NAME);
  // Predicate<String> equalsCoachella = Predicates.equalTo("Coachella");
  // Map<Integer, String> filteredValues = Maps.filterValues(rowToFestName,
  // equalsCoachella);
  // System.out.println("Results 1:");
  //
  // for (Integer key : filteredValues.keySet()) {
  // System.out.println(key +":"+ rowToFestName.get(key));
  // }
  //
  // System.out.println("Raw Table:");
  // for (Integer key : FestData.getTable().rowKeySet()) {
  // System.out.println(key +":"+ FestData.getTable().row(key));
  // }
  //
  // System.out.println("Search Results:");
  // Map<Integer, Map<String, String>> resultRows =
  // FestData.searchForRows(FestData.FEST_NAME, "Lollapalooza");
  // for (Integer key: resultRows.keySet()) {
  // System.out.println(key +":"+ resultRows.get(key));
  // }

  // HashMap<String, String> criteria = new HashMap<String, String>();
  // criteria.put(FestData.FEST_WEEK, "1"); //Any day in week 1 of any fest
  // criteria.put(FestData.FEST_YEAR, "2013"); //Restrict to days in 2013
  // criteria.put(FestData.FEST_DAYOFMONTH, "3"); //Restrict to days on the
  // 3rd of any month
  //
  // Map<Integer, Map<String, String>> rowsMatchingAll =
  // FestData.rowsMatchingAll(criteria);
  // System.out.println("Search Results:");
  // for (Integer key : rowsMatchingAll.keySet()) {
  // System.out.println(key + ":" + rowsMatchingAll.get(key));
  // }
  // }

  // An item in the ListView of sets is clicked
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    this.lastSetSelected = (JSONObject) getSetListAdapter().getItem(position);
    final int festivalMaxNumberOfWeeks = CalendarUtils.getFestivalMaxNumberOfWeeks(_application.getFestival());

    try {// TODO Hard coded strings means you are going to hell
      String setId = lastSetSelected.getString(AndroidConstants.JSON_KEY_SETS__SET_ID);
      JSONObject lastRatingWeek1 = _application.getUserRatingsJAHM().getJSONObject(setId, "1");
      lastRatingPair.first = lastRatingWeek1;

      JSONObject lastRatingWeek2 = null;
      if (festivalMaxNumberOfWeeks == 2) {
        lastRatingWeek2 = _application.getUserRatingsJAHM().getJSONObject(setId, "2");
        lastRatingPair.second = lastRatingWeek2;
      }

    } catch (JSONException e) {
      LogController.OTHER.logMessage("JSONException retrieving user's last rating");
      e.printStackTrace();
    }

    // Variable setup is done
    LogController.SET_DATA.logMessage("You Clicked On: " + lastSetSelected + " previous ratings "
        + lastRatingPair.first + "/" + lastRatingPair.second);

    boolean lastSelectedSetInFuture = false;
    try {
      // Issue here with weekToQuery returning 0;
      lastSelectedSetInFuture = CalendarUtils.isSetInTheFuture(lastSetSelected, _application.getWeekToQuery(),
          _application.getFestival());
    } catch (JSONException e) {
      LogController.ERROR.logMessage("FestivalActivity - JSONException calling CalendarUtils.isSetInTheFuture");
      e.printStackTrace();
    }
    // lastSelectedSetInFuture = false; // DEBUG and test ratings
    // lastSelectedSetInFuture = true; // DEBUG and test alerts
    LogController.SET_DATA.logMessage("Selected set in the future?:" + lastSelectedSetInFuture);
    if (lastSelectedSetInFuture) {
      // Set in the future, Ask to set alarm
      showDialog(AndroidConstants.DIALOG_ALERTS);

    } else if (!_application.getIsLoggedIn()) {
      // Set not in the future, user not logged in
      _beginSigninProcess(); // Get the user logged in, cannot rate any
      // sets right now

    } else {
      // Set not in the future, user is logged in, go ahead and rate
      // something
      if (lastSetSelected == null) {
        LogController.ERROR.logMessage(this.toString() + " UNEXPECTED: lastSetSelected is null");
      }

      LogController.LIFECYCLE_ACTIVITY.logMessage(this.toString() + " About to launch Dialog!");
      showDialog(AndroidConstants.DIALOG_RATE);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    String infoMessage = "FestivalActivity.onActivityResult req=" + requestCode + " resultCode=" + resultCode
        + " data: " + data;
    System.out.println(infoMessage);

    if (data != null) {
      System.out.println("Intent Data: " + data.getDataString());
      System.out.println("Intent Extras: " + AndroidUtils.bundleValues(data.getExtras()));
    }

    if (resultCode == Activity.RESULT_OK) { // Login success
      switch (requestCode) {

      case AuthConstants.INTENT_CHOOSE_LOGIN_TYPE:
        Bundle results = data.getExtras();
        _application.processLoginData(results);
        break;

      case AuthConstants.INTENT_FACEBOOK_LOGIN: {
        // Assuming it is Facebook
        LogController.LIFECYCLE_ACTIVITY.logMessage("onActivityResult called by Facebook API");
        // Required by Facebook API
        _application.getAuthModel().getFacebookObject().authorizeCallback(requestCode, resultCode, data);
        break;
      }

      case AuthConstants.INTENT_TWITTER_LOGIN: {
        // Assuming it is Facebook
        LogController.LIFECYCLE_ACTIVITY.logMessage("onActivityResult called by Twitter API");
        _application.getAuthModel().twitterAuthCallback(requestCode, resultCode, data);
        break;
      }

      default:
        _application.showErrorDialog("Unexpected Response", "An unexpected response was received from another window",
            infoMessage);

        break;
      }
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    LogController.LIFECYCLE_ACTIVITY.logMessage("FestivalActivity.onNewIntent() called");
    setIntent(intent);// must store the new intent unless getIntent() will
    // return the old one
    checkForExtraData();
  }

  // TODO: Create Interface for Overridden Methods
  @Deprecated
  protected void checkForExtraData() { 
  }

  protected void clickDialogFirstUseButtonOK() {
    _application.setDataFirstUse(false);
    // TODO _storageManager.save();
    dialogFirstUse.dismiss();
    _showClickToRate(); // display 'tap set to rate it' toast
  }

  private void _showClickToRate() {
    Toast clickToRate = Toast.makeText(this, "Tap any set to rate it!", 20);
    clickToRate.show();
  }

  private void _beginSigninProcess() {
    Toast featureRequiresSignin = Toast.makeText(this, AuthConstants.MSG_SIGNIN_REQUIRED, 25);
    featureRequiresSignin.show();

    // This shows the 'enter email' dialog, no longer needed
    // showDialog(DIALOG_GETEMAIL);

    Intent festivalAuthIntent = new Intent(this, ChooseLoginActivity.class);
    startActivityForResult(festivalAuthIntent, AuthConstants.INTENT_CHOOSE_LOGIN_TYPE);
  }

  protected void launchGetDataThread() {
    LogController.LIFECYCLE_THREAD.logMessage("Launching getData thread");

    new Thread() {
      @Override
      public void run() {
        try {
          getSetListAdapter().setData(_application.getDataFromServer());
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        _networkIOHandler.sendEmptyMessage(AndroidConstants.THREAD_UPDATE_UI);
      }
    }.start();
    LogController.LIFECYCLE_THREAD.logMessage("getData thread launched");
  }

  protected void launchSubmitRatingThread() {
    LogController.LIFECYCLE_THREAD.logMessage("Launching Submit Rating thread");

    new Thread() {
      @Override
      public void run() {
        try {
          getSetListAdapter().setData(_application.getDataFromServer());
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Message msgToSend = Message.obtain(_networkIOHandler, AndroidConstants.THREAD_SUBMIT_RATING);
        msgToSend.sendToTarget();
      }
    }.start();
    LogController.LIFECYCLE_THREAD.logMessage("Rating thread launched, UI thread continues");

  }

  public synchronized void doTwitterPost() {
    if (_queuedTwitterPost == null) {
      return;
    }

    if (!_application.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      System.out.println("Warning: trying to post without Twitter permissions");
    }

    // use _queuedFacebookPost
    String result = _application.getAuthModel().tweetToTwitter(_queuedTwitterPost);
    System.out.println("Twitter result:");
    System.out.println(result);
    _queuedTwitterPost = null;
  }

  public synchronized void doFacebookPost() {
    if (_queuedFacebookPost == null) {
      return;
    }

    if (!_application.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
      System.out.println("Warning: trying to post without facebook permissions");
    }

    // use _queuedFacebookPost
    String result = _application.getAuthModel().postToFacebookWall(_queuedFacebookPost);
    System.out.println("Facebook result:");
    System.out.println(result);
    _queuedFacebookPost = null;
  }

  public Activity getLastActivity() {
    return this;
  }

  protected void clickDialogSubmitRatingButtonOK() {
    // incomplete
    if (!_lastRateDialogVerify()) {
      return;
    }

    rateDialogSubmitRating();
    dialogRate.dismiss();
  }

  protected void clickDialogSubmitRatingTwitter() throws JSONException {
    System.out.println("Clicked post on Twitter");
    if (!_lastRateDialogVerify()) {
      return;
    }
    rateDialogSubmitRating();

    _application.getAuthModel().ensurePermission(AuthModel.PERMISSION_TWITTER_TWEET);

    _queuedTwitterPost = _buildSocialNetworkPost();

    // https://api.twitter.com/1/statuses
    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      System.out.println("Twitter Auth available now, posting immediately");
      doTwitterPost();
    } else {
      System.out.println("Twitter Auth not ready, posting later");
    }

    dialogRate.dismiss();
  }

  protected void clickDialogSubmitRatingFacebook() throws JSONException {
    System.out.println("Clicked post on FB");
    if (!_lastRateDialogVerify()) {
      return;
    }
    rateDialogSubmitRating();
    _application.getAuthModel().ensurePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL);

    _queuedFacebookPost = _buildSocialNetworkPost();
    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
      System.out.println("FB Auth available now, posting immediately");
      doFacebookPost();
    } else {
      System.out.println("FB Auth not ready, posting later");
    }

    dialogRate.dismiss();
  }

  // protected SocialNetworkPost _buildSocialNetworkPost() throws JSONException {
  // return null;
  // }
  //
  // protected void rateDialogSubmitRating() {
  // }
  //
  // protected boolean _lastRateDialogVerify() {
  // return false;
  // }

  protected CustomSetListAdapter getSetListAdapter() {
    return customSetListAdapter;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_item_email_me) {
      LogController.USER_ACTION_UI.logMessage("Menu button 'email me' pressed");

      if (!_application.getIsLoggedIn()) {
        Toast.makeText(this, "Try rating at least one set first", 15).show();
      } else {
        try {
          showDialog(AndroidConstants.DIALOG_GETEMAIL);
        } catch (Exception e) {
          LogController.ERROR.logMessage("Error requesting ratings email");
          e.printStackTrace();
        }
      }
      return true;

    } else if (item.getItemId() == R.id.menu_item_delete_email) {
      LogController.USER_ACTION_UI.logMessage("Menu button 'delete email' pressed");
      _application.clearLoginData();
      refreshData();
      return true;

    } else if (item.getItemId() == R.id.menu_item_manage_alerts) {
      LogController.USER_ACTION_UI.logMessage("Menu button 'manage alerts' pressed");
      Intent intent = new Intent();
      intent.setClass(this, AlertsActivity.class);
      startActivity(intent);
      return true;

    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_global_options, menu);
    return true;
  }

  // Dialog handling, called once the first time this activity displays
  // (a/each
  // type of)? dialog
  @Override
  protected Dialog onCreateDialog(int id) {
    if (id == AndroidConstants.DIALOG_FIRST_USE) {
      return createDialogfirstUse();
    }

    if (id == AndroidConstants.DIALOG_GETEMAIL) {
      return createDialogGetEmail();
    }

    if (id == AndroidConstants.DIALOG_ALERTS) {
      return createDialogAlerts();
    }

    if (id == AndroidConstants.DIALOG_RATE) {
      return createDialogRate();
    }

    if (id == AndroidConstants.DIALOG_NETWORK_ERROR) {
      return createDialogNetworkError();
    }

    return super.onCreateDialog(id);
  }

  private Dialog createDialogfirstUse() {
    dialogFirstUse = new Dialog(this);
    dialogFirstUse.setContentView(R.layout.dialog_first_use);
    dialogFirstUse.setTitle(AuthConstants.DIALOG_TITLE_FIRST_USE);

    Button buttonOK = (Button) dialogFirstUse.findViewById(R.id.button_firstuse_ok);
    buttonOK.setOnClickListener(this);
    return dialogFirstUse;
  }

  private Dialog createDialogGetEmail() {
    dialogEmail = new Dialog(this);
    dialogEmail.setContentView(R.layout.get_email_address);
    dialogEmail.setTitle(AuthConstants.DIALOG_TITLE_GET_EMAIL);

    Button buttonOK = (Button) dialogEmail.findViewById(R.id.button_provideEmail);
    buttonOK.setOnClickListener(this);

    Button buttonCancel = (Button) dialogEmail.findViewById(R.id.button_declineEmail);
    buttonCancel.setOnClickListener(this);

    return dialogEmail;
  }

  private Dialog createDialogAlerts() {
    dialogAlerts = new Dialog(this);
    dialogAlerts.setContentView(R.layout.dialog_alert_multipurpose);
    // alertsDialog.setTitle(AuthConstants.DIALOG_TITLE_GET_EMAIL);
    dialogAlerts.setTitle("Alert Dialog Title");

    Button buttonOK = (Button) dialogAlerts.findViewById(R.id.button_ok);
    buttonOK.setOnClickListener(this);

    Button buttonCancel = (Button) dialogAlerts.findViewById(R.id.button_cancel);
    buttonCancel.setOnClickListener(this);

    RadioButton radioNearNumbers = (RadioButton) dialogAlerts.findViewById(R.id.radioNearNumberfield);
    radioNearNumbers.setOnClickListener(this);

    RadioButton radioWithText = (RadioButton) dialogAlerts.findViewById(R.id.radioWithText);
    radioWithText.setOnClickListener(this);

    return dialogAlerts;
  }

  // TODO: Create Interface for Overridden Methods
  protected Dialog createDialogRate() {
    return null;
  }

  private Dialog createDialogNetworkError() {
    dialogNetworkError = new Dialog(this);
    dialogNetworkError.setContentView(R.layout.dialog_network_error);
    dialogNetworkError.setTitle("Network Error");

    Button buttonOK = (Button) dialogNetworkError.findViewById(R.id.button_network_error_ok);
    buttonOK.setOnClickListener(this);
    return dialogNetworkError;
  }

  // Dialog handling, called before any dialog is shown
  @Override
  protected void onPrepareDialog(int id, Dialog dialog) {
    super.onPrepareDialog(id, dialog);
    LogController.USER_ACTION_UI.logMessage("onPrepareDialog");

    if (id == AndroidConstants.DIALOG_GETEMAIL) {
      prepareDialogGetEmail();
    }

    if (id == AndroidConstants.DIALOG_RATE) {
      prepareDialogRateSet();
    }

    if (id == AndroidConstants.DIALOG_ALERTS) {
      prepareDialogAlerts();
    }
  }

  // TODO: Create Interface for Overridden Methods
  protected void prepareDialogRateSet() {
  }

  private void prepareDialogAlerts() {
    final FestivalEnum fest = _application.getFestival();
    final RadioButton radioNearNumbers = (RadioButton) dialogAlerts.findViewById(R.id.radioNearNumberfield);
    final RadioButton radioWithText = (RadioButton) dialogAlerts.findViewById(R.id.radioWithText);
    final EditText numberBox = (EditText) dialogAlerts.findViewById(R.id.numberBox);

    Alert existingAlert = null;
    try {
      existingAlert = _application.getAlertManager().getAlertForSet(fest, lastSetSelected,
          _application.getWeekToQuery());
    } catch (JSONException e) {
      LogController.ERROR.logMessage(e.getClass().getSimpleName() + " parsing selected set ID for modifying alerts");
      e.printStackTrace();
    }

    radioNearNumbers.setChecked(true);
    radioWithText.setChecked(false);

    // Still don't know how to keep the keyboard from popping up

    Integer alertReminderTime = AndroidConstants.ALERT_DEFAULT_REMINDERTIME;
    if (existingAlert != null) {
      this.dialogAlerts.setTitle("Edit Alert for This Set");
      // -> Prepare dialog 1) Edit current alert (minutes before set) 2)
      // Cancel existing alert
      radioWithText.setVisibility(View.VISIBLE);
      radioWithText.setText("Cancel this alert");
      // get the number of minutes value on this alert and populate the
      // number box
      alertReminderTime = existingAlert.getMinutesBeforeSet(); // Override
      // default
      // for
      // existing
      // alert
    } else {
      this.dialogAlerts.setTitle("This Set is Happening Later");
      radioWithText.setVisibility(View.INVISIBLE);
    }
    numberBox.setText(alertReminderTime + "");
    numberBox.selectAll(); // Easily allows user to overwrite
  }

  private void prepareDialogGetEmail() {
    EditText emailField = (EditText) dialogEmail.findViewById(R.id.textField_enterEmail);
    if (_application.getLoginData().emailAddress == null) {
      emailField.setText("");
    } else {
      emailField.setText(_application.getLoginData().emailAddress);
      emailField.selectAll();
      emailField.requestFocus();
    }
  }

  protected void handleClickDialogAlerts(View viewClicked) {
    final RadioButton radioNearNumbers = (RadioButton) dialogAlerts.findViewById(R.id.radioNearNumberfield);
    final RadioButton radioWithText = (RadioButton) dialogAlerts.findViewById(R.id.radioWithText);
    final EditText numberBox = (EditText) dialogAlerts.findViewById(R.id.numberBox);
    final int weekToQuery = _application.getWeekToQuery();
    final FestivalEnum fest = _application.getFestival();

    if (viewClicked.getId() == R.id.button_ok) {
      LogController.USER_ACTION_UI.logMessage("Alert Dialog - OK Clicked");

      if (radioNearNumbers.isChecked()) {
        // Create or update an alert
        try {
          Integer minutesBefore = Integer.parseInt(numberBox.getText().toString());
          _application.getAlertManager().addAlertForSet(fest, lastSetSelected, weekToQuery, minutesBefore);

        } catch (JSONException e) {
          LogController.ERROR.logMessage("FestivalActivity: Error adding/updating alert: " + e.getClass());
          e.printStackTrace();
        } catch (FileNotFoundException e) {
          LogController.ERROR.logMessage("FestivalActivity: Error adding/updating alert: " + e.getClass());
          e.printStackTrace();
        } catch (IOException e) {
          LogController.ERROR.logMessage("FestivalActivity: Error adding/updating alert: " + e.getClass());
          e.printStackTrace();
        }

      } else if (radioWithText.isChecked()) {
        // User intends to cancel alert
        try {
          _application.getAlertManager().removeAlertForSet(fest, lastSetSelected, weekToQuery);
        } catch (FileNotFoundException e) {
          LogController.ERROR.logMessage("FestivalActivity: Error cancelling alert: " + e.getClass());
          e.printStackTrace();
        } catch (IOException e) {
          LogController.ERROR.logMessage("FestivalActivity: Error cancelling alert: " + e.getClass());
          e.printStackTrace();
        } catch (JSONException e) {
          LogController.ERROR.logMessage("FestivalActivity: Error cancelling alert: " + e.getClass());
          e.printStackTrace();
        }
      }
      dialogAlerts.dismiss();

    } else if (viewClicked.getId() == R.id.button_cancel) {
      LogController.USER_ACTION_UI.logMessage("Alert Dialog - Cancel Clicked");
      dialogAlerts.dismiss();

    } else if (viewClicked.getId() == R.id.radioNearNumberfield) {
      LogController.USER_ACTION_UI.logMessage("Alert Dialog - Radio near number field Clicked");
      RadioButton otherButton = (RadioButton) dialogAlerts.findViewById(R.id.radioWithText);
      otherButton.setChecked(false);

    } else if (viewClicked.getId() == R.id.radioWithText) {
      LogController.USER_ACTION_UI.logMessage("Alert Dialog - Radio near textonly field Clicked");
      RadioButton otherButton = (RadioButton) dialogAlerts.findViewById(R.id.radioNearNumberfield);
      otherButton.setChecked(false);
    }
  }

  protected void clickDialogConfirmEmailButtonOK() {
    EditText emailField = (EditText) dialogEmail.findViewById(R.id.textField_enterEmail);
    String email = emailField.getText().toString();

    LogController.OTHER.logMessage("User provided email address: " + email);

    // if
    // (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    if (!FieldVerifier.isValidEmail(email)) {
      Toast invalidEmail = Toast.makeText(this, "Please enter your real email address.", 25);
      invalidEmail.show();

    } else { // Email is valid. Send ratings.
      if (!_application.saveData()) {
        showDialog(AndroidConstants.DIALOG_NETWORK_ERROR);
      }

      dialogEmail.dismiss();
      try {

        System.out.println("Requesting ratings email.");

        List<NameValuePair> parameterList = new ArrayList<NameValuePair>();

        LoginData loginData = _application.getLoginData();

        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, email));
        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TYPE, loginData.loginType + ""));
        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_ID, loginData.accountIdentifier));
        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TOKEN, loginData.accountToken));
        if (loginData.emailAddress != null) {
          parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, loginData.emailAddress));
        }
        String result = ServiceUtils.emailMyRatings(parameterList, this, HttpConstants.SERVER_URL_LOLLAPALOOZER);

      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  // TODO: Create Interface for Overridden Methods
  public void redrawUI() {
  }

  // TODO: Create Interface for Overridden Methods
  public void refreshData() {
  }

  // TODO: Create Interface for Overridden Methods
  protected void rateDialogSubmitRating() {
  }

  // TODO: Create Interface for Overridden Methods
  protected SocialNetworkPost _buildSocialNetworkPost() throws JSONException {
    return null;
  }

  // TODO: Create Interface for Overridden Methods
  protected boolean _lastRateDialogVerify() {
    return false;
  }
}