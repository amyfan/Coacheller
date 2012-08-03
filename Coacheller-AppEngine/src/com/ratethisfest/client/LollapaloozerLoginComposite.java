package com.ratethisfest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
  com.google.gwt.user.client.ui.Button googleButton;

  @UiField
  com.google.gwt.user.client.ui.Button facebookButton;

  @UiField
  com.google.gwt.user.client.ui.Button twitterButton;

  public LollapaloozerLoginComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  private void initUiElements() {
    title.setText("CHOOSE LOGIN OPTION");

  }

  @Override
  public String getTitle() {
    return PageToken.LOGIN.getValue();
  }

}