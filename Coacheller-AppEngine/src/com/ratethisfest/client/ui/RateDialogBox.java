package com.ratethisfest.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class RateDialogBox extends DialogBox {
  private static final Binder binder = GWT.create(Binder.class);
  @UiField
  Button button;

  interface Binder extends UiBinder<Widget, RateDialogBox> {
  }

  public RateDialogBox() {
    setWidget(binder.createAndBindUi(this));
    setAutoHideEnabled(true);
    // setText("My Title");
    setGlassEnabled(true);
    center();
  }

  @UiHandler("button")
  void onButtonClick(ClickEvent event) {
    hide();
  }
}