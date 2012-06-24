package com.ratethisfest.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.server.logic.EmailSender;
import com.ratethisfest.server.logic.JSONUtils;
import com.ratethisfest.server.logic.LollaRatingManager;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.HttpConstants;
import com.ratethisfest.shared.Set;

/**
 * HTTP Servlet, intended for Android/etc. device use.
 * 
 * @author Amy
 * 
 */
@SuppressWarnings("serial")
public class LollapaloozerServlet extends HttpServlet {

  /**
   * This method is accessible by visiting the URL
   * 
   * @param req
   * @param resp
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("UTF-8");
   

    // Helpful in debugging
    // String queryString = req.getQueryString();
    //
    // ArrayList<String> parameters = new ArrayList<String>();
    // for (Enumeration<String> e = req.getParameterNames();
    // e.hasMoreElements();) {
    // String value = e.nextElement();
    // parameters.add(value);
    // }
    
    
    resp.setContentType("text/plain");

    String action = checkNull(req.getParameter(HttpConstants.PARAM_ACTION));
    String email = checkNull(req.getParameter(HttpConstants.PARAM_EMAIL));
    String day = checkNull(req.getParameter(HttpConstants.PARAM_DAY));
    String year = checkNull(req.getParameter(HttpConstants.PARAM_YEAR));

    String respString = "";
    


    if (action.equals(HttpConstants.ACTION_GET_SETS)) {
      respString = getSetsJson(year, day);
    } else if (action.equals(HttpConstants.ACTION_GET_RATINGS)) {
      respString = getRatingsJsonByUser(email, year, day);
    }

    resp.getWriter().println(respString);
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
    
    
    
    String action = checkNull(req.getParameter(HttpConstants.PARAM_ACTION));
    String email = checkNull(req.getParameter(HttpConstants.PARAM_EMAIL));
    String setId = checkNull(req.getParameter(HttpConstants.PARAM_SET_ID));
    String score = checkNull(req.getParameter(HttpConstants.PARAM_SCORE));
    String notes = checkNull(req.getParameter(HttpConstants.PARAM_NOTES));

    if (notes.isEmpty()) {
      // TODO: this is temp until notes implemented on android
      notes = null;
    }
    if (action.equals(HttpConstants.ACTION_ADD_RATING)) {
      addRating(email, setId, score, notes);
    } else if (action.equals(HttpConstants.ACTION_EMAIL_RATINGS)) {
      EmailSender.emailRatings(email);
    }

  }

  private String getSetsJson(String yearString, String day) {
    String resp = null;

    if (!FieldVerifier.isValidYear(yearString)) {
      resp = FieldVerifier.YEAR_ERROR;
    } else if (!FieldVerifier.isValidDay(day)) {
      resp = FieldVerifier.DAY_ERROR;
    } else {
      List<Set> sets = null;

      Integer year = Integer.valueOf(yearString);
      if (day != null && !day.isEmpty()) {
        sets = LollaRatingManager.getInstance().findSetsByYearAndDay(year, DayEnum.fromValue(day));
      } else {
        sets = LollaRatingManager.getInstance().findSetsByYear(year);
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

  /**
   * TODO: filter by year & day
   * 
   * @param email
   * @param year
   * @param day
   * @return
   */
  private String getRatingsJsonByUser(String email, String year, String day) {
    String resp = null;

    List<Rating> ratings = null;

    if (email != null) {
      ratings = LollaRatingManager.getInstance().findAllRatingsByUser(email);
    }

    JSONArray jsonArray = JSONUtils.convertRatingsToJSONArray(ratings);
    if (jsonArray != null) {
      resp = jsonArray.toString();
    }

    return resp;
  }

  private String addRating(String email, String setId, String score, String notes) {

    String resp = null;

    if (!FieldVerifier.isValidEmail(email)) {
      resp = FieldVerifier.EMAIL_ERROR;
    } else if (!FieldVerifier.isValidScore(score)) {
      resp = FieldVerifier.SCORE_ERROR;
    } else if (setId != null) {
      resp = LollaRatingManager.getInstance().addRatingBySetId(email, Long.valueOf(setId),
          Integer.valueOf(score), notes);
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
