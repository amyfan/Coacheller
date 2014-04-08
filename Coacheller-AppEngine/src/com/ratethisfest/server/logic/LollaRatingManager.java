package com.ratethisfest.server.logic;


/**
 * Contains logic related to all requests made by the app client
 * 
 * @author Amy
 * 
 */
public class LollaRatingManager {

  public LollaRatingManager() {
  }

  /**
   * TODO: about to be deprecated once gwt login auth implemented
   * 
   * @param email
   * @param year
   * @return
   */
  // public List<Rating> findRatingsByUserEmailAndYear(String email, Integer year) {
  // Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
  // List<Rating> ratings = null;
  // if (userKey != null) {
  // QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYear(FestivalEnum.LOLLAPALOOZA, year);
  // List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
  // ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
  // }
  // return ratings;
  // }
  //
  // public List<Rating> findRatingsByUserAndYear(String authType, String authId, String authToken, String email,
  // Integer year) {
  // Key<AppUser> userKey = UserAccountManager.getInstance().manageAppUser(authType, authId, authToken, email);
  // List<Rating> ratings = null;
  // QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYear(FestivalEnum.LOLLAPALOOZA, year);
  // List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
  // ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
  // return ratings;
  // }

}