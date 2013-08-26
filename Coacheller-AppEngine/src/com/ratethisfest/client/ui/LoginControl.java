package com.ratethisfest.client.ui;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import auth.logins.client.LoginStatusService;
import auth.logins.client.LoginStatusServiceAsync;

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
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LoginControl extends Composite implements HasText {

  private static LoginControlUiBinder uiBinder = GWT.create(LoginControlUiBinder.class);

  interface LoginControlUiBinder extends UiBinder<Widget, LoginControl> {
  }

  @UiField
  InlineHyperlink linkGoogle;
  @UiField
  HorizontalPanel loggedInDisplay;
  @UiField
  HorizontalPanel loggedOutDisplay;
  @UiField
  InlineHyperlink linkFacebook;
  @UiField
  InlineHyperlink linkTwitter;
  @UiField
  InlineHyperlink linkLogOut;
  @UiField Label labelUpdating;

  private LoginStatusServiceAsync loginStatusSvc = GWT.create(LoginStatusService.class);
  private Logger logger = Logger.getLogger(this.getClass().getName());

  public LoginControl() {
    initWidget(uiBinder.createAndBindUi(this));
    customInitWidget();
  }

  public LoginControl(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
    customInitWidget();
    // button.setText(firstName);
  }

  private void customInitWidget() {
    this.loggedInDisplay.setVisible(false);
    this.loggedOutDisplay.setVisible(true);


    // Set up the callback object.
    AsyncCallback<HashMap<String, String>> callback = new AsyncCallback<HashMap<String, String>>() {
      public void onFailure(Throwable caught) {
        // TODO: Do something with errors.
        logger.log(Level.SEVERE, "Login Status Callback Exception Caught");
      }

      public void onSuccess(HashMap<String, String> result) {
        // TODO Do something with result
        logger.log(Level.SEVERE, "Login Status Service Success");

        int currentRow = 0;
        // loginStatusTable.setText(currentRow, 0, "KEY");
        // loginStatusTable.setText(currentRow, 1, "VALUE");
        currentRow++;

        for (String key : result.keySet()) {
          String value = result.get(key);
          logger.log(Level.SEVERE, currentRow + ": " + key + " = " + value);
          currentRow++;
        }
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
    Window.Location.replace("/sessionsTest?RTFAction=UserReqFacebookAuthScribe");
  }
  @UiHandler("linkGoogle")
  void onLinkGoogleClick(ClickEvent event) {
    Window.Location.replace("/sessionsTest?RTFAction=UserReqGoogleAuth");
  }
  @UiHandler("linkTwitter")
  void onLinkTwitterClick(ClickEvent event) {
    Window.Location.replace("/sessionsTest?RTFAction=UserReqTwitterAuth");
  }
}
