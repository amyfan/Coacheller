package com.coacheller.server.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.coacheller.server.domain.Rating;
import com.coacheller.server.logic.JSONUtils;
import com.coacheller.server.logic.RatingManager;
import com.coacheller.shared.DayEnum;
import com.coacheller.shared.FieldVerifier;
import com.coacheller.shared.Set;

/**
 * HTTP Servlet, intended for Android/etc. device use.
 * 
 * @author Amy
 * 
 */
@SuppressWarnings("serial")
public class CoachellerServlet extends HttpServlet {
  private final String ACTION_GET_SETS = "get_sets";
  private final String ACTION_GET_RATINGS = "get_ratings";
  private final String ACTION_UPDATE_RATING = "update_rating";

  /**
   * This method is accessible by visiting the URL
   * 
   * @param req
   * @param resp
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String action = checkNull(req.getParameter("action"));
    String email = checkNull(req.getParameter("email"));
    String day = checkNull(req.getParameter("day"));
    String year = checkNull(req.getParameter("year"));
    String artist = checkNull(req.getParameter("artist"));
    String score = checkNull(req.getParameter("score"));
    String weekend = checkNull(req.getParameter("weekend"));

    String respString = "";

    if (action.equals(ACTION_GET_SETS)) {
      respString = getSetsJson(email, year, day);
    } else if (action.equals(ACTION_GET_RATINGS)) {
      respString = getRatingsJsonByUser(email);
    } else if (action.equals(ACTION_UPDATE_RATING)) {
      // TODO: prefer to stick this in doPost()
      respString = addRatingBySetArtist(email, artist, year, weekend, score);
    }

    resp.getWriter().println();
  }

  /**
   * TODO: Ideally invoke this when updating a rating
   * 
   * @param req
   * @param resp
   * @throws IOException
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String action = checkNull(req.getParameter("action"));

    if (action.equals(ACTION_UPDATE_RATING)) {

    }

  }

  private String getSetsJson(String email, String yearString, String day) {
    String resp = null;

    if (!FieldVerifier.isValidEmail(email)) {
      resp = FieldVerifier.EMAIL_ERROR;
    } else if (!FieldVerifier.isValidYear(yearString)) {
      resp = FieldVerifier.YEAR_ERROR;
    } else if (!FieldVerifier.isValidDay(day)) {
      resp = FieldVerifier.DAY_ERROR;
    } else {
      List<Set> sets = null;

      Integer year = Integer.valueOf(yearString);
      if (day != null && !day.isEmpty()) {
        sets = RatingManager.getInstance().findSetsByYearAndDay(year, DayEnum.fromValue(day));
      } else {
        sets = RatingManager.getInstance().findSetsByYear(year);
      }

      JSONArray jsonArray = JSONUtils.convertSetsToJSONArray(sets);
      if (jsonArray != null) {
        resp = jsonArray.toString();
      }
    }

    if (resp == null) {
      resp = "no data hrm";
    }

    return resp;
  }

  private String getRatingsJsonByUser(String email) {
    String resp = null;

    List<Rating> ratings = null;

    if (email != null) {
      ratings = RatingManager.getInstance().findRatingsByUser(email);
    }

    JSONArray jsonArray = JSONUtils.convertRatingsToJSONArray(ratings);
    if (jsonArray != null) {
      resp = jsonArray.toString();
    }

    return resp;
  }

  private String addRatingBySetArtist(String email, String setArtist, String year, String weekend,
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

  private String checkNull(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }
}
