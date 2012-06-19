package com.ratethisfest.server.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ratethisfest.shared.FestivalEnum;
import com.ratethisfest.shared.Set;

/**
 * 
 * @author Amy
 * 
 */
public class LollaApiJson {
  private static final Logger log = Logger.getLogger(LollaApiJson.class.getName());

  /**
   * 
   * @param array
   * @return
   */
  public static final List<Set> convertJsonToSets(String json) {
    List<Set> sets = new ArrayList<Set>();
    try {
      JSONObject jsonObject = new JSONObject(json);
      JSONObject jsonObject2 = jsonObject.getJSONObject("events");
      JSONArray array = jsonObject2.getJSONArray("event");
      for (int i = 0; i < array.length(); i++) {
        JSONObject obj;
        try {
          Set set = new Set();
          obj = array.getJSONObject(i);
          if (obj.get("title") != null) {
            set.setArtistName((String) obj.get("title"));
          } else {
            break;
          }
          if (obj.get("date") != null) {
            String date = (String) obj.get("date");
            String[] result = date.split(" ");
            String day = result[0];
            if (day != null && !day.isEmpty()) {
              // TODO: parse into enum for validity?
              set.setDay(day);
            }
          }
          if (obj.get("time") != null) {
            String timeString = (String) obj.get("time");
            boolean pm = false;
            if (timeString.contains("PM")) {
              pm = true;
            }
            timeString = timeString.replaceAll(" ", "");
            timeString = timeString.replaceAll(":", "");
            timeString = timeString.replaceAll("A", "");
            timeString = timeString.replaceAll("P", "");
            timeString = timeString.replaceAll("M", "");
            Integer time = Integer.parseInt(timeString);
            if (pm && time < 1200) {
              time += 1200;
            }
            set.setTimeOne(time);
          }
          set.setNumRatingsOne(0);
          set.setNumRatingsTwo(0);
          set.setScoreSumOne(0);
          set.setScoreSumTwo(0);
          set.setAvgScoreOne(0.0);
          set.setAvgScoreTwo(0.0);
          set.setFestival(FestivalEnum.LOLLAPALOOZA.getValue());
          sets.add(set);
        } catch (JSONException e) {
          e.printStackTrace();
          log.log(Level.SEVERE, "convertJsonToSets: " + e.getMessage());
          continue;
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "convertJsonToSets: " + e.getMessage());
    }
    return sets;
  }
}