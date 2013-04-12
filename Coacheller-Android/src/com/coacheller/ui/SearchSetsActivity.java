package com.coacheller.ui;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.coacheller.CoachellerApplication;
import com.coacheller.R;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.CalendarUtils;
import com.ratethisfest.android.log.LogController;

public class SearchSetsActivity extends Activity implements OnClickListener {
  public final static String YEAR = "YEAR";
  public final static String WEEK = "WEEK";
  public final static String DAY = "DAY";

  private String yearSelected;
  private String weekSelected;
  private String daySelected;

  private CoachellerApplication appController;
  private Spinner _weekendSpinner;
  private Spinner _daySpinner;
  private Spinner _yearSpinner;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogController.LIFECYCLE_ACTIVITY.logMessage("Search Sets Activity Launched");
    appController = (CoachellerApplication) getApplication();
    appController.registerSearchSetsActivity(SearchSetsActivity.this);
    this.initializeApp();
  }

  public void initializeApp() {
    setContentView(R.layout.sets_search);

    _yearSpinner = (Spinner) this.findViewById(R.id.search_spinner_year);
    AndroidUtils.populateSpinnerWithArray(_yearSpinner, android.R.layout.simple_spinner_item, R.array.names_year,
        android.R.layout.simple_spinner_dropdown_item);
    _yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        LogController.USER_ACTION_UI.logMessage("Day Selected: " + parent.getSelectedItem() + ", position: "
            + parent.getSelectedItemPosition());
        yearSelected = parent.getSelectedItem().toString();

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        LogController.USER_ACTION_UI.logMessage("No Day Selected - Unexpected condition - " + ", position: "
            + parent.getSelectedItemPosition());
      }
    });

    _weekendSpinner = (Spinner) this.findViewById(R.id.search_spinner_week);
    AndroidUtils.populateSpinnerWithArray(_weekendSpinner, android.R.layout.simple_spinner_item, R.array.names_week,
        android.R.layout.simple_spinner_dropdown_item);
    _weekendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        LogController.USER_ACTION_UI.logMessage("Week Selected: " + parent.getSelectedItem() + ", position: "
            + parent.getSelectedItemPosition());
        String stringName = parent.getSelectedItem().toString();
        weekSelected = stringName.substring(stringName.length() - 1, stringName.length());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        LogController.USER_ACTION_UI.logMessage("No Week Selected - Unexpected condition" + ", position: "
            + parent.getSelectedItemPosition());
      }
    });

    if (CalendarUtils.whatWeekIsToday() == 1) {
      LogController.OTHER.logMessage("Date suggests week 1");
      setUIWeek1();

    } else if (CalendarUtils.whatWeekIsToday() == 2) {
      LogController.OTHER.logMessage("Date suggests week 2");
      setUIWeek2();
    }

    _daySpinner = (Spinner) this.findViewById(R.id.search_spinner_day);
    AndroidUtils.populateSpinnerWithArray(_daySpinner, android.R.layout.simple_spinner_item, R.array.names_day,
        android.R.layout.simple_spinner_dropdown_item);
    _daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        LogController.USER_ACTION_UI.logMessage("Day Selected: " + parent.getSelectedItem() + ", position: "
            + parent.getSelectedItemPosition());
        daySelected = parent.getSelectedItem().toString();

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        LogController.USER_ACTION_UI.logMessage("No Day Selected - Unexpected condition" + ", position: "
            + parent.getSelectedItemPosition());
      }
    });

    Calendar cal = Calendar.getInstance();
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.SATURDAY) {
      _daySpinner.setSelection(1);
    } else if (dayOfWeek == Calendar.SUNDAY) {
      _daySpinner.setSelection(2);
    } else {
      // Select Friday
      _daySpinner.setSelection(0);
    }

    // Register "this" as the onClick listener for search button
    Button searchButton = (Button) this.findViewById(R.id.sets_search_button_search);
    searchButton.setOnClickListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    LogController.LIFECYCLE_ACTIVITY.logMessage("SearchSetsActivity OnResume()");
    String lastQueriedDay = appController.getDayToQuery();
    int lastQueriedWeek = appController.getWeekToQuery();
    int lastQueriedYear = appController.getYearToQuery();

    LogController.OTHER.logMessage("SearchSetsActivity found last queried day[" + lastQueriedDay + "] week["
        + lastQueriedWeek + "] year[" + lastQueriedYear + "]");

    if (lastQueriedYear == 2013) {
      _yearSpinner.setSelection(0);
    } else if (lastQueriedYear == 2012) {
      _yearSpinner.setSelection(1);
    }

    if (lastQueriedWeek == 1) {
      _weekendSpinner.setSelection(0);
    } else if (lastQueriedWeek == 2) {
      _weekendSpinner.setSelection(1);
    }

    if (lastQueriedDay.equals("Friday")) {
      _daySpinner.setSelection(0);
    } else if (lastQueriedDay.equals("Saturday")) {
      _daySpinner.setSelection(1);
    } else if (lastQueriedDay.equals("Sunday")) {
      _daySpinner.setSelection(2);
    }

  }

  /**
   * Fires when the Search ("Go!") button is pressed, this object is the OnClick listener.
   * 
   * @param v
   */

  public void onClick(View v) {
    if (v.getId() == R.id.sets_search_button_search) {
      LogController.USER_ACTION_UI.logMessage("Search button pressed");

      try {
        // Intent intent = new Intent();
        // Bundle bun = new Bundle();
        //
        // bun.putString(YEAR, yearSelected);
        // bun.putString(WEEK, weekSelected);
        // bun.putString(DAY, daySelected);
        //
        // intent.setClass(this, CoachellerActivity.class);
        //
        // intent.putExtras(bun);
        // startActivity(intent);

        appController.updateSearchFields(yearSelected, weekSelected, daySelected);

        finish();
      } catch (Exception e) {
        e.printStackTrace();
        Toast exceptionText = Toast.makeText(this, "Exception Launching Activity", 5);
        exceptionText.show();
      }
    } else {
      LogController.USER_ACTION_UI.logMessage("An unknown button was pressed - Unexpected");
    }

  }

  public void setUIWeek1() {
    _weekendSpinner.setSelection(0);
  }

  public void setUIWeek2() {
    _weekendSpinner.setSelection(1);
  }
}
