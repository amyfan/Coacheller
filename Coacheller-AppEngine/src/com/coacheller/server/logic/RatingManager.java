package com.coacheller.server.logic;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.Set;
import com.coacheller.server.persistence.CoachellerDAO;

/**
 * Contains logic related to all requests made by the app client
 * 
 * @author Amy
 * 
 */
public class RatingManager {

  private static final Logger log = Logger.getLogger(RatingManager.class.getName());

  private CoachellerDAO crimeDao;

  // Private constructor prevents instantiation from other classes
  private RatingManager() {
    crimeDao = new CoachellerDAO();
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

  public Set createSet() {
    // initializes ID
    return crimeDao.updateSet(new Set());
  }

  public Set findSet(Long id) {
    Set incident = crimeDao.findSet(id);
    return incident;
  }

  public List<Set> findAllSets() {
    List<Set> incidents = crimeDao.findAllSets();
    return incidents;
  }

  public List<Set> findSetsByYear(Integer year) {
    List<Set> incidents = crimeDao.findSetsByYear(year);
    return incidents;
  }

  public List<Set> findSetsByDay(Date date) {
    List<Set> incidents = crimeDao.findSetsByDay(date);
    return incidents;
  }

}