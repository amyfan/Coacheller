package com.coacheller.server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;
import com.coacheller.server.persistence.SetDAO;

/**
 * Class to populate the datastore with crime data and averages
 * 
 * @author Amy
 * 
 */
public class SetDataLoader {
  private static final int DAY_INDEX = 0;
  private static final int TIME_INDEX = 1;
  private static final int ARTIST_NAME = 2;

  private static final Logger log = Logger.getLogger(RatingManager.class.getName());

  private SetDAO setDao;

  // Private constructor prevents instantiation from other classes
  private SetDataLoader() {
    setDao = new SetDAO();
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

  public void insertSets(BufferedReader setFile) {
    // parse input file and for every row, create a new Set and persist
    String line;
    try {
      line = setFile.readLine();
      while (line != null) {
        try {
          Set set = new Set();
          String[] fields = line.split(",");

          set.setDay(fields[DAY_INDEX]);
          set.setTime(Integer.valueOf(fields[TIME_INDEX]));
          set.setArtistName(fields[ARTIST_NAME]);

          setDao.updateSet(set);

          line = setFile.readLine();
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
    setDao.deleteAllSets();
  }

  public Set updateSet(Set set) {
    return setDao.updateSet(set);
  }

  public void deleteSet(Set set) {
    setDao.deleteSet(set.getId());
  }

}