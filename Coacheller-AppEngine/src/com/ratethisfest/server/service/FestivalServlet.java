package com.ratethisfest.server.service;

import java.util.List;

import javax.servlet.http.HttpServlet;

import org.json.JSONArray;

import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.server.logic.JSONUtils;
import com.ratethisfest.server.logic.RatingManager;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.LoginType;
import com.ratethisfest.shared.Set;

@SuppressWarnings("serial")
public abstract class FestivalServlet extends HttpServlet {

  protected boolean verifyToken(String authType, String authId, String authToken) {
    // TODO: you may put the verification code here
    return true;
  }

  protected String getSetsJson(FestivalEnum fest, String yearString, String day) {
    String resp = null;

    if (!FieldVerifier.isValidYear(yearString)) {
      resp = FieldVerifier.YEAR_ERROR;
    } else if (!FieldVerifier.isValidDay(day)) {
      resp = FieldVerifier.DAY_ERROR;
    } else {
      List<Set> sets = null;

      Integer year = Integer.valueOf(yearString);

      if (day != null && !day.isEmpty()) {
        sets = RatingManager.getInstance().findSetsByYearAndDay(fest, year, DayEnum.fromValue(day));
      } else {
        sets = RatingManager.getInstance().findSetsByYear(fest, year);
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
   * 
   * @param email
   * @param year
   * @param day
   * @return
   */
  protected String getRatingsJsonByUser(FestivalEnum fest, String authType, String authId, String authToken,
      String email, String year, String day) {
    String resp = null;

    List<Rating> ratings = null;

    if (authId != null) {
      if (!FieldVerifier.isValidYear(year)) {
        resp = FieldVerifier.YEAR_ERROR;
      } else if (!FieldVerifier.isValidDay(day)) {
        resp = FieldVerifier.DAY_ERROR;
      } else {
        ratings = RatingManager.getInstance().findRatingsByUserYearAndDay(fest, authType, authId, authToken, email,
            Integer.valueOf(year), DayEnum.fromValue(day));
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

  protected String addRating(String authType, String authId, String authToken, String email, String setId,
      String weekend, String score, String notes) {

    String resp = null;

    // if (!FieldVerifier.isValidEmail(email)) {
    // resp = FieldVerifier.EMAIL_ERROR;
    // } else
    if (!FieldVerifier.isValidWeekend(weekend)) {
      weekend = "1";
    } else if (!FieldVerifier.isValidScore(score)) {
      resp = FieldVerifier.SCORE_ERROR;
    } else if (authId != null && setId != null) {
      resp = RatingManager.getInstance().addRatingBySetId(authType, authId, authToken, email, Long.valueOf(setId),
          Integer.valueOf(weekend), Integer.valueOf(score), notes);
    } else {
      resp = "null args";
    }

    return resp;
  }

  protected String checkNull(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }

  /**
   * temporary method to convert older auth_type format to LoginType enum val
   */
  protected String updateAuthType(String authType) {
    String returnString = authType;
    if (LoginType.fromString(authType) == null) {
      if (authType.contains("GOOGLE")) {
        returnString = LoginType.GOOGLE.getName();
      } else if (authType.contains("FACEBOOK_BROWSER")) {
        returnString = LoginType.FACEBOOK_BROWSER.getName();
      } else if (authType.contains("FACEBOOK")) {
        returnString = LoginType.FACEBOOK.getName();
      } else if (authType.contains("TWITTER")) {
        returnString = LoginType.TWITTER.getName();
      }
    }
    return returnString;
  }
}
