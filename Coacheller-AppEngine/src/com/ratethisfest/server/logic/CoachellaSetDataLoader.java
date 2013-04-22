package com.ratethisfest.server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.FestivalEnum;
import com.ratethisfest.shared.MathUtils;
import com.ratethisfest.shared.Set;

public class CoachellaSetDataLoader extends SetDataLoader {
  private static final Logger log = Logger.getLogger(CoachellaSetDataLoader.class.getName());

  private static final int FESTIVAL_INDEX = 0;
  private static final int YEAR_INDEX = 1;
  private static final int DAY_INDEX = 2;
  private static final int TIME_ONE_INDEX = 3;
  private static final int TIME_TWO_INDEX = 4;
  private static final int STAGE_ONE_INDEX = 5;
  private static final int STAGE_TWO_INDEX = 6;
  private static final int ARTIST_NAME_INDEX = 7;

  // Private constructor prevents instantiation from other classes
  private CoachellaSetDataLoader() {
    super(CoachellaRatingManager.getInstance());
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final CoachellaSetDataLoader instance = new CoachellaSetDataLoader();
  }

  public static CoachellaSetDataLoader getInstance() {
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

          set.setFestival(fields[FESTIVAL_INDEX]);
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
          ratingMgr.updateSet(set);

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

  /**
   * Updates set information, as well as adding any new sets
   * 
   * @param setFile
   */
  public void updateSets(BufferedReader setFile) {
    String line;
    try {
      line = setFile.readLine();
      while (line != null) {
        try {
          String[] fields = line.split(",");

          Set set = ratingMgr.findSetByArtist(FestivalEnum.fromValue(fields[FESTIVAL_INDEX]),
              fields[ARTIST_NAME_INDEX], Integer.valueOf(fields[YEAR_INDEX]));

          if (set == null) {
            // add new set to the database
            set = new Set();
            set.setFestival(fields[FESTIVAL_INDEX]);
            set.setYear(Integer.valueOf(fields[YEAR_INDEX]));
            set.setArtistName(fields[ARTIST_NAME_INDEX]);
            set.setDay(fields[DAY_INDEX]);
            set.setNumRatingsOne(0);
            set.setNumRatingsTwo(0);
            set.setScoreSumOne(0);
            set.setScoreSumTwo(0);
            set.setAvgScoreOne(0.0);
            set.setAvgScoreTwo(0.0);
            set.setDateCreated(new Date());
          }
          set.setTimeOne(Integer.valueOf(fields[TIME_ONE_INDEX]));
          set.setTimeTwo(Integer.valueOf(fields[TIME_TWO_INDEX]));
          set.setStageOne(fields[STAGE_ONE_INDEX]);
          set.setStageTwo(fields[STAGE_TWO_INDEX]);

          ratingMgr.updateSet(set);

          line = setFile.readLine();
        } catch (Exception e) {
          e.printStackTrace();
          log.log(Level.SEVERE, "updateSets: " + e.getMessage());
          break;
          // continue;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "updateSets: " + e.getMessage());
    }
  }

  /**
   * TODO: Temporary test method
   */
  public void updateSetFestival() {
    List<Set> sets = ratingMgr.findAllSets();
    for (Set set : sets) {
      set.setFestival(FestivalEnum.COACHELLA.getValue());
      ratingMgr.updateSet(set);
    }
  }

  /**
   * TODO: take in year as param
   */
  public void recalculateSetRatingAverages() {
    List<Set> sets = CoachellaRatingManager.getInstance().findSetsByYear(2013);
    for (Set set : sets) {
      List<Rating> ratings = ratingMgr.findRatingsBySetId(set.getId());
      int wkndOneCount = 0;
      int wkndTwoCount = 0;
      int wkndOneTotal = 0;
      int wkndTwoTotal = 0;
      for (Rating rating : ratings) {
        if (rating.getScore() != null) {
          if (rating.getWeekend() == 1) {
            wkndOneTotal += rating.getScore();
            ++wkndOneCount;
          } else if (rating.getWeekend() == 2) {
            wkndTwoTotal += rating.getScore();
            ++wkndTwoCount;
          }
        }
      }
      if (wkndOneCount > 0 || (set.getAvgScoreOne() != null && set.getAvgScoreOne() > 0)) {
        double average;
        if (wkndOneCount > 0) {
          average = wkndOneTotal / wkndOneCount;
        } else {
          average = 0;
        }
        set.setNumRatingsOne(wkndOneCount);
        set.setScoreSumOne(wkndOneTotal);
        set.setAvgScoreOne(MathUtils.roundTwoDecimals(average));
        ratingMgr.updateSet(set);
      }
      if (wkndTwoCount > 0 || (set.getAvgScoreTwo() != null && set.getAvgScoreTwo() > 0)) {
        double average;
        if (wkndTwoCount > 0) {
          average = wkndTwoTotal / wkndTwoCount;
        } else {
          average = 0;
        }
        set.setNumRatingsTwo(wkndTwoCount);
        set.setScoreSumTwo(wkndTwoTotal);
        set.setAvgScoreTwo(MathUtils.roundTwoDecimals(average));
        ratingMgr.updateSet(set);
      }
    }
  }
}
