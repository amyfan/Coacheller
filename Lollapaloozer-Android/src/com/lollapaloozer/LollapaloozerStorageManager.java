package com.lollapaloozer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

public class LollapaloozerStorageManager {

  private HashMap<String, Object> _data;
  private Context _context;

  public LollapaloozerStorageManager(Context context) {
    _context = context;
  }

  public Set<String> getProperties() {
    return _data.keySet();
  }

  // Don't use this ever again
  /*
   * priva/
   *//**//* te String resolveSav *//**//* eFilePath() { */
  /* File sdCar *//* d = Environment.getE *//* xternalStora *//* geDirectory(); */
  /* return sdCard *//* .getAbsolutePath() + _context *//* .getString(R.stri *//*
                                                                                * ng
                                                                                * .
                                                                                * save_file_path
                                                                                * )
                                                                                * ;
                                                                                */
  /* } *//**/

  private String getSaveFileName() {
    return _context.getString(R.string.save_file_name);
  }

  // File gets saved here on a good day
  // /data/data/com.lollapaloozer/files/CoachellerData.dat
  public synchronized void save() {

    try {
      FileOutputStream fos = _context.openFileOutput(getSaveFileName(), Context.MODE_PRIVATE);
      ObjectOutputStream os = new ObjectOutputStream(fos);
      os.writeObject(_data);
      os.close();
      // System.out.println("FILE SHOULD BE WRITTEN NOW");
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  // File should get loaded from here, if we are all extremely lucky
  // /data/data/com.lollapaloozer/files/CoachellerData.dat
  public synchronized void load() {

    try {
      // fis = _context.openFileInput(saveFile.getName());
      FileInputStream fis = _context.openFileInput(getSaveFileName());
      ObjectInputStream is = new ObjectInputStream(fis);
      _data = (HashMap<String, Object>) is.readObject();
      is.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (StreamCorruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (_data == null) {
      _data = new HashMap<String, Object>();
    }
  }

  public synchronized void putString(String name, String value) {
    _data.put(name, value);
  }

  public synchronized String getString(String name) {
    return (String) _data.get(name);
  }

  public synchronized void putJSONArray(String dataName, JSONArray data) {
    _data.put(dataName, data.toString());
  }

  public synchronized JSONArray getJSONArray(String dataName) throws JSONException {
    String storedArrayAsString = (String) _data.get(dataName);

    if (storedArrayAsString == null) {
      return new JSONArray();
    } else {
      return new JSONArray(storedArrayAsString);
    }
  }

  public synchronized void putObject(String objectName, Object obj) {
    _data.put(objectName, obj);
  }

  public synchronized Object getObject(String objectName) {
    return (Object) _data.get(objectName);
  }

}
