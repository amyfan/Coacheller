package com.coacheller.server.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.AppUser;
import com.coacheller.server.domain.DayEnum;
import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.persistence.RatingDAO;
import com.coacheller.server.persistence.SetDAO;
import com.googlecode.objectify.Key;

/**
 * Contains logic related to all requests made by the app client
 * 
 * @author Amy
 * 
 */
public class RatingManager {

  private static final Logger log = Logger.getLogger(RatingManager.class.getName());

  private RatingDAO ratingDao;
  private SetDAO setDao;

  // Private constructor prevents instantiation from other classes
  private RatingManager() {
    ratingDao = new RatingDAO();
    setDao = new SetDAO();
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final RatingManager instance = new RatingManager();
  }

  public static RatingManager getInstance() {
    return SingletonHolder.instance;
  }

  public Rating updateRating(Rating rating) {
    UserAccountManager uam = UserAccountManager.getInstance();
    if (rating.getRater() == null && rating.getRaterId() != null) {
      rating.setRater(uam.getAppUserKeyById(rating.getRaterId()));
    }
    if (rating.getSet() == null && rating.getSetId() != null) {
      rating.setSet(setDao.findSetKeyById(rating.getSetId()));
    }
    return ratingDao.updateRating(rating);
  }

  public Rating findRating(Long id) {
    Rating rating = ratingDao.findRating(id);
    return rating;
  }

  public List<Rating> findAllRatings() {
    List<Rating> ratings = ratingDao.findAllRatings();
    return ratings;
  }

  public List<Rating> findRatingsBySetId(Long setId) {
    Key<Set> setKey = setDao.findSetKeyById(setId);
    List<Rating> ratings = ratingDao.findRatingsBySetKey(setKey);
    return ratings;
  }

  public List<Rating> findRatingsBySetArtist(String setArtist) {
    // TODO: figure out whether to keep this method & query year properly
    Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, null);
    List<Rating> ratings = new ArrayList<Rating>();
    if (setKey != null) {
      ratings = ratingDao.findRatingsBySetKey(setKey);
    }
    return ratings;
  }

  public List<Rating> findRatingsBySetArtistAndUser(String setArtist, String email) {
    // TODO: figure out whether to keep this method & query year properly
    Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, null);
    UserAccountManager uam = UserAccountManager.getInstance();
    Key<AppUser> userKey = uam.getAppUserKeyByEmail(email);
    List<Rating> ratings = new ArrayList<Rating>();
    if (setKey != null) {
      ratings = ratingDao.findRatingsBySetKeyAndUserKey(setKey, userKey);
    }
    return ratings;
  }

  public List<Rating> findRatingsByWeekend(Integer weekend) {
    List<Rating> ratings = ratingDao.findRatingsByWeekend(weekend);
    return ratings;
  }

  public void deleteAllRatings() {
    ratingDao.deleteAllRatings();
  }

  public void deleteRating(Rating rating) {
    ratingDao.deleteRating(rating.getId());
  }

  public Set updateSet(Set set) {
    return setDao.updateSet(set);
  }

  public Set findSet(Long id) {
    Set set = setDao.findSet(id);
    return set;
  }

  public Key<Set> findSetKeyByArtistAndYear(String artist, Integer year) {
    Key<Set> set = setDao.findSetKeyByArtistAndYear(artist, year);
    return set;
  }

  public Set findSetByArtistAndYear(String artist, Integer year) {
    Set set = setDao.findSetByArtistAndYear(artist, year);
    return set;
  }

  public List<Set> findAllSets() {
    List<Set> set = setDao.findAllSets();
    return set;
  }

  public List<Set> findSetsByYear(Integer year) {
    List<Set> ratings = setDao.findSetsByYear(year);
    return ratings;
  }

  public List<Set> findSetsByYearAndDay(Integer year, DayEnum day) {
    List<Set> set = setDao.findSetsByYearAndDay(year, day);
    return set;
  }

  public void deleteAllSets() {
    setDao.deleteAllSets();
  }

  public void deleteSet(Set set) {
    setDao.deleteSet(set.getId());
  }

}