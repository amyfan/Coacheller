package com.coacheller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

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

    
    JSONArray results = sendHttpRequestToServer();
    
    
    try {
      
      JSONObject obj = results.getJSONObject(0);
      CoachellerApplication.debug(this, obj.toString());
      CoachellerApplication.debug(this, obj.names().toString());
      
      
      JSONArraySortMap sortMapArtist = new JSONArraySortMap(results, "artist", JSONArraySortMap.VALUE_STRING);
      for (int i = 0; i < results.length(); i++) {  
        CoachellerApplication.debug(this, sortMapArtist.getSortedStringValue(i).toString());
      }
      
      JSONArraySortMap sortMapId = new JSONArraySortMap(results, "id", JSONArraySortMap.VALUE_INTEGER);
      for (int i = 0; i < results.length(); i++) {  
        CoachellerApplication.debug(this, sortMapId.getSortedIntValue(i).toString());
      }
      
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Sends an HttpRequest to SDCrimeZone-AppEngine with lat, long, radius (and
   * date). Gets the JSON result and returns it. Should ONLY be called from main
   * page.
   * 
   **/
  private JSONArray sendHttpRequestToServer() {
    // get the current GPS coordinates, distance, and dates selected

    try {

      HttpResponse response;
      HttpClient hc = new DefaultHttpClient();

      // TODO: obviously populate properties properly

      // Sample URL
      // http://ratethisfest.appspot.com/coachellerServlet?year=2012&day=Friday&weekend=1&email=sample@email.com
      String requestString = "http://ratethisfest.appspot.com/coachellerServlet?year="
          + "2012" + "&day=" + "Friday" + "&weekend=" + "1" + "&email=" + "sample@email.com";
      CoachellerApplication.debug(this, "HTTPGet = " +requestString);
      HttpGet get = new HttpGet(requestString);
      response = hc.execute(get);
      
      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        CoachellerApplication.debug(this, "Received HTTP Response");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
            builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONArray finalResult = new JSONArray(tokener);     
        return finalResult;
  
        /*
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
        */
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