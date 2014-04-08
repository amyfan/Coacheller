package com.ratethisfest.server.logic;

import java.util.List;

import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.MathUtils;
import com.ratethisfest.shared.Set;

/**
 * Class to populate the datastore with crime data and averages
 * 
 * @author Amy
 * 
 */
public abstract class SetDataLoader {
  protected RatingManager ratingMgr;

  protected SetDataLoader(RatingManager ratingMgr) {
    this.ratingMgr = ratingMgr;
  }

  public void clearSetRatingAverages(FestivalEnum fest) {
    List<Set> sets = ratingMgr.findAllSets(fest);
    for (Set set : sets) {
      set.setNumRatingsOne(0);
      set.setScoreSumOne(0);
      set.setAvgScoreOne(0.0);
      set.setNumRatingsTwo(0);
      set.setScoreSumTwo(0);
      set.setAvgScoreTwo(0.0);
      ratingMgr.updateSet(set);
    }
  }

  /**
   * 
   * @param fest
   * @param year
   */
  public void recalculateSetRatingAveragesByYear(FestivalEnum fest, Integer year) {
    List<Set> sets = ratingMgr.findSetsByYear(fest, year);
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