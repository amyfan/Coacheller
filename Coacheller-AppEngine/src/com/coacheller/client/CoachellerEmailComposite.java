package com.coacheller.client;

import com.coacheller.shared.FieldVerifier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CoachellerEmailComposite extends Composite {

  interface Binder extends UiBinder<Widget, CoachellerEmailComposite> {
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

  public CoachellerEmailComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  private void initUiElements() {
    title.setText("Coachella Set Rater");
    emailLabel.setText("Email (as ID)");

    userEmailAddressInput.getElement().setPropertyString("placeholder", "Enter email address here");

    submitButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String email = userEmailAddressInput.getText();

        if (!FieldVerifier.isValidEmail(email)) {
          infoBox.setText(FieldVerifier.EMAIL_ERROR);
        } else {
          FlowControl.go(new CoachellerRateComposite(email));
        }
      }
    });

    backButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        FlowControl.go(new CoachellerViewComposite());
      }
    });
  }

  @Override
  public String getTitle() {
    return PageToken.EMAIL.getValue();
  }

}