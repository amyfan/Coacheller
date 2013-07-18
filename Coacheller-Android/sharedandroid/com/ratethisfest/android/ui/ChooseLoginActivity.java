package com.ratethisfest.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.coacheller.CoachellerApplication;
import com.coacheller.R;
import com.ratethisfest.android.auth.AuthActivityInt;
import com.ratethisfest.android.auth.AuthProviderInt;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.shared.AuthConstants;

/**
 * Activity to choose login type
 * 
 */
public class ChooseLoginActivity extends Activity implements OnClickListener, AuthActivityInt {

  private boolean _debugMode = false;

  // Framework
  private CoachellerApplication _app;
  private TextView _loginStatusText;
  private TextView _accountNameText;
  private TextView _tokenIdText;

  private Button _buttonLoginFacebookWeb;
  private Button _buttonInvalidateTokens;
  private Button _buttonDismissActivity;

  // App
  private boolean _firstStart = true;

  private static final int SWIPE_MIN_DISTANCE = 120;
  private static final int SWIPE_MAX_OFF_PATH = 250;
  private static final int SWIPE_THRESHOLD_VELOCITY = 200;
  private GestureDetector _gestureDetector;
  private View.OnTouchListener _gestureListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auth_choose_login);
    LogController.LIFECYCLE_ACTIVITY.logMessage("OnCreate starting");

    // CANNOT do this in constructor / member list
    _app = (CoachellerApplication) getApplication();
    _app.registerChooseLoginActivity(ChooseLoginActivity.this);
    // _app.getAuthModel().checkAccounts();

    // Framework
    _loginStatusText = (TextView) this.findViewById(R.id.text_login_status);
    _accountNameText = (TextView) this.findViewById(R.id.text_account_name);
    _tokenIdText = (TextView) this.findViewById(R.id.text_token_id);

    findViewById(R.id.btn_login_twitter).setOnClickListener(this);
    findViewById(R.id.btn_login_google).setOnClickListener(this);

    // Need references to these buttons so they can disappear
    findViewById(R.id.btn_login_facebook).setOnClickListener(this);

    _buttonLoginFacebookWeb = (Button) findViewById(R.id.btn_login_facebook_browser);
    _buttonLoginFacebookWeb.setOnClickListener(this);

    _buttonInvalidateTokens = (Button) findViewById(R.id.btn_invalidate_tokens);
    _buttonInvalidateTokens.setOnClickListener(this);
    _buttonDismissActivity = (Button) findViewById(R.id.btn_dismiss_activity);
    _buttonDismissActivity.setOnClickListener(this);

    _gestureDetector = new GestureDetector(new MyGestureDetector());
    _gestureListener = new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return _gestureDetector.onTouchEvent(event);
      }
    };

    findViewById(android.R.id.content).setOnClickListener(ChooseLoginActivity.this);
    findViewById(android.R.id.content).setOnTouchListener(_gestureListener);

    // App
    LogController.LIFECYCLE_ACTIVITY.logMessage("OnCreate complete");

  }

  @Override
  protected void onResume() {
    super.onResume();

    CoachellerApplication app = (CoachellerApplication) getApplication();
    app.setLastAuthActivity(this);

    AuthProviderInt currentProvider = _app.getAuthModel().getCurrentAuthProvider();
    if (currentProvider != null) {
      currentProvider.extendAccess();
    }

    if (_firstStart) {
      LogController.LIFECYCLE_ACTIVITY.logMessage("ChooseLoginActivity First Launch, invalidating all logins");
      _app.getAuthModel().invalidateTokens();
      _firstStart = false;
    }

    _updateUI();
  }

  private void _returnToMainActivity() {
    Intent returnIntent = getIntent();
    int result;

    if (_app.getAuthModel().isLoggedInPrimary()) {
      result = RESULT_OK;
      returnIntent.putExtra(AuthConstants.INTENT_EXTRA_LOGIN_TYPE, _app.getAuthModel()
          .getCurrentAuthProviderType());

      returnIntent.putExtra(AuthConstants.INTENT_EXTRA_ACCOUNT_IDENTIFIER, _app.getAuthModel()
          .getCurrentAuthProvider().getVerifiedAccountIdentifier());

      returnIntent.putExtra(AuthConstants.INTENT_EXTRA_LOGIN_TOKEN, _app.getAuthModel()
          .getCurrentAuthProvider().getAuthToken());

    } else {
      result = RESULT_CANCELED;
    }

    setResult(result, returnIntent);
    finish();
  }

  private void _updateUI() {
    if (_debugMode) {
      _accountNameText.setVisibility(View.VISIBLE);
      _tokenIdText.setVisibility(View.VISIBLE);
      _buttonInvalidateTokens.setVisibility(View.VISIBLE);
      _buttonDismissActivity.setVisibility(View.VISIBLE);

      if (_app.getAuthModel().isLoggedInPrimary()) {
        AuthProviderInt currentProvider = _app.getAuthModel().getCurrentAuthProvider();
        _setLoginStatus(currentProvider.getAccountType());
        _accountNameText.setText("Account: " + currentProvider.getLocalAccountName() + "\r\n"
            + currentProvider.getAccountType() + " confirms you are the owner of "
            + currentProvider.getVerifiedAccountIdentifier());
        _tokenIdText.setText("Token ID: " + currentProvider.getAuthToken());
      } else {
        _setLoginStatus("Not Logged In");
        _accountNameText.setText("-");
        _tokenIdText.setText("-");
      }

      if (this.findViewById(R.layout.auth_choose_login) != null) {
        this.findViewById(R.layout.auth_choose_login).invalidate();
      }

    } else {
      _loginStatusText.setText("");
      // _loginStatusText.setVisibility(View.GONE);

      _accountNameText.setVisibility(View.GONE);
      _tokenIdText.setVisibility(View.GONE);
      _buttonInvalidateTokens.setVisibility(View.GONE);
      _buttonDismissActivity.setVisibility(View.GONE);
      if (_app.getAuthModel().isLoggedInPrimary()) {
        _returnToMainActivity();
      }

    }

  }

  private void _setLoginStatus(String status) {
    _loginStatusText.setText(AuthConstants.UI_STR_LOGIN_STATUS + " " + status);
  }

  // Default is For Facebook
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    String infoMessage = "ChooseLoginActivity.onActivityResult(requestCode=[" + requestCode
        + "], resultCode=[" + resultCode + "] with data: " + data;
    LogController.LIFECYCLE_ACTIVITY.logMessage(infoMessage);

    LogController.LIFECYCLE_ACTIVITY.logMessage("");

    switch (requestCode) {
    case AuthConstants.INTENT_TWITTER_LOGIN:
      if (resultCode == Activity.RESULT_OK) {
        _app.getAuthModel().twitterAuthCallback(requestCode, resultCode, data);
        break;
      }

    case AuthConstants.INTENT_FACEBOOK_LOGIN: {
      LogController.LIFECYCLE_ACTIVITY.logMessage("onActivityResult called with unknown values: " + requestCode + ","
          + resultCode);
      _app.getAuthModel().getFacebookObject().authorizeCallback(requestCode, resultCode, data);
      break;
    }
    default:
      _app.showErrorDialog("Unexpected Response",
          "An unexpected response was received from another window", infoMessage);

      break;
    }
  }

  @Override
  public void onClick(View arg0) {
    String buttonClickedName = this.getResources().getResourceEntryName(arg0.getId());
    System.out.println("Button Click: " + buttonClickedName);

    if (buttonClickedName.equals(this.getResources().getResourceEntryName(R.id.btn_login_google))) {
      _app.getAuthModel().primaryLogin(AuthConstants.LOGIN_TYPE_GOOGLE);
    }

    if (buttonClickedName.equals(this.getResources().getResourceEntryName(R.id.btn_login_twitter))) {
      _app.getAuthModel().primaryLogin(AuthConstants.LOGIN_TYPE_TWITTER);
    }
    if (buttonClickedName.equals(this.getResources().getResourceEntryName(R.id.btn_login_facebook))) {
      _app.getAuthModel().primaryLogin(AuthConstants.LOGIN_TYPE_FACEBOOK);
    }

    if (buttonClickedName.equals(this.getResources().getResourceEntryName(
        R.id.btn_login_facebook_browser))) {
      _app.getAuthModel().primaryLogin(AuthConstants.LOGIN_TYPE_FACEBOOK_BROWSER);
    }

    if (buttonClickedName.equals(this.getResources().getResourceEntryName(
        R.id.btn_invalidate_tokens))) {
      _app.getAuthModel().invalidateTokens();
      _updateUI();
    }

    if (buttonClickedName.equals(this.getResources()
        .getResourceEntryName(R.id.btn_dismiss_activity))) {
      _returnToMainActivity();
    }
  }

  class MyGestureDetector extends SimpleOnGestureListener {
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      try {
        if (_debugMode == true) {
          return true;
        }

        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
          return false;
        }

        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {// Left Swipe
          _loginStatusText.setText(_loginStatusText.getText() + "L");
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {// Right Swipe
          _loginStatusText.setText(_loginStatusText.getText() + "R");
        }

      } catch (Exception e) {
        // nothing
      }

      if (_loginStatusText.getText().equals("LRLR")) {
        _debugMode = true;
        _updateUI();
      }

      return true;
    }
  }

  @Override
  public synchronized void doTwitterPost() {
    LogController.OTHER.logMessage("WARNING: ChooseLoginActivity Empty interface method has been called");
  }

  @Override
  public synchronized void doFacebookPost() {
    LogController.OTHER.logMessage("WARNING: ChooseLoginActivity Empty interface method has been called");
  }

  @Override
  public void modelChanged() {
    // Run method in main UI thread
    View v = findViewById(android.R.id.content);
    v.post(new Runnable() {
      @Override
      public void run() {
        _updateUI();
      }
    });
  }

  @Override
  public Activity getLastActivity() {
    return this;
  }


}