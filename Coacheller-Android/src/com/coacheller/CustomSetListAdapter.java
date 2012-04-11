package com.coacheller;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;


public class CustomSetListAdapter implements ListAdapter {

  private JSONArray _data;
  private JSONArraySortMap _sortMap;
  private Context _context;
  
  public CustomSetListAdapter(Context context) {
    _context = context;
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
    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.row_set_info, parent, false);
    
    try {
      TextView textSetTime = (TextView)rowView.findViewById(R.id.text_set_time);
      String timeStr = _sortMap.getSortedJSONObj(position).getString("time");
      int timeStrLen = timeStr.length();
      timeStr = timeStr.substring(0, timeStrLen-2) +":"+ timeStr.substring(timeStrLen-2, timeStrLen);
      textSetTime.setText(timeStr);
      
      
      TextView textArtistName = (TextView)rowView.findViewById(R.id.text_artist_name);
      textArtistName.setText(_sortMap.getSortedJSONObj(position).getString("artist"));
      
      TextView textWeek1Rating = (TextView)rowView.findViewById(R.id.text_wk1_rating);
      textWeek1Rating.setText("Wk1 Avg: "+ _sortMap.getSortedJSONObj(position).getString("avg_score_one"));
      
      TextView textWeek2Rating = (TextView)rowView.findViewById(R.id.text_wk2_rating);
      textWeek2Rating.setText("Wk2 Avg: "+ _sortMap.getSortedJSONObj(position).getString("avg_score_two"));
      
      //TextView textMyRating = (TextView)rowView.findViewById(R.id.text_my_rating);
      
      
      
    } catch (JSONException e) {
       //TODO Auto-generated catch block
      e.printStackTrace();
    }
   
    return rowView;
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
    return true;
  }

  @Override
  public boolean isEnabled(int position) {
    // TODO Auto-generated method stub
    return false;
  }
  
  

}
