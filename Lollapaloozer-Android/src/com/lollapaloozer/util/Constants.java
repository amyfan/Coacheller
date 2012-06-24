package com.lollapaloozer.util;

public class Constants {
	
	//Data
	public static final int DATA_NOTE_VISIBLE_MAX_LENGTH = 140;

	// UI String
	public static final String UI_STR_LOGIN_STATUS = "Login Status: ";

	public static final String MSG_EMAIL_FORMAT = "Please enter your real email address.";
	public static final String MSG_SIGNIN_REQUIRED = "Sign in so we can remember this for you.";
	
	public static final String DIALOG_TITLE_GET_EMAIL = "Keep Track of Everything";
	public static final String DIALOG_TITLE_FIRST_USE = "Welcome, Fellow Lollapaloozer";

	// User Authentication

	public static final String CONSUMER_KEY = "yit4Mu71Mj93eNILUo3uCw";
	public static final String CONSUMER_SECRET = "rdYvdK4g3ckWVdnvzmAj6JXmj9RoI05rIb4nVYQsoI";

	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

	public static final String OAUTH_CALLBACK_SCHEME = "http";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
			+ "://lollapaloozer.appspot.com/dummy"; // todo update this

	// public static final String OAUTH_CALLBACK_SCHEME = "http";
	// public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME +
	// "://msnbc.com"; //todo update this

	public static final String OAUTH_CALLBACK_PARAM_TOKEN = "oauth_token";
	public static final String OAUTH_CALLBACK_PARAM_VERIFIER = "oauth_verifier";

	public static final int DEFAULT_PORT_HTTP = 80;
	public static final int DEFAULT_PORT_HTTPS = 443;
	public static final String OAUTH_REQUEST_CONTENT_TYPE = "application/x-www-form-urlencoded";

	public static final int INTENT_REQ_TWITTER_LOGIN = 1;
	public static final String INTENT_EXTRA_ALIAS_TWITTER_AUTH = "intent.extra.twitter.auth";
	public static final String INTENT_EXTRA_OAUTH1_TOKEN = "intent.extra.oauth1.token";
	public static final String INTENT_EXTRA_OAUTH1_VERIFIER = "intent.extra.oauth1.verifier";


}