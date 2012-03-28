package com.coacheller.server.service;

import java.util.List;

import org.json.JSONArray;

import com.coacheller.client.GreetingService;
import com.coacheller.server.domain.DayEnum;
import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.logic.JSONUtils;
import com.coacheller.server.logic.RatingManager;
import com.coacheller.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

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
    RatingManager ratingMgr = RatingManager.getInstance();

    String resp = null;
    List<Set> sets = null;

    if (yearString != null) {
      Integer year = Integer.valueOf(yearString);
      if (day != null) {
        sets = ratingMgr.findSetsByYearAndDay(year, DayEnum.valueOf(day));
      }
      sets = ratingMgr.findSetsByYear(year);
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
