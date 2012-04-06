package com.coacheller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CoachellerActivity extends Activity implements View.OnClickListener {

  public static Location _currLocation;
  public static String _selectedRadius;
  public static String _selectedDate;

  /** Called by Android Framework when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CoachellerApplication.debug(this, "App Launched");

    this.createLocationManager(); // TODO: get rid of this?
    this.initializeApp();

  }

  public void initializeApp() {
    setContentView(R.layout.sets_search);
    Calendar cal = Calendar.getInstance();

    // Setup Week Spinner
    Spinner weekendSpinner = (Spinner) this.findViewById(R.id.search_spinner_week);
    populateSpinnerWithArray(weekendSpinner, R.array.names_week);
    weekendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // TODO: save selection in memory
        CoachellerApplication.debug(parent.getContext(),
            "Week Selected: " + parent.getSelectedItem());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        CoachellerApplication.debug(parent.getContext(), "No Week Selected - Unexpected condition");
      }
    });

    // Detect which week we are in
    // Hard coded, so I am probably going to hell,
    // but the error is not fatal next year
    // TODO un-hardcode day of month value
    if (cal.get(Calendar.DAY_OF_MONTH) < 19) {

      CoachellerApplication.debug(this, "Date suggests week 1");
      weekendSpinner.setSelection(0);

    } else {
      CoachellerApplication.debug(this, "Date suggests week 2");
      weekendSpinner.setSelection(1);
    }

    // Setup day spinner
    Spinner daySpinner = (Spinner) this.findViewById(R.id.search_spinner_day);
    populateSpinnerWithArray(daySpinner, R.array.names_day);
    daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // TODO Save selection in memory
        CoachellerApplication.debug(parent.getContext(),
            "Day Selected: " + parent.getSelectedItem());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        CoachellerApplication.debug(parent.getContext(), "No Day Selected - Unexpected condition");
      }
    });

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
   * populateSpinnerWithArray Populates dropdown boxes with options, based on
   * string arrays.
   * 
   * @param spinner
   * @param stringArrayResId
   */
  public static void populateSpinnerWithArray(Spinner spinner, int stringArrayResId) {
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
        android.R.layout.simple_spinner_item, spinner.getContext().getResources()
            .getStringArray(stringArrayResId));
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
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
        String results = this.sendHttpRequestToServer(v);
        Intent intent = new Intent();
        Bundle bun = new Bundle();

        bun.putString("results", results); // add two parameters: a
                                           // string and a boolean
        EditText addr = (EditText) this.findViewById(R.id.addressText);
        String currentAddress = addr.getText().toString();

        bun.putString("year", _selectedDate);
        bun.putString("radius", _selectedRadius);

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

  /**
   * Sends an HttpRequest to SDCrimeZone-AppEngine with lat, long, radius (and
   * date). Gets the JSON result and returns it. Should ONLY be called from main
   * page.
   * 
   **/
  private String sendHttpRequestToServer(View v) {
    // get the current GPS coordinates, distance, and dates selected

    JSONArray jsonObjs = new JSONArray();

    EditText addr = (EditText) this.findViewById(R.id.addressText);
    Spinner dist = (Spinner) this.findViewById(R.id.distanceList);
    String currentAddress = addr.getText().toString();

    try {

      HttpResponse response;
      HttpClient hc = new DefaultHttpClient();

      // TODO: obviously populate properties properly

      // Sample URL
      // http://ratethisfest.appspot.com/coachellerServlet?year=2012&day=Friday&weekend=1&email=sample@email.com
      String requestString = "HTTPGet = http://ratethisfest.appspot.com/coachellerServlet?year="
          + "2012" + "&day=" + "Friday" + "&weekend=" + "1" + "&email=" + "sample@email.com";
      CoachellerApplication.debug(this, requestString);
      HttpGet get = new HttpGet(requestString);
      response = hc.execute(get);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        File responseFile = File.createTempFile("results", "json", this.getFilesDir());
        // Buffers
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(response.getEntity()
            .getContent()));
        BufferedWriter bufWriter = new BufferedWriter(new FileWriter(responseFile));
        int nbCharRead = 0;
        int i = 0;
        int totalRead = 0;
        char[] buffer = new char[10000];

        while ((nbCharRead = bufReader.read(buffer, 0, 10000)) != -1) {
          totalRead += nbCharRead;
          // System.out.println("buffer = " + String.valueOf(buffer));
          bufWriter.write(buffer, 0, nbCharRead);
        }

        if (bufWriter != null) {
          bufWriter.flush();
          bufWriter.close();
        }

        return responseFile.getPath();
      } else {
        CoachellerApplication.debug(this, "HTTP Response was not OK: "
            + response.getStatusLine().getStatusCode());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;

  }

  // ///////Not needed for Coacheller for Android, Saved for Example
  // /TODO: Delete from source

  public void onClickOld(View v) {
    if (v.getId() == R.id.submitButton) {
      try {
        String results = this.sendHttpRequestToServer(v);
        Intent intent = new Intent();
        Bundle bun = new Bundle();

        bun.putString("results", results); // add two parameters: a
                                           // string and a boolean
        EditText addr = (EditText) this.findViewById(R.id.addressText);
        String currentAddress = addr.getText().toString();

        bun.putString("year", _selectedDate);
        bun.putString("radius", _selectedRadius);

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
    }

  }

  // Register app for location updates, not needed for Coacheller
  public void createLocationManager() {
    // Acquire a reference to the system Location Manager
    LocationManager locationManager = (LocationManager) this
        .getSystemService(Context.LOCATION_SERVICE);

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
      public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        updateAddressWithCurrentLocation(location);
      }

      public void onStatusChanged(String provider, int status, Bundle extras) {
      }

      public void onProviderEnabled(String provider) {
      }

      public void onProviderDisabled(String provider) {
      }
    };

    // Register the listener with the Location Manager to receive location
    // updates
    locationManager
        .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
  }

  /**
   * Take user to landing page for SD Crime Zone. Populate the dropdown boxes
   * (Distance and Date)
   */
  public void initializeAppOld() {

    setContentView(R.layout.main);

    // get current GPS coordinates and set as default for app
    EditText currLocationText = (EditText) this.findViewById(R.id.addressText);
    currLocationText.setText(getString(R.string.defaultLocation));

    // the distance dropdown list
    Spinner distanceList = (Spinner) this.findViewById(R.id.distanceList);
    populateSpinnerWithArray(distanceList, R.array.distanceArray);
    distanceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        _selectedRadius = parent.getSelectedItem().toString();
        _selectedRadius = _selectedRadius.replaceFirst("\\smile", "");
      }

      public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
        _selectedRadius = parent.getSelectedItem().toString();
        _selectedRadius = _selectedRadius.replaceFirst("\\smile", "");
      }
    });

    // the year dropdown list
    Spinner dateList = (Spinner) this.findViewById(R.id.datesList);
    populateSpinnerWithArray(dateList, R.array.dateArray);
    dateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        _selectedDate = parent.getSelectedItem().toString();
      }

      public void onNothingSelected(AdapterView<?> parent) {
        // parent.setSelection(0);
        _selectedDate = parent.getSelectedItem().toString();
      }
    });
    Button submitButton = (Button) this.findViewById(R.id.submitButton);
    submitButton.setOnClickListener(this);
  }

  public void updateAddressWithCurrentLocation(Location location) {
    EditText addrText = (EditText) this.findViewById(R.id.addressText);
    if (addrText != null
        && addrText.getText().toString().equals(getString(R.string.defaultLocation))) {
      _currLocation = location;
    }
    // this.debug("Location= " + String.valueOf(location.getLatitude())
    // + ", " + String.valueOf(location.getLongitude()));
  }

}