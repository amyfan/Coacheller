package com.coacheller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONTokener;

import android.content.Context;

import com.coacheller.shared.HttpConstants;

public class ServiceUtils {
  private final static String HTTP_SUCCESS = "Received HTTP Response";
  private final static String HTTP_FAILURE = "HTTP Response was not OK: ";

  public static JSONArray getSets(String year, String day, Context context) {
    try {
      // Returning FAKE DATA
      // if (1 < 3) {
      // return FakeDataSource.getData();
      // }

      HttpResponse response;
      HttpClient hc = new DefaultHttpClient();

      StringBuilder requestString = new StringBuilder();
      requestString.append(HttpConstants.SERVER_URL);
      requestString.append(HttpConstants.PARAM_YEAR);
      requestString.append("=");
      requestString.append(year);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_DAY);
      requestString.append("=");
      requestString.append(day);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_ACTION);
      requestString.append("=");
      requestString.append(HttpConstants.ACTION_GET_SETS);

      CoachellerApplication.debug(context, "HTTPGet = " + requestString.toString());
      HttpGet get = new HttpGet(requestString.toString());
      response = hc.execute(get);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        CoachellerApplication.debug(context, HTTP_SUCCESS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
            .getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
          builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONArray finalResult = new JSONArray(tokener);
        return finalResult;

      } else {
        CoachellerApplication.debug(context, HTTP_FAILURE
            + response.getStatusLine().getStatusCode());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public static JSONArray getRatings(String email, Context context) {
    try {
      // Returning FAKE DATA
      // if (1 < 3) {
      // return FakeDataSource.getData();
      // }

      HttpResponse response;
      HttpClient hc = new DefaultHttpClient();

      StringBuilder requestString = new StringBuilder();
      requestString.append(HttpConstants.SERVER_URL);
      requestString.append(HttpConstants.PARAM_EMAIL);
      requestString.append("=");
      requestString.append(email);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_ACTION);
      requestString.append("=");
      requestString.append(HttpConstants.ACTION_GET_SETS);

      CoachellerApplication.debug(context, "HTTPGet = " + requestString.toString());
      HttpGet get = new HttpGet(requestString.toString());
      response = hc.execute(get);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        CoachellerApplication.debug(context, HTTP_SUCCESS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
            .getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
          builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONArray finalResult = new JSONArray(tokener);
        return finalResult;

      } else {
        CoachellerApplication.debug(context, HTTP_FAILURE
            + response.getStatusLine().getStatusCode());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * @param email
   * @param artist
   * @param year
   * @param weekend
   * @param score
   * @param context
   * @return
   */
  public static JSONArray addRating(String email, String artist, String year, String weekend,
      String score, Context context) {
    try {
      // Returning FAKE DATA
      // if (1 < 3) {
      // return FakeDataSource.getData();
      // }

      HttpResponse response;
      HttpClient hc = new DefaultHttpClient();

      StringBuilder requestString = new StringBuilder();
      requestString.append(HttpConstants.SERVER_URL);
      requestString.append(HttpConstants.PARAM_EMAIL);
      requestString.append("=");
      requestString.append(email);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_ARTIST);
      requestString.append("=");
      requestString.append(artist);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_YEAR);
      requestString.append("=");
      requestString.append(year);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_WEEKEND);
      requestString.append("=");
      requestString.append(weekend);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_SCORE);
      requestString.append("=");
      requestString.append(score);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_ACTION);
      requestString.append("=");
      requestString.append(HttpConstants.ACTION_ADD_RATING);

      CoachellerApplication.debug(context, "HTTPPost = " + requestString.toString());
      HttpPost post = new HttpPost(requestString.toString());
      response = hc.execute(post);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        CoachellerApplication.debug(context, HTTP_SUCCESS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
            .getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
          builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONArray finalResult = new JSONArray(tokener);
        return finalResult;

      } else {
        CoachellerApplication.debug(context, HTTP_FAILURE
            + response.getStatusLine().getStatusCode());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

}
