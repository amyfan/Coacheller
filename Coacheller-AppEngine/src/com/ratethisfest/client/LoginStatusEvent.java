package com.ratethisfest.client;

import java.util.HashMap;

import auth.logins.data.LoginStatus;

import com.google.gwt.event.shared.GwtEvent;



public class LoginStatusEvent extends GwtEvent<LoginStatusEventHandler> {

  public static Type<LoginStatusEventHandler> TYPE = new Type<LoginStatusEventHandler>();
  
  private final LoginStatus _loginStatus;
  
  public LoginStatusEvent(LoginStatus loginStatus) {
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
  
  public LoginStatus getLoginStatus() {
    return _loginStatus;
  }

}
