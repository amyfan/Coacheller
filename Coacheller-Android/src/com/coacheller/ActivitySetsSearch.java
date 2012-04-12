package com.coacheller;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ActivitySetsSearch extends Activity implements OnClickListener {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
  
      CoachellerApplication.debug(this, "Search Sets Activity Launched");
      this.initializeApp();
  }
  
  
  public void initializeApp() {
    setContentView(R.layout.sets_search);
    

    // Setup Week Spinner
    Spinner weekendSpinner = (Spinner) this.findViewById(R.id.search_spinner_week);
    CoachellerApplication.populateSpinnerWithArray(weekendSpinner, R.array.names_week);
    weekendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        CoachellerApplication.debug(parent.getContext(),
            "Week Selected: " + parent.getSelectedItem());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        CoachellerApplication.debug(parent.getContext(), "No Week Selected - Unexpected condition");
      }
    });

    if (CoachellerApplication.whichWeekIsToday() == 1) {
      CoachellerApplication.debug(this, "Date suggests week 1");
      weekendSpinner.setSelection(0);
      
    } else if (CoachellerApplication.whichWeekIsToday() == 2) {
      CoachellerApplication.debug(this, "Date suggests week 2");
      weekendSpinner.setSelection(1);
    }

    // Setup day spinner
    Spinner daySpinner = (Spinner) this.findViewById(R.id.search_spinner_day);
    CoachellerApplication.populateSpinnerWithArray(daySpinner, R.array.names_day);
    daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        CoachellerApplication.debug(parent.getContext(),
            "Day Selected: " + parent.getSelectedItem());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        CoachellerApplication.debug(parent.getContext(), "No Day Selected - Unexpected condition");
      }
    });

    Calendar cal = Calendar.getInstance();
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.FRIDAY) {
      daySpinner.setSelection(0);
    } else if (dayOfWeek == Calendar.SATURDAY) {
      daySpinner.setSelection(1);
    } else if (dayOfWeek == Calendar.SUNDAY) {
      daySpinner.setSelection(2);
    } else {
      // Select Friday
      daySpinner.setSelection(0);
    }

    // Register "this" as the onClick listener for search button
    Button searchButton = (Button) this.findViewById(R.id.sets_search_button_search);
    searchButton.setOnClickListener(this);
  }
  
  /**
   * Fires when the Search ("Go!") button is pressed, this object is the OnClick
   * listener.
   * 
   * @param v
   */

  public void onClick(View v) {
    if (v.getId() == R.id.sets_search_button_search) {
      CoachellerApplication.debug(this, "Search button pressed");

      try {
        String results = "";
        Intent intent = new Intent();
        Bundle bun = new Bundle();

        bun.putString("results", results); // add two parameters: a
                                           // string and a boolean
        //EditText addr = (EditText) this.findViewById(R.id.addressText);
        //String currentAddress = addr.getText().toString();

        //bun.putString("year", _selectedDate);
       //bun.putString("radius", _selectedRadius);

        /*
         * Check if the current address entered is actually in San Diego
         */
        /*
         * if (latlong[0] >= 33.427045 || latlong[1] <= -117.612003 ||
         * latlong[0] <= 32 || latlong[1] >= -116.0775811) { Toast notInSD =
         * Toast.makeText(this, "Currently only supporting San Diego locations",
         * 5); notInSD.show(); } else {
         */
        // intent.setClass(this, SDCrimeSummaryActivity.class);
        intent.putExtras(bun);
        startActivity(intent);
        // }
      } catch (Exception e) {
        e.printStackTrace();
        Toast addrNotFound = Toast.makeText(this, "Address Not Found", 5);
        addrNotFound.show();
      }
    } else {
      CoachellerApplication.debug(this, "An unknown button was pressed - Unexpected");
    }

  }

}
