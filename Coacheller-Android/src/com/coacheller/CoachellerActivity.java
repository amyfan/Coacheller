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

  private CustomSetListAdapter _setListAdapter;
  private int _weekToQuery;
  private String _timeFieldName;
  private Dialog _lastRateDialog;
  private Dialog _lastGetEmailDialog;
  private JSONObject _lastItemSelected;
  private HashMap<Integer, Integer> _ratingSelectedIdToValue = new HashMap<Integer, Integer>();

  private boolean _tried_to_get_email = false;

  /** Called by Android Framework when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    CoachellerApplication.debug(this, "CoachellerActivity Launched");

    _weekToQuery = 1;

    _ratingSelectedIdToValue.put(R.id.radio_button_week1, 1);
    _ratingSelectedIdToValue.put(R.id.radio_button_week2, 2);

    _ratingSelectedIdToValue.put(R.id.radio_button_score1, 1);
    _ratingSelectedIdToValue.put(R.id.radio_button_score2, 2);
    _ratingSelectedIdToValue.put(R.id.radio_button_score3, 3);
    _ratingSelectedIdToValue.put(R.id.radio_button_score4, 4);
    _ratingSelectedIdToValue.put(R.id.radio_button_score5, 5);

    if (_weekToQuery == 1) {
      _timeFieldName = "time_one";
    } else if (_weekToQuery == 2) {
      _timeFieldName = "time_two";
    }

    initializeApp();
  }

  public void onResume() {
    super.onResume();

    Toast clickToRate = Toast.makeText(this, "Tap any set to rate it!", 25);
    clickToRate.show();
  }

  private void initializeApp() {
    setContentView(R.layout.sets_list);

    _setListAdapter = new CustomSetListAdapter(this, _timeFieldName);
    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.setAdapter(_setListAdapter);
    viewSetsList.setOnItemClickListener(this);

    Button buttonSearchSets = (Button) this.findViewById(R.id.buttonChangeToSearchSets);
    buttonSearchSets.setOnClickListener(this);

    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    CoachellerApplication.populateSpinnerWithArray(spinnerSortType, R.array.search_types);
    spinnerSortType.setOnItemSelectedListener(this);

    // TODO: pass proper values (year can remain hard-coded for now)
    JSONArray results = ServiceUtils.getSets("2012", "Friday", this);

    _setListAdapter.setData(results);

    try {

      _setListAdapter.sortByField(_timeFieldName, JSONArraySortMap.VALUE_INTEGER);

      /*
       * JSONObject obj = results.getJSONObject(0);
       * CoachellerApplication.debug(this, obj.toString());
       * CoachellerApplication.debug(this, obj.names().toString());
       * 
       * JSONArraySortMap sortMapArtist = new JSONArraySortMap(results,
       * "artist", JSONArraySortMap.VALUE_STRING); for (int i = 0; i <
       * results.length(); i++) { CoachellerApplication.debug(this,
       * sortMapArtist.getSortedJSONObj(i).toString()); }
       * 
       * JSONArraySortMap sortMapId = new JSONArraySortMap(results, "id",
       * JSONArraySortMap.VALUE_INTEGER); for (int i = 0; i < results.length();
       * i++) { CoachellerApplication.debug(this,
       * sortMapId.getSortedJSONObj(i).toString()); }
       */
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    CoachellerApplication.debug(this, "Sample Data is Ready");

  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
    // TODO Auto-generated method stub
    CoachellerApplication.debug(this,
        "Search Type Spinner: Selected -> " + parent.getSelectedItem() + "(" + arg2 + ")");
    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);

    try {
      if (parent.getSelectedItem().toString().toLowerCase().equals("time")) {
        _setListAdapter.sortByField(_timeFieldName, JSONArraySortMap.VALUE_INTEGER);

      } else if (parent.getSelectedItem().toString().toLowerCase().equals("artist")) {
        _setListAdapter.sortByField("artist", JSONArraySortMap.VALUE_STRING);
      }

      viewSetsList.invalidateViews();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
    CoachellerApplication.debug(this, "Search Type Spinner: Nothing Selected");
    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    spinnerSortType.setSelection(0);
  }

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
      } else {
        _lastGetEmailDialog.dismiss();
        showDialog(DIALOG_RATE);
      }
    }

    if (viewClicked.getId() == R.id.button_declineEmail) {
      Toast youMustObey = Toast
          .makeText(
              this,
              "You may not decline...  Coacheller will not be denied!  ALL YOUR EMAILS ARE BELONG TO US!",
              25);
      youMustObey.show();
      // _lastGetEmailDialog.dismiss();
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
        int scoreSelectedValue = _ratingSelectedIdToValue.get(scoreSelectedId);

        CoachellerApplication.debug(this, "Selected Week[" + weekSelectedValue + "] Score["
            + scoreSelectedValue + "] WeekId[" + weekSelectedId + "] ScoreId[" + scoreSelectedId
            + "]");

        scoreGroup.clearCheck();
        _lastRateDialog.dismiss();
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

    if (!_tried_to_get_email) {
      showDialog(DIALOG_GETEMAIL);
      _tried_to_get_email = true;
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
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      int week = CoachellerApplication.whichWeekIsToday();
      RadioGroup weekGroup = (RadioGroup) _lastRateDialog.findViewById(R.id.radio_pick_week);
      if (week == 1) {
        RadioButton buttonWeek1 = (RadioButton) _lastRateDialog
            .findViewById(R.id.radio_button_week1);
        buttonWeek1.setChecked(true);
      } else if (week == 2) {
        RadioButton buttonWeek2 = (RadioButton) _lastRateDialog
            .findViewById(R.id.radio_button_week2);
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

      EditText emailField = (EditText) _lastGetEmailDialog.findViewById(R.id.textField_enterEmail);
      emailField.setText("me@here.com");
      emailField.selectAll();

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

}