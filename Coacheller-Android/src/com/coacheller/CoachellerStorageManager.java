package com.coacheller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.os.Environment;

public class CoachellerStorageManager {
  
  private HashMap<String, Object> _data;
  private Context _context;
  
  public CoachellerStorageManager(Context context) {
    _context = context;
  }
  
 
  public Set<String> getProperties() {
    return _data.keySet();
  }
  
  public void save() {
    File sdCard = Environment.getExternalStorageDirectory(); 
    File saveFile = new File (sdCard.getAbsolutePath() + R.string.save_file_full_path);
    saveFile.getParentFile().mkdirs();
    
    try {
      FileOutputStream fos = _context.openFileOutput(saveFile.getAbsolutePath(), Context.MODE_PRIVATE);
      ObjectOutputStream os = new ObjectOutputStream(fos);
      os.writeObject(_data);
      os.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }
  
  public void load() {
    File sdCard = Environment.getExternalStorageDirectory(); 
    File saveFile = new File (sdCard.getAbsolutePath() + R.string.save_file_full_path);

    
    try {
      FileInputStream fis= _context.openFileInput(saveFile.getAbsolutePath());
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
    
  }
  
  public void putString(String name, String value) {
    _data.put(name, value);
  }
  
  public String getString(String name) {
    return (String)_data.get(name);
  }

}
