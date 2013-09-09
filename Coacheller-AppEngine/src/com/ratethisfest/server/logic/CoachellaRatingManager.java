package com.ratethisfest.server.logic;

import java.util.ArrayList;
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
public class CoachellaRatingManager extends RatingManager {

  private static final Logger log = Logger.getLogger(CoachellaRatingManager.class.getName());

  // Private constructor prevents instantiation from other classes
  private CoachellaRatingManager() {
    super();
  }

  // private CoachellaRatingManager(RatingDAO ratingDao, SetDAO setDao) {
  // super(ratingDao, setDao);
  // }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final CoachellaRatingManager instance = new CoachellaRatingManager();
  }

  public static CoachellaRatingManager getInstance() {
    return SingletonHolder.instance;
  }

  /**
   * Assumption: Service layer had already authenticated the token (if exists)
   * 
   * @param authType
   * @param authId
   * @param authToken
   * @param email
   * @param setId
   * @param weekend
   * @param score
   * @param notes
   * @return
   */
  public String addRatingBySetId(String authType, String authId, String authToken, String email,
      Long setId, Integer weekend, Integer score, String notes) {
    String resp = null;

    Key<Set> setKey = setDao.findSetKeyById(setId);
    // TODO: put in check for authId too!!
    List<Rating> ratings = findRatingsBySetKeyAndUser(setKey, email);
    if (ratings == null || ratings.size() == 0) {
      // add new rating
      if (setKey != null) {
        Key<AppUser> userKey = UserAccountManager.getInstance().manageAppUser(authType, authId,
            authToken, email);
        addRating(userKey, setKey, weekend, score, notes);
        resp = "rating added";
      } else {
        resp = "invalid artist name";
        log.log(Level.WARNING, "invalid set id: " + setId);
      }
    } else {
      // update existing rating
      updateRating(ratings.get(0), weekend, score, notes);
      resp = "rating updated";
    }

    return resp;
  }

  private Rating addRating(Key<AppUser> userKey, Key<Set> setKey, Integer weekend, Integer score,
      String notes) {
    Rating rating = new Rating();
    rating.setSet(setKey);
    rating.setScore(score);
    rating.setWeekend(weekend);
    rating.setNotes(notes);

    rating.setRater(userKey);

    updateScoreAverageAfterAdd(rating);

    rating.setDateCreated(new Date());

    return ratingDao.updateRating(rating);
  }

  private Rating updateRating(Rating rating, Integer weekend, Integer score, String notes) {
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

  public List<Rating> findRatingsByUserEmailAndYear(String email, Integer year) {
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = null;
    if (userKey != null) {
      QueryResultIterable<Key<Set>> setKeys = setDao
          .findSetKeysByYear(FestivalEnum.COACHELLA, year);
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
    QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYear(FestivalEnum.COACHELLA, year);
    List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
    ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
    return ratings;
  }

  public List<Rating> findRatingsByUserYearAndDay(String authType, String authId, String authToken,
      String email, Integer year, DayEnum day) {
    Key<AppUser> userKey = UserAccountManager.getInstance().manageAppUser(authType, authId,
        authToken, email);
    List<Rating> ratings = null;
    QueryResultIterable<Key<Set>> setKeys = setDao.findSetKeysByYearAndDay(FestivalEnum.COACHELLA,
        year, day);
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
          FestivalEnum.COACHELLA, year, day);
      List<Key<Set>> setKeyList = CollectionUtils.iterableToList(setKeys);
      ratings = ratingDao.findRatingsByUserKeyAndSetKeys(userKey, setKeyList);
    }
    return ratings;
  }

  private List<Rating> findRatingsBySetArtistAndUser(String setArtist, String email, Integer weekend) {
    // TODO: figure out whether to keep this method & query year properly
    Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, null);
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = new ArrayList<Rating>();
    if (setKey != null) {
      ratings = ratingDao.findRatingsByUserKeyAndSetKeyAndWeek(userKey, setKey, weekend);
    }
    return ratings;
  }

  public List<Rating> findRatingsByWeekend(Integer weekend) {
    List<Rating> ratings = ratingDao.findRatingsByWeekend(FestivalEnum.COACHELLA, weekend);
    return ratings;
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

  @Deprecated
  private Key<Set> findSetKeyByArtistAndYear(String artist, Integer year) {
    Key<Set> set = setDao.findSetKeyByArtistAndYear(FestivalEnum.COACHELLA, artist, year);
    return set;
  }

  public List<Set> findSetsByYear(Integer year) {
    List<Set> ratings = setDao.findSetsByYear(FestivalEnum.COACHELLA, year);
    return ratings;
  }

  public List<Set> findSetsByYearAndDay(Integer year, DayEnum day) {
    List<Set> set = setDao.findSetsByYearAndDay(FestivalEnum.COACHELLA, year, day);
    return set;
  }

}