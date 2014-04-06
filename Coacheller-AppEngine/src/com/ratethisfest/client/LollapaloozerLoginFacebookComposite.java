package com.ratethisfest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ratethisfest.client.auth.FacebookAuthUtils;
import com.ratethisfest.client.ui.MainViewComposite;
import com.ratethisfest.shared.AuthConstants;

public class LollapaloozerLoginFacebookComposite extends Composite {

  interface Binder extends UiBinder<Widget, LollapaloozerLoginFacebookComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  private final LollapaloozerServiceAsync lollapaloozerService = GWT
      .create(LollapaloozerService.class);
  private String token = null;

  @UiField
  Label title;

  public LollapaloozerLoginFacebookComposite(String code) {
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
      title.setText("Logging you in via Facebook...");
    } else {
      title.setText("Invalid login, redirecting back to home page...");
    }
  }

  private void goHome() {
    FlowControl.go(new MainViewComposite());
  }

  private void getAccessToken(String code) {
    String requestUrl = FacebookAuthUtils.buildTokenRequestUrl(code,
        AuthConstants.LOLLA_FACEBOOK_APP_ID, AuthConstants.LOLLA_FACEBOOK_APP_SECRET,
        "http://www.lollapaloozer.com/");

    // Window.Location.assign(requestUrl);

    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(requestUrl));
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
            String token = parseToken(response.getText());
            getData(token);
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

  private String parseToken(String returnText) {
    String token = returnText.replace("access_token=", "");
    token = token.substring(0, token.indexOf("&"));
    return token;
  }

  private void getData(String token) {
    String requestUrl = FacebookAuthUtils.buildGraphApiRequestUrl(token);

    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(requestUrl));
    try {
      Request request = builder.sendRequest(null, new RequestCallback() {
        @Override
        public void onError(Request request, Throwable exception) {
          // Couldn't connect to server (could be timeout, SOP violation, etc.)
          title.setText("getData: Couldn't connect to server");
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
          if (200 == response.getStatusCode()) {
            title.setText(response.getText());
            // TODO: process user info, store session, & redirect back to
            // another page (perhaps home page, with authenticated user token)
            JSONValue jsonValue = JSONParser.parseLenient(response.getText());
            JSONObject obj = jsonValue.isObject();
            JSONValue jsonValue2 = null;
            JSONString name = null;
            JSONString email = null;
            JSONString id = null;
            if ((jsonValue2 = obj.get("name")) != null) {
              // if ((name = jsonValue2.isString()) != null) {
              // title.setText("name: " + name.stringValue());
              // }
              name = jsonValue2.isString();
            }
            if ((jsonValue2 = obj.get("email")) != null) {
              email = jsonValue2.isString();
            }
            if ((jsonValue2 = obj.get("id")) != null) {
              id = jsonValue2.isString();
            }
          } else {
            title.setText(response.getStatusText());
          }
        }
      });
    } catch (RequestException e) {
      // Couldn't connect to server
      title.setText("getData: RequestException" + e.getMessage());
    }
  }

  @Override
  public String getTitle() {
    return PageToken.LOGIN_FACEBOOK.getValue();
  }

}