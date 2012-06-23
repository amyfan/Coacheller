package com.lollapaloozer.auth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;



public class AuthDemoModel {
	
	private GoogleAuthProvider _authProviderGoogle;
	private FacebookAuthProvider _authProviderFacebook;
	private AuthDemoActivity _activity;
	
	private String _verifiedAccountName = null;
	private TwitterAuthProvider _authProviderTwitter;
	
	
	
	public AuthDemoModel (AuthDemoActivity activity) {
		_activity = activity;
		_authProviderGoogle = new GoogleAuthProvider(_activity);
		_authProviderFacebook = new FacebookAuthProvider(_activity);
		_authProviderTwitter = new TwitterAuthProvider(_activity);
	}
	
	public boolean isLoggedIn() {
		int numLogins = 0;
		if (_authProviderGoogle.isLoggedIn()) {
			numLogins++;
		}
		
		if (_authProviderFacebook.isLoggedIn()) {
			numLogins++;
		}
		
		if (_authProviderTwitter.isLoggedIn()) {
			numLogins++;
		}
		
		if (numLogins > 1) {
			System.out.println("WARNING: Multiple concurrent login types detected");
		}
		
		if (numLogins > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public AuthProvider getCurrentAuthProvider() {
		if (_authProviderGoogle.isLoggedIn()) {
			return _authProviderGoogle;
		}
		
		if (_authProviderFacebook.isLoggedIn()) {
			return _authProviderFacebook;
		}
		
		if (_authProviderTwitter.isLoggedIn()) {
			return _authProviderTwitter;
		}
		
		return null;
	}

	public void checkAccounts() {
		AccountManager aMgr = AccountManager.get(_activity);
		
		System.out.println("Warning: The following requires permission GET_ACCOUNTS");
		for (Account a: aMgr.getAccounts()) {
			System.out.println(a.name +" "+ a.type  +" "+ a.toString());
		}
		
		System.out.println("Installed Authenticators: ");
		for (AuthenticatorDescription d : aMgr.getAuthenticatorTypes()) {
			System.out.print(d.type);
			System.out.print("/ ");
		}
		System.out.println();
	}
	
	



	public Facebook getFacebookObject() {
		return _authProviderFacebook.getFacebookObject();
	}
	
	public void invalidateTokens() {
		_authProviderGoogle.logout();
		_authProviderFacebook.logout();
		_authProviderTwitter.logout();
	}

	public void loginToGoogle() {
		_authProviderGoogle.login();
	}

	public void loginToFacebook() {
		_authProviderFacebook.login();	
	}

	public void loginToTwitter() {
		_authProviderTwitter.login();
	}
	
	public void twitterAuthCallback(int requestCode, int resultCode, Intent data) {
		_authProviderTwitter.requestTokenCallback(requestCode, resultCode, data);
	}



}
