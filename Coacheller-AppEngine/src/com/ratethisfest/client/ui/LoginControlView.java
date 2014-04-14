package com.ratethisfest.client.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import auth.logins.ServletInterface;
import auth.logins.client.LoginStatusService;
import auth.logins.client.LoginStatusServiceAsync;
import auth.logins.data.LoginStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ratethisfest.client.ClientResources;
import com.ratethisfest.client.Coacheller_AppEngine;
import com.ratethisfest.client.LoginStatusEvent;
import com.ratethisfest.client.LoginStatusEventHandler;
import com.ratethisfest.shared.LoginType;

public class LoginControlView extends Composite implements HasText {

  private static LoginControlUiBinder uiBinder = GWT.create(LoginControlUiBinder.class);

  interface LoginControlUiBinder extends UiBinder<Widget, LoginControlView> {
  }

  @UiField
  InlineHyperlink linkGoogle;
  @UiField
  HorizontalPanel loggedInDisplay;
  @UiField
  HorizontalPanel loginProviderOptions;
  @UiField
  InlineHyperlink linkFacebook;
  @UiField
  InlineHyperlink linkTwitter;
  @UiField
  InlineHyperlink linkLogOut;
  @UiField
  Label labelLoading;
  @UiField
  InlineLabel labelUsername;
  @UiField
  InlineLabel labelDecorationGoogleAndFacebook;
  @UiField
  InlineLabel labelDecorationFacebookAndTwitter;
  @UiField
  InlineLabel labelDebug;
  @UiField
  InlineHyperlink linkDestroyAccount;

  private LoginStatusServiceAsync loginStatusSvc = GWT.create(LoginStatusService.class);
  private Logger logger = Logger.getLogger(this.getClass().getName());
  public static final String SERVLET_PATH = "/sessionsTest";

  public LoginControlView() {
    initWidget(uiBinder.createAndBindUi(this));
    labelDebug.setVisible(false);
    linkDestroyAccount.setVisible(false);
    Coacheller_AppEngine.EVENT_BUS.addHandler(LoginStatusEvent.TYPE, new LoginStatusEventHandler() {

      @Override
      public void onLoginStatusChange(LoginStatusEvent event) {
        // LoginControlView.this.updateUI(event.getLoginStatus());
        LoginControlView.this.updateUI(Coacheller_AppEngine.getLoginStatus());
      }
    });
    customInitWidget();
  }

  private void customInitWidget() {
    labelLoading.setVisible(true);
    this.loggedInDisplay.setVisible(false);
    this.loginProviderOptions.setVisible(false);

    // Set up the callback object.
    AsyncCallback<LoginStatus> callback = new AsyncCallback<LoginStatus>() {
      public void onFailure(Throwable caught) {
        // TODO: Do something with errors.
        logger.log(Level.SEVERE, "Login Status Callback Exception Caught");
      }

      public void onSuccess(LoginStatus result) {
        // Start reading login results
        logger.log(Level.SEVERE, "Login Status Service Success");

        // LoginControlView.this.updateUI(result);
        LoginStatusEvent event = new LoginStatusEvent(result);
        Coacheller_AppEngine.setLoginStatus(result); // Could probably just have coacheller_appengine respond to the
                                                     // event
        Coacheller_AppEngine.EVENT_BUS.fireEvent(event);
      }
    };

    // Make the call to the stock price service.
    loginStatusSvc.getLoginInfo(callback);

    final ClientResources clientResources = GWT.create(ClientResources.class);

    Image googleImage = new Image(clientResources.signin_google());
    linkGoogle.getElement().appendChild(googleImage.getElement());

    Image facebookImage = new Image(clientResources.signin_facebook());
    linkFacebook.getElement().appendChild(facebookImage.getElement());

    Image twitterImage = new Image(clientResources.signin_twitter());
    linkTwitter.getElement().appendChild(twitterImage.getElement());

    // TODO: temporarily hiding until getting twitter login to work again
    linkTwitter.setVisible(false);
  }

  @Override
  public void setText(String text) {
    // button.setText(text);
  }

  @Override
  public String getText() {
    // return button.getText();
    return "";
  }

