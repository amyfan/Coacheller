package com.ratethisfest.server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ratethisfest.shared.FestivalEnum;
import com.ratethisfest.shared.Set;

public class LollaSetDataLoader extends SetDataLoader {
  private static final Logger log = Logger.getLogger(LollaSetDataLoader.class.getName());

  private static final int FESTIVAL_INDEX = 0;
  private static final int YEAR_INDEX = 1;
  private static final int DAY_INDEX = 2;
  private static final int TIME_ONE_INDEX = 3;
  private static final int STAGE_ONE_INDEX = 4;
  private static final int ARTIST_NAME_INDEX = 5;

  // Private constructor prevents instantiation from other classes
  private LollaSetDataLoader() {
    super(LollaRatingManager.getInstance());
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final LollaSetDataLoader instance = new LollaSetDataLoader();
  }

  public static LollaSetDataLoader getInstance() {
    return SingletonHolder.instance;
  }

  public void insertSetsFromApi(Integer year) {
    // String bandsQuery = ApiStrings.LOLLAPALOOZA_URL_BANDS;
    String eventsQuery = ApiStrings.LOLLAPALOOZA_URL_EVENTS;
    if (year == 2012) {
      // bandsQuery += ApiStrings.LOLLAPALOOZA_KEY_2012;
      eventsQuery += ApiStrings.LOLLAPALOOZA_KEY_2012;
    } else if (year == 2011) {
      // bandsQuery += ApiStrings.LOLLAPALOOZA_KEY_2011;
      eventsQuery += ApiStrings.LOLLAPALOOZA_KEY_2011;
    }

    Integer eventsPageNum = 1;

    List<Set> sets = new ArrayList<Set>();

    try {
      String requestString = eventsQuery + "&page=" + eventsPageNum;

      URL url = new URL(requestString);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String line = reader.readLine();
      StringBuilder builder = new StringBuilder();

      while (line != null) {
        builder.append(line).append("\n");
        while ((line = reader.readLine()) != null) {
          builder.append(line).append("\n");
        }
        List<Set> newSets = LollaApiJson.convertJsonToSets(builder.toString());
        if (newSets == null || newSets.isEmpty()) {
          break;
        }

        sets.addAll(newSets);

        eventsPageNum++;
        requestString = eventsQuery + "&page=" + eventsPageNum;

        url = new URL(requestString);
        reader.close();
        reader = new BufferedReader(new InputStreamReader(url.openStream()));
        line = reader.readLine();
        builder = new StringBuilder();
      }
      reader.close();

    } catch (MalformedURLException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "insertSetsFromApi: " + e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "insertSetsFromApi: " + e.getMessage());
    }
    ratingMgr.deleteAllSetsByYear(FestivalEnum.LOLLAPALOOZA, 2012);
    for (Set set : sets) {
      set.setYear(year);
      set.setDateCreated(new Date());
      ratingMgr.updateSet(set);
    }
  }

  public void insertSetsFromFile(BufferedReader setFile) {
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
          set.setStageOne(fields[STAGE_ONE_INDEX]);
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
          log.log(Level.SEVERE, "insertSetsFromFile: " + e.getMessage());
          break;
          // continue;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "insertSetsFromFile: " + e.getMessage());
    }
  }

  /**
   * Updates set information, as well as adding any new sets
   * 
   * @param setFile
   */
  public void updateSetsFromFile(BufferedReader setFile) {
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
          set.setStageOne(fields[STAGE_ONE_INDEX]);

          ratingMgr.updateSet(set);

          line = setFile.readLine();
        } catch (Exception e) {
          e.printStackTrace();
          log.log(Level.SEVERE, "updateSetsFromFile: " + e.getMessage());
          break;
          // continue;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "updateSetsFromFile: " + e.getMessage());
    }
  }

}
