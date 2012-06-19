package com.ratethisfest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FestivalIndexComposite extends Composite {

  interface Binder extends UiBinder<Widget, FestivalIndexComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  @UiField
  Label title;

  @UiField
  com.google.gwt.user.client.ui.Button coachellaButton;

  @UiField
  com.google.gwt.user.client.ui.Button lollaButton;

  public FestivalIndexComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  private void initUiElements() {
    title.setText("CHOOSE YOUR FEST");

    coachellaButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        FlowControl.go(new CoachellerViewComposite());
      }
    });

    lollaButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        FlowControl.go(new LollapaloozerViewComposite());
      }
    });
  }

  @Override
  public String getTitle() {
    return PageToken.FESTIVAL_INDEX.getValue();
  }

}