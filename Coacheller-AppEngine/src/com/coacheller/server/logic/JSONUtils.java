package com.coacheller.server.logic;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;

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
        ret.put("day", set.getDay());
        ret.put("time", set.getTime());
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
        ret.put("score", rating.getScore());
        ret.put("weekend", rating.getWeekend());
        jsonObjs.put(ret);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return jsonObjs;
  }
}