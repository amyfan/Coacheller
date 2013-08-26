package com.ratethisfest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ratethisfest.client.auth.GoogleAuthUtils;
import com.ratethisfest.client.ui.LollapaloozerViewComposite;
import com.ratethisfest.shared.AuthConstants;

public class LollapaloozerLoginGoogleComposite extends Composite {

  interface Binder extends UiBinder<Widget, LollapaloozerLoginGoogleComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  @UiField
  Label title;

  public LollapaloozerLoginGoogleComposite(String code) {
    initWidget(uiBinder.createAndBindUi(this));

    if (code == null || code.equals("")) {
      initUiElements(false);
      goHome();
    } else {
      initUiElements(true);
      getAccessToken(code);
    }

  }

  private void initUiElements(boolean isValid) {
    if (isValid) {
      title.setText("Logging you in via Google...");
    } else {
      title.setText("Invalid login, redirecting back to home page...");
    }
  }

  private void goHome() {
    FlowControl.go(new LollapaloozerViewComposite());
  }

  private void getAccessToken(String code) {
    String requestUrl = GoogleAuthUtils.buildTokenRequestUrl(code,
        AuthConstants.LOLLA_GOOGLE_WEB_CLIENT_ID, AuthConstants.LOLLA_GOOGLE_WEB_CLIENT_SECRET,
        "http://www.lollapaloozer.com/");

    // Window.Location.assign(requestUrl);

    RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(requestUrl));

    try {
      Request request = builder.sendRequest(null, new RequestCallback() {
        @Override
        public void onError(Request request, Throwable exception) {
          // Couldn't connect to server (could be timeout, SOP violation, etc.)
          title.setText("Couldn't connect to server");
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
          if (200 == response.getStatusCode()) {
            // TODO: parse access token
            title.setText(response.getText());
          } else {
            title.setText(response.getStatusText());
          }
        }
      });
    } catch (RequestException e) {
      // Couldn't connect to server
      title.setText("RequestException" + e.getMessage());
    }
  }

  @Override
  public String getTitle() {
    return PageToken.LOGIN_GOOGLE.getValue();
  }

}