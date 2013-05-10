package com.coacheller.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coacheller.R;
import com.ratethisfest.android.AndroidConstants;
import com.ratethisfest.android.data.CustomSetListAdapter;
import com.ratethisfest.android.data.JSONArrayHashMap;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.DateTimeUtils;

public class CoachSetListAdapter extends CustomSetListAdapter {

  static class ViewHolder {
    public TextView textTime;
    public TextView textArtist;
    public TextView textStage;
    public TextView ratingWk1;
    public TextView ratingWk2;
    public TextView myRating;
    public TextView myComment1;
    public TextView myComment2;
  }

  public CoachSetListAdapter(Context context, String timeFieldName, String stageFieldName,
      JSONArrayHashMap myRatings_JAHM) {
    setContext(context);
    setTimeFieldName(timeFieldName);
    setStageFieldName(stageFieldName);

    // setNewJAHM(myRatings_JAHM);
    _myRatings_JAHM = myRatings_JAHM;
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
      viewHolder.ratingWk2 = (TextView) rowView.findViewById(R.id.text_in);
      viewHolder.myRating = (TextView) rowView.findViewById(R.id.text_my_rating);
      viewHolder.myComment1 = (TextView) rowView.findViewById(R.id.text_note1);
      viewHolder.myComment2 = (TextView) rowView.findViewById(R.id.text_note2);
      rowView.setTag(viewHolder);
    }

    if (!haveData()) {
      return rowView;
    }

    try {

      ViewHolder holder = (ViewHolder) rowView.getTag();
      
      
      //Crash sometimes happens here in test mode
      //Suspect problem related to race condition exposed with no-latency test data
      //believed to be fixed with null check in haveData() function
      //LogController.SET_DATA.logMessage("_sortMap field:"+ _sortMap);
      JSONObject setObj = _sortMap.getSortedJSONObj(position);  
      
      String setId = setObj.getString(AndroidConstants.JSON_KEY_SETS__SET_ID); // Get
      // the set Id

      // Get Ratings for this set Id
      JSONObject ratingsObjWk1 = _myRatings_JAHM.getJSONObject(setId, "1");
      JSONObject ratingsObjWk2 = _myRatings_JAHM.getJSONObject(setId, "2");

      String score1 = "*";
      if (ratingsObjWk1 != null) {
        score1 = ratingsObjWk1.get(AndroidConstants.JSON_KEY_RATINGS__SCORE).toString();
      }

      String score2 = "*";
      if (ratingsObjWk2 != null) {
        score2 = ratingsObjWk2.get(AndroidConstants.JSON_KEY_RATINGS__SCORE).toString();
      }

      int milTime = setObj.getInt(_timeFieldName);
      holder.textTime.setText(DateTimeUtils.militaryToCivilianTime(milTime));
      holder.textArtist.setText(setObj.getString(AndroidConstants.JSON_KEY_SETS__ARTIST));
      holder.textStage.setText(setObj.getString(_stageFieldName).toUpperCase());
      String week1Avg = setObj.getString(AndroidConstants.JSON_KEY_SETS__AVG_SCORE_ONE);
      String week2Avg = setObj.getString(AndroidConstants.JSON_KEY_SETS__AVG_SCORE_TWO);

      if (week1Avg.equals("0")) {
        week1Avg = "";
      } else {
        week1Avg = "Wk1: " + week1Avg;
      }

      if (week2Avg.equals("0")) {
        week2Avg = "";
      } else {
        week2Avg = "Wk2: " + week2Avg;
      }

      holder.ratingWk1.setText(week1Avg);
      holder.ratingWk2.setText(week2Avg);

      if (!score1.equals("*") || !score2.equals("*")) {
        holder.myRating.setText("My Rtg: " + score1 + "/" + score2);
      } else {
        holder.myRating.setText("");
      }

      String myNote1 = "";
      if (ratingsObjWk1 != null && ratingsObjWk1.has(AndroidConstants.JSON_KEY_RATINGS__NOTES)) {
        myNote1 = ratingsObjWk1.get(AndroidConstants.JSON_KEY_RATINGS__NOTES).toString();
        LogController.MULTIWEEK.logMessage("Found week1 note: " + myNote1);
      }

      String myNote2 = "";
      if (ratingsObjWk2 != null && ratingsObjWk2.has(AndroidConstants.JSON_KEY_RATINGS__NOTES)) {
        myNote2 = ratingsObjWk2.get(AndroidConstants.JSON_KEY_RATINGS__NOTES).toString();
        LogController.MULTIWEEK.logMessage("Found week2 note: " + myNote2);
      }

      if (myNote1.equals("")) {
        holder.myComment1.setVisibility(View.GONE);
      } else {
        if (myNote1.length() > AuthConstants.DATA_NOTE_VISIBLE_MAX_LENGTH) {
          myNote1 = myNote1.substring(0, AuthConstants.DATA_NOTE_VISIBLE_MAX_LENGTH) + "...";
        }
        holder.myComment1.setText(myNote1);
        holder.myComment1.setVisibility(View.VISIBLE);
      }

      if (myNote2.equals("")) {
        holder.myComment2.setVisibility(View.GONE);
      } else {
        if (myNote2.length() > AuthConstants.DATA_NOTE_VISIBLE_MAX_LENGTH) {
          myNote2 = myNote2.substring(0, AuthConstants.DATA_NOTE_VISIBLE_MAX_LENGTH) + "...";
        }
        holder.myComment2.setText(myNote2);
        holder.myComment2.setVisibility(View.VISIBLE);
      }

      // Gnarly debug thing
      // CoachellerApplication.debug(_context,"Artist["+
      // holder.textArtist.getText() +"] Rating["+ score1 +"/"+ score2);

    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return rowView;
  }
}
