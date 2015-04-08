import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImportLollapaloozaHTMLSchedule {

  public static final String FEST_NAME = "FEST_NAME";
  public static final String YEAR = "YEAR";
  public static final String DAY_NAME = "DAY_NAME";
  public static final String TIME_ONE = "TIME_ONE";
  public static final String TIME_TWO = "TIME_TWO";
  public static final String STAGE_ONE = "STAGE_ONE";
  public static final String STAGE_TWO = "STAGE_TWO";
  public static final String ARTIST_NAME = "ARTIST_NAME";

  public static void main(String[] args) {
    System.out.println("Launch");

    try {
      String sourceFileName = "Lollapalooza 2013-08-04.htm";
      String destinationFileName = sourceFileName + ".txt";
      parseHTML("html/" + sourceFileName, "text/" + destinationFileName);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static void parseHTML(String source, String output) throws FileNotFoundException, IOException {

    // Path currentRelativePath = Paths.get("");
    // String s = currentRelativePath.toAbsolutePath().toString();
    // System.out.println("Current relative path is: " + s);

    File sourceFile = new File(source);
    // FileInputStream inStream = new FileInputStream(sourceFile);
    // InputStreamReader streamReader = new InputStreamReader(inStream);
    File outputFile = new File(output);
    FileOutputStream outStream = new FileOutputStream(outputFile);
    OutputStreamWriter streamWriter = new OutputStreamWriter(outStream);
    BufferedWriter bufWriter = new BufferedWriter(streamWriter);

    HashMap<String, String> stageIdentifierToNameMap = new HashMap<String, String>();
    HashMap<String, String> accumulatedValues = new HashMap<String, String>();
    accumulatedValues.put(FEST_NAME, "Lollapalooza");
    accumulatedValues.put(YEAR, "2013");

    Document document = Jsoup.parse(sourceFile, "UTF-8");

    // Match Day Name
    // <li class="ds-schedule-days-current-date"><a
    // href="/events/2013/08/04/">Sunday, August 4</a></li>
    Elements matchedDayNameElements = document.select("li[class=ds-schedule-days-current-date]");
    for (Element matchedDayNameElement : matchedDayNameElements) {

      String dayNameText = matchedDayNameElement.text();
      String dayName = (dayNameText.split(","))[0];
      System.out.println("Found Day Name Element: [" + dayNameText + "] representing[" + dayName + "]");
      accumulatedValues.put(DAY_NAME, dayName);
    }

    // Match header
    Elements matchedHeaderElements = document.select("tr[class=ds-schedule-header]");
    for (Element result : matchedHeaderElements) {
      // System.out.println("Header Element: "+ result.toString());

      Integer stageNumberFromLeft = 0;
      Elements matchedStageElements = matchedHeaderElements.select("th[class*=ds-venue-column]");
      for (Element stageElement : matchedStageElements) {
        // System.out.println("Stage Element: "+
        // stageElement.toString());
        String stageName = stageElement.text();
        String stageIdentifier = stageElement.classNames().iterator().next();
        stageNumberFromLeft++;

        System.out.println("Found Stage Number[" + stageNumberFromLeft + "] Title[" + stageName + "] identifier["
            + stageIdentifier + "]");
        stageIdentifierToNameMap.put(stageIdentifier, stageName);

      }
    }

    // Match Sets
    Elements matchedStageSetsElements = document.select("td[class*=ds-stage]");
    for (Element matchedStageSetsElement : matchedStageSetsElements) {
      // System.out.println("Stage with Sets: "+
      // matchedStageSetsElement.toString());
      String stageName = findStageNameMatchingIdentifiers(matchedStageSetsElement.classNames(),
          stageIdentifierToNameMap);
      System.out.println("Collecting Sets for Stage: " + stageName);
      accumulatedValues.put(STAGE_ONE, stageName);
      accumulatedValues.put(STAGE_TWO, stageName);

      Elements matchedSetsElements = matchedStageSetsElement.select("div[class*=ds-event-box]");
      for (Element result : matchedSetsElements) {
        // System.out.println("Found item: " + result.toString());

        Elements titleSelections = result.select("strong[class=ds-event-title");
        for (Element titleSelectionElement : titleSelections) {
          String artistName = titleSelectionElement.text();
          System.out.println("Artist name?: " + artistName);
          accumulatedValues.put(ARTIST_NAME, artistName);
        }

        Elements timeSelections = result.select("span[class=ds-time-range]");
        for (Element timeSelectionElement : timeSelections) {
          String timeDescriptor = timeSelectionElement.text();
          String[] timeDescriptorFragments = timeDescriptor.split("-");
          String startTime = timeDescriptorFragments[0].trim().replace(":", "");
          String endTime = timeDescriptorFragments[1].trim().replace(":", "");
          Integer startTime24Hour = convertToTime24Hour(startTime, 900);
          System.out.println("Time?: " + timeDescriptor + " StartTime24Hour[" + startTime24Hour + "]");
          accumulatedValues.put(TIME_ONE, startTime24Hour.toString());
          accumulatedValues.put(TIME_TWO, startTime24Hour.toString());

          String outputStr = accumulatedValues.get("FEST_NAME") + "," + accumulatedValues.get("YEAR") + ","
              + accumulatedValues.get("DAY_NAME") + "," + accumulatedValues.get("TIME_ONE") + "," +
              // accumulatedValues.get("TIME_TWO") +","+
              accumulatedValues.get("STAGE_ONE") + "," +
              // accumulatedValues.get("STAGE_TWO") +","+
              accumulatedValues.get("ARTIST_NAME");

          System.out.println(outputStr);
          bufWriter.write(outputStr);
          bufWriter.write("\r\n");
          bufWriter.flush();

        }
      }

    }

    // System.out.println("Total Items Found: " +
    // matchedSetsElements.size());

    // Elements answerers = document.select("#answers .user-details a");
    // for (Element answerer : answerers) {
    // System.out.println("Answerer: " + answerer.text());
    // }
  }

  public static String findStageNameMatchingIdentifiers(Set<String> possibleIdentifiers,
      HashMap<String, String> identifiersToNamesMap) {
    Set<String> keySet = identifiersToNamesMap.keySet();
    for (String identifier : possibleIdentifiers) {
      if (keySet.contains(identifier)) {
        return identifiersToNamesMap.get(identifier);
      }
    }
    return null;
  }

  // Any time after latestTime12Hour will be assumed to be AM
  public static Integer convertToTime24Hour(String time12Hour, int latestTime12Hour) {
    Integer timeInteger = new Integer(time12Hour);

    if (timeInteger > latestTime12Hour) {
      return timeInteger;
    } else {
      return timeInteger + 1200;
    }
  }

}
