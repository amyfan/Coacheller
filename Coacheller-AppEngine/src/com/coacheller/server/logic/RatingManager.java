package com.coacheller.server.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.AppUser;
import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.persistence.RatingDAO;
import com.coacheller.server.persistence.SetDAO;
import com.coacheller.shared.DayEnum;
import com.coacheller.shared.FieldVerifier;
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

  public String addRatingBySetArtist(String email, String setArtist, Integer year, Integer weekend,
      Integer score) {
    String resp = null;

    List<Rating> ratings = findRatingsBySetArtistAndUser(setArtist, email, weekend);
    if (ratings == null || ratings.size() == 0) {
      Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, year);
      if (setKey != null) {
        addRating(setKey, email, weekend, score);
        resp = "rating added";
      } else {
        resp = "invalid artist name";
      }
    } else {
      updateRating(ratings.get(0), weekend, score);
      resp = "rating updated";
    }

    return resp;
  }

  private Rating addRating(Key<Set> setKey, String email, Integer weekend, Integer score) {
    Rating rating = new Rating();
    rating.setSet(setKey);
    rating.setSetId(setKey.getId());
    rating.setScore(score);
    rating.setWeekend(weekend);

    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    if (userKey == null) {
      AppUser user = UserAccountManager.getInstance().createAppUser(email);
      rating.setRaterId(user.getId());
    } else {
      rating.setRater(userKey);
      rating.setRaterId(userKey.getId());
    }

    // update set's avg score
    Set set = setDao.findSet(rating.getSetId());
    if (weekend == 1) {
      Integer numRatings;
      Integer sum;
      if (set.getNumRatingsOne() == null) {
        numRatings = 0;
        sum = 0;
      } else {
        numRatings = set.getNumRatingsOne();
        sum = set.getScoreSumOne();
      }
      numRatings++;
      sum += score;
      double average = sum;
      average = average / numRatings;
      set.setNumRatingsOne(numRatings);
      set.setScoreSumOne(sum);
      set.setAvgScoreOne(average);
      updateSet(set);
    } else {
      Integer numRatings;
      Integer sum;
      if (set.getNumRatingsTwo() == null) {
        numRatings = 0;
        sum = 0;
      } else {
        numRatings = set.getNumRatingsTwo();
        sum = set.getScoreSumTwo();
      }
      numRatings++;
      sum += score;
      double average = sum;
      average = average / numRatings;
      set.setNumRatingsTwo(numRatings);
      set.setScoreSumTwo(sum);
      set.setAvgScoreTwo(average);
      updateSet(set);
    }

    return ratingDao.updateRating(rating);
  }

  private Rating updateRating(Rating rating, Integer weekend, Integer score) {
    Integer difference = score - rating.getScore();
    rating.setScore(score);

    UserAccountManager uam = UserAccountManager.getInstance();
    if (rating.getRater() == null && rating.getRaterId() != null) {
      rating.setRater(uam.getAppUserKeyById(rating.getRaterId()));
    }
    if (rating.getSetId() != null) {
      // update set's avg score
      Set set = setDao.findSet(rating.getSetId());
      if (weekend == 1) {
        Integer sum = set.getScoreSumOne();
        sum += difference;
        double average = sum;
        average = average / set.getNumRatingsOne();
        set.setScoreSumOne(sum);
        set.setAvgScoreOne(average);
        updateSet(set);
      } else {
        Integer sum = set.getScoreSumTwo();
        sum += difference;
        double average = sum;
        average = average / set.getNumRatingsTwo();
        set.setScoreSumTwo(sum);
        set.setAvgScoreTwo(average);
        updateSet(set);
      }
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

  private List<Rating> findRatingsBySetArtistAndUser(String setArtist, String email, Integer weekend) {
    // TODO: figure out whether to keep this method & query year properly
    Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, null);
    UserAccountManager uam = UserAccountManager.getInstance();
    Key<AppUser> userKey = uam.getAppUserKeyByEmail(email);
    List<Rating> ratings = new ArrayList<Rating>();
    if (setKey != null) {
      ratings = ratingDao.findRatingsBySetKeyAndUserKey(setKey, userKey, weekend);
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

  private Key<Set> findSetKeyByArtistAndYear(String artist, Integer year) {
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