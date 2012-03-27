package com.coacheller.server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.persistence.CoachellerDataStore;

/**
 * Class to populate the datastore with crime data and averages
 * 
 * @author Amy
 * 
 */
public class SetDataLoader {
  private static final int DATE_INDEX = 0;
  private static final int TIME_INDEX = 1;
  private static final int BCC_INDEX = 2;
  private static final int ADDRESS_INDEX = 3;
  private static final int LATITUDE_INDEX = 4;
  private static final int LONGITUDE_INDEX = 5;
  private static final String DATE_FORMAT = "M/d/yyyy HH:mm";

  private static final Logger log = Logger.getLogger(RatingManager.class.getName());

  private CoachellerDataStore coachellerDao;

  // Private constructor prevents instantiation from other classes
  private SetDataLoader() {
    coachellerDao = new CoachellerDataStore();
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final SetDataLoader instance = new SetDataLoader();
  }

  public static SetDataLoader getInstance() {
    return SingletonHolder.instance;
  }

  public void insertSets(BufferedReader incidentFile) {
    // parse input file and for every row, create a new Set and persist
    String line;
    try {
      line = incidentFile.readLine();
      SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
      SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
      while (line != null) {
        try {
          Set set = new Set();
          String[] fields = line.split(",");
          
          String dateString = fields[DATE_INDEX] + " " + fields[TIME_INDEX];
          Date date = dateFormat.parse(dateString);
          set.setSetDate(date);
                    
          coachellerDao.updateSet(set);
          
          line = incidentFile.readLine();
        } catch (ParseException e) {
          e.printStackTrace();
          continue;
        } catch (Exception e) {
          e.printStackTrace();
          continue;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void deleteAllSets() {
    coachellerDao.deleteAllSets();
  }

  public Set updateSet(Set incident) {
    return coachellerDao.updateSet(incident);
  }

  public void deleteSet(Set incident) {
    coachellerDao.deleteSet(incident.getId());
  }

  /**
   * Populate the database with incident averages by radius and year for the
   * entire city
   */
  public void calculateSetAverages() {
    // TODO: logic for calculating averages goes here
    Rating average = new Rating();
  }
}