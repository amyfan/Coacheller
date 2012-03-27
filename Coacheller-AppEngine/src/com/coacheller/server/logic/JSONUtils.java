package com.coacheller.server.logic;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.coacheller.server.domain.Set;

/**
 * 
 * @author Amy
 * 
 */
public class JSONUtils {

  /**
   * TODO: Make this more generic
   * 
   * @param incidents
   * @return
   */
  public static final JSONArray convertIncidentsToJSONArray(List<Set> incidents) {
    if (incidents == null)
      return null;
    JSONArray jsonObjs = new JSONArray();
    for (Set incident : incidents) {
      JSONObject ret = new JSONObject();
      try {
        ret.put("artist", incident.getArtistName());
        ret.put("day", "SAT");
        ret.put("time", "1200");
        jsonObjs.put(ret);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return jsonObjs;
  }
}