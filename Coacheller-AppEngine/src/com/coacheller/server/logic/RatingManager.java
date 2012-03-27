package com.coacheller.server.logic;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.persistence.CoachellerDataStore;

/**
 * Contains logic related to all requests made by the app client
 * 
 * @author Amy
 * 
 */
public class RatingManager {

  private static final Logger log = Logger.getLogger(RatingManager.class.getName());

  private CoachellerDataStore crimeDao;

  // Private constructor prevents instantiation from other classes
  private RatingManager() {
    crimeDao = new CoachellerDataStore();
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

  public List<Set> findSetsByRadius(Date startDate) {
    List<Set> incidents = crimeDao.findSetsByStartDate(startDate);
    return incidents;
  }

  public List<Set> findSetsByYearAndRadius(Integer year, Double latitude,
      Double longitude, Integer radius) {
    List<Set> incidents = crimeDao.findSetsByYearAndRadius(year, latitude, longitude,
        radius);
    return incidents;
  }

  public List<Set> findSetsByStartDate(Date startDate) {
    List<Set> incidents = crimeDao.findSetsByStartDate(startDate);
    return incidents;
  }

  public List<Rating> findAverageSetNumbersByYearAndRadius(Integer year,
      Integer radius) {
    List<Rating> averages = crimeDao.findAverageSetNumbersByYearAndRadius(year,
        radius);
    return averages;
  }

}