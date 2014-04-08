package com.ratethisfest.server.persistence;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.Set;

/**
 * 
 * @author Amy
 * 
 */
public class SetDAO {
  private static final Logger log = Logger.getLogger(SetDAO.class.getName());

  private DAO dao;

  public SetDAO() {
    dao = new DAO();
  }

  /**
   * Find a {@link Set} by id.
   * 
   * @param id
   *          the {@link Set} id
   * @return the associated {@link Set}, or null if not found
   */
  public Set findSet(Long id) {
    if (id == null) {
      return null;
    }

    Set set = dao.getObjectify().get(Set.class, id);
    return set;
  }

  public Key<Set> findSetKeyById(Long id) {
    if (id == null) {
      return null;
    }

    Iterable<Key<Set>> q = dao.getObjectify().query(Set.class).filter("id", id).fetchKeys();
    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public Set findSetByKey(Key<Set> setKey) {
    if (setKey == null) {
      return null;
    }

    Set set = dao.getObjectify().get(setKey);
    return set;
  }

  public Set findSetByArtistAndYear(FestivalEnum festival, String artist, Integer year) {
    Query<Set> q;
    if (year != null) {
      q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue()).filter("artist", artist)
          .filter("year", year);
    } else {
      q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue()).filter("artist", artist);
    }

    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public Key<Set> findSetKeyByArtistAndYear(FestivalEnum festival, String artist, Integer year) {
    if (artist == null) {
      return null;
    }

    Iterable<Key<Set>> q;
    if (year != null) {
      q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue()).filter("artistName", artist)
          .filter("year", year).fetchKeys();
    } else {
      q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue()).filter("artistName", artist)
          .fetchKeys();
    }
    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public List<Set> findAllSets(FestivalEnum festival) {
    Query<Set> q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue());
    return q.list();
  }

  public List<Set> findSetsByYear(FestivalEnum festival, Integer year) {
    Query<Set> q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue()).filter("year", year);
    return q.list();
  }

  public List<Set> findSetsByYearAndDay(FestivalEnum festival, Integer year, DayEnum day) {
    Query<Set> q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue()).filter("year", year)
        .filter("day", day.getValue());
    return q.list();
  }

  public QueryResultIterable<Key<Set>> findSetKeysByYear(FestivalEnum festival, Integer year) {
    QueryResultIterable<Key<Set>> q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue())
        .filter("year", year).fetchKeys();
    return q;
  }

  public QueryResultIterable<Key<Set>> findSetKeysByYearAndDay(FestivalEnum festival, Integer year, DayEnum day) {
    QueryResultIterable<Key<Set>> q = dao.getObjectify().query(Set.class).filter("festival", festival.getValue())
        .filter("year", year).filter("day", day.getValue()).fetchKeys();
    return q;
  }

  public Set updateSet(Set set) {
    set.setDateModified(new Date());
    dao.getObjectify().put(set); // id populated in this statement
    System.out.println("Updated Set to datastore: " + set.toString());
    return set;
  }

  public void deleteSet(Long id) {
    System.out.println("Deleting Set from datastore: " + id);
    dao.getObjectify().delete(Set.class, id);
  }

  public void deleteAllSetsByFestivalAndYear(FestivalEnum festival, Integer year) {
    System.out.println("Deleting all " + year + " " + festival.getValue() + " Sets from datastore: ");
    dao.getObjectify().delete(
        dao.getObjectify().query(Set.class).filter("festival", festival.getValue()).filter("year", year).fetchKeys());
  }

  public int getSetCount() {
    return dao.getObjectify().query(Set.class).count();
  }
}