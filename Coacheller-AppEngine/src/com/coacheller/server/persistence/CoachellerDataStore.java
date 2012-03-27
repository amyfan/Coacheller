package com.coacheller.server.persistence;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * 
 * @author Amy
 * 
 */
public class CoachellerDataStore {
  private static final Logger log = Logger.getLogger(CoachellerDataStore.class.getName());

  private DAO dao;

  public CoachellerDataStore() {
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

    Set incident = dao.getObjectify().get(Set.class, id);
    return incident;
  }

  public Set findSetByKey(Key<Set> incidentKey) {
    if (incidentKey == null) {
      return null;
    }

    Set incident = dao.getObjectify().get(incidentKey);
    return incident;
  }

  public List<Set> findAllSets() {
    Query<Set> q = dao.getObjectify().query(Set.class);
    return q.list();
  }

  public List<Set> findSetsByStartDate(Date startDate) {
    // TODO: this query is wrong, implement appropriate date range filter here
    Query<Set> q = dao.getObjectify().query(Set.class).filter("startDate", startDate);
    return q.list();
  }

  public List<Set> findSetsByYear(Integer year) {
    Query<Set> q = dao.getObjectify().query(Set.class).filter("year", year);
    return q.list();
  }

  public List<Set> findSetsByYearAndRadius(Integer year, Double latitute,
      Double longitude, Integer radius) {
    // TODO: this query is wrong, implement location query data here
    Query<Set> q = dao.getObjectify().query(Set.class).filter("year", year);
    return q.list();
  }

  public Set updateSet(Set incident) {
    dao.getObjectify().put(incident); // id populated in this statement
    System.out.println("Updated Set to datastore: " + incident.toString());
    return incident;
  }

  public void deleteSet(Long id) {
    System.out.println("Deleting Set from datastore: " + id);
    dao.getObjectify().delete(Set.class, id);
  }

  /**
   * TODO: verify that this works
   */
  public void deleteAllSets() {
    System.out.println("Deleting all Sets from datastore: ");
    dao.getObjectify().delete(dao.getObjectify().query(Set.class).fetchKeys());
  }

  /**
   * TODO: this query's radius filter is wrong
   * 
   * @param year
   * @param radius
   * @return
   */
  public List<Rating> findAverageSetNumbersByYearAndRadius(Integer year,
      Integer radius) {
    Query<Rating> q = dao.getObjectify().query(Rating.class)
        .filter("year", year).filter("radius", radius);
    return q.list();
  }

  public int getSetCount() {
    return dao.getObjectify().query(Set.class).count();
  }
}