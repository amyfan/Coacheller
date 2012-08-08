package com.ratethisfest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LollapaloozerLoginComposite extends Composite {

  interface Binder extends UiBinder<Widget, LollapaloozerLoginComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  @UiField
  Label title;

  @UiField
  Anchor facebookUrl;

  @UiField
  Anchor googleUrl;

  @UiField
  Anchor twitterUrl;

  public LollapaloozerLoginComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  private void initUiElements() {
    title.setText("CHOOSE LOGIN OPTION");
    StringBuilder urlStringBuilder = new StringBuilder();
    urlStringBuilder.append("https://www.facebook.com/dialog/oauth/?");
    urlStringBuilder.append("client_id=");
    urlStringBuilder.append("186287061500005");
    urlStringBuilder.append("&redirect_uri=");
    urlStringBuilder.append("http://www.lollapaloozer.com/#login_facebook");
    // to prevent cross site forgery
    // urlStringBuilder.append("&state=");
    // urlStringBuilder.append("YOUR_STATE_VALUE");
    // TODO
    // urlStringBuilder.append("&scope=");
    // urlStringBuilder.append("COMMA_SEPARATED_LIST_OF_PERMISSION_NAMES");
    facebookUrl.setHref(urlStringBuilder.toString());
    facebookUrl.setText("Log in with Facebook");

    urlStringBuilder = new StringBuilder();
    urlStringBuilder.append("https://accounts.google.com/o/oauth2/auth/?");
    urlStringBuilder.append("client_id=");
    urlStringBuilder.append("186287061500005");
    urlStringBuilder.append("&redirect_uri=");
    urlStringBuilder.append("http://www.lollapaloozer.com/#login_google");
    googleUrl.setHref(urlStringBuilder.toString());
    googleUrl.setText("Log in with Google");
  }

  @Override
  public String getTitle() {
    return PageToken.LOGIN.getValue();
  }

}