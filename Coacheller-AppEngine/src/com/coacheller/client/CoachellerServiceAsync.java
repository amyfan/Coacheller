package com.coacheller.client;

import java.util.List;

import com.coacheller.shared.RatingGwt;
import com.coacheller.shared.Set;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CoachellerService</code>.
 */
public interface CoachellerServiceAsync {
  void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

  void reloadSetData(AsyncCallback<String> callback) throws IllegalArgumentException;

  void recalculateSetRatingAverages(AsyncCallback<String> callback) throws IllegalArgumentException;

  void getSetArtists(String yearString, String day, AsyncCallback<List<String>> callback)
      throws IllegalArgumentException;

  void getSets(String yearString, String day, AsyncCallback<List<Set>> callback)
      throws IllegalArgumentException;

  void addRatingBySetArtist(String email, String setArtist, String year, String weekend,
      String score, String notes, AsyncCallback<String> callback) throws IllegalArgumentException;

  void getRatingsByUserEmail(String email, AsyncCallback<List<RatingGwt>> callback)
      throws IllegalArgumentException;

  void deleteRatingsByUser(String email, AsyncCallback<String> callback)
      throws IllegalArgumentException;

  void deleteRating(Long ratingId, AsyncCallback<String> callback) throws IllegalArgumentException;

}
