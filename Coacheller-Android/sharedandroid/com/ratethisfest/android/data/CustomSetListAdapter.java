package com.ratethisfest.android.data;

import org.json.JSONArray;
import org.json.JSONException;

import com.ratethisfest.android.AndroidConstants;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.ListAdapter;

public abstract class CustomSetListAdapter implements ListAdapter {

  protected Context _context;
  protected String _timeFieldName;
  protected String _stageFieldName;
  protected JSONArray _data; // unsorted sets
  protected JSONArraySortMap _sortMap; // sorted sets
  protected JSONArrayHashMap _myRatings_JAHM;

  public void setContext(Context context) {
    _context = context;
  }

  public void setTimeFieldName(String name) {
    _timeFieldName = name;
  }

  public void setStageFieldName(String name) {
    _stageFieldName = name;
  }

  public void setData(JSONArray data) {
    _data = data;
  }

  public void sortByField(String fieldName, int dataType) throws JSONException {
    _sortMap = new JSONArraySortMap(_data, fieldName, dataType);
  }

  @Override
  public int getCount() {
    if (_data == null) {
      return 0;
    } else {
      return _data.length();
    }
  }

  @Override
  public Object getItem(int position) {
    try {
      return _sortMap.getSortedJSONObj(position);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getItemViewType(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public boolean hasStableIds() {
    // TODO Auto-generated method stub
    return false;
  }

  // Implemented prior to week 2, hope nothing breaks.
  @Override
  public boolean isEmpty() {
    if (_data.length() == 0) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void registerDataSetObserver(DataSetObserver observer) {
    // TODO Auto-generated method stub

  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver observer) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean areAllItemsEnabled() {
    return true;
  }

  @Override
  public boolean isEnabled(int position) { // Everything is clickable
    return true;
  }

  // If we are ready to draw the view
  // Needed for concurrency issues
  protected boolean haveData() {
    if (_data != null) {
      return true;
    }
    return false;
  }

  public void resortSetList(String sortMode) throws JSONException {
    if (AndroidConstants.SORT_TIME.equals(sortMode)) {
      sortByField(_timeFieldName, JSONArraySortMap.VALUE_INTEGER);
    } else if (AndroidConstants.SORT_ARTIST.equals(sortMode)) {
      sortByField(AndroidConstants.SORT_ARTIST, JSONArraySortMap.VALUE_STRING);
    } else if (AndroidConstants.SORT_STAGE.equals(sortMode)) {
      sortByField(_stageFieldName, JSONArraySortMap.VALUE_STRING);
    }
  }
}
