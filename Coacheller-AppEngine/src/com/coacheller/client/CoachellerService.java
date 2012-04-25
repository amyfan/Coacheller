package com.coacheller.client;

import java.util.List;

import com.coacheller.shared.RatingGwt;
import com.coacheller.shared.Set;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface CoachellerService extends RemoteService {
  String greetServer(String name) throws IllegalArgumentException;

  String reloadSetData() throws IllegalArgumentException;

  String recalculateSetRatingAverages() throws IllegalArgumentException;

  List<String> getSetArtists(String day, String yearString) throws IllegalArgumentException;

  List<Set> getSets(String day, String yearString) throws IllegalArgumentException;

  String addRatingBySetArtist(String email, String setArtist, String year, String weekend,
      String score, String notes) throws IllegalArgumentException;

  List<RatingGwt> getRatingsByUserEmail(String email) throws IllegalArgumentException;

  String deleteRatingsByUser(String email) throws IllegalArgumentException;

  String deleteRating(Long ratingId) throws IllegalArgumentException;

  String emailRatingsToUser(String email) throws IllegalArgumentException;

}
