package com.ratethisfest.server.logic;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.googlecode.objectify.Key;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.MathUtils;
import com.ratethisfest.shared.Set;

/**
 * Contains logic related to all requests made by the app client
 * 
 * @author Amy
 * 
 */
public class LollaRatingManager extends RatingManager {

  private static final Logger log = Logger.getLogger(LollaRatingManager.class.getName());

  // Private constructor prevents instantiation from other classes
  private LollaRatingManager() {
    super();
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final LollaRatingManager instance = new LollaRatingManager();
  }

  public static LollaRatingManager getInstance() {
    return SingletonHolder.instance;
  }
  
  //Get all ratings by user -MA
  public List<Rating> findRatingsByUser(Long userId) {  //Avoids implementation details of user
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyById(userId);
    return ratingDao.findAllRatingsByUserKey(userKey);
  }  
  
  //Get all ratings by user for one set -MA
  public List<Rating> findRatingsByUserAndSet(Long userId, Long setId) {  //Avoids implementation details of user, set
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyById(userId);
    Key<Set> setKey = setDao.findSetKeyById(setId);
    
    return ratingDao.findRatingsByUserKeyAndSetKey(userKey, setKey);
  }

  //Derived from similar private method.  Non-browser (i.e. mobile) clients must authenticate every request -MA
  public Rating addRating(long userId, long setId, Integer week, Integer score, String notes) {
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyById(userId);
    Key<Set> setKey = setDao.findSetKeyById(setId);
    
    List<Rating> existingRatingList = ratingDao.findRatingsByUserKeyAndSetKeyAndWeek(userKey, setKey, week);
    if (existingRatingList.size() == 0) {
      log.info("No existing rating, creating new");
      Rating rating = new Rating();
      rating.setSet(setKey);
      rating.setScore(score);
      rating.setWeekend(week);
      rating.setNotes(notes);
      rating.setRater(userKey);
      rating.setDateCreated(new Date());
      
      updateScoreAverageAfterAdd(rating);
      
      return ratingDao.updateRating(rating);
      
    } else if (existingRatingList.size() == 1) {
      log.info("Rating exists, updating");
      Rating existingRating = existingRatingList.get(0);
      return updateRating(existingRating, score, notes);
      
    } else {
      log.info("Unexpected - More than 1 user rating found");
      return null;
    }
  }

  /**
   * TODO: about to be deprecated once gwt login auth implemented
   * 
   * @param email
   * @param year
   * @return
   */
  public List<Rating> findRatingsByUserEmailAndYear(String email, Integer year) {
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = null;
    if (userKey != null) {
      QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYear(FestivalEnum.LOLLAPALOOZA,
          year);
      List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
      ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
    }
    return ratings;
  }

  public List<Rating> findRatingsByUserAndYear(String authType, String authId, String authToken,
      String email, Integer year) {
    Key<AppUser> userKey = UserAccountManager.getInstance().manageAppUser(authType, authId,
        authToken, email);
    List<Rating> ratings = null;
    QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYear(FestivalEnum.LOLLAPALOOZA,
        year);
    List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
    ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
    return ratings;
  }

  public List<Rating> findRatingsByUserYearAndDay(String authType, String authId, String authToken,
      String email, Integer year, DayEnum day) {
    Key<AppUser> userKey = UserAccountManager.getInstance().manageAppUser(authType, authId,
        authToken, email);
    List<Rating> ratings = null;
    QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYearAndDay(
        FestivalEnum.LOLLAPALOOZA, year, day);
    List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
    ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
    return ratings;
  }

  @Deprecated
  public List<Rating> findRatingsByUserYearAndDay(String email, Integer year, DayEnum day) {
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = null;
    if (userKey != null) {
      QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYearAndDay(
          FestivalEnum.LOLLAPALOOZA, year, day);
      List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
      ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
    }
    return ratings;
  }

  /**
   * Assumption: Service layer had already authenticated the token (if exists)
   * 
   * @param authType
   * @param authId
   * @param authToken
   * @param email
   * @param setId
   * @param score
   * @param notes
   * @return
   */
  public String addRatingBySetId(String authType, String authId, String authToken, String email,
      Long setId, Integer score, String notes) {
    String resp = null;

    Key<Set> setKey = setDao.findSetKeyById(setId);
    List<Rating> ratings = findRatingsBySetKeyAndUser(setKey, email);
    if (ratings == null || ratings.size() == 0) {
      // add new rating
      if (setKey != null) {
        Key<AppUser> userKey = UserAccountManager.getInstance().manageAppUser(authType, authId,
            authToken, email);
        addRating(userKey, setKey, score, notes);
        resp = "rating added";
      } else {
        resp = "invalid artist name";
        log.log(Level.WARNING, "invalid set id: " + setId);
      }
    } else {
      // update existing rating
      // TODO: for now, not going to do extra auth (cuz presumably this rating
      // was acquired legally)
      updateRating(ratings.get(0), score, notes);
      resp = "rating updated";
    }

    return resp;
  }

  private Rating addRating(Key<AppUser> userKey, Key<Set> setKey, Integer score, String notes) {
    Rating rating = new Rating();
    rating.setSet(setKey);
    rating.setScore(score);
    rating.setWeekend(1);
    rating.setNotes(notes);

    rating.setRater(userKey);

    updateScoreAverageAfterAdd(rating);

    rating.setDateCreated(new Date());

    return ratingDao.updateRating(rating);
  }

