package com.coacheller.server.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.coacheller.server.domain.Set;
import com.coacheller.server.logic.JSONUtils;
import com.coacheller.server.logic.RatingManager;
import com.coacheller.shared.DayEnum;
import com.coacheller.shared.FieldVerifier;

/**
 * HTTP Servlet, intended for Android/etc. device use.
 * 
 * @author Amy
 * 
 */
@SuppressWarnings("serial")
public class CoachellerServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String action = checkNull(req.getParameter("action"));
    String email = checkNull(req.getParameter("email"));
    String day = checkNull(req.getParameter("day"));
    String yearString = checkNull(req.getParameter("year"));
    String weekendString = checkNull(req.getParameter("weekend"));

    if (!FieldVerifier.isValidEmail(email)) {
      resp.getWriter().println(FieldVerifier.EMAIL_ERROR);
    } else if (!FieldVerifier.isValidYear(yearString)) {
      resp.getWriter().println(FieldVerifier.YEAR_ERROR);
    } else if (!FieldVerifier.isValidDay(day)) {
      resp.getWriter().println(FieldVerifier.DAY_ERROR);
    } else {
      List<Set> sets = null;

      if (yearString != null && !yearString.isEmpty()) {
        Integer year = Integer.valueOf(yearString);
        if (day != null && !day.isEmpty()) {
          sets = RatingManager.getInstance().findSetsByYearAndDay(year, DayEnum.fromValue(day));
        } else {
          sets = RatingManager.getInstance().findSetsByYear(year);
        }
      }

      JSONArray jsonArray = JSONUtils.convertSetsToJSONArray(sets);
      if (jsonArray != null) {
        resp.getWriter().println(jsonArray.toString());
      } else {
        resp.getWriter().println("no data hrm");
      }
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String action = checkNull(req.getParameter("action"));

    if (action.compareToIgnoreCase("load") == 0) {

    }

  }

  private String checkNull(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }
}
