package com.coacheller.server.logic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coacheller.server.domain.Rating;
import com.coacheller.shared.RatingGwt;
import com.coacheller.shared.Set;

/**
 * 
 * @author Amy
 * 
 */
public class JSONUtils {

  /**
   * 
   * @param sets
   * @return
   */
  public static final JSONArray convertSetsToJSONArray(List<Set> sets) {
    if (sets == null) {
      return null;
    }
    JSONArray jsonObjs = new JSONArray();
    for (Set set : sets) {
      JSONObject ret = new JSONObject();
      try {
        ret.put("id", set.getId());
        ret.put("artist", set.getArtistName());
        ret.put("year", set.getYear());
        ret.put("day", set.getDay());
        ret.put("time_one", set.getTimeOne());
        ret.put("time_two", set.getTimeTwo());
        ret.put("stage_one", set.getStageOne());
        ret.put("stage_two", set.getStageTwo());
        ret.put("avg_score_one", set.getAvgScoreOne());
        ret.put("avg_score_two", set.getAvgScoreTwo());
        jsonObjs.put(ret);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return jsonObjs;
  }

  /**
   * TODO: impl if i create SetGwts
   * 
   * @param sets
   * @return
   */
  public static final List<Set> convertSetsToSetGwts(List<Set> sets) {
    if (sets == null) {
      return null;
    }
    List<Set> setGwts = new ArrayList<Set>();
    for (Set rating : sets) {
      Set setGwt = new Set();
      setGwt.setId(rating.getId());

      setGwts.add(setGwt);
    }
    return setGwts;
  }

  /**
   * 
   * @param array
   * @return
   */
  public static final List<Set> convertJSONArrayToSets(JSONArray array) {
    List<Set> sets = new ArrayList<Set>();
    for (int i = 0; i < array.length(); i++) {
      JSONObject obj;
      try {
        Set set = new Set();
        obj = array.getJSONObject(i);
        if (obj.get("id") != null) {
          set.setId((Long) obj.get("id"));
        }
        if (obj.get("artist") != null) {
          set.setArtistName((String) obj.get("artist"));
        }
        if (obj.get("year") != null) {
          set.setYear((Integer) obj.get("year"));
        }
        if (obj.get("day") != null) {
          set.setDay((String) obj.get("day"));
        }
        if (obj.get("time") != null) {
          set.setTimeOne((Integer) obj.get("time"));
        }
        if (obj.get("avg_score_one") != null) {
          set.setAvgScoreOne((Double) obj.get("avg_score_one"));
        }
        if (obj.get("avg_score_two") != null) {
          set.setAvgScoreTwo((Double) obj.get("avg_score_two"));
        }

        sets.add(set);
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        continue;
      }
    }
    return sets;
  }

  /**
   * 
   * @param ratings
   * @return
   */
  public static final JSONArray convertRatingsToJSONArray(List<Rating> ratings) {
    if (ratings == null) {
      return null;
    }
    JSONArray jsonObjs = new JSONArray();
    for (Rating rating : ratings) {
      JSONObject ret = new JSONObject();
      try {
        ret.put("rater_id", rating.getRater().getId());
        ret.put("set_id", rating.getSet().getId());
        ret.put("weekend", rating.getWeekend());
        ret.put("score", rating.getScore());
        jsonObjs.put(ret);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return jsonObjs;
  }

  /**
   * 
   * @param ratings
   * @return
   */
  public static final List<RatingGwt> convertRatingsToRatingGwts(List<Rating> ratings) {
    if (ratings == null) {
      return null;
    }
    List<RatingGwt> ratingGwts = new ArrayList<RatingGwt>();
    for (Rating rating : ratings) {
      RatingGwt ratingGwt = new RatingGwt();
      ratingGwt.setId(rating.getId());
      ratingGwt.setRaterId(rating.getRater().getId());
      ratingGwt.setSetId(rating.getSet().getId());
      ratingGwt.setWeekend(rating.getWeekend());
      ratingGwt.setScore(rating.getScore());
      ratingGwts.add(ratingGwt);
    }
    return ratingGwts;
  }

  /**
   * 
   * @param array
   * @return
   */
  @Deprecated
  public static final List<Rating> convertJSONArrayToRatings(JSONArray array) {
    List<Rating> ratings = new ArrayList<Rating>();
    for (int i = 0; i < array.length(); i++) {
      JSONObject obj;
      try {
        Rating rating = new Rating();
        obj = array.getJSONObject(i);
        // if (obj.get("rater_id") != null) {
        // rating.setRaterId((Long) obj.get("rater_id"));
        // }
        // if (obj.get("set_id") != null) {
        // rating.setSetId((Long) obj.get("set_id"));
        // }
        if (obj.get("weekend") != null) {
          rating.setWeekend((Integer) obj.get("weekend"));
        }
        if (obj.get("score") != null) {
          rating.setScore((Integer) obj.get("score"));
        }

        ratings.add(rating);
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        continue;
      }
    }
    return ratings;
  }

}