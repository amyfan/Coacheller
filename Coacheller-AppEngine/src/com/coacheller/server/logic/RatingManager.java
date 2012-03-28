package com.coacheller.server.logic;

import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.DayEnum;
import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.persistence.RatingDAO;
import com.coacheller.server.persistence.SetDAO;

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
    List<Rating> ratings = ratingDao.findRatingsBySetId(setId);
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