  private Rating updateRating(Rating rating, Integer score, String notes) {
    Integer difference = score - rating.getScore();
    rating.setScore(score);
    if (notes != null) {
      // in the case of rating updated from android app that doesn't yet have
      // notes capability
      rating.setNotes(notes);
    }

    updateScoreAverageAfterUpdate(rating, difference);

    return ratingDao.updateRating(rating);
  }

  public void deleteRatingsByYear(Integer year) {
    QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYear(FestivalEnum.LOLLAPALOOZA,
        year);
    List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
    QueryResultIterable<Key<Rating>> ratingKeys = ratingDao.findRatingsBySetKeys(setKeyList);
    ratingDao.deleteRatings(ratingKeys);
  }

  private void updateScoreAverageAfterAdd(Rating rating) {
    // update set's avg score
    Set set = setDao.findSet(rating.getSet().getId());
    Integer numRatings;
    Integer sum;
    if (rating.getWeekend() == 1) {
      if (set.getNumRatingsOne() == null) {
        numRatings = 0;
        sum = 0;
      } else {
        numRatings = set.getNumRatingsOne();
        sum = set.getScoreSumOne();
      }
      numRatings++;
      sum += rating.getScore();
      double average = sum;
      average = average / numRatings;
      set.setNumRatingsOne(numRatings);
      set.setScoreSumOne(sum);
      set.setAvgScoreOne(MathUtils.roundTwoDecimals(average));
      updateSet(set);
    } else {
      if (set.getNumRatingsTwo() == null) {
        numRatings = 0;
        sum = 0;
      } else {
        numRatings = set.getNumRatingsTwo();
        sum = set.getScoreSumTwo();
      }
      numRatings++;
      sum += rating.getScore();
      double average = sum;
      average = average / numRatings;
      set.setNumRatingsTwo(numRatings);
      set.setScoreSumTwo(sum);
      set.setAvgScoreTwo(MathUtils.roundTwoDecimals(average));
      updateSet(set);
    }
  }

  private void updateScoreAverageAfterUpdate(Rating rating, Integer difference) {
    // update set's avg score
    if (rating.getSet() != null) {
      Set set = setDao.findSetByKey(rating.getSet());
      if (rating.getWeekend() == 1) {
        Integer sum = set.getScoreSumOne();
        sum += difference;
        double average = sum;
        average = average / set.getNumRatingsOne();
        set.setScoreSumOne(sum);
        set.setAvgScoreOne(MathUtils.roundTwoDecimals(average));
        updateSet(set);
      } else {
        Integer sum = set.getScoreSumTwo();
        sum += difference;
        double average = sum;
        average = average / set.getNumRatingsTwo();
        set.setScoreSumTwo(sum);
        set.setAvgScoreTwo(MathUtils.roundTwoDecimals(average));
        updateSet(set);
      }
      // is this even necessary?:
      // rating.setSet(setDao.findSetKeyById(rating.getSet().getId()));
    }
  }

  @Override
  protected void updateScoreAverageAfterDelete(Rating rating) {
    // update set's avg score
    Set set = setDao.findSet(rating.getSet().getId());
    Integer numRatings;
    Integer sum;
    if (rating.getWeekend() == 1) {
      if (set.getNumRatingsOne() == null) {
        numRatings = 0;
        sum = 0;
      } else {
        numRatings = set.getNumRatingsOne();
        sum = set.getScoreSumOne();
      }
      numRatings--;
      if (numRatings < 1) {
        set.setNumRatingsOne(0);
        set.setScoreSumOne(0);
        set.setAvgScoreOne(0.0);
      } else {
        sum -= rating.getScore();
        double average = sum;
        average = average / numRatings;
        set.setNumRatingsOne(numRatings);
        set.setScoreSumOne(sum);
        set.setAvgScoreOne(MathUtils.roundTwoDecimals(average));
      }
      updateSet(set);
    } else {
      if (set.getNumRatingsTwo() == null) {
        numRatings = 0;
        sum = 0;
      } else {
        numRatings = set.getNumRatingsTwo();
        sum = set.getScoreSumTwo();
      }
      numRatings--;
      if (numRatings < 1) {
        set.setNumRatingsTwo(0);
        set.setScoreSumTwo(0);
        set.setAvgScoreTwo(0.0);
      } else {
        sum -= rating.getScore();
        double average = sum;
        average = average / numRatings;
        set.setNumRatingsTwo(numRatings);
        set.setScoreSumTwo(sum);
        set.setAvgScoreTwo(MathUtils.roundTwoDecimals(average));
      }
      updateSet(set);
    }
  }

  public List<Set> findSetsByYear(FestivalEnum fest, Integer year) {
    List<Set> ratings = setDao.findSetsByYear(fest, year);
    return ratings;
  }

  public List<Set> findSetsByYearAndDay(FestivalEnum fest, Integer year, DayEnum day) {
    List<Set> set = setDao.findSetsByYearAndDay(fest, year, day);
    return set;
  }

  // public void deleteSetsByYear(Integer year) {
  // setDao.deleteAllSetsByFestivalAndYear(FestivalEnum.LOLLAPALOOZA, year);
  // }

}