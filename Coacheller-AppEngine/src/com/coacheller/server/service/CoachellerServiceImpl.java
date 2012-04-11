package com.coacheller.server.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.coacheller.client.CoachellerService;
import com.coacheller.server.domain.Rating;
import com.coacheller.server.logic.JSONUtils;
import com.coacheller.server.logic.RatingManager;
import com.coacheller.server.logic.SetDataLoader;
import com.coacheller.server.logic.UserAccountManager;
import com.coacheller.shared.DayEnum;
import com.coacheller.shared.FieldVerifier;
import com.coacheller.shared.RatingGwt;
import com.coacheller.shared.Set;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service. Currently used for GWT
 * client.
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

  public List<String> getSetArtists(String yearString, String day) {
    List<Set> sets = null;

    Integer year = Integer.valueOf(yearString);
    if (day != null && !day.isEmpty()) {
      sets = RatingManager.getInstance().findSetsByYearAndDay(year, DayEnum.fromValue(day));
    } else {
      sets = RatingManager.getInstance().findSetsByYear(year);
    }

    List<String> artistNames = new ArrayList<String>();

    for (Set set : sets) {
      artistNames.add(set.getArtistName());
    }

    return artistNames;
  }

  public List<Set> getSets(String yearString, String day) {
    List<Set> sets = null;

    Integer year = Integer.valueOf(yearString);
    if (day != null && !day.isEmpty()) {
      sets = RatingManager.getInstance().findSetsByYearAndDay(year, DayEnum.fromValue(day));
    } else {
      sets = RatingManager.getInstance().findSetsByYear(year);
    }

    return sets;
  }

  public String addRatingBySetArtist(String email, String setArtist, String year, String weekend,
      String score) {

    String resp = null;

    if (!FieldVerifier.isValidEmail(email)) {
      resp = FieldVerifier.EMAIL_ERROR;
    } else if (!FieldVerifier.isValidYear(year)) {
      resp = FieldVerifier.YEAR_ERROR;
    } else if (!FieldVerifier.isValidWeekend(weekend)) {
      resp = FieldVerifier.WEEKEND_ERROR;
    } else if (!FieldVerifier.isValidScore(score)) {
      resp = FieldVerifier.SCORE_ERROR;
    } else if (setArtist != null) {
      resp = RatingManager.getInstance().addRatingBySetArtist(email, setArtist,
          Integer.valueOf(year), Integer.valueOf(weekend), Integer.valueOf(score));
    } else {
      resp = "null args";
    }

    return resp;
  }

  public List<RatingGwt> getRatingsBySetArtist(String email, String setArtist) {

    List<RatingGwt> ratingGwts = null;

    if (setArtist != null) {
      List<Rating> ratings = RatingManager.getInstance().findRatingsBySetArtist(setArtist);
      if (ratings != null) {
        ratingGwts = JSONUtils.convertRatingsToRatingGwts(ratings);
      }
    }

    return ratingGwts;
  }

  public String getRatingsJsonBySetArtist(String email, String setArtist) {
    String resp = "null args";

    if (!FieldVerifier.isValidEmail(email)) {
      resp = FieldVerifier.EMAIL_ERROR;
    } else {
      List<Rating> ratings = null;

      if (setArtist != null) {
        ratings = RatingManager.getInstance().findRatingsBySetArtist(setArtist);
      }

      if (ratings != null) {
        JSONArray jsonArray = JSONUtils.convertRatingsToJSONArray(ratings);
        if (jsonArray != null) {
          resp = jsonArray.toString();
        }
      }
    }

    return resp;
  }

  public List<RatingGwt> getRatingsByUserEmail(String email) {

    List<RatingGwt> ratingGwts = null;

    if (email != null) {
      List<Rating> ratings = RatingManager.getInstance().findRatingsByUser(email);
      if (ratings != null) {
        ratingGwts = JSONUtils.convertRatingsToRatingGwts(ratings);
      }
    }

    return ratingGwts;
  }

  public List<RatingGwt> getRatingsBySet(String email, String setIdString) {

    List<RatingGwt> ratingGwts = null;

    if (setIdString != null) {
      Long setId = Long.valueOf(setIdString);
      List<Rating> ratings = RatingManager.getInstance().findRatingsBySetId(setId);
      if (ratings != null) {
        ratingGwts = JSONUtils.convertRatingsToRatingGwts(ratings);
      }
    }

    return ratingGwts;
  }

  public String getRatingsJsonBySet(String email, String setIdString) {
    String resp = null;

    List<Rating> ratings = null;

    if (setIdString != null) {
      Long setId = Long.valueOf(setIdString);
      ratings = RatingManager.getInstance().findRatingsBySetId(setId);
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
    SetDataLoader.getInstance().clearSetRatingAverages();
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
    // TODO: move this to res file
    url = "http://ratethisfest.appspot.com/resources/sets_2012.txt";
    success = loadFile(url);

    return success;
  }

  public String recalculateSetRatingAverages() {
    SetDataLoader.getInstance().recalculateSetRatingAverages();
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
