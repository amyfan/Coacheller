package com.ratethisfest.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Coacheller_AppEngine implements EntryPoint, ValueChangeHandler<String> {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    // AppUserWidget widget = new AppUserWidget();
    // RootPanel.get().add(widget);
    History.addValueChangeHandler(this);
    if (History.getToken().isEmpty()) {
      History.newItem(PageToken.INDEX.getValue());
    }
    Composite c = new FestivalIndexComposite();
    if (Window.Location.getHostName().contains("coacheller")) {
      c = new CoachellerViewComposite();
    } else if (Window.Location.getHostName().contains("lollapaloozer")) {
      c = new LollapaloozerViewComposite();
    }
    // else if (Window.Location.getHostName().contains("127.0.0.1")) {
    // c = new LollapaloozerViewComposite();
    // }

    FlowControl.go(c);
  }

  public void onValueChange(ValueChangeEvent<String> e) {
    FlowControl.go(History.getToken());
  }
}
