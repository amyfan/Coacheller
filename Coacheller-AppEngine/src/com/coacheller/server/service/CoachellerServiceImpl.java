package com.coacheller.server.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.json.JSONArray;

import com.coacheller.client.CoachellerService;
import com.coacheller.server.domain.AppUser;
import com.coacheller.server.domain.DayEnum;
import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.logic.JSONUtils;
import com.coacheller.server.logic.RatingManager;
import com.coacheller.server.logic.SetDataLoader;
import com.coacheller.server.logic.UserAccountManager;
import com.coacheller.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CoachellerServiceImpl extends RemoteServiceServlet implements CoachellerService {

  public String greetServer(String input) throws IllegalArgumentException {
    // Verify that the input is valid.
    if (!FieldVerifier.isValidName(input)) {
      // If the input is not valid, throw an IllegalArgumentException back to
      // the client.
      throw new IllegalArgumentException("Name must be at least 4 characters long");
    }

    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");

    // Escape data from the client to avoid cross-site script vulnerabilities.
    input = escapeHtml(input);
    userAgent = escapeHtml(userAgent);

    return "Hello, " + input + "!<br><br>I am running " + serverInfo
        + ".<br><br>It looks like you are using:<br>" + userAgent;
  }

  public String getSets(String email, String yearString, String day) {
    // TODO: validate inputs properly
    RatingManager ratingMgr = RatingManager.getInstance();

    String resp = null;
    List<Set> sets = null;

    if (yearString != null && !yearString.isEmpty()) {
      Integer year = Integer.valueOf(yearString);
      if (day != null && !day.isEmpty()) {
        sets = ratingMgr.findSetsByYearAndDay(year, DayEnum.fromValue(day));
      } else {
        sets = ratingMgr.findSetsByYear(year);
      }
    }

    JSONArray jsonArray = JSONUtils.convertSetsToJSONArray(sets);
    if (jsonArray != null) {
      resp = jsonArray.toString();
    }

    if (resp == null) {
      resp = "no data hrm";
    }

    return resp;
  }

  public String addRatingBySetArtist(String email, String setArtist, String weekend, String score) {
    // TODO: validate inputs properly
    RatingManager ratingMgr = RatingManager.getInstance();
    UserAccountManager uam = UserAccountManager.getInstance();

    String resp = null;

    if (email != null && setArtist != null && score != null) {
      Rating rating;
      List<Rating> ratings = ratingMgr.findRatingsBySetArtistAndUser(setArtist, email);
      if (ratings == null || ratings.size() == 0) {
        rating = new Rating();
        // TODO omit year later
        Key<Set> setKey = ratingMgr.findSetKeyByArtistAndYear(setArtist, null);
        if (setKey != null) {
          rating.setSet(setKey);
          rating.setSetId(setKey.getId());
          rating.setScore(Integer.valueOf(score));
          rating.setWeekend(Integer.valueOf(weekend));

          Key<AppUser> userKey = uam.getAppUserKeyByEmail(email);
          if (userKey == null) {
            AppUser user = uam.createAppUser(email);
            rating.setRaterId(user.getId());
          } else {
            rating.setRater(userKey);
            rating.setRaterId(userKey.getId());
          }

          ratingMgr.updateRating(rating);
          resp = "rating added";
        } else {
          resp = "invalid artist name";
        }
      } else {
        rating = ratings.get(0);
        rating.setScore(Integer.valueOf(score));
        ratingMgr.updateRating(rating);
        resp = "rating updated";
      }
    } else {
      resp = "null args";
    }

    return resp;
  }

  public String getRatingsBySetArtist(String email, String setArtist) {
    RatingManager ratingMgr = RatingManager.getInstance();

    String resp = "null data";
    List<Rating> ratings = null;

    if (setArtist != null) {
      ratings = ratingMgr.findRatingsBySetArtist(setArtist);
    }

    if (ratings != null) {
      JSONArray jsonArray = JSONUtils.convertRatingsToJSONArray(ratings);
      if (jsonArray != null) {
        resp = jsonArray.toString();
      }
    }

    return resp;
  }

  public String getRatingsBySet(String email, String setIdString) {
    RatingManager ratingMgr = RatingManager.getInstance();

    String resp = null;
    List<Rating> ratings = null;

    if (setIdString != null) {
      Long setId = Long.valueOf(setIdString);
      ratings = ratingMgr.findRatingsBySetId(setId);
    }

    JSONArray jsonArray = JSONUtils.convertRatingsToJSONArray(ratings);
    if (jsonArray != null) {
      resp = jsonArray.toString();
    }

    return resp;
  }

  public String deleteAllRatings() {
    String resp = "fail";
    RatingManager.getInstance().deleteAllRatings();
    resp = "ratings deleted";
    return resp;
  }

  public String deleteAllUsers() {
    String resp = "fail";
    UserAccountManager.getInstance().deleteAllAppUsers();
    resp = "users deleted";
    return resp;
  }

  public String loadSetData() {
    String success;
    String url;
    // TODO: move all this to res file
    // url = "http://http://127.0.0.1:8888/resources/sets_2012.txt";
    url = "http://ratethisfest.appspot.com/resources/sets_2012.txt";
    success = loadFile(url);

    return success;
  }

  public String calculateSetRatingAverages() {
    SetDataLoader.getInstance().calculateSetRatingAverages();
    return "success i believe";
  }

  public String registerUser() {
    String success = null;
    return success;
  }

  private String loadFile(String url) {
    String success = "something happened";
    try {
      URL inputData = new URL(url);
      URLConnection urlConn = inputData.openConnection();
      InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), "UTF8");
      BufferedReader in = new BufferedReader(is);
      SetDataLoader dataLoader = SetDataLoader.getInstance();
      RatingManager ratingMgr = RatingManager.getInstance();
      ratingMgr.deleteAllSets();
      dataLoader.insertSets(in);
      success = "success i believe";
    } catch (Exception e) {
      success = "something happened";
    } finally {
    }
    return success;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   * 
   * @param html
   *          the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }
}
