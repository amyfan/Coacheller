package com.ratethisfest.server.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.ratethisfest.data.HttpConstants;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.server.logic.CoachellaEmailSender;
import com.ratethisfest.server.logic.CoachellaRatingManager;
import com.ratethisfest.server.logic.JSONUtils;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.Set;

/**
 * HTTP Servlet, intended for Android/etc. device use.
 * 
 * @author Amy
 * 
 */
@SuppressWarnings("serial")
public class CoachellerServlet extends HttpServlet {

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

    String action = checkNull(req.getParameter(HttpConstants.PARAM_ACTION));
    String authType = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TYPE));
    String authId = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_ID));
    String authToken = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TOKEN));
    String email = checkNull(req.getParameter(HttpConstants.PARAM_EMAIL));
    String day = checkNull(req.getParameter(HttpConstants.PARAM_DAY));
    String year = checkNull(req.getParameter(HttpConstants.PARAM_YEAR));

    String respString = "";

    if (action.equals(HttpConstants.ACTION_GET_SETS)) {
      respString = getSetsJson(year, day);
    } else if (action.equals(HttpConstants.ACTION_GET_RATINGS)) {
      respString = getRatingsJsonByUser(authType, authId, authToken, email, year, day);
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
    String methodName = "doPost";
    PrintWriter out = resp.getWriter();

    out.println(methodName + " handling request for the following parameters:");

    // Value is not a string but rather an array of strings
    Map<String, String[]> parameterMap = req.getParameterMap();
    for (String s : parameterMap.keySet()) {
      out.println(s + " = " + parameterMap.get(s)[0]);
    }
    // above just helpful println's

    String action = checkNull(req.getParameter(HttpConstants.PARAM_ACTION));
    String authType = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TYPE));
    String authId = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_ID));
    String authToken = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TOKEN));
    String email = checkNull(req.getParameter(HttpConstants.PARAM_EMAIL));
    String setId = checkNull(req.getParameter(HttpConstants.PARAM_SET_ID));
    String weekend = checkNull(req.getParameter(HttpConstants.PARAM_WEEKEND));
    String score = checkNull(req.getParameter(HttpConstants.PARAM_SCORE));
    String notes = checkNull(req.getParameter(HttpConstants.PARAM_NOTES));

    if (notes.isEmpty()) {
      // TODO: this is temp until notes implemented on android
      notes = null;
    }
    if (action.equals(HttpConstants.ACTION_ADD_RATING)) {
      addRating(authType, authId, authToken, email, setId, weekend, score, notes);
    } else if (action.equals(HttpConstants.ACTION_EMAIL_RATINGS)) {
      CoachellaEmailSender.emailRatings(email);
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
        sets = CoachellaRatingManager.getInstance().findSetsByYearAndDay(year,
            DayEnum.fromValue(day));
      } else {
        sets = CoachellaRatingManager.getInstance().findSetsByYear(year);
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
  private String getRatingsJsonByUser(String authType, String authId, String authToken,
      String email, String year, String day) {
    String resp = null;

    List<Rating> ratings = null;

    if (authId != null) {
      if (!FieldVerifier.isValidYear(year)) {
        resp = FieldVerifier.YEAR_ERROR;
      } else if (!FieldVerifier.isValidDay(day)) {
        resp = FieldVerifier.DAY_ERROR;
      } else {
        ratings = CoachellaRatingManager.getInstance().findRatingsByUserYearAndDay(authType,
            authId, authToken, email, Integer.valueOf(year), DayEnum.fromValue(day));
      }
    }

    if (ratings != null) {
      JSONArray jsonArray = JSONUtils.convertRatingsToJSONArray(ratings);
      if (jsonArray != null) {
        resp = jsonArray.toString();
      }
    }

    return resp;
  }

  private String addRating(String authType, String authId, String authToken, String email,
      String setId, String weekend, String score, String notes) {

    String resp = null;

    // if (!FieldVerifier.isValidEmail(email)) {
    // resp = FieldVerifier.EMAIL_ERROR;
    // } else
    if (!FieldVerifier.isValidWeekend(weekend)) {
      resp = FieldVerifier.WEEKEND_ERROR;
    } else if (!FieldVerifier.isValidScore(score)) {
      resp = FieldVerifier.SCORE_ERROR;
    } else if (authId != null && setId != null) {
      resp = CoachellaRatingManager.getInstance().addRatingBySetId(authType, authId, authToken,
          email, Long.valueOf(setId), Integer.valueOf(weekend), Integer.valueOf(score), notes);
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
