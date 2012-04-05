package com.coacheller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

  public static Location currLocation;
  public static String selectedRadius;
  public static String selectedDate;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.createLocationManager();
    this.initializeApp();
  }

  /**
   * Take user to landing page for SD Crime Zone. Populate the dropdown boxes
   * (Distance and Date)
   */
  public void initializeApp() {
    setContentView(R.layout.main);
    // get current GPS coordinates and set as default for app
    EditText currLocationText = (EditText) this.findViewById(R.id.addressText);
    currLocationText.setText(getString(R.string.defaultLocation));
    // the distance dropdown list
    Spinner distanceList = (Spinner) this.findViewById(R.id.distanceList);
    populateSpinnerWithArray(distanceList, R.array.distanceArray);
    distanceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedRadius = parent.getSelectedItem().toString();
        selectedRadius = selectedRadius.replaceFirst("\\smile", "");
      }

      public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
        selectedRadius = parent.getSelectedItem().toString();
        selectedRadius = selectedRadius.replaceFirst("\\smile", "");
      }
    });

    // the distance dropdown list
    Spinner dateList = (Spinner) this.findViewById(R.id.datesList);
    populateSpinnerWithArray(dateList, R.array.dateArray);
    dateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedDate = parent.getSelectedItem().toString();
      }

      public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
        selectedDate = parent.getSelectedItem().toString();
      }
    });
    Button submitButton = (Button) this.findViewById(R.id.submitButton);
    submitButton.setOnClickListener(this);
  }

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

  public void updateAddressWithCurrentLocation(Location location) {
    EditText addrText = (EditText) this.findViewById(R.id.addressText);
    if (addrText != null
        && addrText.getText().toString().equals(getString(R.string.defaultLocation))) {
      currLocation = location;
    }
    // this.debug("Location= " + String.valueOf(location.getLatitude())
    // + ", " + String.valueOf(location.getLongitude()));
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
   * Handles all the apps onclick functions
   * 
   * @param v
   */
  public void onClick(View v) {
    if (v.getId() == R.id.submitButton) {
      try {
        String results = this.sendHttpRequestToServer(v);
        Intent intent = new Intent();
        Bundle bun = new Bundle();

        bun.putString("results", results); // add two parameters: a
                                           // string and a boolean
        EditText addr = (EditText) this.findViewById(R.id.addressText);
        String currentAddress = addr.getText().toString();

        bun.putString("year", selectedDate);
        bun.putString("radius", selectedRadius);

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
      CoachellerApplication.debug(this,
          "HTTPGet = http://ratethisfest.appspot.com/coachellerServlet?year=" + "2012" + "&day="
              + "Friday" + "&weekend=" + "1" + "&email=" + "sample@email.com");
      HttpGet get = new HttpGet("http://ratethisfest.appspot.com/coachellerServlet?year=" + "2012"
          + "&day=" + "Friday" + "&weekend=" + "1" + "&email=" + "sample@email.com");

      response = hc.execute(get);

      // get the response from the Google Apps Engine server, should be in JSON
      // format
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
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }

}