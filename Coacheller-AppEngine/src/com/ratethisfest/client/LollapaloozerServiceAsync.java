package com.ratethisfest.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

/**
 * The async counterpart of <code>CoachellerService</code>.
 */
public interface LollapaloozerServiceAsync {
  void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

  void insertSetData(AsyncCallback<String> callback) throws IllegalArgumentException;

  void updateSetData(AsyncCallback<String> callback) throws IllegalArgumentException;

  void recalculateSetRatingAverages(AsyncCallback<String> callback) throws IllegalArgumentException;
      

  void getSets(FestivalEnum fest, String yearString, DayEnum day, AsyncCallback<List<Set>> callback) throws IllegalArgumentException;

  void getRatingsByUserEmail(String email, Integer year, AsyncCallback<List<RatingGwt>> callback)
      throws IllegalArgumentException;

  void deleteRatingsByUser(String email, AsyncCallback<String> callback)
      throws IllegalArgumentException;

  void deleteRating(Long ratingId, AsyncCallback<String> callback) throws IllegalArgumentException;

  void deleteRatingsByYear(Integer year, AsyncCallback<String> callback) throws IllegalArgumentException;

  void emailRatingsToUser(String email, AsyncCallback<String> callback)
      throws IllegalArgumentException;

  void getAllRatings(AsyncCallback<List<RatingGwt>> callback);

  void getRatingsForSet(Set targetSet, AsyncCallback<List<RatingGwt>> callback);

  void addRating(Long setId, String weekend, String score, String notes, AsyncCallback<String> callback);

}
