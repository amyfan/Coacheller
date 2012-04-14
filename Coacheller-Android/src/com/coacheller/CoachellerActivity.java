package com.coacheller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.coacheller.shared.FieldVerifier;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CoachellerActivity extends Activity implements View.OnClickListener,
    OnItemSelectedListener, OnItemClickListener {

  private static final int DIALOG_RATE = 1;
  private static final int DIALOG_GETEMAIL = 2;
  private static final int SORT_TIME = 1;
  private static final int SORT_ARTIST = 2;

  private static final String USER_EMAIL = "USER_EMAIL";
  public static final String QUERY_RATINGS__SET_ID = "set_id";
  public static final String QUERY_RATINGS__WEEK = "weekend";
  public static final String QUERY_SETS__SET_ID = "id";
  public static final String QUERY_SETS__TIME_ONE = "time_one";
  public static final String QUERY_SETS__TIME_TWO = "time_two";
  public static final String QUERY_RATINGS__RATING = "score";
  
  private static final int REFRESH_INTERVAL__SECONDS = 15;

  private CustomSetListAdapter _setListAdapter;
  private int _weekToQuery;
  private int _sortMode;
  private String _timeFieldName;
  private Dialog _lastRateDialog;
  private Dialog _lastGetEmailDialog;
  private JSONObject _lastItemSelected;
  private HashMap<Integer, Integer> _ratingSelectedIdToValue = new HashMap<Integer, Integer>();
  private CoachellerStorageManager _storageManager;

  private String _dayToExamine;
  //private boolean _have_email = false;
  private String _obtained_email = null;
  private JSONArrayHashMap _myRatings_JAHM;
  private long _lastRefresh = 0;

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

    _storageManager = new CoachellerStorageManager(this);
    _storageManager.load();

    initJAHM();
    
    setContentView(R.layout.sets_list);
    _setListAdapter = new CustomSetListAdapter(this, QUERY_SETS__TIME_ONE, _myRatings_JAHM);
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
    
 
    

    processExtraData();


  }

  private void obtainEmailFromStorage() {
    String loadedEmail = _storageManager.getString(USER_EMAIL);

    if ((loadedEmail != null) && FieldVerifier.isValidEmail(loadedEmail)) {
      _obtained_email = loadedEmail;
    }

    CoachellerApplication.debug(this, "Using email: " + _obtained_email + ", on disk:[" + loadedEmail + "]");
  }

  private void rebuildJAHM() {
    if (_obtained_email != null) { // Get my ratings

      JSONArray myRatings = ServiceUtils.getRatings(_obtained_email, _dayToExamine, this);
      try {
        //TODO this may not be correct.  JAHM should only be initialized once.
        _myRatings_JAHM = new JSONArrayHashMap(myRatings, QUERY_RATINGS__SET_ID,
            QUERY_RATINGS__WEEK);
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        // Could not get my ratings :(
        e.printStackTrace();
      }
    } else {
      //TODO This may not be correct.  initJAHM() may have already been called on startup and
      //TODO the reference to the JAHM may have been passed to the adapter already
      initJAHM();
    }
    _setListAdapter.updateJAHM(_myRatings_JAHM);
  }

  private void initJAHM() {
    _myRatings_JAHM = new JSONArrayHashMap(QUERY_RATINGS__SET_ID, QUERY_RATINGS__WEEK);
  }

  public void onResume() {
    super.onResume();
    
    if ((System.currentTimeMillis() - _lastRefresh)/1000 > REFRESH_INTERVAL__SECONDS) {
      refreshData();
    }
    
    Toast clickToRate = Toast.makeText(this, "Tap any set to rate it!", 25);
    clickToRate.show();
    
    
  }

  private void refreshData() {
    // Below here is stuff to be done each refresh
    
    if (!_dayToExamine.equals("Friday") && !_dayToExamine.equals("Saturday")
        && !_dayToExamine.equals("Sunday")) {
      _dayToExamine = "Friday";
    }
    
    String weekString="";
    if (_weekToQuery == 1) {
      _timeFieldName = QUERY_SETS__TIME_ONE;
      weekString = getString(R.string.name_week1_short);
    } else if (_weekToQuery == 2) {
      _timeFieldName = QUERY_SETS__TIME_TWO;
       weekString = getString(R.string.name_week2_short);
    }
    
     
    TextView titleView = (TextView) this.findViewById(R.id.text_set_list_title);
    titleView.setText(_dayToExamine +", Week "+ _weekToQuery +" "+ weekString);
    _setListAdapter.setTimeFieldName(_timeFieldName);
    
    obtainEmailFromStorage();
    rebuildJAHM();


    // TODO: pass proper values (year can remain hard-coded for now)
    JSONArray results = ServiceUtils.getSets("2012", _dayToExamine, this);
    _setListAdapter.setData(results);
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
      CoachellerApplication.debug(this, "Unexpected sort mode: "+_sortMode);
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
        _storageManager.putString(USER_EMAIL, email);
        _storageManager.save();
        _obtained_email = email;
        _lastGetEmailDialog.dismiss();
        showDialog(DIALOG_RATE);
      }
    }

    if (viewClicked.getId() == R.id.button_declineEmail) {
      // Toast youMustObey =
      // Toast.makeText(this,"You may not decline...  Coacheller will not be denied!  ALL YOUR EMAILS ARE BELONG TO US!",25);
      // youMustObey.show();

      _lastGetEmailDialog.dismiss();
      // showDialog(DIALOG_RATE);
    }

    if (viewClicked.getId() == R.id.buttonChangeToSearchSets) {
      System.out.println("Button: Find Other Sets");
      Intent intent = new Intent();
      intent.setClass(this, ActivitySetsSearch.class);
      // intent.putExtras(bun);
      startActivity(intent);
    }

    if (viewClicked.getId() == R.id.button_rate_okgo) {
      RadioGroup weekGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_week);
      int weekSelectedId = weekGroup.getCheckedRadioButtonId();

      RadioGroup scoreGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_score);
      int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();

      if (weekSelectedId == -1 || scoreSelectedId == -1) {
        Toast selectEverything = Toast.makeText(this,
            "Please select a rating and week of this Set", 25);
        selectEverything.show();

      } else {
        int weekSelectedValue = _ratingSelectedIdToValue.get(weekSelectedId);
        String scoreSelectedValue = _ratingSelectedIdToValue.get(scoreSelectedId) + "";

        CoachellerApplication.debug(this, "Selected Week[" + weekSelectedValue + "] Score["
            + scoreSelectedValue + "] WeekId[" + weekSelectedId + "] ScoreId[" + scoreSelectedId
            + "]");

        scoreGroup.clearCheck();
        _lastRateDialog.dismiss();

        int checkedRadioId = ((RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_week))
            .getCheckedRadioButtonId();
        String weekNumber = _ratingSelectedIdToValue.get(checkedRadioId) + "";

        // submit rating
        ServiceUtils.addRating(_storageManager.getString(USER_EMAIL), ((TextView) _lastRateDialog
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

          
          //Don't need since we are refreshing
          //ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
          //viewSetsList.invalidateViews(); 

        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        refreshData();  //If this is removed, uncomment ListView and invalidate above ^^^

      }
    }

    if (viewClicked.getId() == R.id.button_rate_cancel) {
      // Dialog dialog = (Dialog) viewClicked.getParent();
      RadioGroup scoreGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_score);
      scoreGroup.clearCheck();
      _lastRateDialog.dismiss();
    }

  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    JSONObject obj = (JSONObject) _setListAdapter.getItem(position);
    _lastItemSelected = obj;
    CoachellerApplication.debug(this, "You Clicked On: " + obj);

    if (_obtained_email == null) {
      showDialog(DIALOG_GETEMAIL);
      // _tried_to_get_email = true;
    } else {

      showDialog(DIALOG_RATE);
    }
  }

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
      if (week == 1) {
        
        buttonWeek1.setChecked(true);
        buttonWeek2.setClickable(false);
        
      } else if (week == 2) {
        buttonWeek1.setClickable(false);
        buttonWeek2.setChecked(true);
        
      } else {
        // Don't suggest a week
        weekGroup.clearCheck();
      }

      // TODO If the user already rated this set, default to their last rating
      RadioGroup scoreGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_score);
      scoreGroup.clearCheck();

    }

  }

  @Override
  protected Dialog onCreateDialog(int id) {

    if (id == DIALOG_GETEMAIL) {
      _lastGetEmailDialog = new Dialog(this);
      _lastGetEmailDialog.setContentView(R.layout.get_email_address);
      _lastGetEmailDialog.setTitle("Keep Track of Everything");

      //TODO should display 'your email here' message and clear it when user selects field
      EditText emailField = (EditText) _lastGetEmailDialog.findViewById(R.id.textField_enterEmail);
      //emailField.setText("me@here.com");
      //emailField.selectAll();

      Button buttonOK = (Button) _lastGetEmailDialog.findViewById(R.id.button_provideEmail);
      buttonOK.setOnClickListener(this);

      Button buttonCancel = (Button) _lastGetEmailDialog.findViewById(R.id.button_declineEmail);
      buttonCancel.setOnClickListener(this);

      return _lastGetEmailDialog;
    }

    if (id == DIALOG_RATE) {

      _lastRateDialog = new Dialog(this);
      _lastRateDialog.setContentView(R.layout.dialog_rate_set);
      _lastRateDialog.setTitle("Rate this Set!");

      Button buttonOK = (Button) _lastRateDialog.findViewById(R.id.button_rate_okgo);
      buttonOK.setOnClickListener(this);

      Button buttonCancel = (Button) _lastRateDialog.findViewById(R.id.button_rate_cancel);
      buttonCancel.setOnClickListener(this);
      return _lastRateDialog;
    }

    return super.onCreateDialog(id);
  }
  
  
 
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);//must store the new intent unless getIntent() will return the old one
    processExtraData();
  }
  
  
  private void processExtraData(){
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();
    
    if (bundle == null) {
      return;
    }
    
    String week = intent.getExtras().getString("WEEK");
    String day = intent.getExtras().getString("DAY");
    
    CoachellerApplication.debug(this, "Searching week["+ week +"] day["+ day +"]");
    _weekToQuery = Integer.valueOf(week);
    _dayToExamine = day;
    refreshData();
  }

}