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

  private ArrayList<CustomPair<Integer, JSONObject>> sortedArrayOfPairs = new ArrayList<CustomPair<Integer, JSONObject>>();

  public JSONArraySortMap(JSONArray arrayToSort, String parameterToSort, int valueType,
      String optionalSecondParam, int valueTypeTwo) throws JSONException {

    ArrayList<CustomPair<Integer, JSONObject>> arrayOfPairs = createArrayOfPairs(arrayToSort);
    if (optionalSecondParam != null) {
      arrayOfPairs = sortThePairArray(arrayOfPairs, optionalSecondParam, valueTypeTwo);
    }
    sortedArrayOfPairs = sortThePairArray(arrayOfPairs, parameterToSort, valueType);
  }

  private ArrayList<CustomPair<Integer, JSONObject>> createArrayOfPairs(JSONArray arrayToSort)
      throws JSONException {
    ArrayList<CustomPair<Integer, JSONObject>> arrayOfPairs = new ArrayList<CustomPair<Integer, JSONObject>>();

    // read values in
    for (int i = 0; i < arrayToSort.length(); i++) {
      JSONObject currentObj = arrayToSort.getJSONObject(i);
      CustomPair<Integer, JSONObject> nextPair = new CustomPair<Integer, JSONObject>(i, currentObj);
      // System.out.println(i + " || " + value.toString());
      arrayOfPairs.add(nextPair);

    }

    return arrayOfPairs;
  }

  private ArrayList<CustomPair<Integer, JSONObject>> sortThePairArray(
      ArrayList<CustomPair<Integer, JSONObject>> arrayToSort, String parameterToSort, int valueType)
      throws JSONException {
    final int type = valueType;
    final String param = parameterToSort;

    Comparator<CustomPair<Integer, JSONObject>> comparator = new Comparator<CustomPair<Integer, JSONObject>>() {

      @Override
      public int compare(CustomPair<Integer, JSONObject> pairA,
          CustomPair<Integer, JSONObject> pairB) {
        try {
          if (type == VALUE_INTEGER) {
            Integer aValue = pairA.second.getInt(param);
            Integer bValue = pairB.second.getInt(param);
            return aValue.compareTo(bValue);
          } else if (type == VALUE_STRING) {
            String aValue = pairA.second.getString(param);
            String bValue = pairB.second.getString(param);
            return aValue.toLowerCase().compareTo(bValue.toLowerCase());
          } else {
            throw new RuntimeException();
          }
        } catch (JSONException e) {
          throw new RuntimeException();
        }
      }
    };

    Collections.sort(arrayToSort, comparator);

    return arrayToSort;
  }

  public JSONObject getSortedJSONObj(int index) throws JSONException {
    return sortedArrayOfPairs.get(index).second;
  }
}
