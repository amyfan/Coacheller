package com.coacheller.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CoachellerService</code>.
 */
public interface CoachellerServiceAsync {
  void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

  void loadSetData(AsyncCallback<String> callback) throws IllegalArgumentException;

  void calculateSetRatingAverages(AsyncCallback<String> callback) throws IllegalArgumentException;

  void getSets(String email, String yearString, String day, AsyncCallback<String> callback)
      throws IllegalArgumentException;

  void addRatingBySetArtist(String email, String setArtist, String weekend, String score,
      AsyncCallback<String> callback) throws IllegalArgumentException;

  void getRatingsBySetArtist(String email, String setArtist, AsyncCallback<String> callback)
      throws IllegalArgumentException;

  void getRatingsBySet(String email, String setIdString, AsyncCallback<String> callback)
      throws IllegalArgumentException;

  void deleteAllRatings(AsyncCallback<String> callback) throws IllegalArgumentException;

  void deleteAllUsers(AsyncCallback<String> callback) throws IllegalArgumentException;

}
