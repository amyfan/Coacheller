package com.ratethisfest.client.ui;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mortbay.log.Log;

import auth.logins.ServletInterface;
import auth.logins.client.LoginStatusService;
import auth.logins.client.LoginStatusServiceAsync;
import auth.logins.other.LoginType;
import auth.logins.server.LoginStatusServiceImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ratethisfest.client.Coacheller_AppEngine;
import com.ratethisfest.client.LoginStatusEvent;
import com.ratethisfest.client.LoginStatusEventHandler;

public class LoginControl extends Composite implements HasText {
  
  private static LoginControlUiBinder uiBinder = GWT.create(LoginControlUiBinder.class);

  interface LoginControlUiBinder extends UiBinder<Widget, LoginControl> {
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

  public LoginControl() {
    initWidget(uiBinder.createAndBindUi(this));
    Coacheller_AppEngine.EVENT_BUS.addHandler(LoginStatusEvent.TYPE, new LoginStatusEventHandler() {

      @Override
      public void onLoginStatusChange(LoginStatusEvent event) {
        LoginControl.this.updateUI(event.getLoginStatus());
        
      }});
    customInitWidget();
  }

  private void customInitWidget() {
    labelLoading.setVisible(true);
    this.loggedInDisplay.setVisible(false);
    this.loginProviderOptions.setVisible(false);

    // Set up the callback object.
    AsyncCallback<HashMap<String, String>> callback = new AsyncCallback<HashMap<String, String>>() {
      public void onFailure(Throwable caught) {
        // TODO: Do something with errors.
        logger.log(Level.SEVERE, "Login Status Callback Exception Caught");
      }

      public void onSuccess(HashMap<String, String> result) {
        // Start reading login results
        logger.log(Level.SEVERE, "Login Status Service Success");


        //LoginControl.this.updateUI(result);
        LoginStatusEvent event = new LoginStatusEvent(result);
        Coacheller_AppEngine.EVENT_BUS.fireEvent(event);
      }
    };

    // Make the call to the stock price service.
    loginStatusSvc.getLoginInfo(callback);
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
    Window.Location.replace("/sessionsTest?RTFAction=" + ServletInterface.ACTION_FACEBOOK_AUTH_SCRIBE);
  }

  @UiHandler("linkGoogle")
  void onLinkGoogleClick(ClickEvent event) {
    Window.Location.replace("/sessionsTest?RTFAction=" + ServletInterface.ACTION_GOOGLE_AUTH);
  }

  @UiHandler("linkTwitter")
  void onLinkTwitterClick(ClickEvent event) {
    Window.Location.replace("/sessionsTest?RTFAction=" + ServletInterface.ACTION_TWITTER_AUTH);
  }

  @UiHandler("linkLogOut")
  void onLinkLogOutClick(ClickEvent event) {
    Window.Location.replace("/sessionsTest?RTFAction=" + ServletInterface.ACTION_LOGOUT);
  }

  @UiHandler("linkDestroyAccount")
  void onLinkDestroyAccountClick(ClickEvent event) {
    Window.Location.replace("/sessionsTest?RTFAction=" + ServletInterface.ACTION_DESTROY_ACCOUNT);
  }

  private void updateUI(HashMap<String, String> result) {
    labelLoading.setVisible(false);
    
    int currentRow = 0;
    currentRow++;
    for (String key : result.keySet()) {
      String value = result.get(key);
      logger.log(Level.SEVERE, currentRow + ": " + key + " = " + value);
      currentRow++;
    }

    // Change UI depending on whether user is logged in
    if (result.containsKey(LoginStatusServiceImpl.NOT_LOGGED_IN)) {
      loggedInDisplay.setVisible(false);  //Hide Greeting
      loginProviderOptions.setVisible(true);  //Show login options
      
    } else {
      
      String userName = result.get(LoginStatusServiceImpl.PROPERTY_PERSON_NAME);
      labelUsername.setText("Hello, " + userName + ". [");
      loggedInDisplay.setVisible(true);  //Show Greeting
      
      //Login Options display needs to be set up...
      boolean loggedInGoogle = result.containsKey(LoginType.GOOGLE.getName());
      boolean loggedInFacebook = result.containsKey(LoginType.FACEBOOK.getName());
      boolean loggedInTwitter = result.containsKey(LoginType.TWITTER.getName());

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
        linkTwitter.setVisible(true);
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

}
