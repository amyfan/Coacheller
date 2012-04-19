package com.coacheller.server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.coacheller.server.domain.Rating;
import com.coacheller.shared.MathUtils;
import com.coacheller.shared.Set;

/**
 * Class to populate the datastore with crime data and averages
 * 
 * @author Amy
 * 
 */
public class SetDataLoader {
  private static final Logger log = Logger.getLogger(SetDataLoader.class.getName());

  private static final int YEAR_INDEX = 0;
  private static final int DAY_INDEX = 1;
  private static final int TIME_ONE_INDEX = 2;
  private static final int TIME_TWO_INDEX = 3;
  private static final int STAGE_ONE_INDEX = 4;
  private static final int STAGE_TWO_INDEX = 5;
  private static final int ARTIST_NAME_INDEX = 6;

  // Private constructor prevents instantiation from other classes
  private SetDataLoader() {
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

          set.setYear(Integer.valueOf(fields[YEAR_INDEX]));
          set.setDay(fields[DAY_INDEX]);
          set.setTimeOne(Integer.valueOf(fields[TIME_ONE_INDEX]));
          set.setTimeTwo(Integer.valueOf(fields[TIME_TWO_INDEX]));
          set.setStageOne(fields[STAGE_ONE_INDEX]);
          set.setStageTwo(fields[STAGE_TWO_INDEX]);
          set.setArtistName(fields[ARTIST_NAME_INDEX]);
          set.setNumRatingsOne(0);
          set.setNumRatingsTwo(0);
          set.setScoreSumOne(0);
          set.setScoreSumTwo(0);
          set.setAvgScoreOne(0.0);
          set.setAvgScoreTwo(0.0);

          set.setDateCreated(new Date());
          RatingManager.getInstance().updateSet(set);

          line = setFile.readLine();
        } catch (Exception e) {
          e.printStackTrace();
          log.log(Level.SEVERE, "insertSets: " + e.getMessage());
          break;
          // continue;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "insertSets: " + e.getMessage());
    }
  }

  public void clearSetRatingAverages() {
    List<Set> sets = RatingManager.getInstance().findAllSets();
    for (Set set : sets) {
      set.setNumRatingsOne(0);
      set.setScoreSumOne(0);
      set.setAvgScoreOne(0.0);
      set.setNumRatingsTwo(0);
      set.setScoreSumTwo(0);
      set.setAvgScoreTwo(0.0);
      RatingManager.getInstance().updateSet(set);
    }
  }

  public void recalculateSetRatingAverages() {
    List<Set> sets = RatingManager.getInstance().findAllSets();
    for (Set set : sets) {
      List<Rating> ratings = RatingManager.getInstance().findRatingsBySetId(set.getId());
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
        set.setNumRatingsOne(wkndOneCount);
        set.setScoreSumOne(wkndOneTotal);
        set.setAvgScoreOne(MathUtils.roundTwoDecimals(average));
        RatingManager.getInstance().updateSet(set);
      }
      if (wkndTwoCount > 0) {
        double average = wkndTwoTotal;
        average = average / wkndTwoCount;
        set.setNumRatingsTwo(wkndTwoCount);
        set.setScoreSumTwo(wkndTwoTotal);
        set.setAvgScoreTwo(MathUtils.roundTwoDecimals(average));
        RatingManager.getInstance().updateSet(set);
      }
    }
  }
}