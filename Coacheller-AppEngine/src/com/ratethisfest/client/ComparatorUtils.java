package com.ratethisfest.client;

import java.util.Comparator;

import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

public class ComparatorUtils {

  public static final Comparator<? super Set> SET_NAME_COMPARATOR = new Comparator<Set>() {
    @Override
    public int compare(Set t0, Set t1) {
      return t0.getArtistName().compareToIgnoreCase(t1.getArtistName());
    }
  };

  public static final Comparator<? super Set> SET_SCORE_COMPARATOR = new Comparator<Set>() {
    @Override
    public int compare(Set t0, Set t1) {
      // Sort by cumulative scores first
      if (t0.getAvgScoreOne() < t1.getAvgScoreOne()) {
        return 1;
      } else if (t0.getAvgScoreOne() > t1.getAvgScoreOne()) {
        return -1;
      } else {
        // Sort items alphabetically within each group
        return t0.getArtistName().compareToIgnoreCase(t1.getArtistName());
      }
    }
  };

  public static final Comparator<? super Set> DOUBLE_SET_SCORE_COMPARATOR = new Comparator<Set>() {
    @Override
    public int compare(Set t0, Set t1) {
      // Sort by cumulative scores first
      if (averageScore(t0) < averageScore(t1)) {
        return 1;
      } else if (averageScore(t0) > averageScore(t1)) {
        return -1;
      } else {
        // Sort items alphabetically within each group
        return t0.getArtistName().compareToIgnoreCase(t1.getArtistName());
      }
    }

    private double averageScore(Set set) {
      double sumOne = set.getAvgScoreOne() * set.getNumRatingsOne();
      double sumTwo = set.getAvgScoreTwo() * set.getNumRatingsTwo();
      double average = sumOne + sumTwo;
      if (set.getNumRatingsOne() > 0 || set.getNumRatingsTwo() > 0) {
        average = average / (set.getNumRatingsOne() + set.getNumRatingsTwo());
      }
      return average;
    }
  };

  public static final Comparator<? super Set> SET_TIME_COMPARATOR = new Comparator<Set>() {
    @Override
    public int compare(Set t0, Set t1) {
      // Sort by set time first
      if (t0.getTimeOne() > t1.getTimeOne()) {
        return 1;
      } else if (t0.getTimeOne() < t1.getTimeOne()) {
        return -1;
      } else {
        // Sort items alphabetically within each group
        return t0.getArtistName().compareToIgnoreCase(t1.getArtistName());
      }
    }
  };

  public static final Comparator<? super String> STRING_NAME_COMPARATOR = new Comparator<String>() {
    @Override
    public int compare(String t0, String t1) {
      return t0.compareToIgnoreCase(t1);
    }
  };

  public static final Comparator<? super RatingGwt> RATING_NAME_COMPARATOR = new Comparator<RatingGwt>() {
    @Override
    public int compare(RatingGwt t0, RatingGwt t1) {
      // Sort by set time first
      if (t0.getScore() < t1.getScore()) {
        return 1;
      } else if (t0.getScore() > t1.getScore()) {
        return -1;
      } else {
        // Sort items alphabetically within each group
        return t0.getArtistName().compareToIgnoreCase(t1.getArtistName());
      }
    }
  };

}
