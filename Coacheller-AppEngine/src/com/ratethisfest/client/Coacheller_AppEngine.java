package com.ratethisfest.client;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mortbay.log.Log;

import auth.logins.client.LoginStatusService;
import auth.logins.client.LoginStatusServiceAsync;

import com.google.appengine.api.datastore.Entity;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.ratethisfest.client.ui.LollapaloozerViewComposite;
import com.ratethisfest.shared.FestivalEnum;

public class Coacheller_AppEngine implements EntryPoint, ValueChangeHandler<String>  {

  private LoginStatusServiceAsync loginStatusSvc = GWT.create(LoginStatusService.class);
  private Logger logger = Logger.getLogger(this.getClass().getName());
  public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);


  // This is the entry point method.
  @Override
  public void onModuleLoad() {
    logger.log(Level.SEVERE, "Coacheller_AppEngine Entry Point");

    // DeferredCommand.addCommand( //Deprecated
    // This change made because otherwise exceptions thrown in onModuleLoad cannot be seen
    Scheduler.get().scheduleDeferred(new Command() {
      public void execute() {
        onModuleLoad2();
      }
    });
  }

  // Makes exceptions visible
  private void onModuleLoad2() {
    // AppUserWidget widget = new AppUserWidget();
    // RootPanel.get().add(widget);
    History.addValueChangeHandler(this);
    if (History.getToken().isEmpty()) {
      History.newItem(PageToken.INDEX.getValue());
    }

    logger.log(Level.SEVERE, "Updated browser history object");
    
    Composite c = new FestivalIndexComposite(); // Appears to be overridden by later code

    
    String hostName = Window.Location.getHostName();
    logger.log(Level.SEVERE, "Got host name");
    
    FestivalEnum fest = FestivalEnum.fromHostname(hostName); // Get fest based on hostname
    if (fest == null) {
      logger.log(Level.SEVERE, "fest obtained is null");
      Window.setTitle("RateThisFest");
    } else {
      logger.log(Level.SEVERE, "fest obtained is non-null: "+ fest.getName());
      Window.setTitle(fest.getRTFAppName()); // Set window title to RTF App Name
    }

    
    if (FestivalEnum.COACHELLA.equals(fest)) {
      c = new CoachellerViewComposite();
    } else if (FestivalEnum.LOLLAPALOOZA.equals(fest)) {
      // TODO: need much more robust way to parse redirected URL's!
      if (Window.Location.getHref().contains("code=4")) {
        String code = Window.Location.getParameter("code");
        c = new LollapaloozerLoginGoogleComposite(code);
      } else if (Window.Location.getParameter("code") != null && !Window.Location.getParameter("code").equals("")) {
        String code = Window.Location.getParameter("code");
        c = new LollapaloozerLoginFacebookComposite(code);
      } else {
        c = new LollapaloozerViewComposite();
      }
    } else if (Window.Location.getHostName().contains("127.0.0.1")) {
      c = new LollapaloozerViewComposite();
    }

    logger.log(Level.SEVERE, "About to use FlowControl.go");
    FlowControl.go(c);
  }

  @Override
  public void onValueChange(ValueChangeEvent<String> e) {
    FlowControl.go(History.getToken());
  }


  
}
