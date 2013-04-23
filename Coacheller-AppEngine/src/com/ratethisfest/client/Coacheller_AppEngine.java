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
  @Override
  public void onModuleLoad() {
    // AppUserWidget widget = new AppUserWidget();
    // RootPanel.get().add(widget);
    History.addValueChangeHandler(this);
    if (History.getToken().isEmpty()) {
      History.newItem(PageToken.INDEX.getValue());
    }
    Composite c = new FestivalIndexComposite();
    if (Window.Location.getHostName().contains("coacheller")) {
      Window.setTitle("Coacheller");
      c = new CoachellerViewComposite();
    } else if (Window.Location.getHostName().contains("lollapaloozer")) {
      Window.setTitle("Lollapaloozer");
      // TODO: need much more robust way to parse redirected URL's!
      if (Window.Location.getHref().contains("code=4")) {
        String code = Window.Location.getParameter("code");
        c = new LollapaloozerLoginGoogleComposite(code);
      } else if (Window.Location.getParameter("code") != null
          && !Window.Location.getParameter("code").equals("")) {
        String code = Window.Location.getParameter("code");
        c = new LollapaloozerLoginFacebookComposite(code);
      } else {
        c = new LollapaloozerViewComposite();
      }
    }
    // else if (Window.Location.getHostName().contains("127.0.0.1")) {
    // c = new LollapaloozerViewComposite();
    // }

    FlowControl.go(c);
  }

  @Override
  public void onValueChange(ValueChangeEvent<String> e) {
    FlowControl.go(History.getToken());
  }
}
