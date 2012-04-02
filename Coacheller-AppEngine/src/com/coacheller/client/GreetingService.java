package com.coacheller.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
  String greetServer(String name) throws IllegalArgumentException;
  String loadSetData() throws IllegalArgumentException;
  String calculateSetRatingAverages() throws IllegalArgumentException;
  String getSets(String email, String day, String yearString) throws IllegalArgumentException;
  String addRatingBySetArtist(String email, String setArtist, String weekend, String score) throws IllegalArgumentException;
  String getRatingsBySetArtist(String email, String setArtist) throws IllegalArgumentException;
  String getRatingsBySet(String email, String setIdString) throws IllegalArgumentException;
  String deleteAllRatings() throws IllegalArgumentException;
  String deleteAllUsers() throws IllegalArgumentException;
}
