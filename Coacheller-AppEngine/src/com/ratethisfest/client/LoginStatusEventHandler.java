package com.ratethisfest.client;

import com.google.gwt.event.shared.EventHandler;

public interface LoginStatusEventHandler extends EventHandler {
    void onLoginStatusChange(LoginStatusEvent event);
}
