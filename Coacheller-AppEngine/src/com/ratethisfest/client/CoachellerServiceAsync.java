package com.ratethisfest.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

/**
 * The async counterpart of <code>CoachellerService</code>.
 */
public interface CoachellerServiceAsync {
  void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

  void updateSetData(AsyncCallback<String> callback) throws IllegalArgumentException;

  void recalculateSetRatingAverages(AsyncCallback<String> callback) throws IllegalArgumentException;

  void getSetArtists(String yearString, String day, AsyncCallback<List<String>> callback)
      throws IllegalArgumentException;

  void getSets(String yearString, String day, AsyncCallback<List<Set>> callback)
      throws IllegalArgumentException;

  void addRating(String email, String setArtist, String setTime, String day,
      String year, String weekend, String score, String notes, AsyncCallback<String> callback) throws IllegalArgumentException;

  void getRatingsByUserEmail(String email, AsyncCallback<List<RatingGwt>> callback)
      throws IllegalArgumentException;

  void deleteRatingsByUser(String email, AsyncCallback<String> callback)
      throws IllegalArgumentException;

  void deleteRating(Long ratingId, AsyncCallback<String> callback) throws IllegalArgumentException;

  void emailRatingsToUser(String email, AsyncCallback<String> callback) throws IllegalArgumentException;

}
