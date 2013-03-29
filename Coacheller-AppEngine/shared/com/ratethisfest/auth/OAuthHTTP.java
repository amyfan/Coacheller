package com.ratethisfest.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OAuthHTTP {

  private URL _url;
  private HttpURLConnection _urlConnection;
  private JSONObject _JSONResult;
  private ArrayList<NameValue> _reqProperties = new ArrayList<NameValue>();

  public OAuthHTTP(String url) throws MalformedURLException {
    _url = new URL(url);

  }

  public boolean execute() throws JSONException, IOException {
    try {
      _urlConnection = (HttpURLConnection) _url.openConnection();
      System.out.println(_urlConnection.getURL());

      for (NameValue nv : _reqProperties) {
        _urlConnection.setRequestProperty(nv._name, nv._value);
      }

      _urlConnection.connect();
      // Better way to do this?
      _JSONResult = _parseJSONResponse(_urlConnection.getInputStream());
      return true; // success
    } catch (IOException e) {
      System.out.println("IOException code path");
      _JSONResult = _parseJSONResponse(_urlConnection.getErrorStream());
      e.printStackTrace();
      return false; // request completed without success
    } catch (Exception e) {
      System.out.println("Exception code path");
      _JSONResult = _parseJSONResponse(_urlConnection.getErrorStream());
      e.printStackTrace();
      return false; // request completed without success
    }
  }

  public void setRequestProperty(String name, String value) {
    _reqProperties.add(new NameValue(name, value));
  }

  public String getJSONResultString(String propertyName) throws JSONException {
    return _JSONResult.getString(propertyName);
  }

  public boolean getJSONResultBoolean(String propertyName) throws JSONException {
    return _JSONResult.getBoolean(propertyName);
  }

  private JSONObject _parseJSONResponse(InputStream inputStream) throws IOException, JSONException {
    StringBuilder builder = _readStream(inputStream, false);

    if (builder == null) {
      return null;
    }

    System.out.println("JSON Parsed:");
    JSONTokener tokener = new JSONTokener(builder.toString());

    // JSONArray finalResult = new JSONArray(tokener);

    JSONObject returnObject = new JSONObject(tokener);
    Iterator<String> it = returnObject.keys();

    while (it.hasNext()) {
      String fieldName = it.next();
      System.out.println(fieldName + ": " + returnObject.getString(fieldName));
    }

    return returnObject;
  }

  private StringBuilder _readStream(InputStream inStream, boolean output) throws IOException {
    if (inStream == null) {
      return null;
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
    StringBuilder builder = new StringBuilder();
    int linesRead = 0;

    for (String line = null; (line = reader.readLine()) != null;) {
      if (output) {

        System.out.println(line);
      }

      linesRead++;
      builder.append(line).append("\n");
    }
    System.out.println(linesRead + " lines read");
    reader.close();
    return builder;
  }
}
