package com.ratethisfest.server.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.FestivalEnum;
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

  /**
   * TODO
   * 
   * @param email
   * @param setArtist
   * @param setTime
   * @param year
   * @param score
   * @param notes
   * @return
   */
  public String addRatingBySetArtist(String email, String setArtist, Integer setTime, DayEnum day,
      Integer year, Integer score, String notes) {
    String resp = null;

    List<Rating> ratings = findRatingsBySetArtistAndUser(setArtist, email);
    if (ratings == null || ratings.size() == 0) {
      // add new rating
      Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, year);
      if (setKey != null) {
        addRating(setKey, email, score, notes);
        resp = "rating added";
      } else {
        resp = "invalid artist name";
        log.log(Level.WARNING, "invalid artist name: " + setArtist);
      }
    } else {
      // update existing rating
      updateRating(ratings.get(0), score, notes);
      resp = "rating updated";
    }

    return resp;
  }

  private Rating addRating(Key<Set> setKey, String email, Integer score, String notes) {
    Rating rating = new Rating();
    rating.setSet(setKey);
    rating.setScore(score);
    rating.setWeekend(1);
    rating.setNotes(notes);

    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    if (userKey == null) {
      UserAccountManager.getInstance().createAppUser(email);
      userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    }
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

  public List<Rating> findRatingsBySetArtist(String setArtist) {
    // TODO: figure out whether to keep this method & query year properly
    Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, null);
    List<Rating> ratings = new ArrayList<Rating>();
    if (setKey != null) {
      ratings = ratingDao.findRatingsBySetKey(setKey);
    }
    return ratings;
  }

  public List<Rating> findRatingsByUser(String email) {
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = null;
    if (userKey != null) {
      ratings = ratingDao.findRatingsByUserKey(FestivalEnum.LOLLAPALOOZA, userKey);
    }
    return ratings;
  }

  private List<Rating> findRatingsBySetArtistAndUser(String setArtist, String email) {
    // TODO: figure out whether to keep this method & query year properly
    Key<Set> setKey = findSetKeyByArtistAndYear(setArtist, null);
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = new ArrayList<Rating>();
    if (setKey != null) {
      ratings = ratingDao.findRatingsBySetKeyAndUserKey(setKey, userKey, 1);
    }
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

  private Key<Set> findSetKeyByArtistAndYear(String artist, Integer year) {
    Key<Set> set = setDao.findSetKeyByArtistAndYear(FestivalEnum.LOLLAPALOOZA, artist, year);
    return set;
  }

  public List<Set> findSetsByYear(Integer year) {
    List<Set> ratings = setDao.findSetsByYear(FestivalEnum.LOLLAPALOOZA, year);
    return ratings;
  }

  public List<Set> findSetsByYearAndDay(Integer year, DayEnum day) {
    List<Set> set = setDao.findSetsByYearAndDay(FestivalEnum.LOLLAPALOOZA, year, day);
    return set;
  }

}