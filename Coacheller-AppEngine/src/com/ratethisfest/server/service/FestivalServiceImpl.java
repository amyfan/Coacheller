package com.ratethisfest.server.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import auth.logins.other.LoginManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ratethisfest.client.FestivalService;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.server.logic.CoachellaEmailSender;
import com.ratethisfest.server.logic.CoachellaSetDataLoader;
import com.ratethisfest.server.logic.JSONUtils;
import com.ratethisfest.server.logic.LollaEmailSender;
import com.ratethisfest.server.logic.LollaSetDataLoader;
import com.ratethisfest.server.logic.RatingManager;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

/**
 * The server side implementation of the RPC service. Currently used for GWT client.
 */
@SuppressWarnings("serial")
public class FestivalServiceImpl extends RemoteServiceServlet implements FestivalService {
  private static final Logger log = Logger.getLogger(FestivalServiceImpl.class.getName());

  @Override
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

    return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"
        + userAgent;
  }

  // Gets user logged in persisted in session state -MA
  private AppUser getCurrentLogin() {
    AppUser currentLogin = LoginManager.getLoggedInUser(getThreadLocalRequest().getSession());
    return currentLogin;
  }

  void createSession(String username) {
    getThreadLocalRequest().getSession().setAttribute("username", username);
  }

  boolean validateSession(String username) {
    if (getThreadLocalRequest().getSession().getAttribute("username") != null) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public List<Set> getSets(FestivalEnum fest, String yearString, DayEnum day) {
    List<Set> sets = null;

    Integer year = Integer.valueOf(yearString);
    if (day != null) {
      sets = RatingManager.getInstance().findSetsByYearAndDay(fest, year, day);
    } else {
      sets = RatingManager.getInstance().findSetsByYear(fest, year);
    }

    return sets;
  }

  @Override
  public String addRating(Long setId, String weekend, String score, String notes) {
    AppUser currentLogin = getCurrentLogin();

    String resp = null;

    if (!FieldVerifier.isValidScore(score)) {
      resp = FieldVerifier.SCORE_ERROR;
    } else if (!FieldVerifier.isValidWeekend(weekend)) {
      resp = FieldVerifier.WEEKEND_ERROR;
    } else if (setId != null) {
      // TODO: implement GWT login auth!
      // resp = LollaRatingManager.getInstance().addRatingBySetId(null, null, null, email, setId,
      // Integer.valueOf(score), notes);
      RatingManager.getInstance().addRating(currentLogin.getId(), setId, Integer.valueOf(weekend),
          Integer.valueOf(score), notes);
    } else {
      log.log(Level.WARNING, "addRatingBySetArtist: null args");
      resp = "null args";
    }

    return resp;
  }

  @Override
  public List<RatingGwt> getAllRatings() {
    AppUser currentLogin = getCurrentLogin();
    if (currentLogin == null) { // If not logged in, return
      String error = "Action requires login";
      log.info(error);
      return null;
    }

    List<Rating> ratings = RatingManager.getInstance().findRatingsByUser(currentLogin.getId());
    return JSONUtils.convertRatingsToRatingGwts(ratings);
  }

  @Override
  public List<RatingGwt> getRatingsForSet(Set targetSet) {
    AppUser currentLogin = getCurrentLogin();
    if (currentLogin == null) { // If not logged in, return
      String error = "Action requires login";
      log.info(error);
      return null;
    }
    List<Rating> ratings = RatingManager.getInstance().findRatingsByUserAndSet(currentLogin.getId(), targetSet.getId());
    return JSONUtils.convertRatingsToRatingGwts(ratings);
  }

  @Override
  public List<RatingGwt> getRatingsByYear(FestivalEnum fest, Integer year) {
    AppUser currentLogin = getCurrentLogin();
    if (currentLogin == null) { // If not logged in, return
      String error = "Action requires login";
      log.info(error);
      return null;
    }

    List<Rating> ratings = RatingManager.getInstance().findRatingsByUserAndYear(fest, currentLogin.getId(), year);
    return JSONUtils.convertRatingsToRatingGwts(ratings);
  }

  @Override
  public String deleteRating(Long ratingId) {
    String resp = "fail";
    if (ratingId != null) {
      RatingManager.getInstance().deleteRatingById(ratingId);
      resp = "rating deleted";
    }
    return resp;
  }

  @Override
  public String deleteRatingsByUser(String email) {
    String resp = "fail";
    RatingManager.getInstance().deleteRatingsByUser(email);
    resp = "ratings deleted";
    return resp;
  }

  /**
   * SHOULD ONLY BE USED SPARINGLY, PRIOR TO FESTIVAL START
   * 
   * @param year
   * @return
   */
  @Override
  public String deleteRatingsByYear(FestivalEnum fest, Integer year) {
    String resp = "fail";
    RatingManager.getInstance().deleteRatingsByYear(fest, year);
    resp = "ratings deleted";
    return resp;
  }

  @Override
  public String emailRatingsToUser(String email) {
    // TODO
    // String resp = CoachellaEmailSender.emailRatings(email);
    // return resp;
    return "";
  }

  /**
   * Called by GWT
   */
  @Override
  public String emailRatingsToUser(FestivalEnum fest) {
    String resp = "Ratings not sent";
    AppUser appUser = getCurrentLogin();
    if (appUser.getEmail() != null) {
      switch (fest) {
      case COACHELLA:
        CoachellaEmailSender cEmailSender = new CoachellaEmailSender(appUser.getAuthType(), appUser.getAuthId(),
            appUser.getAuthToken(), appUser.getEmail());
        resp = cEmailSender.emailRatings();
        break;
      case LOLLAPALOOZA:
        LollaEmailSender lEmailSender = new LollaEmailSender(appUser.getAuthType(), appUser.getAuthId(),
            appUser.getAuthToken(), appUser.getEmail());
        resp = lEmailSender.emailRatings();
        break;
      case TESTFEST:
        resp = "Ratings not sent";
        break;
      }
    }
    return resp;
  }

  /**
   * Called only by Lolla API
   */
  private String insertSetDataFromApi() {
    String success = "something happened";
    // LollaRatingManager.getInstance().deleteSetsByYear(2012);
    // LollaSetDataLoader.getInstance().insertSetsFromApi(2012);
    success = "success i believe";
    return success;
  }

  @Override
  public String updateSetData(FestivalEnum fest) {
    String success;
    String url;
    // TODO: move this to res file
    url = "http://ratethisfest.appspot.com/resources/sets_coachella_2015.txt";
    // url = "http://127.0.0.1:8888/resources/sets_coachella_2013.txt";
    // "http://ratethisfest.appspot.com/resources/sets_lolla_2012.txt";
    // url = "http://127.0.0.1:8888/resources/sets_lolla_2012.txt";
    success = loadFile(fest, url);

    return success;
  }

  public String updateSetFile() {
    String success = "something happened";
    // LollaSetDataLoader.getInstance().updateSetFestival();
    success = "success i believe";
    return success;
  }

  @Override
  public String recalculateSetRatingAverages(FestivalEnum fest) {
    switch (fest) {
    case COACHELLA:
      CoachellaSetDataLoader.getInstance().recalculateSetRatingAveragesByYear(fest, 2014);
      break;
    case LOLLAPALOOZA:
      LollaSetDataLoader.getInstance().recalculateSetRatingAveragesByYear(fest, 2014);
      break;
    case TESTFEST:
      break;
    }

    return "success i believe";
  }

  private String loadFile(FestivalEnum fest, String url) {
    String success = "something happened";
    try {
      URL inputData = new URL(url);
      URLConnection urlConn = inputData.openConnection();
      InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), "UTF8");
      BufferedReader in = new BufferedReader(is);
      // RatingManager.getInstance().deleteAllSets();

      switch (fest) {
      case COACHELLA:
        CoachellaSetDataLoader.getInstance().updateSetsFromFile(in);
        break;
      case LOLLAPALOOZA:
        LollaSetDataLoader.getInstance().updateSetsFromFile(in);
        break;
      case TESTFEST:
        break;
      }

      success = "success i believe";
    } catch (Exception e) {
      success = "something happened";
      log.log(Level.WARNING, "loadFile: " + e.getMessage());
    } finally {
    }
    return success;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to prevent cross-site script vulnerabilities.
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
