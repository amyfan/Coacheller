package com.lollapaloozer.data;

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

import com.lollapaloozer.R;
import com.lollapaloozer.ui.LollapaloozerActivity;
import com.lollapaloozer.util.LollapaloozerHelper;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.DateTimeUtils;

public class CustomSetListAdapter implements ListAdapter {

  private JSONArray _data;
  private JSONArraySortMap _sortMap;
  private Context _context;
  private String _timeFieldName;
  private String _stageFieldName;
  private JSONArrayHashMap _myRatings_JAHM;

  static class ViewHolder {
    public TextView textTime;
    public TextView textArtist;
    public TextView textStage;
    public TextView ratingWk1;
    public TextView myRating;
    public TextView myComment;
  }

  public CustomSetListAdapter(Context context, String timeFieldName, String stageFieldName,
      JSONArrayHashMap myRatings_JAHM) {
    _context = context;
    setTimeFieldName(timeFieldName);
    setStageFieldName(stageFieldName);

    // setNewJAHM(myRatings_JAHM);
    _myRatings_JAHM = myRatings_JAHM;
  }

  /*
   * public void setNewJAHM(JSONArrayHashMap myRatings_JAHM) {
   * 
   * }
   */

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
  public View getView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;

    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) _context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_set_info, parent, false);
      ViewHolder viewHolder = new ViewHolder();
      viewHolder.textTime = (TextView) rowView.findViewById(R.id.text_set_time);
      viewHolder.textArtist = (TextView) rowView.findViewById(R.id.text_artist_name);
      viewHolder.textStage = (TextView) rowView.findViewById(R.id.text_stage);
      viewHolder.ratingWk1 = (TextView) rowView.findViewById(R.id.text_wk1_rating);
      viewHolder.myRating = (TextView) rowView.findViewById(R.id.text_my_rating);
      viewHolder.myComment = (TextView) rowView.findViewById(R.id.text_noteGoesHere);
      rowView.setTag(viewHolder);
    }

    if (!haveData()) {
      // Important to avoid crashes
      return rowView;
    }

    try {

      ViewHolder holder = (ViewHolder) rowView.getTag();
      JSONObject setObj = _sortMap.getSortedJSONObj(position);
      String setId = setObj.getString(LollapaloozerActivity.QUERY_SETS__SET_ID); // Get
      // the
      // set
      // Id

      // Get Ratings for this set Id
      JSONObject ratingsObjWk1 = _myRatings_JAHM.getJSONObject(setId, "1");

      String score1 = "*";
      if (ratingsObjWk1 != null) {
        score1 = ratingsObjWk1.get(LollapaloozerActivity.QUERY_RATINGS__RATING).toString();
      }

      int milTime = setObj.getInt(_timeFieldName);
      holder.textTime.setText(DateTimeUtils.militaryToCivilianTime(milTime));
      holder.textArtist.setText(setObj.getString("artist"));
      holder.textStage.setText(setObj.getString(_stageFieldName).toUpperCase());
      String week1Avg = setObj.getString("avg_score_one");

      if (week1Avg.equals("0")) {
        week1Avg = "";
      } else {
        week1Avg = "Avg Score: " + week1Avg;
      }

      holder.ratingWk1.setText(week1Avg);

      if (!score1.equals("*")) {
        holder.myRating.setText("My Rating: " + score1);
      } else {
        holder.myRating.setText("");
      }

      String myNote = "";
      if (ratingsObjWk1 != null && ratingsObjWk1.has(LollapaloozerActivity.QUERY_RATINGS__NOTES)) {
        myNote = ratingsObjWk1.get(LollapaloozerActivity.QUERY_RATINGS__NOTES).toString();
        LollapaloozerHelper.debug(_context, "Found note: " + myNote);
      }

      if (myNote.equals("")) {
        holder.myComment.setVisibility(View.GONE);
      } else {
        if (myNote.length() > AuthConstants.DATA_NOTE_VISIBLE_MAX_LENGTH) {
          myNote = myNote.substring(0, AuthConstants.DATA_NOTE_VISIBLE_MAX_LENGTH) + "...";
        }
        holder.myComment.setText(myNote);
        holder.myComment.setVisibility(View.VISIBLE);
      }

      // Gnarly debug thing
      // LollapaloozerHelper.debug(_context,"Artist["+
      // holder.textArtist.getText() +"] Rating["+ score1 +"/"+ score2);

    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return rowView;
  }

  // If we are ready to draw the view
  // Needed for concurrency issues
  private boolean haveData() {
    if (_data != null) {
      return true;
    }
    return false;
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

}
