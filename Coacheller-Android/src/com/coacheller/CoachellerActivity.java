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
    
    CoachellerApplication.debug(this, "CoachellerActivity Launched");
    initializeApp();
  }

  

  



  private void initializeApp() {
    setContentView(R.layout.sets_list);
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

  @Override
  public void onClick(View arg0) {
    // TODO Auto-generated method stub
    
  }

}