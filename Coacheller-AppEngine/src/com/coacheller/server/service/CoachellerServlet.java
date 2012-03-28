package com.coacheller.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.coacheller.server.domain.DayEnum;
import com.coacheller.server.domain.Set;
import com.coacheller.server.logic.JSONUtils;
import com.coacheller.server.logic.RatingManager;
import com.coacheller.server.logic.SetDataLoader;

@SuppressWarnings("serial")
public class CoachellerServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    String action = checkNull(req.getParameter("action"));
    String userEmail = checkNull(req.getParameter("email"));
    String day = checkNull(req.getParameter("day"));
    String yearString = checkNull(req.getParameter("year"));

    try {
      RatingManager ratingMgr = RatingManager.getInstance();

      List<Set> sets = null;

      if (!yearString.isEmpty()) {
        Integer year = Integer.valueOf(yearString);
        if (!day.isEmpty()) {
          sets = ratingMgr.findSetsByYearAndDay(year, DayEnum.valueOf(day));
        }
        sets = ratingMgr.findSetsByYear(year);
      }

      JSONArray jsonArray = JSONUtils.convertSetsToJSONArray(sets);
      if (jsonArray != null) {
        resp.getWriter().println(jsonArray.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
      resp.getWriter().println("ERROR processing request");
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String action = checkNull(req.getParameter("action"));

    if (action.compareToIgnoreCase("load") == 0) {
      String serverName = req.getServerName();
      String url;
      // TODO: move all this to res file
      if (serverName.compareToIgnoreCase("127.0.0.1") == 0
          || serverName.compareToIgnoreCase("localhost") == 0) {
        url = String.format("http://%s:8888/resources/sets_2012.txt", serverName);
      } else {
        url = "http://ratethisfest.appspot.com/resources/sets_2012.txt";
      }
      loadFile(url);
    }

  }

  private void loadFile(String url) {
    try {
      URL inputData = new URL(url);
      URLConnection urlConn = inputData.openConnection();
      InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), "UTF8");
      BufferedReader in = new BufferedReader(is);
      SetDataLoader dataLoader = SetDataLoader.getInstance();
      RatingManager ratingMgr = RatingManager.getInstance();
      ratingMgr.deleteAllSets();
      dataLoader.insertSets(in);
    } catch (Exception e) {
    } finally {
    }
  }

  private String checkNull(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }
}
