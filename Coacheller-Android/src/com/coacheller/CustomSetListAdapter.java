package com.coacheller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class CustomSetListAdapter implements ListAdapter {

  private JSONArray _data;
  private JSONArraySortMap _sortMap;
  private Context _context;
  private String _timeFieldName;
  private JSONArrayHashMap _myRatings_JAHM;
  
  static class ViewHolder {
    public TextView textTime;
    public TextView textArtist;
    public TextView ratingWk1;
    public TextView ratingWk2;
    public TextView myRating;
}
  
  public CustomSetListAdapter(Context context, String timeFieldName, JSONArrayHashMap myRatings_JAHM) {
    _context = context;
    _timeFieldName = timeFieldName;
    _myRatings_JAHM = myRatings_JAHM;
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
  public View getView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    
    
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_set_info, parent, false);
      ViewHolder viewHolder = new ViewHolder();
      viewHolder.textTime = (TextView)rowView.findViewById(R.id.text_set_time);
      viewHolder.textArtist = (TextView)rowView.findViewById(R.id.text_artist_name);
      viewHolder.ratingWk1 = (TextView)rowView.findViewById(R.id.text_wk1_rating);
      viewHolder.ratingWk2 = (TextView)rowView.findViewById(R.id.text_wk2_rating);
      viewHolder.myRating = (TextView)rowView.findViewById(R.id.text_my_rating);
      rowView.setTag(viewHolder);
    }
    
    try {

      ViewHolder holder = (ViewHolder) rowView.getTag();
      JSONObject setObj = _sortMap.getSortedJSONObj(position);
      String setId = setObj.getString(CoachellerActivity.QUERY_SETS__SET_ID);  //Get the set Id
      
      //Get Ratings for this set Id
      JSONObject ratingsObjWk1 = _myRatings_JAHM.getJSONObject(setId, "1"); 
      JSONObject ratingsObjWk2 = _myRatings_JAHM.getJSONObject(setId, "2");
      
      String score1 = "*";
      if (ratingsObjWk1 != null) {
        score1 = ratingsObjWk1.get(CoachellerActivity.QUERY_RATINGS__RATING).toString();
      }
      
      String score2 = "*";
      if (ratingsObjWk2 != null) {
        score2 = ratingsObjWk2.get(CoachellerActivity.QUERY_RATINGS__RATING).toString();
      }
      
      int milTime = setObj.getInt(_timeFieldName);
      holder.textTime.setText(militaryToCivilianTime(milTime));
      holder.textArtist.setText(  setObj.getString("artist"));
      String week1Avg = setObj.getString("avg_score_one");
      String week2Avg = setObj.getString("avg_score_two");
      
      if (week1Avg.equals("0")) {
        week1Avg = "";
      } else {
        week1Avg = "Wk1 Avg: "+ week1Avg;
      }
      
      if (week2Avg.equals("0")) {
        week2Avg = "";
      } else {
        week2Avg = "Wk2 Avg: "+ week2Avg;
      }
      holder.ratingWk1.setText(week1Avg);
      holder.ratingWk2.setText(week2Avg);
      
      if (!score1.equals("*") || !score2.equals("*")) {
         holder.myRating.setText("My Rtg: "+ score1 +"/"+ score2);
      } else {
        holder.myRating.setText("");
      }
     
     
      
    } catch (JSONException e) {
       //TODO Auto-generated catch block
      e.printStackTrace();
    }
   
    return rowView;
  }
  
  public String militaryToCivilianTime(int milTime) {
    String ampm;
    if (milTime < 1200 || milTime == 2400) {
      ampm = "a";
    } else {
      ampm = "p";
    }
    
    if (milTime < 100) {
      milTime += 1200;
    }
    
    if (milTime >= 1300) {
      milTime -= 1200;
    }
     
    String timeStr = milTime +"";
    
    int timeStrLen = timeStr.length();
    timeStr = timeStr.substring(0, timeStrLen-2) +":"+ timeStr.substring(timeStrLen-2, timeStrLen) + ampm;
    return timeStr;
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
  public boolean isEnabled(int position) {  //Everything is clickable
    return true;
  }
  
  

}
