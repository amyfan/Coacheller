package com.lollapaloozer.auth;

import com.lollapaloozer.R;
import com.lollapaloozer.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AuthDemoActivity extends Activity implements OnClickListener {

	// Framework
	private TextView _loginStatusText;
	private TextView _accountNameText;
	private TextView _tokenIdText;

	// App
	private AuthDemoModel _model;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth_choose);
		System.out.println("OnCreate starting");

		// Framework
		_loginStatusText = (TextView) this.findViewById(R.id.text_login_status);
		_accountNameText = (TextView) this.findViewById(R.id.text_account_name);
		_tokenIdText = (TextView) this.findViewById(R.id.text_token_id);

		this.findViewById(R.id.btn_login_facebook).setOnClickListener(this);
		this.findViewById(R.id.btn_login_twitter).setOnClickListener(this);
		this.findViewById(R.id.btn_login_google).setOnClickListener(this);
		this.findViewById(R.id.btn_invalidate_tokens).setOnClickListener(this);

		// App
		_model = new AuthDemoModel(this);
		_model.checkAccounts();
		System.out.println("OnCreate complete");
	}

	@Override
	protected void onResume() {
		super.onResume();

		AuthProvider currentProvider = _model.getCurrentAuthProvider();
		if (currentProvider != null) {
			currentProvider.extendAccess();
		}

		_updateUI();

	}

	@Override
	public void onClick(View arg0) {
		String buttonClickedName = this.getResources().getResourceEntryName(
				arg0.getId());
		System.out.println("Button Click: " + buttonClickedName);

		if (buttonClickedName.equals(this.getResources().getResourceEntryName(
				R.id.btn_login_google))) {
			_model.loginToGoogle(); // UI Update done on callback
		}
		if (buttonClickedName.equals(this.getResources().getResourceEntryName(
				R.id.btn_login_twitter))) {
			_model.loginToTwitter();

		}
		if (buttonClickedName.equals(this.getResources().getResourceEntryName(
				R.id.btn_login_facebook))) {
			_model.loginToFacebook();

		}
		if (buttonClickedName.equals(this.getResources().getResourceEntryName(
				R.id.btn_invalidate_tokens))) {
			_model.invalidateTokens();
			_updateUI();
		}
	}

	private void _updateUI() {

		if (_model.isLoggedIn()) {
			AuthProvider currentProvider = _model.getCurrentAuthProvider();
			_setLoginStatus(currentProvider.getAccountType());
			_accountNameText.setText("Account: "
					+ currentProvider.getLocalAccountName() + "\r\n"
					+ currentProvider.getAccountType()
					+ " confirms you are the owner of "
					+ currentProvider.getVerifiedAccountIdentifier());
			_tokenIdText.setText("Token ID: " + currentProvider.getAuthToken());
		} else {
			_setLoginStatus("Not Logged In");
			_accountNameText.setText("");
			_tokenIdText.setText("");
		}

		if (this.findViewById(R.layout.auth_choose) != null) {
			this.findViewById(R.layout.auth_choose).invalidate();
		}

	}

	private void _setLoginStatus(String status) {
		_loginStatusText.setText(Constants.UI_STR_LOGIN_STATUS + " " + status);
	}

	// For Facebook
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("onActivityResult(requestCode=[" + requestCode
				+ "], resultCode=[" + resultCode + "]");

		switch (requestCode) {
		case Constants.INTENT_REQ_TWITTER_LOGIN:
			_model.twitterAuthCallback(requestCode, resultCode, data);
			break;
		default:
			System.out.println("onACtivityResult called with unknown values: "
					+ requestCode + "," + resultCode);
			_model.getFacebookObject().authorizeCallback(requestCode,
					resultCode, data);
			break;
		}
	}

	public void modelChanged() {
		// Run method in main UI thread
		View v = findViewById(android.R.id.content);
		v.post(new Runnable() {
			public void run() {
				_updateUI();
			}
		});

		// _updateUI();
	}

}