package com.coacheller;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONArrayHashMap {
  
  private String _keyName;
  
  private HashMap<String, JSONObject> _hash = new HashMap<String, JSONObject>();
  
  public JSONArrayHashMap(String keyName) {
    _keyName = keyName;
  }
  
  public JSONArrayHashMap(JSONArray data, String keyName) throws JSONException {
    _keyName = keyName;
    
    for (int i = 0; i < data.length(); i++) {
      JSONObject obj = (JSONObject) data.get(i);
      _hash.put(obj.getString(_keyName), obj);
    }
  }
  
  public JSONArrayHashMap(JSONArray data, String firstKeyName, String secondKeyName) throws JSONException {
    _keyName = firstKeyName +"-"+ secondKeyName;
    
    for (int i = 0; i < data.length(); i++) {
      JSONObject obj = (JSONObject) data.get(i);
      storeTwoKeyObj(firstKeyName, secondKeyName, obj);
    }
  }
  
  private void storeTwoKeyObj(String key1Name, String key2Name, JSONObject obj) throws JSONException {
    _hash.put(obj.getString(key1Name) +"-"+ obj.getString(key2Name), obj);
  }
  
  public JSONObject getJSONObject(String value) {
    return _hash.get(value);
  }
  
  public void addValues(String key1, String key2, JSONObject obj) throws JSONException {
    storeTwoKeyObj(key1, key2, obj);
  }
  
  public JSONObject getJSONObject(String value1, String value2) {
    return _hash.get(value1 +"-"+ value2);
  }
  
  public Set<String> getKeys() {
    return _hash.keySet();
  }
  
  public String getKeyName() {
    return _keyName;
  }

}
