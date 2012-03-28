package com.coacheller.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
    void getSets(String email, String day, String yearString, AsyncCallback<String> callback)
        throws IllegalArgumentException;
    void getRatingsBySet(String email, String setIdString, AsyncCallback<String> callback)
        throws IllegalArgumentException;
}
