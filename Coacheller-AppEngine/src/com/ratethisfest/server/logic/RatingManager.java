package com.ratethisfest.server.logic;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.server.persistence.RatingDAO;
import com.ratethisfest.server.persistence.SetDAO;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.FestivalEnum;
import com.ratethisfest.shared.Set;

/**
 * Contains logic related to all requests made by the app client
 * 
 * @author Amy
 * 
 */
public abstract class RatingManager {

  protected RatingDAO ratingDao;
  protected SetDAO setDao;

  protected RatingManager() {
    this.ratingDao = new RatingDAO();
    this.setDao = new SetDAO();
  }

  protected Rating findRating(Long id) {
    Rating rating = ratingDao.findRating(id);
    return rating;
  }

  protected List<Rating> findRatingsBySetId(Long setId) {
    Key<Set> setKey = setDao.findSetKeyById(setId);
    List<Rating> ratings = ratingDao.findRatingsBySetKey(setKey);
    return ratings;
  }

  protected List<Rating> findRatingsBySetKeyAndUser(Key<Set> setKey, String email) {
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = new ArrayList<Rating>();
    if (setKey != null) {
      ratings = ratingDao.findRatingsByUserKeyAndSetKey(userKey, setKey, 1);
    }
    return ratings;
  }

  public List<Rating> findAllRatingsByUser(String email) {
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    List<Rating> ratings = null;
    if (userKey != null) {
      ratings = ratingDao.findAllRatingsByUserKey(userKey);
    }
    return ratings;
  }

  public Set findSetByArtistAndTime(FestivalEnum festival, String artist, Integer year,
      DayEnum day, Integer time) {
    Set set = setDao.findSetByArtistAndYear(festival, artist, year);
    return set;
  }

  public void deleteRatingById(Long id) {
    updateScoreAverageAfterDelete(findRating(id));
    ratingDao.deleteRating(id);
  }

  protected void updateScoreAverageAfterDelete(Rating rating) {
  }

  public void deleteRatingsByUser(String email) {
    // TODO: maybe recalc score avgs, but i'll leave that up to be done manually
    Key<AppUser> userKey = UserAccountManager.getInstance().getAppUserKeyByEmail(email);
    if (userKey != null) {
      ratingDao.deleteRatingsByUser(userKey);
    }
  }

  protected void deleteAllRatings(FestivalEnum festival) {
    ratingDao.deleteAllRatingsByFestival(festival);
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

  protected void deleteAllSets(FestivalEnum festival) {
    setDao.deleteAllSetsByFestival(festival);
  }

  public void deleteSet(Set set) {
    setDao.deleteSet(set.getId());
  }

}