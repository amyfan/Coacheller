package com.coacheller.server.logic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
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
        if (obj.get("day") != null) {
          set.setDay((String) obj.get("day"));
        }
        if (obj.get("time") != null) {
          set.setTime((Integer) obj.get("time"));
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

  public static final String convertJSONArrayStringToSetString(String arrayString) {
    StringBuilder setString = new StringBuilder();
    try {
      JSONArray array = new JSONArray(arrayString);
      for (int i = 0; i < array.length(); i++) {
        JSONObject obj;
        obj = array.getJSONObject(i);

        setString.append("Artist: ");
        setString.append((String) obj.get("artist"));
        setString.append(", Artist: ");
        setString.append("Day: ");
        setString.append((String) obj.get("day"));
        setString.append(", Day: ");
        setString.append("Time: ");
        setString.append((Integer) obj.get("time"));
        setString.append("\n");
      }
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return setString.toString();
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
   * @param array
   * @return
   */
  public static final List<Rating> convertJSONArrayToRatings(JSONArray array) {
    List<Rating> ratings = new ArrayList<Rating>();
    for (int i = 0; i < array.length(); i++) {
      JSONObject obj;
      try {
        Rating rating = new Rating();
        obj = array.getJSONObject(i);
        if (obj.get("rater_id") != null) {
          rating.setRaterId((Long) obj.get("rater_id"));
        }
        if (obj.get("set_id") != null) {
          rating.setSetId((Long) obj.get("set_id"));
        }
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