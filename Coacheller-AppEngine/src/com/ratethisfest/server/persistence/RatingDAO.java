package com.ratethisfest.server.persistence;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.FestivalEnum;
import com.ratethisfest.shared.Set;

/**
 * 
 * @author Amy
 * 
 */
public class RatingDAO {
  private static final Logger log = Logger.getLogger(RatingDAO.class.getName());

  private DAO dao;

  public RatingDAO() {
    dao = new DAO();
  }

  /**
   * Find a {@link Rating} by id.
   * 
   * @param id
   *          the {@link Rating} id
   * @return the associated {@link Rating}, or null if not found
   */
  public Rating findRating(Long id) {
    if (id == null) {
      return null;
    }

    Rating rating = dao.getObjectify().get(Rating.class, id);
    return rating;
  }

  public Rating findRatingByKey(Key<Rating> ratingKey) {
    if (ratingKey == null) {
      return null;
    }

    Rating rating = dao.getObjectify().get(ratingKey);
    return rating;
  }

  public List<Rating> findAllRatings() {
    Query<Rating> q = dao.getObjectify().query(Rating.class);
    return q.list();
  }

  public List<Rating> findRatingsBySetKey(Key<Set> setKey) {
    Query<Rating> q = dao.getObjectify().query(Rating.class).filter("set", setKey);
    return q.list();
  }

  public List<Rating> findRatingsByUserKey(FestivalEnum festival, Key<AppUser> userKey) {
    Query<Rating> q = dao.getObjectify().query(Rating.class)
        .filter("festival", festival.getValue()).filter("rater", userKey);
    return q.list();
  }

  public List<Rating> findRatingsBySetKeyAndUserKey(Key<Set> setKey, Key<AppUser> userKey,
      Integer weekend) {
    Query<Rating> q = dao.getObjectify().query(Rating.class).filter("set", setKey)
        .filter("rater", userKey).filter("weekend", weekend);
    return q.list();
  }

  public List<Rating> findRatingsByWeekend(FestivalEnum festival, Integer weekend) {
    Query<Rating> q = dao.getObjectify().query(Rating.class).filter("weekend", weekend);
    return q.list();
  }

  public Rating updateRating(Rating rating) {
    rating.setDateModified(new Date());
    dao.getObjectify().put(rating); // id populated in this statement
    System.out.println("Updated Rating to datastore: " + rating.toString());
    return rating;
  }

  public void deleteRating(Long id) {
    System.out.println("Deleting Rating from datastore: " + id);
    dao.getObjectify().delete(Rating.class, id);
  }

  public void deleteRatingsByUser(Key<AppUser> user) {
    System.out.println("Deleting all Ratings from datastore: ");
    QueryResultIterable<Key<Rating>> ratings = dao.getObjectify().query(Rating.class)
        .filter("rater", user).fetchKeys();
    dao.getObjectify().delete(ratings);
  }

  public void deleteAllRatingsByFestival(FestivalEnum festival) {
    System.out.println("Deleting all Ratings from datastore: ");
    dao.getObjectify().delete(
        dao.getObjectify().query(Rating.class).filter("festival", festival.getValue()).fetchKeys());
  }

  public int getRatingCount() {
    return dao.getObjectify().query(Rating.class).count();
  }
}