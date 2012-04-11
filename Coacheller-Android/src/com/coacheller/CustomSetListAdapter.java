package com.coacheller;

import org.json.JSONArray;
import org.json.JSONException;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;


public class CustomSetListAdapter implements ListAdapter {

  private JSONArray _data;
  private JSONArraySortMap _sortMap;
  
  public CustomSetListAdapter() {
  }
  
  public void setData(JSONArray data) {
    _data = data;
  }
  
  public void sortByField(String fieldName, int dataType) throws JSONException {
    _sortMap = new JSONArraySortMap(_data, fieldName, dataType);
  }
  
  private void assertInitialized() {
    if (_data == null) {
      throw new RuntimeException("Was never initialized");
    }
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
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
  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getViewTypeCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean hasStableIds() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
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
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEnabled(int position) {
    // TODO Auto-generated method stub
    return false;
  }
  
  

}
