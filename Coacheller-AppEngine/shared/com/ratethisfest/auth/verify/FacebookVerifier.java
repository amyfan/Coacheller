package com.ratethisfest.auth.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

//import com.facebook.android.FacebookError;
//import com.facebook.android.Util;
import com.ratethisfest.android.log.LogController;

public class FacebookVerifier implements AuthVerifier {

  private int _failuresToSimulate;

  
  //Commented so it compiles
  @Override
  public boolean verify(String authToken, String identifier) {
//    LogController.AUTH_FACEBOOK.logMessage("Verifying Facebook token: " + authToken + " identifier: " + identifier);
//
//    HttpClient hc = new DefaultHttpClient();
//    HttpGet req = new HttpGet("https://graph.facebook.com/me?access_token=" + authToken);
//
//    try {
//      HttpResponse response = hc.execute(req);
//
//      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
//          .getContent(), "UTF-8"));
//      StringBuilder builder = new StringBuilder();
//      for (String line = null; (line = reader.readLine()) != null;) {
//        builder.append(line).append("\n");
//      }
//
//      
//      JSONObject json = Util.parseJson(builder.toString());
//      String userID = json.getString("id");
//      String userName = json.getString("name");
//      String email = json.getString("email");
//
//      if (identifier.equals(email)) {
//        LogController.AUTH_FACEBOOK.logMessage("Verification passed");
//        return true;
//      }
//
//    } catch (ClientProtocolException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch (FacebookError e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch (JSONException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//
//    LogController.AUTH_FACEBOOK.logMessage("Verification failed");
    return false;
  }

  @Override
  public void simulateFailure(int failures) {
    _failuresToSimulate = failures;
    throw new RuntimeException("Not Implemented for facebook");
  }

}
