package com.ratethisfest.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greetLolla")
public interface LollapaloozerService extends RemoteService {
  String greetServer(String name) throws IllegalArgumentException;

  String insertSetData() throws IllegalArgumentException;

  String updateSetData() throws IllegalArgumentException;

  String recalculateSetRatingAverages() throws IllegalArgumentException;


  String addRating(Long setId, String weekend, String score, String notes);

  List<RatingGwt> getRatingsByUserEmail(String email, Integer year) throws IllegalArgumentException;

  String deleteRatingsByUser(String email) throws IllegalArgumentException;

  String deleteRating(Long ratingId) throws IllegalArgumentException;

  String deleteRatingsByYear(Integer year) throws IllegalArgumentException;

  String emailRatingsToUser(String email) throws IllegalArgumentException;

  List<RatingGwt> getAllRatings();

  List<RatingGwt> getRatingsForSet(Set targetSet);

  List<Set> getSets(FestivalEnum fest, String yearString, DayEnum day) throws IllegalArgumentException;


}
