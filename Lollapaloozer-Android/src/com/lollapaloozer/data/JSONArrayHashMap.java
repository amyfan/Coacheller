package com.lollapaloozer.data;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONArrayHashMap {

  private String _key1Name;
  private String _key2Name;

  
  //TODO: Could be expanded to operate with an arbitrary number of keys
  private HashMap<String, JSONObject> _hash = new HashMap<String, JSONObject>();

  public JSONArrayHashMap(String keyName) {
    _key1Name = keyName;
  }

  // TODO this could be the reason for the crash in the release version.
  // TODO This constructor does not initialize the hashmap
  // TODO Inconsistency between re-creating and re-using JAHM object could have
  // made the bug harder to find

  public void setKeyNames(String key1Name, String key2Name) {
    _key1Name = key1Name;
    _key2Name = key2Name;

  }
  
  public void rebuildDataWith(JSONArray data) throws JSONException {
    wipeData();
    for (int i = 0; i < data.length(); i++) {
      JSONObject obj = (JSONObject) data.get(i);
      storeTwoKeyObj(_key1Name, _key2Name, obj);
    }
  }

  public JSONArrayHashMap(String key1Name, String key2Name) {
    setKeyNames(key1Name, key2Name);
  }
  
  public JSONArrayHashMap(JSONArray data, String firstKeyName, String secondKeyName)
  throws JSONException {
    setKeyNames(firstKeyName, secondKeyName);
    rebuildDataWith(data);
  }

  public void wipeSchema() {
    _key1Name = "";
    _key2Name = "";
    wipeData();
  }
  
  public void wipeData() {
    _hash.clear();
  }

  // Not used in release
  /*
   * public JSONArrayHashMap(JSONArray data, String keyName) throws
   * JSONException { _key1Name = keyName;
   * 
   * for (int i = 0; i < data.length(); i++) { JSONObject obj = (JSONObject)
   * data.get(i); _hash.put(obj.getString(_key1Name), obj); } }
   */



  private void storeTwoKeyObj(String key1Name, String key2Name, JSONObject obj)
      throws JSONException {
    _hash.put(obj.getString(key1Name) + "-" + obj.getString(key2Name), obj);
  }

  // This should be called using a function specific to a two-parameter key
  public JSONObject getJSONObject(String value) {
    return _hash.get(value);
  }

  public void addValues(String key1, String key2, JSONObject obj) throws JSONException {
    storeTwoKeyObj(key1, key2, obj);
  }

  public JSONObject getJSONObject(String value1, String value2) {
    return _hash.get(value1 + "-" + value2);
  }

  public Set<String> getKeys() {
    return _hash.keySet();
  }

  // Not used, so commented
  /*
   * public String getKeyName() { return _key1Name; }
   */
}
