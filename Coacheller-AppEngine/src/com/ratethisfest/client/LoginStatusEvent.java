package com.ratethisfest.client;

import java.util.HashMap;

import com.google.gwt.event.shared.GwtEvent;



public class LoginStatusEvent extends GwtEvent<LoginStatusEventHandler> {

  public static Type<LoginStatusEventHandler> TYPE = new Type<LoginStatusEventHandler>();
  
  private final HashMap<String, String> _loginStatus;
  
  public LoginStatusEvent(HashMap loginStatus) {
    _loginStatus = loginStatus;
  }
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<LoginStatusEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(LoginStatusEventHandler handler) {
    handler.onLoginStatusChange(this);
  }
  
  public HashMap<String, String> getLoginStatus() {
    return _loginStatus;
  }

}
