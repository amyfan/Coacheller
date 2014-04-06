package com.ratethisfest.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.ratethisfest.client.ui.LoginControl;
import com.ratethisfest.client.ui.MainViewComposite;
import com.ratethisfest.client.ui.RTFTitleBanner;

/**
 * 
 * @author Amy
 * 
 */
public class FlowControl {
  private static FlowControl instance;
  private static Logger logger = Logger.getLogger(FlowControl.class.getName());

  private FlowControl() {
  }

  public static void go(Composite c) {
    logger.log(Level.SEVERE, "Navigating by Composite object method");
    if (instance == null) {
      instance = new FlowControl();
    }
    RootPanel.get().clear();
    // not sure why, but GWT throws an exception without this. Adding to CSS
    // doesn't work.
    RootPanel.get().getElement().getStyle().setPosition(Position.RELATIVE);
    // add, determine height/width, center, then move. height/width are unknown
    // until added to document. Catch-22!

    RootPanel.get().add(new RTFTitleBanner());

    if (c instanceof MainViewComposite) {
      RootPanel.get().add(new LoginControl());
    }

    RootPanel.get().add(c);
    if (c instanceof MainViewComposite) {
      ((MainViewComposite) c).chartShowLoading("Loading..."); // Must be done after chart is made visible by adding
    }

    History.newItem(c.getTitle());
  }

  public static void go(String tokenString) {
    logger.log(Level.SEVERE, "Navigating by URL change");
    PageToken token = getPageFromToken(tokenString);
    String param = getParamFromToken(tokenString);

    if (token == null) {
      go(new MainViewComposite());
    } else {
      String hostName = Window.Location.getHostName();
      if (hostName == null || (!hostName.contains("coacheller") && !hostName.contains("lollapaloozer"))) {
        // for now, localhost/ratethisfest will default to lollapaloozer
        hostName = "lollapaloozer";
      }

      if (PageToken.VIEW.equals(token)) {
        go(new MainViewComposite());
      } else if (PageToken.EMAIL.equals(token)) {
        go(new LollapaloozerEmailComposite());
      } else if (PageToken.LOGIN.equals(token)) {
        go(new LollapaloozerLoginComposite());
      } else if (PageToken.RATE.equals(token)) {
        logger.info("Unexpected:  URL Rate token processed");
      }
    } // End if email is provided

    // TODO: add retrievePoll to PollPhotoWidget
    // else if (PageToken.UPLOAD.equals(token)) {
    // go(new PollPhotoWidget());
    // }
  }

  private static PageToken getPageFromToken(String tokenString) {
    String[] tokens = tokenString.split("=");
    if (tokens.length > 0) {
      String token = tokens[0];
      return PageToken.fromValue(token);
    }
    return null;
  }

  private static String getParamFromToken(String tokenString) {
    String[] tokens = tokenString.split("=");
    if (tokens.length > 1) {
      return tokens[1];
    }
    return null;
  }
}
