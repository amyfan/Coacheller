package com.lollapaloozer.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lollapaloozer.LollapaloozerApplication;
import com.lollapaloozer.R;
import com.ratethisfest.android.AndroidConstants;
import com.ratethisfest.android.data.CustomSetListAdapter;
import com.ratethisfest.android.data.JSONArrayHashMap;
import com.ratethisfest.shared.AuthConstants;
import com.ratethisfest.shared.DateTimeUtils;

public class LollaSetListAdapter extends CustomSetListAdapter {

  static class ViewHolder {
    public TextView textTime;
    public TextView textArtist;
    public TextView textStage;
    public TextView ratingWk1;
    public TextView myRating;
    public TextView myComment;
  }

  public LollaSetListAdapter(Context context, String timeFieldName, String stageFieldName,
      JSONArrayHashMap myRatings_JAHM) {
    _context = context;
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
      String setId = setObj.getString(AndroidConstants.JSON_KEY_SETS__SET_ID); // Get
      // the set Id

      // Get Ratings for this set Id
      JSONObject ratingsObjWk1 = _myRatings_JAHM.getJSONObject(setId, "1");

      String score1 = "*";
      if (ratingsObjWk1 != null) {
        score1 = ratingsObjWk1.get(AndroidConstants.JSON_KEY_RATINGS__SCORE).toString();
      }

      int milTime = setObj.getInt(_timeFieldName);
      holder.textTime.setText(DateTimeUtils.militaryToCivilianTime(milTime));
      holder.textArtist.setText(setObj.getString(AndroidConstants.JSON_KEY_SETS__ARTIST));
      holder.textStage.setText(setObj.getString(_stageFieldName).toUpperCase());
      String week1Avg = setObj.getString(AndroidConstants.JSON_KEY_SETS__AVG_SCORE_ONE);

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
      if (ratingsObjWk1 != null && ratingsObjWk1.has(AndroidConstants.JSON_KEY_RATINGS__NOTES)) {
        myNote = ratingsObjWk1.get(AndroidConstants.JSON_KEY_RATINGS__NOTES).toString();
        LollapaloozerApplication.debug(_context, "Found note: " + myNote);
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

}
