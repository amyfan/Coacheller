package com.lollapaloozer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

import com.lollapaloozer.util.LollapaloozerHelper;
import com.ratethisfest.shared.HttpConstants;

public class LollapaloozerServiceUtils {
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

      LollapaloozerHelper.debug(context, "HTTPGet = " + requestString.toString());
      HttpGet get = new HttpGet(requestString.toString());
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(get);

      return getHttpGetResponse(response, context);

    } catch (ClientProtocolException e) { // TODO: Could have created a
      // custom
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
  public static JSONArray getRatings(String email, String year, String day, Context context)
      throws Exception {

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
      requestString.append(HttpConstants.ACTION_GET_RATINGS);

      LollapaloozerHelper.debug(context, "HTTPGet = " + requestString.toString());
      HttpGet get = new HttpGet(requestString.toString());
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(get);

      return getHttpGetResponse(response, context);

    } catch (ClientProtocolException e) { // TODO: Could have created a
      // custom
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
  public static String addRating(String email, String setId, String score, String comments,
      Context context) throws Exception {
    try {
      // // TODO: pass in PARAM_NOTES here
      // StringBuilder requestStringb = new StringBuilder();
      // requestStringb.append(HttpConstants.SERVER_URL_LOLLAPALOOZER);
      // requestStringb.append(HttpConstants.PARAM_EMAIL).append("=").append(email);
      // requestStringb.append("&").append(HttpConstants.PARAM_SET_ID).append("=").append(URLEncoder.encode(setId));
      // requestStringb.append("&").append(HttpConstants.PARAM_SCORE).append("=").append(URLEncoder.encode(score));
      // requestStringb.append("&").append(HttpConstants.PARAM_NOTES).append("=").append(URLEncoder.encode(comments));
      // requestStringb.append("&").append(HttpConstants.PARAM_ACTION).append("=").append(URLEncoder.encode(HttpConstants.ACTION_ADD_RATING));
      //
      // String requestString = requestStringb.toString();
      //
      // LollapaloozerHelper.debug(context, "HTTPPost = " +
      // requestString);

      //
      // HttpParams params = post.getParams();
      // params.setParameter(HttpConstants.PARAM_EMAIL, email);
      // params.setParameter(HttpConstants.PARAM_SET_ID, setId);
      // params.setParameter(HttpConstants.PARAM_SCORE, score);
      // params.setParameter(HttpConstants.PARAM_ACTION,
      // HttpConstants.ACTION_ADD_RATING);
      // params.setParameter(HttpConstants.PARAM_NOTES, comments);
      // post.setParams(params);

      HttpPost post = new HttpPost(HttpConstants.SERVER_URL_LOLLAPALOOZER);

      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, email));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_SET_ID, setId));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_SCORE, score));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_NOTES, comments));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_ACTION,
          HttpConstants.ACTION_ADD_RATING));

      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(post);

      return getHttpPostResponse(response, context);

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

  public static String emailMyRatings(Context context, String email) throws Exception {
    try {
      HttpPost post = new HttpPost(HttpConstants.SERVER_URL_LOLLAPALOOZER);

      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, email));
      nameValuePairs.add(new BasicNameValuePair(HttpConstants.PARAM_ACTION,
          HttpConstants.ACTION_EMAIL_RATINGS));

      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      HttpClient hc = new DefaultHttpClient();
      HttpResponse response = hc.execute(post);

      return getHttpPostResponse(response, context);

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

  private static JSONArray getHttpGetResponse(HttpResponse response, Context context)
      throws Exception {

    // get the response from GAE server, should be in JSON format
    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      LollapaloozerHelper.debug(context, HTTP_SUCCESS);

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
      LollapaloozerHelper.debug(context, HTTP_FAILURE + response.getStatusLine().getStatusCode());
      throw new Exception();
    }

  }

  private static String getHttpPostResponse(HttpResponse response, Context context)
      throws Exception {

    // get the response from GAE server, should be in JSON format
    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      LollapaloozerHelper.debug(context, HTTP_SUCCESS);

      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
          .getContent(), "UTF-8"));
      StringBuilder builder = new StringBuilder();
      for (String line = null; (line = reader.readLine()) != null;) {
        builder.append(line).append("\n");
      }

      return builder.toString();

    } else {
      LollapaloozerHelper.debug(context, HTTP_FAILURE + response.getStatusLine().getStatusCode());
      throw new Exception();
    }
  }

}
