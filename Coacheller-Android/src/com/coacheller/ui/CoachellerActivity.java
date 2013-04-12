package com.coacheller.ui;

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
import com.coacheller.R;
import com.coacheller.data.CoachSetListAdapter;
import com.ratethisfest.android.AndroidConstants;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.CalendarUtils;
import com.ratethisfest.android.ServiceUtils;
import com.ratethisfest.android.auth.AuthActivityInt;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.CustomPair;
import com.ratethisfest.android.data.LoginData;
import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.HttpConstants;

/**
 * Main Coacheller Activity. Serves as view controller.
 * 
 */
public class CoachellerActivity extends Activity implements View.OnClickListener,
    OnItemSelectedListener, OnItemClickListener, OnCheckedChangeListener, AuthActivityInt {

  private static final int REFRESH_INTERVAL__SECONDS = 15;

  private String sortMode;
  private long _lastRefresh = 0;

  private Dialog rateDialog;
  private Dialog emailDialog;
  private Dialog networkErrorDialog;
  private Dialog firstUseDialog;

  private CoachSetListAdapter setListAdapter;
  // contains set id, stored in setListAdapter
  private JSONObject lastSetSelected;
  // contains actual rating, stored in userRatingsJAHM
  private JSONObject lastRating;
  // contains both week's scores
  private CustomPair<JSONObject, JSONObject> lastRatingScorePair = new CustomPair<JSONObject, JSONObject>(null, null);
  private HashMap<Integer, Integer> _selectedIdToValue = new HashMap<Integer, Integer>();
  private HashMap<String, Integer> _ratingSelectedValueToId = new HashMap<String, Integer>();

  // private int _ratingSelectedWeek;

  private Handler _networkIOHandler;
  private CoachellerApplication _appController;

  /** Called by Android Framework when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogController.LIFECYCLE_ACTIVITY.logMessage("CoachellerActivity Launched");
    _appController = (CoachellerApplication) getApplication();
    _appController.registerCoachellerActivity(CoachellerActivity.this);

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
    setListAdapter = new CoachSetListAdapter(this, AndroidConstants.JSON_KEY_SETS__TIME_ONE,
        AndroidConstants.JSON_KEY_SETS__STAGE_ONE, _appController.getUserRatingsJAHM());
    setListAdapter.setData(new JSONArray());

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.setAdapter(setListAdapter);
    viewSetsList.setOnItemClickListener(this);

    Button buttonSearchSets = (Button) this.findViewById(R.id.buttonChangeToSearchSets);
    buttonSearchSets.setOnClickListener(this);

    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    AndroidUtils.populateSpinnerWithArray(spinnerSortType, android.R.layout.simple_spinner_item, R.array.search_types,
        android.R.layout.simple_spinner_dropdown_item);
    spinnerSortType.setOnItemSelectedListener(this);

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
            _appController.doSubmitRating(lastRating);

            // Don't need since we are refreshing
            // ListView viewSetsList = (ListView)
            // findViewById(R.id.viewSetsList);
            // viewSetsList.invalidateViews();

            // If this is removed, uncomment ListView and invalidate above ^^^
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

    LogController.OTHER.logMessage("Searching year[" + year + "] week[" + week + "] day[" + day + "]");
    _appController.setYearToQuery(Integer.valueOf(year));
    _appController.setDayToQuery(day);
    _appController.setWeekToQuery(Integer.valueOf(week));
    refreshData();
  }

  /** Called by Android Framework when activity (re)gains foreground status */
  public void onResume() {
    super.onResume();
    LogController.LIFECYCLE_ACTIVITY.logMessage(this + " onResume");

    
    _appController.setLastAuthActivity(this);
    
    // TODO: We'll reenable this if we have something significant to say in the
    // beginning
    // if (_appController.isDataFirstUse()) {
    // showDialog(DIALOG_FIRST_USE);
    // } else {
    _showClickToRate();
    // }

    if ((System.currentTimeMillis() - _lastRefresh) / 1000 > REFRESH_INTERVAL__SECONDS) {
      refreshData(); // TODO multi-thread this
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
      emailDialog.dismiss();
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

    if (viewClicked.getId() == R.id.button_go_fb) {
      try {
        clickDialogSubmitRatingFacebook();
      } catch (JSONException e) {
        System.out.println("JSONException gathering data for Facebook post");
        e.printStackTrace();
      }
    }

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
      networkErrorDialog.dismiss();
    }

    if (viewClicked.getId() == R.id.button_network_error_ok) {
      networkErrorDialog.dismiss();
    }

  }

  // An item in the ListView of sets is clicked
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    JSONObject obj = (JSONObject) setListAdapter.getItem(position);
    lastSetSelected = obj;

    try {// TODO Hard coded strings means you are going to hell
      String setId = lastSetSelected.getString(AndroidConstants.JSON_KEY_SETS__SET_ID);
      JSONObject lastRatingWeek1 = _appController.getUserRatingsJAHM().getJSONObject(setId, "1");
      JSONObject lastRatingWeek2 = _appController.getUserRatingsJAHM().getJSONObject(setId, "2");

      // if (lastRatingWeek1 != null) {
      lastRatingScorePair.first = lastRatingWeek1;
      // .getString(AndroidConstants.JSON_KEY_RATINGS__RATING);
      // } else {
      // lastRatingScorePair.first = null;
      // }

      // if (lastRatingWeek2 != null) {
      lastRatingScorePair.second = lastRatingWeek2;
      // .getString(AndroidConstants.JSON_KEY_RATINGS__RATING);
      // } else {
      // lastRatingScorePair.second = null;
      // }

    } catch (JSONException e) {
      LogController.OTHER.logMessage("JSONException retrieving user's last rating");
      e.printStackTrace();
    }
    LogController.OTHER.logMessage("You Clicked On: " + obj + " previous ratings " + lastRatingScorePair.first + "/"
        + lastRatingScorePair.second);

    if (!_appController.getIsLoggedIn()) {
      _beginSigninProcess();
    } else {
      LogController.OTHER.logMessage(this.toString() + "About to launch Dialog!  _lastSetSelected was "
          + lastSetSelected);
      if (lastSetSelected == null) {
        LogController.OTHER.logMessage(this.toString() + " NULL MEMBER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      } else {
        LogController.OTHER.logMessage(this.toString() + " OK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
      showDialog(AndroidConstants.DIALOG_RATE);
    }
  }

  // An item was selected from the list of sets
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
      LogController.OTHER.logMessage("Menu button 'email me' pressed");

      if (!_appController.getIsLoggedIn()) {
        Toast.makeText(this, "Try rating at least one set first", 15).show();
      } else {
        try {
          showDialog(AndroidConstants.DIALOG_GETEMAIL);
        } catch (Exception e) {
          LogController.OTHER.logMessage("Error requesting ratings email");
          e.printStackTrace();
        }
      }
      return true;

    case R.id.menu_item_delete_email:
      LogController.OTHER.logMessage("Menu button 'delete email' pressed");
      _appController.clearLoginData();
      refreshData();
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
    RadioGroup scoreGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_score);
    EditText noteWidget = (EditText) rateDialog.findViewById(R.id.editText_commentsForSet);

    // Selection changed week 1<->2
    if (clickedGroup == rateDialog.findViewById(R.id.radio_pick_week)) {
      try {
        if (checkedId != R.id.radio_button_week1 && checkedId != R.id.radio_button_week2) {
          // Not sure what is selected, clear rating check
          scoreGroup.clearCheck();
          noteWidget.setText("");
        } else {
          JSONObject ratingToCheck = null;;
          if (checkedId == R.id.radio_button_week1) {
            ratingToCheck = lastRatingScorePair.first;
          } else if (checkedId == R.id.radio_button_week2) {
            ratingToCheck = lastRatingScorePair.second;
          }
          if (ratingToCheck != null) {
            int buttonIdToCheck = _ratingSelectedValueToId.get(ratingToCheck
                .getString(AndroidConstants.JSON_KEY_RATINGS__SCORE));
            RadioButton buttonToCheck = (RadioButton) rateDialog.findViewById(buttonIdToCheck);
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

  // Dialog handling, called once the first time this activity displays (a/each
  // type of)? dialog
  @Override
  protected Dialog onCreateDialog(int id) {
    if (id == AndroidConstants.DIALOG_FIRST_USE) {
      firstUseDialog = new Dialog(this);
      firstUseDialog.setContentView(R.layout.dialog_first_use);
      firstUseDialog.setTitle(AuthConstants.DIALOG_TITLE_FIRST_USE);

      Button buttonOK = (Button) firstUseDialog.findViewById(R.id.button_firstuse_ok);
      buttonOK.setOnClickListener(this);
      return firstUseDialog;
    }

    if (id == AndroidConstants.DIALOG_GETEMAIL) {
      emailDialog = new Dialog(this);
      emailDialog.setContentView(R.layout.get_email_address);
      emailDialog.setTitle(AuthConstants.DIALOG_TITLE_GET_EMAIL);

      Button buttonOK = (Button) emailDialog.findViewById(R.id.button_provideEmail);
      buttonOK.setOnClickListener(this);

      Button buttonCancel = (Button) emailDialog.findViewById(R.id.button_declineEmail);
      buttonCancel.setOnClickListener(this);

      return emailDialog;
    }

    if (id == AndroidConstants.DIALOG_RATE) {
      rateDialog = new Dialog(this);
      rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      rateDialog.setContentView(R.layout.dialog_rate_set);

      RadioGroup weekGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_week);
      weekGroup.setOnCheckedChangeListener(this);

      // Setup 'X' close widget
      ImageView close_dialog = (ImageView) rateDialog.findViewById(R.id.imageView_custom_dialog_close);
      close_dialog.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          rateDialog.dismiss();
        }
      });

      Button buttonRateAbove = (Button) rateDialog.findViewById(R.id.button_go_rate_above);
      buttonRateAbove.setOnClickListener(this);

      Button buttonRateInline = (Button) rateDialog.findViewById(R.id.button_go_rate_inline);
      buttonRateInline.setOnClickListener(this);

      ImageButton buttonFB = (ImageButton) rateDialog.findViewById(R.id.button_go_fb);
      buttonFB.setOnClickListener(this);

      ImageButton buttonTweet = (ImageButton) rateDialog.findViewById(R.id.button_go_tweet);
      buttonTweet.setOnClickListener(this);

      return rateDialog;
    }

    if (id == AndroidConstants.DIALOG_NETWORK_ERROR) {
      networkErrorDialog = new Dialog(this);
      networkErrorDialog.setContentView(R.layout.dialog_network_error);
      networkErrorDialog.setTitle("Network Error");

      Button buttonOK = (Button) networkErrorDialog.findViewById(R.id.button_network_error_ok);
      buttonOK.setOnClickListener(this);
      return networkErrorDialog;

    }

    return super.onCreateDialog(id);
  }

  // Dialog handling, called before any dialog is shown
  @Override
  protected void onPrepareDialog(int id, Dialog dialog) {
    super.onPrepareDialog(id, dialog);
    System.out.println("onPrepareDialog");

    // TODO: deprecate?
    if (id == AndroidConstants.DIALOG_GETEMAIL) {
      EditText emailField = (EditText) emailDialog.findViewById(R.id.textField_enterEmail);
      if (_appController.getLoginData().emailAddress == null) {
        emailField.setText("");
      } else {
        emailField.setText(_appController.getLoginData().emailAddress);
        emailField.selectAll();
        emailField.requestFocus();
      }
    }

    if (id == AndroidConstants.DIALOG_RATE) {

      // _lastRateDialog.setTitle("Rate this Set!");
      try {
        TextView subtitleText = (TextView) rateDialog.findViewById(R.id.text_rateBand_subtitle);
        subtitleText.setText(lastSetSelected.getString("artist")); // TODO NPE Here

      } catch (JSONException e) {
        LogController.OTHER.logMessage("JSONException assigning Artist name to Rating dialog");
        e.printStackTrace();
      }

      int week = CalendarUtils.whichWeekIsToday();
      RadioGroup weekGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_week);
      RadioButton buttonWeek1 = (RadioButton) rateDialog.findViewById(R.id.radio_button_week1);
      RadioButton buttonWeek2 = (RadioButton) rateDialog.findViewById(R.id.radio_button_week2);

      int idChanged = -1;

      if (week == 1) {
        buttonWeek1.setClickable(true);
        buttonWeek2.setClickable(false);
        buttonWeek1.setChecked(true);
        idChanged = buttonWeek1.getId();

      } else if (week == 2) {

        buttonWeek1.setClickable(true);
        buttonWeek2.setClickable(true);
        buttonWeek2.setChecked(true);
        idChanged = buttonWeek2.getId();

      } else {
        // Don't suggest a week
        weekGroup.clearCheck();
      }

      // TODO pick user's last rating
      onCheckedChanged(weekGroup, idChanged);

      if (_appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
        ImageButton buttonFB = (ImageButton) rateDialog.findViewById(R.id.button_go_fb);
        buttonFB.setImageResource(R.drawable.post_facebook_large);
        System.out.println(buttonFB.getPaddingTop() + " " + buttonFB.getPaddingLeft() + " "
            + buttonFB.getPaddingBottom() + " " + buttonFB.getPaddingRight());
        buttonFB.setPadding(7, 3, 7, 10);

      }

      if (_appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
        ImageButton buttonTweet = (ImageButton) rateDialog.findViewById(R.id.button_go_tweet);
        buttonTweet.setImageResource(R.drawable.post_twitter_large);
        buttonTweet.setPadding(7, 3, 7, 10);
      }

      if (_appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)
          && _appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {

        Button buttonRateAbove = (Button) rateDialog.findViewById(R.id.button_go_rate_above);
        buttonRateAbove.setVisibility(View.VISIBLE);

        Button buttonRateInline = (Button) rateDialog.findViewById(R.id.button_go_rate_inline);
        buttonRateInline.setVisibility(View.GONE);
      }

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
        _appController.processLoginData(results);
        break;

      case AuthConstants.INTENT_FACEBOOK_LOGIN: {
        // Assuming it is Facebook
        LogController.LIFECYCLE_ACTIVITY.logMessage("onActivityResult called by Facebook API");
        // Required by Facebook API
        _appController.getAuthModel().getFacebookObject().authorizeCallback(requestCode, resultCode, data);
        break;
      }

      case AuthConstants.INTENT_TWITTER_LOGIN: {
        // Assuming it is Facebook
        LogController.LIFECYCLE_ACTIVITY.logMessage("onActivityResult called by Twitter API");
        _appController.getAuthModel().twitterAuthCallback(requestCode, resultCode, data);
        break;
      }

      default:
        _appController.showErrorDialog("Unexpected Response",
            "An unexpected response was received from another window", infoMessage);

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

  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);// must store the new intent unless getIntent() will
                      // return the old one
    checkForExtraData();
  }

  protected void redrawUI() {
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

    if (!_appController.saveData()) {
      showDialog(AndroidConstants.DIALOG_NETWORK_ERROR);
    }
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

  public void refreshData() {
    if (_appController.getWeekToQuery() == 1) {
      setListAdapter.setTimeFieldName(AndroidConstants.JSON_KEY_SETS__TIME_ONE);
      setListAdapter.setStageFieldName(AndroidConstants.JSON_KEY_SETS__STAGE_ONE);
    } else if (_appController.getWeekToQuery() == 2) {
      setListAdapter.setTimeFieldName(AndroidConstants.JSON_KEY_SETS__TIME_TWO);
      setListAdapter.setStageFieldName(AndroidConstants.JSON_KEY_SETS__STAGE_TWO);
    }

    TextView titleView = (TextView) this.findViewById(R.id.text_set_list_title);
    // TODO: add year
    titleView.setText(_appController.getYearToQuery() + " - " + _appController.getDayToQuery() + ", Weekend "
        + _appController.getWeekToQuery());
    // +" "+ weekString);

    _appController.refreshDataFromStorage();

    launchGetDataThread(); // TODO multithread this
  }

  // TODO: deprecate?
  private void clickDialogConfirmEmailButtonOK() {
    EditText emailField = (EditText) emailDialog.findViewById(R.id.textField_enterEmail);
    String email = emailField.getText().toString();

    LogController.OTHER.logMessage("User provided email address: " + email);

    // if
    // (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    if (!FieldVerifier.isValidEmail(email)) {
      Toast invalidEmail = Toast.makeText(this, "Please enter your real email address.", 25);
      invalidEmail.show();

    } else { // Email is valid. Save email and email ratings
      _appController.setLoginEmail(email);

      if (!_appController.saveData()) {
        showDialog(AndroidConstants.DIALOG_NETWORK_ERROR);
      }

      emailDialog.dismiss();
      try {

        System.out.println("Requesting ratings email.");

        List<NameValuePair> parameterList = new ArrayList<NameValuePair>();

        LoginData loginData = _appController.getLoginData();

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
    _appController.setDataFirstUse(false);
    // TODO _storageManager.save();
    firstUseDialog.dismiss();
    _showClickToRate(); // display 'tap set to rate it' toast
  }

  private void clickDialogSubmitRatingButtonOK() {
    // incomplete
    if (!_lastRateDialogVerify()) {
      return;
    }

    rateDialogSubmitRating();
    rateDialog.dismiss();
  }

  private void clickDialogSubmitRatingTwitter() throws JSONException {
    System.out.println("Clicked post on Twitter");
    if (!_lastRateDialogVerify()) {
      return;
    }
    rateDialogSubmitRating();
    
    _appController.getAuthModel().ensurePermission(AuthModel.PERMISSION_TWITTER_TWEET);

    _queuedTwitterPost = _buildSocialNetworkPost();

    // https://api.twitter.com/1/statuses
    if (_appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      System.out.println("Twitter Auth available now, posting immediately");
      doTwitterPost();
    } else {
      System.out.println("Twitter Auth not ready, posting later");
    }

    rateDialog.dismiss();
  }

  private void clickDialogSubmitRatingFacebook() throws JSONException {
    System.out.println("Clicked post on FB");
    if (!_lastRateDialogVerify()) {
      return;
    }
    rateDialogSubmitRating();
    _appController.getAuthModel().ensurePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL);

    _queuedFacebookPost = _buildSocialNetworkPost();
    if (_appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
      System.out.println("FB Auth available now, posting immediately");
      doFacebookPost();
    } else {
      System.out.println("FB Auth not ready, posting later");
    }

    rateDialog.dismiss();
  }

  private SocialNetworkPost _buildSocialNetworkPost() throws JSONException {
    SocialNetworkPost post = new SocialNetworkPost();
    // Build data from dialog here
    // TODO this is in the code in 2 places
    RadioGroup scoreGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_score);
    String submittedRating = _selectedIdToValue.get(scoreGroup.getCheckedRadioButtonId()).toString();

    EditText noteWidget = (EditText) rateDialog.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();
    String artistName = lastSetSelected.getString("artist");

    post.rating = submittedRating;
    post.note = submittedNote;
    post.artistName = artistName;
    return post;
  }

  private boolean _lastRateDialogVerify() {
    RadioGroup scoreGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_score);
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
    //RadioGroup weekGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_week);
    int weekSelectedId = ((RadioGroup) rateDialog.findViewById(R.id.radio_pick_week)).getCheckedRadioButtonId();

    RadioGroup scoreGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_score);
    int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();
    String submittedRating = _selectedIdToValue.get(scoreSelectedId).toString();

    EditText noteWidget = (EditText) rateDialog.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();

    rateDialog.dismiss();

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
      public void run() {
        try {
          setListAdapter.setData(_appController.getDataFromServer());
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
      public void run() {
        try {
          setListAdapter.setData(_appController.getDataFromServer());
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

  @Override
  public synchronized void doTwitterPost() {
    if (_queuedTwitterPost == null) {
      return;
    }

    if (!_appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      System.out.println("Warning: trying to post without Twitter permissions");
    }

    // use _queuedFacebookPost
    String result = _appController.getAuthModel().tweetToTwitter(_queuedTwitterPost);
    System.out.println("Twitter result:");
    System.out.println(result);
    _queuedTwitterPost = null;
  }

  private SocialNetworkPost _queuedFacebookPost;

  @Override
  public synchronized void doFacebookPost() {
    if (_queuedFacebookPost == null) {
      return;
    }

    if (!_appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
      System.out.println("Warning: trying to post without facebook permissions");
    }

    // use _queuedFacebookPost
    String result = _appController.getAuthModel().postToFacebookWall(_queuedFacebookPost);
    System.out.println("Facebook result:");
    System.out.println(result);
    _queuedFacebookPost = null;
  }

  @Override
  public void modelChanged() {
  }

  @Override
  public Activity getLastActivity() {
    return this;
  }

}