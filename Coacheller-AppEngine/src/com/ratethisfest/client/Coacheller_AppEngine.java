package com.ratethisfest.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import auth.logins.client.LoginStatusService;
import auth.logins.client.LoginStatusServiceAsync;
import auth.logins.data.LoginStatus;
import auth.logins.test.sessionsTestServlet;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.ratethisfest.client.ui.MainViewComposite;
import com.ratethisfest.client.ui.RateDialogBox;
import com.ratethisfest.data.FestivalEnum;

public class Coacheller_AppEngine implements EntryPoint, ValueChangeHandler<String> {

  private LoginStatusServiceAsync loginStatusSvc = GWT.create(LoginStatusService.class);
  private Logger logger = Logger.getLogger(this.getClass().getName());
  public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
  private static LoginStatus LOGIN_STATUS = LoginStatus.notLoggedIn(); // Not logged in yet or we just dont know...

  // This is the entry point method.
  @Override
  public void onModuleLoad() {
    // logger.log(Level.SEVERE, "Coacheller_AppEngine Entry Point"); //Causes a box to pop up in some cases...

    // DeferredCommand.addCommand( //Deprecated
    // This change made because otherwise exceptions thrown in onModuleLoad cannot be seen
    Scheduler.get().scheduleDeferred(new Command() {
      public void execute() {
        onModuleLoad2();
      }
    });
  }

  public synchronized static LoginStatus getLoginStatus() {
    return LOGIN_STATUS;
  }

  public synchronized static void setLoginStatus(LoginStatus loginStatus) {
    LOGIN_STATUS = loginStatus;
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
    Composite c;

    FestivalEnum fest = getFestFromSiteName(); // Get fest based on hostname
    if (fest == null) {
      logger.log(Level.SEVERE, "fest obtained is null");
      Window.setTitle("RateThisFest");
      c = new FestivalIndexComposite(); // Appears to be overridden by later code

    } else {
      // logger.log(Level.SEVERE, "fest obtained is non-null: "+ fest.getName());
      Window.setTitle(fest.getRTFAppName()); // Set window title to RTF App Name
      c = new MainViewComposite();

    }

    // if (Window.Location.getHref().contains("code=4")) {
    // String code = Window.Location.getParameter("code");
    // c = new LollapaloozerLoginGoogleComposite(code);
    // } else if (Window.Location.getParameter("code") != null && !Window.Location.getParameter("code").equals("")) {
    // String code = Window.Location.getParameter("code");
    // c = new LollapaloozerLoginFacebookComposite(code);

    if (Window.Location.getHostName().contains("127.0.0.1")) {
      // Do something else for debugging?
    }

    // /CHECK IF THIS IS BEING EXECUTED AT ALL

    logger.log(Level.SEVERE, "Coacheller_AppEngine About to start navigation with FlowControl.go");
    FlowControl.go(c);

    // If we navigate after showing dialog, dialog is hidden
    String conflictedApAccountType = Window.Location.getParameter(sessionsTestServlet.ACCOUNT_OWNERSHIP_CONFLICT);
    if (null != conflictedApAccountType) {
      logger.info("Detected account ownership conflict");
      RateDialogBox rateDialog = new RateDialogBox();
      String targetAccountDescription = Window.Location.getParameter(sessionsTestServlet.DESCRIPTION);
      rateDialog.setTitle("Account Ownership Conflict"); // Supposed to set tooltip ?
      rateDialog.setText("Another RateThisFest user registered this " + conflictedApAccountType + " account."); // Supposed
                                                                                                                // to
                                                                                                                // set
                                                                                                                // title
                                                                                                                // ?
      String messageString = "The "
          + conflictedApAccountType
          + " account ["
          + targetAccountDescription
          + "] has already been registered by another RateThisFest user.  Please log out first, or contact RateThisFest staff if you are certain you own this "
          + conflictedApAccountType + " account.";
      rateDialog.setMessage(messageString);
      rateDialog.show();
      // accountOwnershipConflict //provider name
    }

  }

  @Override
  public void onValueChange(ValueChangeEvent<String> e) {
    FlowControl.go(History.getToken());
  }

  public static FestivalEnum getFestFromSiteName() {
    String hostName = Window.Location.getHostName();
    return FestivalEnum.fromHostname(hostName);
  }

}
