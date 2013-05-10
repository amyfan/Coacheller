package com.ratethisfest.android.data;

import org.json.JSONArray;
import org.json.JSONException;

import com.ratethisfest.android.CalendarUtils;

public class FakeDataSource {

  public static JSONArray getData() throws JSONException {

    Integer currentTime24hr = CalendarUtils.currentTime24hr();
    int timePlusOneMin = currentTime24hr + 1;
    int timePlusTwoMins = currentTime24hr + 2;
    int timePlusThreeMins = currentTime24hr + 3;
    int timePlusFiveMins = currentTime24hr + 5;

    String data = "[{\"id\":18076,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":1,\"year\":2013,\"day\":\"Friday\",\"artist\":\"A FAKE DATASET\",\"time_two\":130},"
        + "{\"id\":18077,\"stage_two\":\"Sahara2\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Expecting 1\",\"time_one\":"
        + timePlusOneMin
        + ",\"year\":2013,\"day\":\"Friday\",\"artist\":\"Plus OneMin\",\"time_two\":"
        + timePlusOneMin
        + "},"
        + "{\"id\":18078,\"stage_two\":\"Sahara2\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Someone 1\",\"time_one\":"
        + timePlusTwoMins
        + ",\"year\":2013,\"day\":\"Friday\",\"artist\":\"Plus TwoMins A\",\"time_two\":"
        + timePlusTwoMins
        + "},"
        + "{\"id\":18079,\"stage_two\":\"Coachella2\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"-All The World-\",\"time_one\":"
        + timePlusThreeMins
        + ",\"year\":2013,\"day\":\"Friday\",\"artist\":\"PlusThree Mins\",\"time_two\":"
        + timePlusThreeMins
        + "},{\"id\":18080,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":1835,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Madeon\",\"time_two\":1835},"
        + "{\"id\":18081,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Else? 1\",\"time_one\":"
        + timePlusTwoMins
        + ",\"year\":2013,\"day\":\"Friday\",\"artist\":\"PlusTwo Mins B\",\"time_two\":"
        + timePlusTwoMins
        + "},{\"id\":18082,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":1400,\"year\":2013,\"day\":\"Friday\",\"artist\":\"The Dear Hunter\",\"time_two\":1400},"
        + "{\"id\":18083,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Git add -A\",\"time_one\":"
        + timePlusFiveMins
        + ",\"year\":2013,\"day\":\"Friday\",\"artist\":\"Plus Five Mins\",\"time_two\":"
        + timePlusFiveMins
        + "},{\"id\":19064,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":1120,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Mea\",\"time_two\":1120},"
        + "{\"id\":19065,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":1315,\"year\":2013,\"day\":\"Friday\",\"artist\":\"R3hab\",\"time_two\":1315},{\"id\":19066,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":1550,\"year\":2013,\"day\":\"Friday\",\"artist\":\"SebastiAn\",\"time_two\":1550},{\"id\":19067,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":1550,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Ximena Sarinana\",\"time_two\":1550},{\"id\":21069,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":2400,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Amon Tobin : ISAM Live\",\"time_two\":2400},{\"id\":21070,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":1515,\"year\":2013,\"day\":\"Friday\",\"artist\":\"EMA\",\"time_two\":1515},{\"id\":21071,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":2015,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Frank Ocean\",\"time_two\":2015},{\"id\":21072,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":2055,\"year\":2013,\"day\":\"Friday\",\"artist\":\"The Rapture\",\"time_two\":2055},{\"id\":21073,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":1900,\"year\":2013,\"day\":\"Friday\",\"artist\":\"WU LYF\",\"time_two\":1900},{\"id\":22055,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":1955,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Alesso\",\"time_two\":1955},{\"id\":22056,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":2130,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Atari Teenage Riot\",\"time_two\":2130},{\"id\":22057,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":1820,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Dawes\",\"time_two\":1820},{\"id\":22058,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":1435,\"year\":2013,\"day\":\"Friday\",\"artist\":\"GIVERS\",\"time_two\":1435},{\"id\":22059,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":1710,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Jimmy Cliff with Tim Armstrong and the Engine Band\",\"time_two\":1710},{\"id\":22060,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":2320,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Refused\",\"time_two\":2320},{\"id\":22061,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":2400,\"year\":2013,\"day\":\"Friday\",\"artist\":\"The Horrors\",\"time_two\":2400},{\"id\":22062,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":1300,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Wolf Gang\",\"time_two\":1300},{\"id\":23069,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":1430,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Breakbot\",\"time_two\":1430},{\"id\":23070,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":1705,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Grouplove\",\"time_two\":1705},{\"id\":23071,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":1935,\"year\":2013,\"day\":\"Friday\",\"artist\":\"M. Ward\",\"time_two\":1935},{\"id\":23072,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":1900,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Madness\",\"time_two\":1900},{\"id\":23073,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":1625,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Neon Indian\",\"time_two\":1625},{\"id\":23074,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":2330,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Swedish House Mafia\",\"time_two\":2330},{\"id\":24070,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":2135,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Afrojack\",\"time_two\":2135},{\"id\":24071,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":1745,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Death Grips\",\"time_two\":1745},{\"id\":24072,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":1320,\"year\":2013,\"day\":\"Friday\",\"artist\":\"honeyhoney\",\"time_two\":1320},{\"id\":24073,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":2050,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Mazzy Star\",\"time_two\":2050},{\"id\":24074,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":1950,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Pulp\",\"time_two\":1950},{\"id\":24075,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":2250,\"year\":2013,\"day\":\"Friday\",\"artist\":\"The Black Angels\",\"time_two\":2250},{\"id\":24076,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":1515,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Yuck\",\"time_two\":1515},{\"id\":25063,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":1200,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Abe Vigoda\",\"time_two\":1200},{\"id\":25064,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":2205,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Explosions in the Sky\",\"time_two\":2205},{\"id\":25065,\"stage_two\":\"Gobi\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Gobi\",\"time_one\":1630,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Gary Clark Jr.\",\"time_two\":1630},{\"id\":25066,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":1330,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Hello Seahorse!\",\"time_two\":1330},{\"id\":25067,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":1440,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Kendrick Lamar\",\"time_two\":1440},{\"id\":32018,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":1300,\"year\":2013,\"day\":\"Friday\",\"artist\":\"Gabe Real\",\"time_two\":1300},{\"id\":32019,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":1740,\"year\":2013,\"day\":\"Friday\",\"artist\":\"GIRLS\",\"time_two\":1740},{\"id\":32020,\"stage_two\":\"Sahara\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Sahara\",\"time_one\":1215,\"year\":2013,\"day\":\"Friday\",\"artist\":\"LA Riots\",\"time_two\":1215},{\"id\":32021,\"stage_two\":\"Mojave\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Mojave\",\"time_one\":2215,\"year\":2013,\"day\":\"Friday\",\"artist\":\"M83\",\"time_two\":2215},{\"id\":32022,\"stage_two\":\"Coachella\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Coachella\",\"time_one\":2145,\"year\":2013,\"day\":\"Friday\",\"artist\":\"The Black Keys\",\"time_two\":2145},{\"id\":32023,\"stage_two\":\"Outdoor\",\"avg_score_one\":0,\"avg_score_two\":0,\"stage_one\":\"Outdoor\",\"time_one\":1300,\"year\":2013,\"day\":\"Friday\",\"artist\":\"The Sheepdogs\",\"time_two\":1300}]";

    JSONArray returnArray = new JSONArray(data);
    return returnArray;
  }

}