  @UiHandler("linkFacebook")
  void onLinkFacebookClick(ClickEvent event) {
    Window.Location.replace(LoginControlView.getUrlLoginFacebook());
  }

  @UiHandler("linkGoogle")
  void onLinkGoogleClick(ClickEvent event) {
    Window.Location.replace(LoginControlView.getUrlLoginGoogle());
  }

  @UiHandler("linkTwitter")
  void onLinkTwitterClick(ClickEvent event) {
    Window.Location.replace(LoginControlView.getUrlLoginTwitter());
  }

  @UiHandler("linkLogOut")
  void onLinkLogOutClick(ClickEvent event) {
    Window.Location.replace(LoginControlView.getUrlLogout());
  }

  @UiHandler("linkDestroyAccount")
  void onLinkDestroyAccountClick(ClickEvent event) {
    Window.Location.replace(LoginControlView.getUrlDestroyAccount());
  }

  private void updateUI(LoginStatus loginStatus) {
    labelLoading.setVisible(false);

    int currentRow = 0;
    currentRow++;

    // Change UI depending on whether user is logged in
    if (!loginStatus.isLoggedIn()) {
      loggedInDisplay.setVisible(false); // Hide Greeting
      loginProviderOptions.setVisible(true); // Show login options

    } else {

      String userName = loginStatus.getProperty(LoginStatus.PROPERTY_PERSON_NAME);
      labelUsername.setText("Hello, " + userName + ". [");
      loggedInDisplay.setVisible(true); // Show Greeting

      // Login Options display needs to be set up...
      boolean loggedInGoogle = loginStatus.isLoggedIn(LoginType.GOOGLE);
      boolean loggedInFacebook = loginStatus.isLoggedIn(LoginType.FACEBOOK);
      boolean loggedInTwitter = loginStatus.isLoggedIn(LoginType.TWITTER);

      if (loggedInGoogle) {
        linkGoogle.setVisible(false);
      } else {
        linkGoogle.setVisible(true);
      }
      if (loggedInFacebook) {
        linkFacebook.setVisible(false);
      } else {
        linkFacebook.setVisible(true);
      }
      if (loggedInTwitter) {
        linkTwitter.setVisible(false);
      } else {
        // TODO: hiding the link until we get auth working again
        linkTwitter.setVisible(false);
        // linkTwitter.setVisible(true);
      }

      // labelDecorationGoogleAndFacebook.setVisible(true);
      // labelDecorationFacebookAndTwitter.setVisible(true);

      if (!loggedInGoogle && !loggedInFacebook) {
        labelDecorationGoogleAndFacebook.setVisible(true);
      } else {
        labelDecorationGoogleAndFacebook.setVisible(false);
      }

      if (!loggedInFacebook && !loggedInTwitter) {
        labelDecorationFacebookAndTwitter.setVisible(true);
      } else {
        labelDecorationFacebookAndTwitter.setVisible(false);
      }

      if (!loggedInGoogle && loggedInFacebook && !loggedInTwitter) {
        labelDecorationGoogleAndFacebook.setVisible(true);
        labelDecorationFacebookAndTwitter.setVisible(false);
      }

      if (loggedInGoogle && loggedInFacebook && loggedInTwitter) {
        // Dont even show it if everything is logged in
        loginProviderOptions.setVisible(false);
      } else {
        loginProviderOptions.setVisible(true);
      }

    }
  }

  public static String getUrlLoginGoogle() {
    return SERVLET_PATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_GOOGLE_AUTH;
  }

  // In this class mainly due to GWT compilation issues
  public static String getUrlLoginFacebook() {
    return SERVLET_PATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "="
        + ServletInterface.ACTION_FACEBOOK_AUTH_SCRIBE;
  }

  public static String getUrlLoginTwitter() {
    return SERVLET_PATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_TWITTER_AUTH;
  }

  public static String getUrlLogout() {
    return SERVLET_PATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_LOGOUT;
  }

  public static String getUrlDestroyAccount() {
    return SERVLET_PATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_DESTROY_ACCOUNT;
  }

}
