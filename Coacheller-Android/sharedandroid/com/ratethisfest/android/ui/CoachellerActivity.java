package com.ratethisfest.android.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coacheller.CoachellerApplication;
import com.lollapaloozer.R;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.ServiceUtils;
import com.ratethisfest.android.alert.Alert;
import com.ratethisfest.android.alert.AlertsActivity;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.CoachSetListAdapter;
import com.ratethisfest.android.data.CustomPair;
import com.ratethisfest.android.data.LoginData;
import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.data.AndroidConstants;
import com.ratethisfest.data.CalendarUtils;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.FestivalEnum;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.HttpConstants;

/**
 * Main Coacheller Activity. Serves as view controller.
 * 
 */
public class CoachellerActivity extends Activity implements View.OnClickListener, OnItemSelectedListener,
    OnItemClickListener, OnCheckedChangeListener {

  private static final int REFRESH_INTERVAL__SECONDS = 15;

  private String sortMode;
  private long _lastRefresh = 0;

  private Dialog dialogRate;
  private Dialog dialogEmail;
  private Dialog dialogAlerts;
  private Dialog dialogNetworkError;
  private Dialog dialogFirstUse;

  private CoachSetListAdapter setListAdapter;
  // contains set id, stored in setListAdapter
  private JSONObject lastSetSelected;
  // contains actual rating, stored in userRatingsJAHM
  private JSONObject lastRating;
  // contains both week's scores
  private CustomPair<JSONObject, JSONObject> lastRatingPair = new CustomPair<JSONObject, JSONObject>(null, null);
  private HashMap<Integer, Integer> _selectedIdToValue = new HashMap<Integer, Integer>();
  private HashMap<String, Integer> _ratingSelectedValueToId = new HashMap<String, Integer>();

  // private int _ratingSelectedWeek;

  private Handler _networkIOHandler;
  private CoachellerApplication _application;

  /** Called by Android Framework when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogController.LIFECYCLE_ACTIVITY.logMessage("CoachellerActivity Launched: " + this);
    _application = (CoachellerApplication) getApplication();
    _application.registerCoachellerActivity(CoachellerActivity.this);

    sortMode = AndroidConstants.SORT_TIME;

    _selectedIdToValue.put(R.id.radio_button_week1, 1);
    _selectedIdToValue.put(R.id.radio_button_week2, 2);

    _selectedIdToValue.put(R.id.radio_button_score1, 1);
    _selectedIdToValue.put(R.id.radio_button_score2, 2);
    _selectedIdToValue.put(R.id.radio_button_score3, 3);
    _selectedIdToValue.put(R.id.radio_button_score4, 4);
    _selectedIdToValue.put(R.id.radio_button_score5, 5);

    _ratingSelectedValueToId.put("1", R.id.radio_button_score1);
    _ratingSelectedValueToId.put("2", R.id.radio_button_score2);
    _ratingSelectedValueToId.put("3", R.id.radio_button_score3);
    _ratingSelectedValueToId.put("4", R.id.radio_button_score4);
    _ratingSelectedValueToId.put("5", R.id.radio_button_score5);

    setContentView(R.layout.sets_list);
    setListAdapter = new CoachSetListAdapter(this, _application, AndroidConstants.JSON_KEY_SETS__TIME_ONE,
        AndroidConstants.JSON_KEY_SETS__STAGE_ONE, _application.getUserRatingsJAHM());
    setListAdapter.setData(new JSONArray());

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.setAdapter(setListAdapter);
    viewSetsList.setOnItemClickListener(this);

    Button buttonSearchSets = (Button) this.findViewById(R.id.buttonChangeToSearchSets);
    buttonSearchSets.setOnClickListener(this);

    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    String[] searchTypeStringArray = _application.getResources().getStringArray(R.array.search_types);
    AndroidUtils.populateSpinnerWithArray(spinnerSortType, android.R.layout.simple_spinner_item, searchTypeStringArray,
        android.R.layout.simple_spinner_dropdown_item);
    spinnerSortType.setOnItemSelectedListener(this);

    try {
      _application.getAlertManager().loadAlerts();
    } catch (StreamCorruptedException e) {
      _application.getAlertManager().exceptionLoadingAlerts(e);
    } catch (FileNotFoundException e) {
      _application.getAlertManager().exceptionLoadingAlerts(e);
    } catch (IOException e) {
      _application.getAlertManager().exceptionLoadingAlerts(e);
    } catch (ClassNotFoundException e) {
      _application.getAlertManager().exceptionLoadingAlerts(e);
    }

    // Above here is stuff to be done once

    _networkIOHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {

        super.handleMessage(msg);

        if (msg.what == AndroidConstants.THREAD_UPDATE_UI) {
          LogController.LIFECYCLE_THREAD.logMessage("Executing update UI thread callback ");
          redrawUI();
        }

        if (msg.what == AndroidConstants.THREAD_SUBMIT_RATING) {
          LogController.LIFECYCLE_THREAD.logMessage("Executing submit rating thread callback ");

          try {
            _application.doSubmitRating(lastRating);

            // Don't need since we are refreshing
            // ListView viewSetsList = (ListView)
            // findViewById(R.id.viewSetsList);
            // viewSetsList.invalidateViews();

            // If this is removed, uncomment ListView and invalidate
            // above ^^^
            refreshData();

          } catch (JSONException je) {
            je.printStackTrace();
          } catch (Exception e) {
            showDialog(AndroidConstants.DIALOG_NETWORK_ERROR);
          }
        }
      }

    };

    checkForExtraData();
  }

  @Deprecated
  private void checkForExtraData() {
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();

    if (bundle == null) {
      return;
    }

    String year = intent.getExtras().getString(SearchSetsActivity.YEAR);
    String week = intent.getExtras().getString(SearchSetsActivity.WEEK);
    String day = intent.getExtras().getString(SearchSetsActivity.DAY);

    LogController.OTHER.logMessage("CoachellerActivity setting search suggestion of year[" + year + "] week[" + week
        + "] day[" + day + "]");
    _application.setYearToQuery(Integer.valueOf(year));
    _application.setDayToQuery(day);
    _application.setWeekToQuery(Integer.valueOf(week));
    refreshData();
  }

  /** Called by Android Framework when activity (re)gains foreground status */
  @Override
  public void onResume() {
    super.onResume();
    LogController.LIFECYCLE_ACTIVITY.logMessage(this + " onResume");
    _application.setLastActivity(this);
    _application.getAuthModel().setLastAuthRelatedActivity(this);

    // TODO: We'll reenable this if we have something significant to say in the beginning
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
  // ImmutableMap<Integer, String> rowToFestName = festTable.column(FestData.FEST_NAME);
  // Predicate<String> equalsCoachella = Predicates.equalTo("Coachella");
  // Map<Integer, String> filteredValues = Maps.filterValues(rowToFestName, equalsCoachella);
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
  // Map<Integer, Map<String, String>> resultRows = FestData.searchForRows(FestData.FEST_NAME, "Lollapalooza");
  // for (Integer key: resultRows.keySet()) {
  // System.out.println(key +":"+ resultRows.get(key));
  // }

  // HashMap<String, String> criteria = new HashMap<String, String>();
  // criteria.put(FestData.FEST_WEEK, "1"); //Any day in week 1 of any fest
  // criteria.put(FestData.FEST_YEAR, "2013"); //Restrict to days in 2013
  // criteria.put(FestData.FEST_DAYOFMONTH, "3"); //Restrict to days on the 3rd of any month
  //
  // Map<Integer, Map<String, String>> rowsMatchingAll = FestData.rowsMatchingAll(criteria);
  // System.out.println("Search Results:");
  // for (Integer key : rowsMatchingAll.keySet()) {
  // System.out.println(key + ":" + rowsMatchingAll.get(key));
  // }
  // }

  // An item in the ListView of sets is clicked
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    this.lastSetSelected = (JSONObject) setListAdapter.getItem(position);
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
      LogController.ERROR.logMessage("CoachellerActivity - JSONException calling CalendarUtils.isSetInTheFuture");
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
      _beginSigninProcess(); // Get the user logged in, cannot rate any sets right now

    } else {
      // Set not in the future, user is logged in, go ahead and rate something
      if (lastSetSelected == null) {
        LogController.ERROR.logMessage(this.toString() + " UNEXPECTED: lastSetSelected is null");
      }

      LogController.LIFECYCLE_ACTIVITY.logMessage(this.toString() + " About to launch Dialog!");
      showDialog(AndroidConstants.DIALOG_RATE);
    }
  }

  // Spinner drop-down selection was made
  @Override
  public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {

    // TODO Auto-generated method stub
    LogController.USER_ACTION_UI.logMessage("Search Type Spinner: Selected -> " + parent.getSelectedItem() + "(" + arg2
        + ")");
    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);

    try {
      sortMode = parent.getSelectedItem().toString().toLowerCase();
      setListAdapter.resortSetList(sortMode);
      viewSetsList.invalidateViews();
    } catch (JSONException e) {
      LogController.OTHER.logMessage("JSONException re-sorting data");
      e.printStackTrace();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.menu_item_email_me:
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

    case R.id.menu_item_delete_email:
      LogController.USER_ACTION_UI.logMessage("Menu button 'delete email' pressed");
      _application.clearLoginData();
      refreshData();
      return true;

    case R.id.menu_item_manage_alerts:
      LogController.USER_ACTION_UI.logMessage("Menu button 'manage alerts' pressed");
      Intent intent = new Intent();
      intent.setClass(this, AlertsActivity.class);
      startActivity(intent);
      return true;

    default:
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
    LogController.USER_ACTION_UI.logMessage("Search Type Spinner: Nothing Selected");
    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    spinnerSortType.setSelection(0);
  }

  @Override
  public void onCheckedChanged(RadioGroup clickedGroup, int checkedId) {
    // todo should use switch
    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    EditText noteWidget = (EditText) dialogRate.findViewById(R.id.editText_commentsForSet);

    // Selection changed week 1<->2
    if (clickedGroup == dialogRate.findViewById(R.id.radio_pick_week)) {
      try {
        if (checkedId != R.id.radio_button_week1 && checkedId != R.id.radio_button_week2) {
          // Not sure what is selected, clear rating check
          scoreGroup.clearCheck();
          noteWidget.setText("");
        } else {
          JSONObject ratingToCheck = null;
          ;
          if (checkedId == R.id.radio_button_week1) {
            ratingToCheck = lastRatingPair.first;
          } else if (checkedId == R.id.radio_button_week2) {
            ratingToCheck = lastRatingPair.second;
          }
          if (ratingToCheck != null) {
            int buttonIdToCheck = _ratingSelectedValueToId.get(ratingToCheck
                .getString(AndroidConstants.JSON_KEY_RATINGS__SCORE));
            RadioButton buttonToCheck = (RadioButton) dialogRate.findViewById(buttonIdToCheck);
            buttonToCheck.setChecked(true);
            noteWidget.setText(ratingToCheck.getString(AndroidConstants.JSON_KEY_RATINGS__NOTES));
          } else {
            scoreGroup.clearCheck();
            noteWidget.setText("");
          }

        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      scoreGroup.invalidate();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    String infoMessage = "CoachellerActivity.onActivityResult req=" + requestCode + " resultCode=" + resultCode
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
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_global_options, menu);
    return true;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    LogController.LIFECYCLE_ACTIVITY.logMessage("CoachellerActivity.onNewIntent() called");
    setIntent(intent);// must store the new intent unless getIntent() will return the old one
    checkForExtraData();
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

  private Dialog createDialogRate() {
    dialogRate = new Dialog(this);
    dialogRate.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialogRate.setContentView(R.layout.dialog_rate_set);

    RadioGroup weekGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_week);
    weekGroup.setOnCheckedChangeListener(this);

    // Setup 'X' close widget
    ImageView close_dialog = (ImageView) dialogRate.findViewById(R.id.imageView_custom_dialog_close);
    close_dialog.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        dialogRate.dismiss();
      }
    });

    Button buttonRateAbove = (Button) dialogRate.findViewById(R.id.button_go_rate_above);
    buttonRateAbove.setOnClickListener(this);

    Button buttonRateInline = (Button) dialogRate.findViewById(R.id.button_go_rate_inline);
    buttonRateInline.setOnClickListener(this);

    ImageButton buttonFB = (ImageButton) dialogRate.findViewById(R.id.button_go_fb);
    buttonFB.setOnClickListener(this);

    ImageButton buttonTweet = (ImageButton) dialogRate.findViewById(R.id.button_go_tweet);
    buttonTweet.setOnClickListener(this);

    return dialogRate;
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
      // -> Prepare dialog 1) Edit current alert (minutes before set) 2) Cancel existing alert
      radioWithText.setVisibility(View.VISIBLE);
      radioWithText.setText("Cancel this alert");
      // get the number of minutes value on this alert and populate the number box
      alertReminderTime = existingAlert.getMinutesBeforeSet(); // Override default for existing alert
    } else {
      this.dialogAlerts.setTitle("This Set is Happening Later");
      radioWithText.setVisibility(View.INVISIBLE);
    }
    numberBox.setText(alertReminderTime + "");
    numberBox.selectAll(); // Easily allows user to overwrite
  }

  private void prepareDialogRateSet() {
    // _lastRateDialog.setTitle("Rate this Set!");
    try {
      TextView subtitleText = (TextView) dialogRate.findViewById(R.id.text_rateBand_subtitle);
      subtitleText.setText(lastSetSelected.getString("artist")); // TODO
      // NPE
      // Here

    } catch (JSONException e) {
      LogController.OTHER.logMessage("JSONException assigning Artist name to Rating dialog");
      e.printStackTrace();
    }

    int weeksOver = CalendarUtils.getlastFestWeekExpired(_application.getFestival());

    RadioGroup weekGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_week);
    RadioButton buttonWeek1 = (RadioButton) dialogRate.findViewById(R.id.radio_button_week1);
    RadioButton buttonWeek2 = (RadioButton) dialogRate.findViewById(R.id.radio_button_week2);

    int idChanged = -1;

    if (weeksOver == 0) { // Still in week 1, disable rating week 2
      buttonWeek1.setClickable(true);
      buttonWeek2.setClickable(false);
      buttonWeek1.setChecked(true);
      idChanged = buttonWeek1.getId();

    } else if (weeksOver <= 1) { // Week 2 or later, enable rating week 2

      buttonWeek1.setClickable(true);
      buttonWeek2.setClickable(true);
      buttonWeek2.setChecked(true);
      idChanged = buttonWeek2.getId();

    } else {
      // Don't suggest a week
      weekGroup.clearCheck();
    }

    int numWeeks = CalendarUtils.getFestivalMaxNumberOfWeeks(_application.getFestival());
    if (numWeeks == 1) {
      weekGroup.setVisibility(View.INVISIBLE);
      TextView selectWeekText = (TextView) dialogRate.findViewById(R.id.layout_radio_choice_minutes_textline_text);
      selectWeekText.setVisibility(View.GONE);
    }

    // TODO pick user's last rating
    onCheckedChanged(weekGroup, idChanged);

    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
      ImageButton buttonFB = (ImageButton) dialogRate.findViewById(R.id.button_go_fb);
      buttonFB.setImageResource(R.drawable.post_facebook_large);
      System.out.println(buttonFB.getPaddingTop() + " " + buttonFB.getPaddingLeft() + " " + buttonFB.getPaddingBottom()
          + " " + buttonFB.getPaddingRight());
      buttonFB.setPadding(7, 3, 7, 10);

    }

    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      ImageButton buttonTweet = (ImageButton) dialogRate.findViewById(R.id.button_go_tweet);
      buttonTweet.setImageResource(R.drawable.post_twitter_large);
      buttonTweet.setPadding(7, 3, 7, 10);
    }

    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)
        && _application.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {

      Button buttonRateAbove = (Button) dialogRate.findViewById(R.id.button_go_rate_above);
      buttonRateAbove.setVisibility(View.VISIBLE);

      Button buttonRateInline = (Button) dialogRate.findViewById(R.id.button_go_rate_inline);
      buttonRateInline.setVisibility(View.GONE);
    }
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

  // Any button in any view or dialog was clicked
  @Override
  public void onClick(View viewClicked) {

    // OK clicked on first use dialog
    if (viewClicked.getId() == R.id.button_firstuse_ok) {
      clickDialogFirstUseButtonOK();
    }

    // "OK" clicked to submit email address
    if (viewClicked.getId() == R.id.button_provideEmail) {
      clickDialogConfirmEmailButtonOK();
    }

    if (viewClicked.getId() == R.id.button_declineEmail) {
      dialogEmail.dismiss();
    }

    if (viewClicked.getId() == R.id.buttonChangeToSearchSets) {
      System.out.println("Button: Switch Day");
      Intent intent = new Intent();
      intent.setClass(this, SearchSetsActivity.class);
      startActivity(intent);
    }

    // Submit rating for a set
    if (viewClicked.getId() == R.id.button_go_rate_inline) { // Selections
      clickDialogSubmitRatingButtonOK();
    } // End rating dialog submitted

    // Submit rating for a set
    if (viewClicked.getId() == R.id.button_go_rate_above) { // Selections
      clickDialogSubmitRatingButtonOK();
    } // End rating dialog submitted

    // Submit rating for a set and do Facebook post
    if (viewClicked.getId() == R.id.button_go_fb) {
      try {
        clickDialogSubmitRatingFacebook();
      } catch (JSONException e) {
        System.out.println("JSONException gathering data for Facebook post");
        e.printStackTrace();
      }
    }

    // Submit rating for a set and do Twitter post
    if (viewClicked.getId() == R.id.button_go_tweet) {
      try {
        clickDialogSubmitRatingTwitter();
      } catch (JSONException e) {
        System.out.println("JSONException gathering data for Twitter post");
        e.printStackTrace();
      }
    }

    if (viewClicked.getId() == R.id.button_network_error_ok) {
      System.out.println("Clicked dismiss network error dialog");
      dialogNetworkError.dismiss();
    }

    if (dialogAlerts != null && dialogAlerts.isShowing()) {
      // This should work instead of having to give a globally unique ID to each button
      handleClickDialogAlerts(viewClicked);
    }
  }

  private void handleClickDialogAlerts(View viewClicked) {
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
          LogController.ERROR.logMessage("CoachellerActivity: Error adding/updating alert: " + e.getClass());
          e.printStackTrace();
        } catch (FileNotFoundException e) {
          LogController.ERROR.logMessage("CoachellerActivity: Error adding/updating alert: " + e.getClass());
          e.printStackTrace();
        } catch (IOException e) {
          LogController.ERROR.logMessage("CoachellerActivity: Error adding/updating alert: " + e.getClass());
          e.printStackTrace();
        }

      } else if (radioWithText.isChecked()) {
        // User intends to cancel alert
        try {
          _application.getAlertManager().removeAlertForSet(fest, lastSetSelected, weekToQuery);
        } catch (FileNotFoundException e) {
          LogController.ERROR.logMessage("CoachellerActivity: Error cancelling alert: " + e.getClass());
          e.printStackTrace();
        } catch (IOException e) {
          LogController.ERROR.logMessage("CoachellerActivity: Error cancelling alert: " + e.getClass());
          e.printStackTrace();
        } catch (JSONException e) {
          LogController.ERROR.logMessage("CoachellerActivity: Error cancelling alert: " + e.getClass());
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

  // TODO: deprecate?
  private void clickDialogConfirmEmailButtonOK() {
    EditText emailField = (EditText) dialogEmail.findViewById(R.id.textField_enterEmail);
    String email = emailField.getText().toString();

    LogController.OTHER.logMessage("User provided email address: " + email);

    // if
    // (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    if (!FieldVerifier.isValidEmail(email)) {
      Toast invalidEmail = Toast.makeText(this, "Please enter your real email address.", 25);
      invalidEmail.show();

    } else { // Email is valid. Save email and email ratings
      _application.setLoginEmail(email);

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

  private void clickDialogFirstUseButtonOK() {
    _application.setDataFirstUse(false);
    // TODO _storageManager.save();
    dialogFirstUse.dismiss();
    _showClickToRate(); // display 'tap set to rate it' toast
  }

  private void clickDialogSubmitRatingButtonOK() {
    // incomplete
    if (!_lastRateDialogVerify()) {
      return;
    }

    rateDialogSubmitRating();
    dialogRate.dismiss();
  }

  private void clickDialogSubmitRatingTwitter() throws JSONException {
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

  private void clickDialogSubmitRatingFacebook() throws JSONException {
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

  public void redrawUI() {
    try {
      setListAdapter.resortSetList(sortMode);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.invalidateViews();

    LogController.OTHER.logMessage("Data Refresh is complete");
    _lastRefresh = System.currentTimeMillis();

    if (!_application.saveData()) {
      showDialog(AndroidConstants.DIALOG_NETWORK_ERROR);
    }
  }

  public void refreshData() {
    if (_application.getWeekToQuery() == 1) {
      setListAdapter.setTimeFieldName(AndroidConstants.JSON_KEY_SETS__TIME_ONE);
      setListAdapter.setStageFieldName(AndroidConstants.JSON_KEY_SETS__STAGE_ONE);
    } else if (_application.getWeekToQuery() == 2) {
      setListAdapter.setTimeFieldName(AndroidConstants.JSON_KEY_SETS__TIME_TWO);
      setListAdapter.setStageFieldName(AndroidConstants.JSON_KEY_SETS__STAGE_TWO);
    }

    TextView titleView = (TextView) this.findViewById(R.id.text_set_list_title);
    String titleString = _application.getFestival().getName() + " " + _application.getYearToQuery() + " - "
        + _application.getDayToQuery();
    if (CalendarUtils.getFestivalMaxNumberOfWeeks(_application.getFestival()) > 1) {
      titleString += ", Weekend " + _application.getWeekToQuery();
    }
    titleView.setText(titleString);

    _application.refreshDataFromStorage();

    launchGetDataThread(); // TODO multithread this
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

    Intent lollapaloozerAuthIntent = new Intent(this, ChooseLoginActivity.class);
    startActivityForResult(lollapaloozerAuthIntent, AuthConstants.INTENT_CHOOSE_LOGIN_TYPE);
  }

  private SocialNetworkPost _buildSocialNetworkPost() throws JSONException {
    SocialNetworkPost post = new SocialNetworkPost();
    // Build data from dialog here
    // TODO this is in the code in 2 places
    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    String submittedRating = _selectedIdToValue.get(scoreGroup.getCheckedRadioButtonId()).toString();

    EditText noteWidget = (EditText) dialogRate.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();
    String artistName = lastSetSelected.getString("artist");

    post.rating = submittedRating;
    post.note = submittedNote;
    post.artistName = artistName;
    return post;
  }

  private boolean _lastRateDialogVerify() {
    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();
    if (scoreSelectedId == -1) {
      Toast selectEverything = Toast.makeText(this, "Please select a rating for this Set", 25);
      selectEverything.show();
      return false;
    }
    return true;
  }

  private void rateDialogSubmitRating() {
    // TODO this is in the code in 2 places
    // RadioGroup weekGroup = (RadioGroup)
    // rateDialog.findViewById(R.id.radio_pick_week);
    int weekSelectedId = ((RadioGroup) dialogRate.findViewById(R.id.radio_pick_week)).getCheckedRadioButtonId();

    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();
    String submittedRating = _selectedIdToValue.get(scoreSelectedId).toString();

    EditText noteWidget = (EditText) dialogRate.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();

    dialogRate.dismiss();

    String weekNumber = _selectedIdToValue.get(weekSelectedId) + "";

    LogController.OTHER.logMessage("Selected Week[" + weekNumber + "] Score[" + submittedRating + "] WeekId["
        + weekSelectedId + "] ScoreId[" + scoreSelectedId + "]");
    try {

      lastRating = new JSONObject();

      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
          lastSetSelected.get(AndroidConstants.JSON_KEY_SETS__SET_ID));
      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__WEEK, weekNumber);
      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__SCORE, submittedRating);
      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__NOTES, submittedNote);

      LogController.OTHER.logMessage("State of last rating object before launching submit thread: "
          + lastRating.toString());
      launchSubmitRatingThread();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void launchGetDataThread() {
    LogController.LIFECYCLE_THREAD.logMessage("Launching getData thread");

    new Thread() {
      @Override
      public void run() {
        try {
          setListAdapter.setData(_application.getDataFromServer());
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        _networkIOHandler.sendEmptyMessage(AndroidConstants.THREAD_UPDATE_UI);
      }
    }.start();
    LogController.LIFECYCLE_THREAD.logMessage("getData thread launched");
  }

  private void launchSubmitRatingThread() {
    LogController.LIFECYCLE_THREAD.logMessage("Launching Submit Rating thread");

    new Thread() {
      @Override
      public void run() {
        try {
          setListAdapter.setData(_application.getDataFromServer());
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

  private SocialNetworkPost _queuedTwitterPost;

  private SocialNetworkPost _queuedFacebookPost;

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

}