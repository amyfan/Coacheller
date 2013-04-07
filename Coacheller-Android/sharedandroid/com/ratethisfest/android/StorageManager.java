package com.ratethisfest.android;

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

import com.ratethisfest.android.log.LogController;

import android.content.Context;

/**
 * TODO: Make this singleton, especially in iOS impl
 * 
 * @author Amy
 * 
 */
public class StorageManager {

  private HashMap<String, Object> _data;
  private Context _context;
  private String saveFileName;

  public StorageManager(Context context, String saveFileName) {
    _context = context;
    this.saveFileName = saveFileName;
  }

  public Set<String> getProperties() {
    return _data.keySet();
  }


  // File gets saved here on a good day
  // /data/data/com.lollapaloozer/files/CoachellerData.dat
  public synchronized void save() {

    try {
      FileOutputStream fos = _context.openFileOutput(saveFileName, Context.MODE_PRIVATE);
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
      FileInputStream fis = _context.openFileInput(saveFileName);
      ObjectInputStream is = new ObjectInputStream(fis);
      _data = (HashMap<String, Object>) is.readObject();
      is.close();
    } catch (FileNotFoundException e) {
      LogController.ERROR.logMessage("FileNotFoundException deserializing stored data");
      e.printStackTrace();
    } catch (StreamCorruptedException e) {
      LogController.ERROR.logMessage("StreamCorruptedException deserializing stored data");
      e.printStackTrace();
    } catch (IOException e) {
      LogController.ERROR.logMessage("IOException deserializing stored data");
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      LogController.ERROR.logMessage("ClassNotFoundException deserializing stored data");
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
