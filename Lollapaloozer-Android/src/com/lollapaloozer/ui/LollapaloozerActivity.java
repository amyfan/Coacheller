package com.lollapaloozer.ui;

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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lollapaloozer.LollapaloozerApplication;
import com.lollapaloozer.R;
import com.lollapaloozer.data.CustomSetListAdapter;
import com.ratethisfest.android.AndroidConstants;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.ServiceUtils;
import com.ratethisfest.android.auth.AuthActivityInt;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.JSONArraySortMap;
import com.ratethisfest.android.data.LoginData;
import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.HttpConstants;

/**
 * Main Lollapaloozer Activity
 * 
 */
public class LollapaloozerActivity extends Activity implements View.OnClickListener,
    OnItemSelectedListener, OnItemClickListener, OnCheckedChangeListener, AuthActivityInt {

  private static final int SORT_TIME = 1;
  private static final int SORT_ARTIST = 2;

  private static final int REFRESH_INTERVAL__SECONDS = 15;

  private int _sortMode;
  private long _lastRefresh = 0;

  private Dialog rateDialog;
  private Dialog emailDialog;
  private Dialog networkErrorDialog;
  private Dialog firstUseDialog;

  private CustomSetListAdapter setListAdapter;
  // contains set id, stored in setListAdapter
  private JSONObject lastSetSelected;
  // contains actual rating, stored in userRatingsJAHM
  private JSONObject lastRating;

  private HashMap<Integer, Integer> _ratingSelectedIdToValue = new HashMap<Integer, Integer>();
  private HashMap<String, Integer> _ratingSelectedScoreToId = new HashMap<String, Integer>();

  private Handler networkIOHandler;
  private LollapaloozerApplication appController;

  /** Called by Android Framework when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    System.out.println(this + " onCreate");

    LollapaloozerApplication.debug(this, "LollapaloozerActivity Launched");
    appController = (LollapaloozerApplication) getApplication();
    appController.registerLollapaloozerActivity(LollapaloozerActivity.this);

    if (appController.getIsLoggedIn()) {
      appController.getLoginData().printDebug();
    }

    _ratingSelectedIdToValue.put(R.id.radio_button_score1, 1);
    _ratingSelectedIdToValue.put(R.id.radio_button_score2, 2);
    _ratingSelectedIdToValue.put(R.id.radio_button_score3, 3);
    _ratingSelectedIdToValue.put(R.id.radio_button_score4, 4);
    _ratingSelectedIdToValue.put(R.id.radio_button_score5, 5);

    _ratingSelectedScoreToId.put("1", R.id.radio_button_score1);
    _ratingSelectedScoreToId.put("2", R.id.radio_button_score2);
    _ratingSelectedScoreToId.put("3", R.id.radio_button_score3);
    _ratingSelectedScoreToId.put("4", R.id.radio_button_score4);
    _ratingSelectedScoreToId.put("5", R.id.radio_button_score5);

    setContentView(R.layout.sets_list);
    setListAdapter = new CustomSetListAdapter(this, AndroidConstants.JSON_KEY_SETS__TIME_ONE,
        AndroidConstants.JSON_KEY_SETS__STAGE_ONE, appController.getUserRatingsJAHM());
    setListAdapter.setData(new JSONArray());

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.setAdapter(setListAdapter);
    viewSetsList.setOnItemClickListener(this);

    Button buttonSearchSets = (Button) this.findViewById(R.id.buttonChangeToSearchSets);
    buttonSearchSets.setOnClickListener(this);

    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    AndroidUtils.populateSpinnerWithArray(spinnerSortType, android.R.layout.simple_spinner_item,
        R.array.search_types, android.R.layout.simple_spinner_dropdown_item);
    spinnerSortType.setOnItemSelectedListener(this);

    _sortMode = SORT_TIME;

    // Above here is stuff to be done once

    // TODO consider changing this to AsyncTask
    networkIOHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {

        super.handleMessage(msg);

        if (msg.what == AndroidConstants.THREAD_UPDATE_UI) {
          System.out.println("Executing update UI thread callback ");
          redrawUI();
        }

        if (msg.what == AndroidConstants.THREAD_SUBMIT_RATING) {
          System.out.println("Executing submit rating thread callback ");

          try {
            appController.doSubmitRating(lastRating);

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

    processExtraData();

  }

  /**
   * TODO: potentially make year a searchable field here
   */
  private void processExtraData() {
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();

    if (bundle == null) {
      return;
    }

    String day = intent.getExtras().getString("DAY");

    LollapaloozerApplication.debug(this, "Searching day[" + day + "]");
    appController.setDayToQuery(day);
    refreshData();
  }

  /** Called by Android Framework when activity (re)gains foreground status */
  public void onResume() {
    super.onResume();
    System.out.println(this + " onResume");

    appController.setLastAuthActivity(this);
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
      intent.setClass(this, SetsSearchActivity.class);
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
  }

  // An item in the ListView of sets is clicked
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    System.out.println(this.toString() + "onItemClick position:" + position + " id:" + id);
    try {// TODO Hard coded strings means you are going to hell
      JSONObject obj = (JSONObject) setListAdapter.getItem(position);
      System.out.println(this + " Assigning " + obj + " to _lastItemSelected");
      lastSetSelected = obj;
  
      String setId = lastSetSelected.getString(AndroidConstants.JSON_KEY_SETS__SET_ID);
      lastRating = appController.getUserRatingsJAHM().getJSONObject(setId, "1");
  
      LollapaloozerApplication.debug(this, "You Clicked On: " + obj + " previous ratings "
          + lastRating);
    } catch (JSONException e) {
      LollapaloozerApplication.debug(this, "JSONException retrieving user's last rating");
      e.printStackTrace();
    }
  
    if (!appController.getIsLoggedIn()) {
      _beginSigninProcess();
    } else {
      System.out.println(this.toString() + "About to launch Dialog!  _lastItemSelected was "
          + lastSetSelected);
      if (lastSetSelected == null) {
        System.out.println(this.toString()
            + " NULL MEMBER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      } else {
        System.out.println(this.toString()
            + " OK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
      showDialog(AndroidConstants.DIALOG_RATE);
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
  
    // TODO Auto-generated method stub
    LollapaloozerApplication.debug(this,
        "Search Type Spinner: Selected -> " + parent.getSelectedItem() + "(" + arg2 + ")");
    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
  
    try {
      if (parent.getSelectedItem().toString().toLowerCase().equals("time")) {
        _sortMode = SORT_TIME;
  
      } else if (parent.getSelectedItem().toString().toLowerCase().equals("artist")) {
        _sortMode = SORT_ARTIST;
      }
  
      setView_reSort();
      viewSetsList.invalidateViews();
    } catch (JSONException e) {
      LollapaloozerApplication.debug(this, "JSONException re-sorting data");
      e.printStackTrace();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.menu_item_email_me:
      LollapaloozerApplication.debug(this, "Menu button 'email me' pressed");
  
      if (!appController.getIsLoggedIn()) {
        Toast.makeText(this, "Try rating at least one set first", 15).show();
      } else {
        try {
          showDialog(AndroidConstants.DIALOG_GETEMAIL);
        } catch (Exception e) {
          LollapaloozerApplication.debug(this, "Error requesting ratings email");
          e.printStackTrace();
        }
      }
      return true;
  
    case R.id.menu_item_delete_email:
      LollapaloozerApplication.debug(this, "Menu button 'Forget Account Info' pressed");
      appController.clearLoginData();
      refreshData();
      return true;
  
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
    LollapaloozerApplication.debug(this, "Search Type Spinner: Nothing Selected");
    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    spinnerSortType.setSelection(0);
  }

  @Override
  public void onCheckedChanged(RadioGroup clickedGroup, int checkedId) {
    // This is only useful when we want to act based on a user changing
    // radio selection i.e. week1/week2.
    RadioGroup scoreGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_score);
    // Formerly used to select week, now always true
    // if (clickedGroup == _rateDialog.findViewById(R.id.radio_pick_score))
    // {
  
  }

  // Dialog handling, called once the first time this activity displays
  // (a/each type of)? dialog
  @Override
  protected Dialog onCreateDialog(int id) {
    if (id == AndroidConstants.DIALOG_FIRST_USE) {
      firstUseDialog = new Dialog(this);
      firstUseDialog.setContentView(R.layout.first_use_dialog);
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
  
      // Setup 'X' close widget
      ImageView close_dialog = (ImageView) rateDialog
          .findViewById(R.id.imageView_custom_dialog_close);
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
  
    if (id == AndroidConstants.DIALOG_GETEMAIL) {
      EditText emailField = (EditText) emailDialog.findViewById(R.id.textField_enterEmail);
      if (appController.getLoginData().emailAddress == null) {
        emailField.setText("");
      } else {
        emailField.setText(appController.getLoginData().emailAddress);
        emailField.selectAll();
        emailField.requestFocus();
      }
    }
  
    if (id == AndroidConstants.DIALOG_RATE) {
      try {
        TextView subtitleText = (TextView) rateDialog.findViewById(R.id.text_rateBand_subtitle);
  
        // Bug finding. Debugger doesn't always find it. Race condition?
        // Strange bug. reproduce by clicking something ASAP when app loads and
        // clicking FB post
        if (subtitleText == null) {
          System.out.println("Subtitletext was " + subtitleText);
        }
  
        System.out.println(this.toString() + "_lastItemSelected was " + lastSetSelected);
        if (lastSetSelected == null) {
          System.out.println(this.toString()
              + " NULL MEMBER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } else {
          System.out.println(this.toString()
              + " OK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
          subtitleText.setText(lastSetSelected.getString("artist"));
        }
      } catch (JSONException e) {
        LollapaloozerApplication
            .debug(this, "JSONException assigning Artist name to Rating dialog");
        e.printStackTrace();
      }
  
      // This checks the appropriate radio button based on the score set
      // in _lastRatings
      // _lastRatings is set by onItemClick not to be confused with
      // onClick
  
      RadioGroup scoreGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_score);
      EditText noteWidget = (EditText) rateDialog.findViewById(R.id.editText_commentsForSet);
      if (lastRating == null) {
  
        scoreGroup.clearCheck();
        noteWidget.setText("");
  
        return; // Abandon if there is no previous data
      }
      try {
        int selectedItemScore = lastRating.getInt(AndroidConstants.JSON_KEY_RATINGS__RATING);
        int buttonIdToCheck;
        buttonIdToCheck = _ratingSelectedScoreToId.get(selectedItemScore + "");
        scoreGroup.check(buttonIdToCheck);
        // RadioButton buttonToCheck = (RadioButton) _rateDialog
        // .findViewById(buttonIdToCheck);
        // buttonToCheck.setChecked(true);
  
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  
      try {
        // Ratings might not include notes
        if (lastRating.has(AndroidConstants.JSON_KEY_RATINGS__NOTES)) {
          String selectedItemNote = lastRating.getString(AndroidConstants.JSON_KEY_RATINGS__NOTES);
          noteWidget = (EditText) rateDialog.findViewById(R.id.editText_commentsForSet);
          noteWidget.setText(selectedItemNote);
        } else {
          noteWidget.setText("");
        }
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  
      if (appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
        ImageButton buttonFB = (ImageButton) rateDialog.findViewById(R.id.button_go_fb);
        buttonFB.setImageResource(R.drawable.post_facebook_large);
        System.out.println(buttonFB.getPaddingTop() + " " + buttonFB.getPaddingLeft() + " "
            + buttonFB.getPaddingBottom() + " " + buttonFB.getPaddingRight());
        buttonFB.setPadding(7, 3, 7, 10);
  
      }
  
      if (appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
        ImageButton buttonTweet = (ImageButton) rateDialog.findViewById(R.id.button_go_tweet);
        buttonTweet.setImageResource(R.drawable.post_twitter_large);
        buttonTweet.setPadding(7, 3, 7, 10);
      }
  
      if (appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)
          && appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
  
        Button buttonRateAbove = (Button) rateDialog.findViewById(R.id.button_go_rate_above);
        buttonRateAbove.setVisibility(View.VISIBLE);
  
        Button buttonRateInline = (Button) rateDialog.findViewById(R.id.button_go_rate_inline);
        buttonRateInline.setVisibility(View.GONE);
      }
  
    } // end if rating dialog
  
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    String infoMessage = "LollapaloozerActivity.onActivityResult req=" + requestCode
        + " resultCode=" + resultCode + " data: " + data;
    System.out.println(infoMessage);
  
    if (data != null) {
      System.out.println("Intent Data: " + data.getDataString());
      System.out.println("Intent Extras: " + AndroidUtils.bundleValues(data.getExtras()));
    }
  
    if (resultCode == Activity.RESULT_OK) { // Login success
      switch (requestCode) {
  
      case AuthConstants.INTENT_CHOOSE_LOGIN_TYPE:
        Bundle results = data.getExtras();
        appController.processLoginData(results);
        break;
  
      case AuthConstants.INTENT_FACEBOOK_LOGIN: {
        // Assuming it is Facebook
        System.out.println("onActivityResult called by Facebook API");
        // Required by Facebook API
        appController.getAuthModel().getFacebookObject()
            .authorizeCallback(requestCode, resultCode, data);
        break;
      }
  
      case AuthConstants.INTENT_TWITTER_LOGIN: {
        // Assuming it is Facebook
        System.out.println("onActivityResult called by Twitter API");
        appController.getAuthModel().twitterAuthCallback(requestCode, resultCode, data);
        break;
      }
  
      default:
        appController.showErrorDialog("Unexpected Response",
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

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);// must store the new intent unless getIntent() will
    // return the old one
    processExtraData();
  }

  private void redrawUI() {
    try {
      setView_reSort();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.invalidateViews();

    LollapaloozerApplication.debug(this, "Data Refresh is complete");
    _lastRefresh = System.currentTimeMillis();

    if (!appController.saveData()) {
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

  private void refreshData() {
    TextView titleView = (TextView) this.findViewById(R.id.text_set_list_title);
    titleView.setText(appController.getDayToQuery() + " - Lollapalooza "
        + appController.getYearToQuery());
    // +" "+ weekString);
    setListAdapter.setTimeFieldName(AndroidConstants.JSON_KEY_SETS__TIME_ONE);
    setListAdapter.setStageFieldName(AndroidConstants.JSON_KEY_SETS__STAGE_ONE);

    appController.refreshDataFromStorage();

    launchGetDataThread(); // TODO multithread this
  }

  private void setView_reSort() throws JSONException {
    if (_sortMode == SORT_TIME) {
      setListAdapter.sortByField(AndroidConstants.JSON_KEY_SETS__TIME_ONE,
          JSONArraySortMap.VALUE_INTEGER);
    } else if (_sortMode == SORT_ARTIST) {
      setListAdapter.sortByField("artist", JSONArraySortMap.VALUE_STRING);
    } else {
      LollapaloozerApplication.debug(this, "Unexpected sort mode: " + _sortMode);
      (new Exception()).printStackTrace();
    }
  }

  private void clickDialogConfirmEmailButtonOK() {
    EditText emailField = (EditText) emailDialog.findViewById(R.id.textField_enterEmail);
    String email = emailField.getText().toString();

    LollapaloozerApplication.debug(this, "User provided email address: " + email);

    // if
    // (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    if (!FieldVerifier.isValidEmail(email)) {
      Toast invalidEmail = Toast.makeText(this, "Please enter your real email address.", 25);
      invalidEmail.show();

    } else { // Email is valid. Save email and email ratings
      appController.setLoginEmail(email);

      if (!appController.saveData()) {
        showDialog(AndroidConstants.DIALOG_NETWORK_ERROR);
      }

      emailDialog.dismiss();
      try {

        System.out.println("Requesting ratings email.");

        List<NameValuePair> parameterList = new ArrayList<NameValuePair>();

        LoginData loginData = appController.getLoginData();

        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, email));
        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TYPE, loginData.loginType
            + ""));
        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_ID,
            loginData.accountIdentifier));
        parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TOKEN,
            loginData.accountToken));
        if (loginData.emailAddress != null) {
          parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL,
              loginData.emailAddress));
        }
        String result = ServiceUtils.emailMyRatings(parameterList, this,
            HttpConstants.SERVER_URL_LOLLAPALOOZER);

      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private void clickDialogFirstUseButtonOK() {
    appController.setDataFirstUse(false);
    // TODO _storageManager.save();
    firstUseDialog.dismiss();
    _showClickToRate(); // display 'tap set to rate it' toast
  }

  private void clickDialogSubmitRatingButtonOK() {
    // incomplete
    if (!_rateDialogVerify()) {
      return;
    }

    rateDialogSubmitRating();
    rateDialog.dismiss();
  }

  private void clickDialogSubmitRatingTwitter() throws JSONException {
    System.out.println("Clicked post on Twitter");
    if (!_rateDialogVerify()) {
      return;
    }
    rateDialogSubmitRating();
    appController.getAuthModel().ensurePermission(AuthModel.PERMISSION_TWITTER_TWEET);

    _queuedTwitterPost = _buildSocialNetworkPost();

    // https://api.twitter.com/1/statuses
    if (appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      System.out.println("Twitter Auth available now, posting immediately");
      doTwitterPost();
    } else {
      System.out.println("Twitter Auth not ready, posting later");
    }

    rateDialog.dismiss();
  }

  private void clickDialogSubmitRatingFacebook() throws JSONException {
    System.out.println("Clicked post on FB");
    if (!_rateDialogVerify()) {
      return;
    }
    rateDialogSubmitRating();
    appController.getAuthModel().ensurePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL);

    _queuedFacebookPost = _buildSocialNetworkPost();
    if (appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
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
    String submittedRating = _ratingSelectedIdToValue.get(scoreGroup.getCheckedRadioButtonId())
        .toString();

    EditText noteWidget = (EditText) rateDialog.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();
    String artistName = lastSetSelected.getString("artist");

    post.rating = submittedRating;
    post.note = submittedNote;
    post.artistName = artistName;
    return post;
  }

  private boolean _rateDialogVerify() {
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
    RadioGroup scoreGroup = (RadioGroup) rateDialog.findViewById(R.id.radio_pick_score);
    String submittedRating = _ratingSelectedIdToValue.get(scoreGroup.getCheckedRadioButtonId())
        .toString();

    EditText noteWidget = (EditText) rateDialog.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();

    try {
      if (lastRating == null) {
        JSONObject newObj = new JSONObject();
        newObj.put(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
            lastSetSelected.get(AndroidConstants.JSON_KEY_SETS__SET_ID));
        newObj.put(AndroidConstants.JSON_KEY_RATINGS__WEEK, "1");
        lastRating = newObj;
      }

      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__RATING, submittedRating);
      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__NOTES, submittedNote);

      launchSubmitRatingThread();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void launchGetDataThread() {
    LollapaloozerApplication.debug(this, "Launching getData thread");

    new Thread() {
      public void run() {
        try {
          setListAdapter.setData(appController.getRatingsFromServer());
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        networkIOHandler.sendEmptyMessage(AndroidConstants.THREAD_UPDATE_UI);
      }
    }.start();
    LollapaloozerApplication.debug(this, "getData thread launched");
  }

  private void launchSubmitRatingThread() {
    LollapaloozerApplication.debug(this, "Launching Rating thread");

    new Thread() {
      public void run() {
        try {
          // TODO: why are we refreshing the ratings first?
          setListAdapter.setData(appController.getRatingsFromServer());
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Message msgToSend = Message.obtain(networkIOHandler, AndroidConstants.THREAD_SUBMIT_RATING);
        msgToSend.sendToTarget();
      }
    }.start();
    LollapaloozerApplication.debug(this, "Rating thread launched");

  }

  private SocialNetworkPost _queuedTwitterPost;

  @Override
  public synchronized void doTwitterPost() {
    if (_queuedTwitterPost == null) {
      return;
    }

    if (!appController.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      System.out.println("Warning: trying to post without Twitter permissions");
    }

    // use _queuedFacebookPost
    String result = appController.getAuthModel().tweetToTwitter(_queuedTwitterPost);
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

    if (!appController.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
      System.out.println("Warning: trying to post without facebook permissions");
    }

    // use _queuedFacebookPost
    String result = appController.getAuthModel().postToFacebookWall(_queuedFacebookPost);
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

  @Override
  public void startWebAuthActivity(String authReqTokenUrl) {
  }
}
