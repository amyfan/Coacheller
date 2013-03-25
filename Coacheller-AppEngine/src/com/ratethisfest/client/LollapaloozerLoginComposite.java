package com.ratethisfest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ratethisfest.client.auth.FacebookAuthUtils;
import com.ratethisfest.client.auth.GoogleAuthUtils;
import com.ratethisfest.shared.AuthConstants;

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
    // Facebook
    facebookUrl.setHref(FacebookAuthUtils.getRequestUrl(AuthConstants.LOLLA_FACEBOOK_APP_ID,
        "http://www.lollapaloozer.com/"));
    facebookUrl.setText("Log in with Facebook");

    // Google
    googleUrl.setHref(GoogleAuthUtils.getRequestUrl(AuthConstants.LOLLA_GOOGLE_WEB_CLIENT_ID,
        "http://www.lollapaloozer.com/"));
    googleUrl.setText("Log in with Google");

    // Twitter
  }

  @Override
  public String getTitle() {
    return PageToken.LOGIN.getValue();
  }

}