package com.ratethisfest.android.data;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONArrayHashMap {

  private String keyName1;
  private String keyName2;

  // TODO: Could be expanded to operate with an arbitrary number of keys
  private HashMap<String, JSONObject> _hash = new HashMap<String, JSONObject>();

  public JSONArrayHashMap(String keyName1, String keyName2) {
    setKeyNames(keyName1, keyName2);
  }

  // TODO this could be the reason for the crash in the release version.
  // TODO This constructor does not initialize the hashmap
  // TODO Inconsistency between re-creating and re-using JAHM object could have
  // made the bug harder to find

  public void setKeyNames(String keyName1, String keyName2) {
    this.keyName1 = keyName1;
    this.keyName2 = keyName2;

  }

  public void rebuildDataWith(JSONArray data) throws JSONException {
    wipeData();
    for (int i = 0; i < data.length(); i++) {
      JSONObject obj = (JSONObject) data.get(i);
      // TODO: is it really necessary to pass these values?
      storeTwoKeyObj(obj);
    }
  }

  public JSONArrayHashMap(JSONArray data, String firstKeyName, String secondKeyName)
      throws JSONException {
    setKeyNames(firstKeyName, secondKeyName);
    rebuildDataWith(data);
  }

  public void wipeSchema() {
    keyName1 = "";
    keyName2 = "";
    wipeData();
  }

  public void wipeData() {
    _hash.clear();
  }

  // Not used in release
  /*
   * public JSONArrayHashMap(JSONArray data, String keyName) throws
   * JSONException { _keyName1 = keyName;
   * 
   * for (int i = 0; i < data.length(); i++) { JSONObject obj = (JSONObject)
   * data.get(i); _hash.put(obj.getString(_keyName1), obj); } }
   */

  private void storeTwoKeyObj(JSONObject obj) throws JSONException {
    _hash.put(obj.getString(keyName1) + "-" + obj.getString(keyName2), obj);
  }

  // This should be called using a function specific to a two-parameter key
  public JSONObject getJSONObject(String value) {
    return _hash.get(value);
  }

  public void addValues(JSONObject obj) throws JSONException {
    storeTwoKeyObj(obj);
  }

  public JSONObject getJSONObject(String value1, String value2) {
    return _hash.get(value1 + "-" + value2);
  }

  public Set<String> getKeys() {
    return _hash.keySet();
  }

  // Not used, so commented
  /*
   * public String getKeyName() { return _keyName1; }
   */
}
