package com.ratethisfest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.ratethisfest.shared.FieldVerifier;

public class LollapaloozerEmailComposite extends Composite {

  interface Binder extends UiBinder<Widget, LollapaloozerEmailComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  @UiField
  Label title;

  @UiField
  Label infoBox;

  @UiField
  Label emailLabel;

  @UiField
  TextBox userEmailAddressInput;

  @UiField
  com.google.gwt.user.client.ui.Button submitButton;

  @UiField
  com.google.gwt.user.client.ui.Button backButton;

  public LollapaloozerEmailComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  private void initUiElements() {
    title.setText("LOLLAPALOOZER 2012");
    emailLabel.setText("Email to track your ratings (will NOT be shared or misused)");

    userEmailAddressInput.getElement().setPropertyString("placeholder", "Enter email address here");

    userEmailAddressInput.addKeyPressHandler(new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        if (((int) event.getCharCode()) == 13) {
          submitEmail();
        }
      }
    });

    submitButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        submitEmail();
      }
    });

    backButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        FlowControl.go(new LollapaloozerViewComposite());
      }
    });
  }

  private void submitEmail() {
    String email = userEmailAddressInput.getText();

    if (!FieldVerifier.isValidEmail(email)) {
      infoBox.setText(FieldVerifier.EMAIL_ERROR);
    } else {
      FlowControl.go(new LollapaloozerRateComposite(email));
    }
  }

  @Override
  public String getTitle() {
    return PageToken.EMAIL_LOLLA.getValue();
  }

}