package com.coacheller.server.persistence;

import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.DayEnum;
import com.coacheller.server.domain.Set;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

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

  public Set findSetByArtistAndYear(String artist, Integer year) {
    Query<Set> q;
    if (year != null) {
      q = dao.getObjectify().query(Set.class).filter("artist", artist).filter("year", year);
    } else {
      q = dao.getObjectify().query(Set.class).filter("artist", artist);
    }

    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public Key<Set> findSetKeyByArtistAndYear(String artist, Integer year) {
    if (artist == null) {
      return null;
    }

    Iterable<Key<Set>> q;
    if (year != null) {
      q = dao.getObjectify().query(Set.class).filter("artistName", artist).filter("year", year)
          .fetchKeys();
    } else {
      q = dao.getObjectify().query(Set.class).filter("artistName", artist).fetchKeys();
    }
    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public List<Set> findAllSets() {
    Query<Set> q = dao.getObjectify().query(Set.class);
    return q.list();
  }

  public List<Set> findSetsByYear(Integer year) {
    Query<Set> q = dao.getObjectify().query(Set.class).filter("year", year);
    return q.list();
  }

  public List<Set> findSetsByYearAndDay(Integer year, DayEnum day) {
    Query<Set> q = dao.getObjectify().query(Set.class).filter("year", year)
        .filter("day", day.getValue());
    return q.list();
  }

  public Set updateSet(Set set) {
    dao.getObjectify().put(set); // id populated in this statement
    System.out.println("Updated Set to datastore: " + set.toString());
    return set;
  }

  public void deleteSet(Long id) {
    System.out.println("Deleting Set from datastore: " + id);
    dao.getObjectify().delete(Set.class, id);
  }

  public void deleteAllSets() {
    System.out.println("Deleting all Sets from datastore: ");
    dao.getObjectify().delete(dao.getObjectify().query(Set.class).fetchKeys());
  }

  public int getSetCount() {
    return dao.getObjectify().query(Set.class).count();
  }
}