package com.coacheller.client;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Amy
 * 
 */
public class FlowControl {
  private static FlowControl instance;

  private FlowControl() {
  }

  public static void go(Composite c) {
    // not sure why we need this yet since everything is static.
    if (instance == null) {
      instance = new FlowControl();
    }
    RootPanel.get().clear();
    // not sure why, but GWT throws an exception without this. Adding to CSS
    // doesn't work.
    RootPanel.get().getElement().getStyle().setPosition(Position.RELATIVE);
    // add, determine height/width, center, then move. height/width are unknown
    // until added to document. Catch-22!
    RootPanel.get().add(c);
    History.newItem(c.getTitle());
  }

  public static void go(String tokenString) {
    PageToken token = getPageFromToken(tokenString);
    String param = getParamFromToken(tokenString);
    if (token == null) {
      go(new CoachellerListComposite());
    } else if (PageToken.LIST.equals(token)) {
      go(new CoachellerListComposite());
    } else if (PageToken.CHART.equals(token)) {
      go(new CoachellerChartComposite());
    }
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
