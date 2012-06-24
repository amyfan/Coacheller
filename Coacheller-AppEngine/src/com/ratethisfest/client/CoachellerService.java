package com.ratethisfest.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greetCoachella")
public interface CoachellerService extends RemoteService {
  String greetServer(String name) throws IllegalArgumentException;

  String updateSetData() throws IllegalArgumentException;

  String recalculateSetRatingAverages() throws IllegalArgumentException;

  List<Set> getSets(String day, String yearString) throws IllegalArgumentException;

  String addRating(String email, Long setId, String weekend, String score, String notes)
      throws IllegalArgumentException;

  List<RatingGwt> getRatingsByUserEmail(String email, Integer year) throws IllegalArgumentException;

  String deleteRatingsByUser(String email) throws IllegalArgumentException;

  String deleteRating(Long ratingId) throws IllegalArgumentException;

  String emailRatingsToUser(String email) throws IllegalArgumentException;

}
