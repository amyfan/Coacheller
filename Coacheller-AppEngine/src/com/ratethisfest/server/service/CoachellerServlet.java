package com.ratethisfest.server.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.data.HttpConstants;
import com.ratethisfest.server.logic.CoachellaEmailSender;
import com.ratethisfest.server.logic.LollaEmailSender;

/**
 * HTTP Servlet, intended for Android/etc. device use.
 * 
 * @author Amy
 * 
 */
@SuppressWarnings("serial")
public class CoachellerServlet extends FestivalServlet {

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
    String authType = updateAuthType(checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TYPE)));
    String authId = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_ID));
    String authToken = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TOKEN));
    String email = checkNull(req.getParameter(HttpConstants.PARAM_EMAIL));
    String day = checkNull(req.getParameter(HttpConstants.PARAM_DAY));
    String year = checkNull(req.getParameter(HttpConstants.PARAM_YEAR));

    String respString = "";

    if (action.equals(HttpConstants.ACTION_GET_SETS)) {
      respString = getSetsJson(FestivalEnum.COACHELLA, year, day);
    } else if (action.equals(HttpConstants.ACTION_GET_RATINGS)) {
      respString = getRatingsJsonByUser(FestivalEnum.COACHELLA, authType, authId, authToken, email, year, day);
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
    String authType = updateAuthType(checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TYPE)));
    String authId = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_ID));
    String authToken = checkNull(req.getParameter(HttpConstants.PARAM_AUTH_TOKEN));
    String email = checkNull(req.getParameter(HttpConstants.PARAM_EMAIL));
    String setId = checkNull(req.getParameter(HttpConstants.PARAM_SET_ID));
    String weekend = checkNull(req.getParameter(HttpConstants.PARAM_WEEKEND));
    String score = checkNull(req.getParameter(HttpConstants.PARAM_SCORE));
    String notes = checkNull(req.getParameter(HttpConstants.PARAM_NOTES));

    if (action.equals(HttpConstants.ACTION_ADD_RATING)) {
      out.println("Calling addRating()");
      if (verifyToken(authType, authId, authToken)) {
        addRating(authType, authId, authToken, email, setId, weekend, score, notes);
      }
    } else if (action.equals(HttpConstants.ACTION_EMAIL_RATINGS)) {
      out.println("Calling emailRatings(authType=" + authType + " authId=" + authId + " email=" + email + " authToken="
          + authToken + ")");
      if (verifyToken(authType, authId, authToken)) {
        // String result = CoachellaEmailSender.emailRatings(authType, authId, authToken, email);
        CoachellaEmailSender emailSender = new CoachellaEmailSender(authType, authId, authToken, email);
        String result = emailSender.emailRatings();
        out.println("Result: " + result);
      } else {
        out.println("Request is refused because user account did not pass verification");
      }
    } // end Email Ratings
    out.println("Done!");
    out.close();
  }

}
