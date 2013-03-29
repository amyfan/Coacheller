package com.ratethisfest.android.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONArraySortMap {

  final public static int VALUE_INTEGER = 0;
  final public static int VALUE_STRING = 1;

  private ArrayList<CustomPair<Integer, Object>> _pairs = new ArrayList<CustomPair<Integer, Object>>();
  JSONArray _arrayToSort;
  private String _parameterToSort;
  private int _valueType;

  public JSONArraySortMap(JSONArray arraySort, String parameterSort, int valType)
      throws JSONException {
    // init
    _arrayToSort = arraySort;
    _valueType = valType;
    _parameterToSort = parameterSort;

    if (_valueType < 0 || 1 < _valueType) {
      throw new RuntimeException();
    }

    // read values in
    for (int i = 0; i < _arrayToSort.length(); i++) {
      JSONObject currentObj = _arrayToSort.getJSONObject(i);
      Object value = null;

      if (_valueType == VALUE_INTEGER) {
        value = currentObj.getInt(_parameterToSort);
      } else if (_valueType == VALUE_STRING) {
        value = currentObj.getString(_parameterToSort);
      }

      CustomPair<Integer, Object> nextPair = new CustomPair<Integer, Object>(i, value);
      // System.out.println(i + " || " + value.toString());
      _pairs.add(nextPair);

    }

    Comparator<CustomPair<Integer, Object>> comparator = new Comparator<CustomPair<Integer, Object>>() {

      public int compare(CustomPair<Integer, Object> pairA, CustomPair<Integer, Object> pairB) {
        if (_valueType == VALUE_INTEGER) {
          Integer aValue = (Integer) pairA.second;
          Integer bValue = (Integer) pairB.second;
          return aValue.compareTo(bValue);
        } else if (_valueType == VALUE_STRING) {
          String aValue = (String) (pairA.second);
          String bValue = (String) (pairB.second);
          return aValue.toLowerCase().compareTo(bValue.toLowerCase());
        } else {
          throw new RuntimeException();
        }
      }
    };

    Collections.sort(_pairs, comparator);
  }

  public JSONObject getSortedJSONObj(int index) throws JSONException {
    // if (_valueType != VALUE_INTEGER) {
    // throw new RuntimeException();
    // }

    Integer indexToReturn = (Integer) _pairs.get(index).first;
    return _arrayToSort.getJSONObject(indexToReturn);
  }

  // Redundant, did not need
  /*
   * public JSONObject getSortedStringValue(int index) throws JSONException { if
   * (_valueType != VALUE_STRING) { throw new RuntimeException(); }
   * 
   * Integer indexToReturn = (Integer) _pairs.get(index).first; return
   * _arrayToSort.getJSONObject(indexToReturn); }
   */
}
