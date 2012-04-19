package com.coacheller.ui;

import java.util.HashMap;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coacheller.CoachellerApplication;
import com.coacheller.CoachellerStorageManager;
import com.coacheller.R;
import com.coacheller.ServiceUtils;
import com.coacheller.R.array;
import com.coacheller.R.id;
import com.coacheller.R.layout;
import com.coacheller.R.string;
import com.coacheller.data.CustomPair;
import com.coacheller.data.CustomSetListAdapter;
import com.coacheller.data.JSONArrayHashMap;
import com.coacheller.data.JSONArraySortMap;
import com.coacheller.shared.FieldVerifier;

public class CoachellerActivity extends Activity implements View.OnClickListener,
    OnItemSelectedListener, OnItemClickListener, OnCheckedChangeListener {

  private static final int DIALOG_RATE = 1;
  private static final int DIALOG_GETEMAIL = 2;
  private static final int DIALOG_NETWORK_ERROR = 3;
  
  private static final int THREAD_UPDATE_UI = 1;
  private static final int THREAD_SUBMIT_RATING = 2;

  private static final int SORT_TIME = 1;
  private static final int SORT_ARTIST = 2;

  // Local Storage
  private static final String DATA_USER_EMAIL = "DATA_USER_EMAIL";
  private static final String DATA_SETS = "DATA_SETS";
  private static final String DATA_RATINGS = "DATA_RATINGS";

  // Database
  public static final String QUERY_RATINGS__SET_ID = "set_id";
  public static final String QUERY_RATINGS__WEEK = "weekend";
  public static final String QUERY_SETS__SET_ID = "id";
  public static final String QUERY_SETS__TIME_ONE = "time_one";
  public static final String QUERY_SETS__TIME_TWO = "time_two";
  public static final String QUERY_SETS__STAGE_ONE = "stage_one";
  public static final String QUERY_SETS__STAGE_TWO = "stage_two";
  public static final String QUERY_RATINGS__RATING = "score";

  private static final int REFRESH_INTERVAL__SECONDS = 15;

  private int _weekToQuery;
  private String _dayToExamine;
  private int _sortMode;
  private long _lastRefresh = 0;

  private String _timeFieldName;
  private String _stageFieldName;

  private Dialog _lastRateDialog;
  private Dialog _lastGetEmailDialog;
  private Dialog _lastNetworkErrorDialog;

  private String _obtained_email = null;
  private CustomSetListAdapter _setListAdapter;
  private JSONObject _lastItemSelected;
  private CustomPair<String, String> _lastRatings = new CustomPair<String, String>(null, null);
  private HashMap<Integer, Integer> _ratingSelectedIdToValue = new HashMap<Integer, Integer>();
  private HashMap<String, Integer> _ratingSelectedScoreToId = new HashMap<String, Integer>();
  
  private CoachellerStorageManager _storageManager;
  private int _ratingSelectedWeek;
  private int _ratingSelectedScore;

  private JSONArrayHashMap _myRatings_JAHM = new JSONArrayHashMap(QUERY_RATINGS__SET_ID,
      QUERY_RATINGS__WEEK);
  private boolean _networkErrors;

  private Handler _networkIOHandler;

  /** Called by Android Framework when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    CoachellerApplication.debug(this, "CoachellerActivity Launched");

    _ratingSelectedIdToValue.put(R.id.radio_button_week1, 1);
    _ratingSelectedIdToValue.put(R.id.radio_button_week2, 2);
    
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


    _storageManager = new CoachellerStorageManager(this);
    _storageManager.load();

    // initJAHM(); //Only initialized once

    setContentView(R.layout.sets_list);
    _setListAdapter = new CustomSetListAdapter(this, QUERY_SETS__TIME_ONE, QUERY_SETS__STAGE_ONE,
        _myRatings_JAHM);
    _setListAdapter.setData(new JSONArray());

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.setAdapter(_setListAdapter);
    viewSetsList.setOnItemClickListener(this);

    Button buttonSearchSets = (Button) this.findViewById(R.id.buttonChangeToSearchSets);
    buttonSearchSets.setOnClickListener(this);

    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    CoachellerApplication.populateSpinnerWithArray(spinnerSortType, R.array.search_types);
    spinnerSortType.setOnItemSelectedListener(this);

    _weekToQuery = CoachellerApplication.whichWeekIsToday();
    _dayToExamine = CoachellerApplication.whatDayIsToday();
    _sortMode = SORT_TIME;

    // Above here is stuff to be done once

    _networkIOHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        
        super.handleMessage(msg);
        
        if (msg.what == THREAD_UPDATE_UI) {
          System.out.println("Executing update UI thread callback ");
          redrawUI();
        }
        
        if (msg.what == THREAD_SUBMIT_RATING) {
          System.out.println("Executing submit rating thread callback ");
          doSubmitRating(_ratingSelectedWeek+"", _ratingSelectedScore+"");
        }
      }  
      
      
    };
    
    processExtraData();

  }

  protected void redrawUI() {
    try {
      setView_reSort();

    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.invalidateViews();

    CoachellerApplication.debug(this, "Data Refresh is complete");
    _lastRefresh = System.currentTimeMillis();

    if (!_networkErrors) {
      _storageManager.save(); // TODO only if both sets and ratings were
                              // retrieved

    } else {
      showDialog(DIALOG_NETWORK_ERROR);
    }
  }

  /** Called by Android Framework when activity (re)gains foreground status */
  public void onResume() {
    super.onResume();

    if ((System.currentTimeMillis() - _lastRefresh) / 1000 > REFRESH_INTERVAL__SECONDS) {
      refreshData(); // TODO multi-thread this
    }

    Toast clickToRate = Toast.makeText(this, "Tap any set to rate it!", 20);
    clickToRate.show();
  }

  private void obtainEmailFromStorage() {
    String loadedEmail = _storageManager.getString(DATA_USER_EMAIL);

    if ((loadedEmail != null) && FieldVerifier.isValidEmail(loadedEmail)) {
      _obtained_email = loadedEmail;
    }

    CoachellerApplication.debug(this, "Using email: " + _obtained_email + ", on disk:["
        + loadedEmail + "]");

  }

  // Only called once, so commented out.
  /*
   * private void initJAHM() { CoachellerApplication.debug(this, "initJAHM()");
   * _myRatings_JAHM = new JSONArrayHashMap(QUERY_RATINGS__SET_ID,
   * QUERY_RATINGS__WEEK); }
   */

  private void refreshData() {
    // Below here is stuff to be done each refresh
    _networkErrors = false;

    

      if (!_dayToExamine.equals("Friday") && !_dayToExamine.equals("Saturday")
          && !_dayToExamine.equals("Sunday")) {
        _dayToExamine = "Friday";
      }

      String weekString = "";
      if (_weekToQuery == 1) {
        _timeFieldName = QUERY_SETS__TIME_ONE;
        _stageFieldName = QUERY_SETS__STAGE_ONE;
        weekString = getString(R.string.name_week1_short);
      } else if (_weekToQuery == 2) {
        _timeFieldName = QUERY_SETS__TIME_TWO;
        _stageFieldName = QUERY_SETS__STAGE_TWO;
        weekString = getString(R.string.name_week2_short);
      }

      TextView titleView = (TextView) this.findViewById(R.id.text_set_list_title);
      titleView.setText(_dayToExamine + ", Weekend " + _weekToQuery);
      // +" "+ weekString);
      _setListAdapter.setTimeFieldName(_timeFieldName);
      _setListAdapter.setStageFieldName(_stageFieldName);

      obtainEmailFromStorage();

      launchGetDataThread(); // TODO multithread this
  }

  private void launchGetDataThread() {
    CoachellerApplication.debug(this, "Launching getData thread");

    new Thread() {
      public void run() {
        try {
          doNetworkOperations();
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        _networkIOHandler.sendEmptyMessage(THREAD_UPDATE_UI);
      }
    }.start();
    CoachellerApplication.debug(this, "getData thread launched");
  }
  
  private void launchSubmitRatingThread() {
       CoachellerApplication.debug(this, "Launching Rating thread");

    new Thread() {
      public void run() {
        try {
          doNetworkOperations();
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Message msgToSend = Message.obtain(_networkIOHandler, THREAD_SUBMIT_RATING);
        msgToSend.sendToTarget();
      }
    }.start();
    CoachellerApplication.debug(this, "Rating thread launched");
    
  }

  public void doNetworkOperations() throws JSONException {

    CoachellerApplication.debug(this, "rebuildJAHM()");
    if (_obtained_email != null) { // Get my ratings

      JSONArray myRatings = null;
      try {
        myRatings = ServiceUtils.getRatings(_obtained_email, _dayToExamine, this);
        _storageManager.putJSONArray(DATA_RATINGS, myRatings);
      } catch (Exception e1) {
        _networkErrors = true;
        CoachellerApplication.debug(this,
            "Exception getting Ratings data, loading from storage if available");
        try {
          myRatings = _storageManager.getJSONArray(DATA_RATINGS);
        } catch (JSONException e) {
          e.printStackTrace();
          CoachellerApplication.debug(this, "JSONException loading ratings from storage");
        }
      }

      try {
        // TODO this may not be correct. JAHM should only be initialized once.
        // _myRatings_JAHM = new JSONArrayHashMap(myRatings,
        // QUERY_RATINGS__SET_ID, QUERY_RATINGS__WEEK);

        if (myRatings == null) {
          CoachellerApplication.debug(this, "Had to initialize ratings data JSONArray");
          myRatings = new JSONArray();
        }

        _myRatings_JAHM.rebuildDataWith(myRatings);

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        // Could not get my ratings :(
        e.printStackTrace();
      }
    } else {
      // TODO This may not be correct. initJAHM() may have already been called
      // on startup and
      // TODO the reference to the JAHM may have been passed to the adapter
      // already
      // initJAHM(); //Commented in hope of addressing crash issue
      
      
      //Need to wipe out ratings if email was just deleted
      _myRatings_JAHM.wipeData();
    }

    //New strategy does not re-instantiate this object, this line should not be needed
    //_setListAdapter.setNewJAHM(_myRatings_JAHM);

    JSONArray setData = null;
    try {
      // TODO: pass proper values (year can remain hard-coded for now)
      setData = ServiceUtils.getSets("2012", _dayToExamine, this);
      _storageManager.putJSONArray(DATA_SETS, setData);
    } catch (Exception e) {
      _networkErrors = true;
      CoachellerApplication.debug(this,
          "Exception getting Set data, loading from storage if available");
      setData = _storageManager.getJSONArray(DATA_SETS);
    }

    if (setData == null) {
      CoachellerApplication.debug(this, "Had to initialize set data JSONArray");
      setData = new JSONArray();
    }
    _setListAdapter.setData(setData);
  }
  
  public void doSubmitRating(String weekNumber, String scoreSelectedValue) {
    // submit rating
    // If Exception is thrown, do not store rating locally
    try {
      ServiceUtils.addRating(_storageManager.getString(DATA_USER_EMAIL), ((TextView) _lastRateDialog
          .findViewById(R.id.text_rateBand_subtitle)).getText().toString(), "2012", weekNumber,
          scoreSelectedValue, this);

      // Need this in order to make the new rating appear in real time

      try {
        JSONObject newObj = new JSONObject();
        String set_id = _lastItemSelected.get(QUERY_SETS__SET_ID) + "";
        newObj.put(QUERY_RATINGS__SET_ID, set_id);
        newObj.put(QUERY_RATINGS__WEEK, weekNumber);
        newObj.put(QUERY_RATINGS__RATING, scoreSelectedValue);

        // CRITICAL that the keys are listed in this order
        _myRatings_JAHM.addValues(QUERY_RATINGS__SET_ID, QUERY_RATINGS__WEEK, newObj);

        // Don't need since we are refreshing
        // ListView viewSetsList = (ListView)
        // findViewById(R.id.viewSetsList);
        // viewSetsList.invalidateViews();

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      refreshData(); // If this is removed, uncomment ListView and
                     // invalidate
                     // above ^^^

    } catch (Exception e1) {
      CoachellerApplication.debug(this, "Error submitting rating");
      e1.printStackTrace();
      showDialog(DIALOG_NETWORK_ERROR);
    }
  }

  // An item was selected from the list of sets
  @Override
  public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {

    // TODO Auto-generated method stub
    CoachellerApplication.debug(this,
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
      CoachellerApplication.debug(this, "JSONException re-sorting data");
      e.printStackTrace();
    }
  }

  private void setView_reSort() throws JSONException {
    if (_sortMode == SORT_TIME) {
      _setListAdapter.sortByField(_timeFieldName, JSONArraySortMap.VALUE_INTEGER);
    } else if (_sortMode == SORT_ARTIST) {
      _setListAdapter.sortByField("artist", JSONArraySortMap.VALUE_STRING);
    } else {
      CoachellerApplication.debug(this, "Unexpected sort mode: " + _sortMode);
      (new Exception()).printStackTrace();
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
    CoachellerApplication.debug(this, "Search Type Spinner: Nothing Selected");
    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    spinnerSortType.setSelection(0);
  }

  // Any button in any view or dialog was clicked
  @Override
  public void onClick(View viewClicked) {

    if (viewClicked.getId() == R.id.button_provideEmail) {
      EditText emailField = (EditText) _lastGetEmailDialog.findViewById(R.id.textField_enterEmail);
      String email = emailField.getText().toString();

      CoachellerApplication.debug(this, "User provided email address: " + email);

      // if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      if (!FieldVerifier.isValidEmail(email)) {
        Toast invalidEmail = Toast.makeText(this, "Please enter your real email address.", 25);
        invalidEmail.show();

      } else { // Email is valid. Save email and let user get on with rating
        _storageManager.putString(DATA_USER_EMAIL, email);
        _storageManager.save();
        _obtained_email = email;
        _lastGetEmailDialog.dismiss();
        showDialog(DIALOG_RATE);
      }
    }

    if (viewClicked.getId() == R.id.button_declineEmail) {
      _lastGetEmailDialog.dismiss();

    }

    if (viewClicked.getId() == R.id.buttonChangeToSearchSets) {
      System.out.println("Button: Switch Day");
      Intent intent = new Intent();
      intent.setClass(this, ActivitySetsSearch.class);
      startActivity(intent);
    }

    //Submit rating for a set
    if (viewClicked.getId() == R.id.button_rate_okgo) {  //Selections incomplete
      RadioGroup weekGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_week);
      int weekSelectedId = weekGroup.getCheckedRadioButtonId();

      RadioGroup scoreGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_score);
      int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();

      if (weekSelectedId == -1 || scoreSelectedId == -1) {
        Toast selectEverything = Toast.makeText(this,
            "Please select a rating and week of this Set", 25);
        selectEverything.show();

      } else { //Selections are valid
        _ratingSelectedWeek = _ratingSelectedIdToValue.get(weekSelectedId);
        _ratingSelectedScore = _ratingSelectedIdToValue.get(scoreSelectedId);

        CoachellerApplication.debug(this, "Selected Week[" + _ratingSelectedWeek + "] Score["
            + _ratingSelectedScore + "] WeekId[" + weekSelectedId + "] ScoreId[" + scoreSelectedId
            + "]");

        //scoreGroup.clearCheck();
        _lastRateDialog.dismiss();

        int checkedRadioId = ((RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_week))
            .getCheckedRadioButtonId();
        String weekNumber = _ratingSelectedIdToValue.get(checkedRadioId) + "";
        

        launchSubmitRatingThread();
      }
    } // End rating dialog submitted

    if (viewClicked.getId() == R.id.button_rate_cancel) {
      // Dialog dialog = (Dialog) viewClicked.getParent();
      RadioGroup scoreGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_score);
      //scoreGroup.clearCheck();
      _lastRateDialog.dismiss();
    }

    if (viewClicked.getId() == R.id.button_network_error_ok) {
      _lastNetworkErrorDialog.dismiss();
    }

  }

  // An item in the ListView of sets is clicked
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    JSONObject obj = (JSONObject) _setListAdapter.getItem(position);
    _lastItemSelected = obj;
    
    try {//TODO Hard coded strings means you are going to hell
      String setId = _lastItemSelected.getString(QUERY_SETS__SET_ID);
      JSONObject lastRatingWeek1 = _myRatings_JAHM.getJSONObject(setId, "1");
      JSONObject lastRatingWeek2 = _myRatings_JAHM.getJSONObject(setId, "2");
      
      if (lastRatingWeek1 != null) {
        _lastRatings.first = lastRatingWeek1.getString(QUERY_RATINGS__RATING);
      } else {
        _lastRatings.first = null;
      }
      
      if (lastRatingWeek2 != null) {
        _lastRatings.second = lastRatingWeek2.getString(QUERY_RATINGS__RATING);
      } else {
        _lastRatings.second = null;
      }
      
      
    } catch (JSONException e) {
      CoachellerApplication.debug(this, "JSONException retrieving user's last rating");
      e.printStackTrace();
    }
    CoachellerApplication.debug(this, "You Clicked On: "+ obj +" previous ratings "+ _lastRatings.first +"/"+ _lastRatings.second);

    if (_obtained_email == null) {
      showDialog(DIALOG_GETEMAIL);
      // _tried_to_get_email = true;
    } else {

      showDialog(DIALOG_RATE);
    }
  }

  // Dialog handling, called before any dialog is shown
  @Override
  protected void onPrepareDialog(int id, Dialog dialog) {

    // Always call through to super implementation
    super.onPrepareDialog(id, dialog);

    if (id == DIALOG_RATE) {

      // _lastRateDialog.setTitle("Rate this Set!");
      try {
        TextView subtitleText = (TextView) _lastRateDialog
            .findViewById(R.id.text_rateBand_subtitle);
        subtitleText.setText(_lastItemSelected.getString("artist"));
        
        

      } catch (JSONException e) {
        CoachellerApplication.debug(this, "JSONException assigning Artist name to Rating dialog");
        e.printStackTrace();
      }

      int week = CoachellerApplication.whichWeekIsToday();
      RadioGroup weekGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_week);
      RadioButton buttonWeek1 = (RadioButton) _lastRateDialog.findViewById(R.id.radio_button_week1);
      RadioButton buttonWeek2 = (RadioButton) _lastRateDialog.findViewById(R.id.radio_button_week2);
      
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

      //TODO pick user's last rating
      onCheckedChanged(weekGroup, idChanged);
      
      
      //RadioGroup scoreGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_score);
      //scoreGroup.clearCheck();

    }

  }

  // Dialog handling, called once the first time this activity displays (a/each
  // type of)? dialog
  @Override
  protected Dialog onCreateDialog(int id) {

    if (id == DIALOG_GETEMAIL) {
      _lastGetEmailDialog = new Dialog(this);
      _lastGetEmailDialog.setContentView(R.layout.get_email_address);
      _lastGetEmailDialog.setTitle("Keep Track of Everything");

      EditText emailField = (EditText) _lastGetEmailDialog.findViewById(R.id.textField_enterEmail);

      Button buttonOK = (Button) _lastGetEmailDialog.findViewById(R.id.button_provideEmail);
      buttonOK.setOnClickListener(this);

      Button buttonCancel = (Button) _lastGetEmailDialog.findViewById(R.id.button_declineEmail);
      buttonCancel.setOnClickListener(this);

      return _lastGetEmailDialog;
    }

    if (id == DIALOG_RATE) {
      _lastRateDialog = new Dialog(this);
      _lastRateDialog.setContentView(R.layout.dialog_rate_set);
      _lastRateDialog.setTitle("Rate This Set!");
      
      RadioGroup weekGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_week);
      weekGroup.setOnCheckedChangeListener(this);

      Button buttonOK = (Button) _lastRateDialog.findViewById(R.id.button_rate_okgo);
      buttonOK.setOnClickListener(this);

      Button buttonCancel = (Button) _lastRateDialog.findViewById(R.id.button_rate_cancel);
      buttonCancel.setOnClickListener(this);
      return _lastRateDialog;
    }

    if (id == DIALOG_NETWORK_ERROR) {
      _lastNetworkErrorDialog = new Dialog(this);
      _lastNetworkErrorDialog.setContentView(R.layout.dialog_network_error);
      _lastNetworkErrorDialog.setTitle("Network Error");

      Button buttonOK = (Button) _lastNetworkErrorDialog.findViewById(R.id.button_network_error_ok);
      buttonOK.setOnClickListener(this);
      return _lastNetworkErrorDialog;

    }

    return super.onCreateDialog(id);
  }

  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);// must store the new intent unless getIntent() will
                      // return the old one
    processExtraData();
  }

  private void processExtraData() {
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();

    if (bundle == null) {
      return;
    }

    String week = intent.getExtras().getString("WEEK");
    String day = intent.getExtras().getString("DAY");

    CoachellerApplication.debug(this, "Searching week[" + week + "] day[" + day + "]");
    _weekToQuery = Integer.valueOf(week);
    _dayToExamine = day;
    refreshData();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu_global_options, menu);
      return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
          case R.id.menu_item_email_me:
              CoachellerApplication.debug(this, "Menu button 'email me' pressed");
              
              if (_obtained_email == null) {
                Toast.makeText(this, "Try rating at least one set first", 15).show();
              } else {
                try {
                  
                  Toast.makeText(this, "This feature coming soon!", 15).show();
                  //ServiceUtils.sendMyRatings(this, _obtained_email);
                } catch (Exception e) {
                  CoachellerApplication.debug(this, "Error requesting ratings email");
                  e.printStackTrace();
                }
              }
              return true;
              
          case R.id.menu_item_delete_email:
              CoachellerApplication.debug(this, "Menu button 'delete email' pressed");
              _obtained_email = null;
              _storageManager.putString(DATA_USER_EMAIL, null);
              _storageManager.save();
              refreshData();
              return true;
              
          default:
              return super.onOptionsItemSelected(item);
      }
  }

  @Override
  public void onCheckedChanged(RadioGroup clickedGroup, int checkedId) {
    //todo should use switch
    RadioGroup scoreGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_score);
    if (clickedGroup == _lastRateDialog.findViewById(R.id.radio_pick_week)) {
     
      
      if (checkedId == R.id.radio_button_week1  && _lastRatings.first != null) {
        int buttonIdToCheck = _ratingSelectedScoreToId.get(_lastRatings.first);
        RadioButton buttonToCheck = (RadioButton) _lastRateDialog.findViewById(buttonIdToCheck);
        buttonToCheck.setChecked(true);

      } else if (checkedId == R.id.radio_button_week2  && _lastRatings.second != null) {
        CoachellerApplication.debug(this, "Last Rating "+ _lastRatings.second);
        int buttonIdToCheck = _ratingSelectedScoreToId.get(_lastRatings.second); 
        CoachellerApplication.debug(this, "Button id to check "+ buttonIdToCheck);
        RadioButton buttonToCheck = (RadioButton) _lastRateDialog.findViewById(buttonIdToCheck);  //TODO duplicate code
        buttonToCheck.setChecked(true);
        
        
        
      } else { //Not sure what is selected, clear rating check
        
        scoreGroup.clearCheck();
      }
      
      scoreGroup.invalidate();
      
    }
    
  }

}