package com.ratethisfest.auth.verify;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

import com.ratethisfest.auth.OAuthHTTP;

public class GoogleAuthVerifier implements AuthVerifier {
  private static int _failuresToSimulate;
  private final String CLIENT_ID;
  private final String CLIENT_SECRET;
 

  //Cannot instantiate without specifying application
  @SuppressWarnings("unused")
  private GoogleAuthVerifier () {
    CLIENT_ID = "";
    CLIENT_SECRET = "";
  }
  
  public GoogleAuthVerifier(String client_id, String client_secret) {
    CLIENT_ID = client_id;
    CLIENT_SECRET = client_secret;
  }

  @Override
  public boolean verify(String authToken, String identifier) {

    System.out.println("Verifying Google token: " + authToken + " identifier: " + identifier);
    System.out.println(_failuresToSimulate);
    try {
      OAuthHTTP oauthreq;
      oauthreq = new OAuthHTTP("https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
      oauthreq.setRequestProperty("client_id", CLIENT_ID);
      oauthreq.setRequestProperty("client_secret", CLIENT_SECRET);
      oauthreq.setRequestProperty("Authorization", "OAuth " + authToken);

      boolean oauthSuccess = oauthreq.execute();

      if (oauthSuccess) {
        boolean tokenOpSuccess = oauthreq.getJSONResultBoolean("verified_email");
        String verifiedAccountName = oauthreq.getJSONResultString("email");

        if (verifiedAccountName.equals(identifier)) {
          if (_failuresToSimulate > 0) {
            _failuresToSimulate--;
            System.out.println("Simulated login failure, " + _failuresToSimulate + " remaining");

          } else {
            System.out.println("Verification passed");
            return true;
          }
        } // End actual success, possible simulated failure

      } else {
        // Actual failure
        System.out.println("OAuth request completed unsuccesfully");
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Verification failed");
    return false;
  }

  @Override
  public void simulateFailure(int failures) {
    _failuresToSimulate = failures;
    System.out.println(_failuresToSimulate);
  }

}
