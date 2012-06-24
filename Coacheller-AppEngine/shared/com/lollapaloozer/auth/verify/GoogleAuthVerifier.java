package com.lollapaloozer.auth.verify;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

import android.accounts.AccountManager;

import com.lollapaloozer.auth.client.OAuthHTTP;

public class GoogleAuthVerifier implements AuthVerifier {

	public boolean verify(String authToken, String identifier) {

		// url = new URL("https://www.googleapis.com/oauth2/v2/userinfo");

		try {
			OAuthHTTP oauthreq;
			oauthreq = new OAuthHTTP(
					"https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
			oauthreq.setRequestProperty("client_id",
					"253259340939.apps.googleusercontent.com");
			oauthreq.setRequestProperty("client_secret",
					"3HqdJ51XXYc6Px83sZuJlfmI");
			oauthreq.setRequestProperty("Authorization", "OAuth " + authToken);
			// conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			// conn.setRequestProperty("Accept","[star]/[star]");

			boolean oauthSuccess = oauthreq.execute();

			if (oauthSuccess) {
				boolean tokenOpSuccess = oauthreq
						.getJSONResultBoolean("verified_email");
				String verifiedAccountName = oauthreq
						.getJSONResultString("email");

				if (verifiedAccountName.equals(identifier)) {
					return true;
				}
			} else {
				System.out.println("OAuth request completed unsuccesfully");
				// TODO probably need to expire token
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

}
