package com.ratethisfest.server.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.data.HttpConstants;
import com.ratethisfest.server.logic.LollaEmailSender;

/**
 * HTTP Servlet, intended for Android/etc. device use.
 * 
 * @author Amy
 * 
 */
@SuppressWarnings("serial")
public class LollapaloozerServlet extends FestivalServlet {
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
      respString = getSetsJson(FestivalEnum.LOLLAPALOOZA, year, day);
    } else if (action.equals(HttpConstants.ACTION_GET_RATINGS)) {

      if (verifyToken(authType, authId, authToken)) {
        respString = getRatingsJsonByUser(FestivalEnum.LOLLAPALOOZA, authType, authId, authToken, email, year, day);
      }

    }

    resp.getWriter().println(respString);
  }

  /**
   * 
   * @param req
   * @param resp
   * @throws IOException
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    out.println("doPost: handling request for the following parameters:");

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

    if (action.equals(HttpConstants.ACTION_ADD_RATING)) {
      out.println("Calling addRating()");
      if (verifyToken(authType, authId, authToken)) {
        addRating(authType, authId, authToken, email, setId, weekend, score, notes);
      }
    } else if (action.equals(HttpConstants.ACTION_EMAIL_RATINGS)) {
      out.println("Calling emailRatings(authType=" + authType + " authId=" + authId + " email=" + email + " authToken="
          + authToken + ")");
      if (verifyToken(authType, authId, authToken)) {
        String result = LollaEmailSender.emailRatings(authType, authId, authToken, email);
        out.println("Result: " + result);
      } else {
        out.println("Request is refused because user account did not pass verification");
      }
    } // end Email Ratings
    out.println("Done!");
    out.close();
  }

}
