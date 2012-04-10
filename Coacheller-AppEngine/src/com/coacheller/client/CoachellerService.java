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

  String loadSetData() throws IllegalArgumentException;

  String recalculateSetRatingAverages() throws IllegalArgumentException;

  List<String> getSetArtists(String day, String yearString) throws IllegalArgumentException;

  List<Set> getSets(String day, String yearString) throws IllegalArgumentException;

  String addRatingBySetArtist(String email, String setArtist, String year, String weekend,
      String score) throws IllegalArgumentException;

  List<RatingGwt> getRatingsBySetArtist(String email, String setArtist)
      throws IllegalArgumentException;

  List<RatingGwt> getRatingsBySet(String email, String setIdString) throws IllegalArgumentException;

  String deleteAllRatings() throws IllegalArgumentException;

  String deleteAllUsers() throws IllegalArgumentException;
}
