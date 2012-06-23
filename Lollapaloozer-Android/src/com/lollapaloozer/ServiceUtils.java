package com.lollapaloozer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

import com.ratethisfest.shared.HttpConstants;

public class ServiceUtils {
  private final static String HTTP_SUCCESS = "Received HTTP Response";
  private final static String HTTP_FAILURE = "HTTP Response was not OK: ";

  /**
   * 
   * @param year
   * @param day
   * @param context
   * @return
   * @throws Exception
   */
  public static JSONArray getSets(String year, String day, Context context) throws Exception {
    try {
      // Returning FAKE DATA
      // if (1 < 3) {
      // return FakeDataSource.getData();
      // }

      StringBuilder requestString = new StringBuilder();
      requestString.append(HttpConstants.SERVER_URL_LOLLAPALOOZER);
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

      LollapaloozerApplication.debug(context, "HTTPGet = " + requestString.toString());
      HttpGet get = new HttpGet(requestString.toString());
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(get);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        LollapaloozerApplication.debug(context, HTTP_SUCCESS);

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
        LollapaloozerApplication.debug(context, HTTP_FAILURE
            + response.getStatusLine().getStatusCode());
        throw new Exception();
      }

    } catch (ClientProtocolException e) { // TODO: Could have created a custom
                                          // Exception class
      e.printStackTrace();
      throw new Exception();
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception();
    } catch (JSONException e) {
      e.printStackTrace();
      throw new Exception();
    }
  }

  // Sample of working URL
  // http://ratethisfest.appspot.com/coachellerServlet?email=testing@this.com&action=get_sets&year=2012&day=Friday
  public static JSONArray getRatings(String email, String day, Context context) throws Exception {

    /**
     * 
     * @param email
     * @param context
     * @return
     */

    try {
      StringBuilder requestString = new StringBuilder();
      requestString.append(HttpConstants.SERVER_URL_LOLLAPALOOZER);
      requestString.append(HttpConstants.PARAM_EMAIL);
      requestString.append("=");
      requestString.append(email);
      requestString.append("&");
      requestString.append("year=2012&day=" + day + "&");
      requestString.append(HttpConstants.PARAM_ACTION);
      requestString.append("=");
      requestString.append(HttpConstants.ACTION_GET_RATINGS);

      LollapaloozerApplication.debug(context, "HTTPGet = " + requestString.toString());
      HttpGet get = new HttpGet(requestString.toString());
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(get);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        LollapaloozerApplication.debug(context, HTTP_SUCCESS);

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
        LollapaloozerApplication.debug(context, HTTP_FAILURE
            + response.getStatusLine().getStatusCode());
        throw new Exception();
      }

    } catch (ClientProtocolException e) { // TODO: Could have created a custom
                                          // Exception class
      e.printStackTrace();
      throw new Exception();
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception();
    } catch (JSONException e) {
      e.printStackTrace();
      throw new Exception();
    }
  }

  /**
   * @param email
   * @param artist
   * @param year
   * @param score
   * @param context
   * @return
   * @throws Exception
   */

  // TODO returns JSONArray which is probably null - is this correct?
  public static String addRating(String email, String setId, String score,
      Context context) throws Exception {
    try {
      // TODO: pass in PARAM_NOTES here
      StringBuilder requestString = new StringBuilder();
      requestString.append(HttpConstants.SERVER_URL_LOLLAPALOOZER);
      requestString.append(HttpConstants.PARAM_EMAIL);
      requestString.append("=");
      requestString.append(email);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_SET_ID);
      requestString.append("=");
      requestString.append(setId);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_SCORE);
      requestString.append("=");
      requestString.append(score);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_ACTION);
      requestString.append("=");
      requestString.append(HttpConstants.ACTION_ADD_RATING);

      LollapaloozerApplication.debug(context, "HTTPPost = " + requestString.toString());
      HttpPost post = new HttpPost(requestString.toString());
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(post);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        LollapaloozerApplication.debug(context, HTTP_SUCCESS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
            .getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
          builder.append(line).append("\n");
        }

        return builder.toString();

      } else {
        LollapaloozerApplication.debug(context, HTTP_FAILURE
            + response.getStatusLine().getStatusCode());
        throw new Exception();
      }

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      throw new Exception();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      throw new Exception();
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception();
    }
  }

  // This may have a bug or 3, I was getting tired
  public static String sendMyRatings(Context context, String email) throws Exception {
    try {
      StringBuilder requestString = new StringBuilder();
      requestString.append(HttpConstants.SERVER_URL_LOLLAPALOOZER);
      requestString.append(HttpConstants.PARAM_EMAIL);
      requestString.append("=");
      requestString.append(email);
      requestString.append("&");
      requestString.append(HttpConstants.PARAM_ACTION);
      requestString.append("=");

      // TODO fix this for email feature
      requestString.append(HttpConstants.ACTION_ADD_RATING);

      LollapaloozerApplication.debug(context, "HTTPPost = " + requestString.toString());
      HttpPost post = new HttpPost(requestString.toString());
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(post);

      // get the response from GAE server, should be in JSON format
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        LollapaloozerApplication.debug(context, HTTP_SUCCESS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
            .getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
          builder.append(line).append("\n");
        }

        return builder.toString();

      } else {
        LollapaloozerApplication.debug(context, HTTP_FAILURE
            + response.getStatusLine().getStatusCode());
        throw new Exception();
      }

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      throw new Exception();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      throw new Exception();
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception();
    }
  }
}
