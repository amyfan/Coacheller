package com.coacheller.server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.coacheller.server.domain.Rating;
import com.coacheller.server.domain.Set;

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

  RatingManager ratingMgr;

  // Private constructor prevents instantiation from other classes
  private SetDataLoader() {
    ratingMgr = RatingManager.getInstance();
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

          ratingMgr.updateSet(set);

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

  public void calculateSetRatingAverages() {
    List<Set> sets = ratingMgr.findAllSets();
    for (Set set : sets) {
      List<Rating> ratings = ratingMgr.findRatingsBySetId(set.getId());
      int wkndOneCount = 0;
      int wkndTwoCount = 0;
      int wkndOneTotal = 0;
      int wkndTwoTotal = 0;
      for (Rating rating : ratings) {
        if (rating.getWeekend() == 1) {
          wkndOneTotal += rating.getScore();
          ++wkndOneCount;
        } else {
          wkndTwoTotal += rating.getScore();
          ++wkndTwoCount;
        }
      }
      if (wkndOneCount > 0) {
        double average = wkndOneTotal;
        average = average / wkndOneCount;
        set.setWkndOneAvgScore(average);
      }
      if (wkndTwoCount > 0) {
        double average = wkndTwoTotal;
        average = average / wkndTwoCount;
        set.setWkndTwoAvgScore(average);
      }
    }
  }
}