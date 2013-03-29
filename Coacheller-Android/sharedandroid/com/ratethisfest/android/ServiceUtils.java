package com.ratethisfest.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
  public static JSONArray getSets(List<NameValuePair> params, Context context, String serverUrl)
      throws Exception {
    try {
      // Returning FAKE DATA
      // if (1 < 3) {
      // return FakeDataSource.getData();
      // }

      String url = serverUrl;
      params.add(new BasicNameValuePair(HttpConstants.PARAM_ACTION, HttpConstants.ACTION_GET_SETS));

      if (!url.endsWith("?")) {
        url += "?";
      }

      String paramString = URLEncodedUtils.format(params, "utf-8");

      url += paramString;

      HttpGet get = new HttpGet(url);
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
  public static JSONArray getRatings(List<NameValuePair> params, Context context, String serverUrl)
      throws Exception {

    /**
     * 
     * @param email
     * @param context
     * @return
     */

    try {

      String url = serverUrl;
      params.add(new BasicNameValuePair(HttpConstants.PARAM_ACTION,
          HttpConstants.ACTION_GET_RATINGS));

      if (!url.endsWith("?")) {
        url += "?";
      }

      String paramString = URLEncodedUtils.format(params, "utf-8");

      url += paramString;
      HttpGet get = new HttpGet(url);

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
  public static String addRating(List<NameValuePair> parameterList, Context context,
      String serverUrl) throws Exception {
    try {
      // // TODO: pass in PARAM_NOTES here
      // StringBuilder requestStringb = new StringBuilder();
      // requestStringb.append(serverUrl);
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

      parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_ACTION,
          HttpConstants.ACTION_ADD_RATING));

      HttpPost post = new HttpPost(serverUrl);
      post.setEntity(new UrlEncodedFormEntity(parameterList));

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

  public static String emailMyRatings(List<NameValuePair> parameterList, Context context,
      String serverUrl) throws Exception {
    try {
      HttpPost post = new HttpPost(serverUrl);

      parameterList.add(new BasicNameValuePair(HttpConstants.PARAM_ACTION,
          HttpConstants.ACTION_EMAIL_RATINGS));

      post.setEntity(new UrlEncodedFormEntity(parameterList));
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
      // CalendarUtils.debug(context, HTTP_SUCCESS);

      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
          .getContent(), "UTF-8"));
      StringBuilder builder = new StringBuilder();
      for (String line = null; (line = reader.readLine()) != null;) {
        builder.append(line).append("\n");
      }
      // CalendarUtils.debug(context, "getHttpGetResponse: " +
      // builder.toString());
      if (builder.toString() == null || builder.toString().equals("null")) {
        return new JSONArray();
      } else {
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONArray finalResult = new JSONArray(tokener);
        return finalResult;
      }
    } else {
      // CalendarUtils.debug(context, HTTP_FAILURE +
      // response.getStatusLine().getStatusCode());
      throw new Exception();
    }

  }

  private static String getHttpPostResponse(HttpResponse response, Context context)
      throws Exception {

    // get the response from GAE server, should be in JSON format
    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      // CalendarUtils.debug(context, HTTP_SUCCESS);

      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
          .getContent(), "UTF-8"));
      StringBuilder builder = new StringBuilder();
      for (String line = null; (line = reader.readLine()) != null;) {
        builder.append(line).append("\n");
      }
      // CalendarUtils.debug(context, "getHttpPostResponse: " +
      // builder.toString());

      return builder.toString();

    } else {
      // CalendarUtils.debug(context, HTTP_FAILURE +
      // response.getStatusLine().getStatusCode());
      throw new Exception();
    }
  }

}
