package com.lollapaloozer.auth;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GoogleAuthProvider implements AuthProvider {

	private final String ACCOUNT_TYPE_REQUESTED = "com.google";

	private AuthDemoActivity _activity;
	private Bundle _currentGoogleLoginTokenBundle = null;
	private JSONObject _currentAuthResult;
	private boolean _confirmedAuthorizedGoogle;
	private String _verifiedAccountName = null;

	private GoogleAuthProvider() {
	}

	public GoogleAuthProvider(AuthDemoActivity activity) {
		_activity = activity;
	}

	@Override
	public boolean isLoggedIn() {
		return _confirmedAuthorizedGoogle;
	}

	@Override
	public void login() {

		System.out.println("Starting");
		AccountManagerFuture<Bundle> bundleFuture = AccountManager.get(
				_activity).getAuthTokenByFeatures(
				ACCOUNT_TYPE_REQUESTED,
				// "Manage your tasks"
				// "https://www.googleapis.com/auth/userinfo.email"
				// "https://www.googleapis.com/auth/userinfo.profile"
				"oauth2:https://www.googleapis.com/auth/userinfo.email"
				// "View your email address"
				// "Steal all of your stuff"
				, null, _activity, null, null,
				new AccountManagerCallback<Bundle>() {

					public void run(AccountManagerFuture<Bundle> future) {

						try {
							_currentGoogleLoginTokenBundle = future.getResult();
							System.out
									.println("Received token bundle from AccountManagerCallback");
						} catch (OperationCanceledException e) {
							Log.e("e", e.getMessage(), e);
							System.out
									.println("User appears to have denied auth request");
						} catch (AuthenticatorException e) {
							Log.e("e", e.getMessage(), e);
						} catch (IOException e) {
							Log.e("e", e.getMessage(), e);
						}
						_useBundle();
						_activity.modelChanged();
					}
				}, null);
		System.out.println("Done with AccountManager call");
	}

	private void _useBundle() {

		for (String s : _currentGoogleLoginTokenBundle.keySet()) {
			System.out.println("Found key/value: " + s + "="
					+ _currentGoogleLoginTokenBundle.getString(s));
		}

		if (_currentGoogleLoginTokenBundle
				.containsKey(AccountManager.KEY_INTENT)) {
			throw new RuntimeException("Unexpected Code Path");
			// TODO return to account selection or something...
		}

		// url = new URL("https://www.googleapis.com/oauth2/v2/userinfo");

		try {
			OAuthHTTP oauthreq;
			oauthreq = new OAuthHTTP(
					"https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
			oauthreq.setRequestProperty("client_id",
					"253259340939.apps.googleusercontent.com");
			oauthreq.setRequestProperty("client_secret",
					"3HqdJ51XXYc6Px83sZuJlfmI");
			oauthreq.setRequestProperty(
					"Authorization",
					"OAuth "
							+ _currentGoogleLoginTokenBundle
									.getString(AccountManager.KEY_AUTHTOKEN));
			// conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			// conn.setRequestProperty("Accept","[star]/[star]");

			boolean oauthSuccess = oauthreq.execute();

			if (oauthSuccess) {
				_confirmedAuthorizedGoogle = oauthreq
						.getJSONResultBoolean("verified_email");
				_verifiedAccountName = oauthreq.getJSONResultString("email");
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
	}

	@Override
	public void logout() {
		if (_currentGoogleLoginTokenBundle != null) {
			AccountManager aMgr = AccountManager.get(_activity);
			aMgr.invalidateAuthToken(getAccountType(), getAuthToken());
		}

		// Wipe login status
		_currentGoogleLoginTokenBundle = null;
		_confirmedAuthorizedGoogle = false; // Not authenticated
		_verifiedAccountName = null;
	}

	public String getAuthToken() {
		return _currentGoogleLoginTokenBundle
				.getString(AccountManager.KEY_AUTHTOKEN);
	}

	public String getAccountType() {
		return _currentGoogleLoginTokenBundle
				.getString(AccountManager.KEY_ACCOUNT_TYPE);
	}

	public String getLocalAccountName() {
		return _currentGoogleLoginTokenBundle
				.getString(AccountManager.KEY_ACCOUNT_NAME);
	}

	@Override
	public String getVerifiedAccountIdentifier() {
		return _verifiedAccountName;
	}

	@Override
	public void extendAccess() {

		System.out
				.println("Activity resumed.  Not sure if google auth token is automatically extended.");
	}

}
