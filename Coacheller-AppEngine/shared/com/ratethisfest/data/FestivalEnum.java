package com.ratethisfest.data;

import java.util.EnumSet;

public enum FestivalEnum {
  COACHELLA("Coachella", 2, HttpConstants.readCommentgetCoachellerServerUrlReadComment(),
      HttpConstants.CLIENT_HOST_COACHELLER, "Coacheller"),
  /* */
  LOLLAPALOOZA("Lollapalooza", 1, HttpConstants.SERVER_URL_LOLLAPALOOZER, HttpConstants.CLIENT_HOST_LOLLAPALOOZER,
      "Lollapaloozer"),
  /* */
  TESTFEST("TestFest", 3, HttpConstants.SERVER_URL_TEST, HttpConstants.CLIENT_HOST_TESTFEST, "TestFestER");

  private final String value; // Fest Name
  private final String webClientHost; // Web hostname for
  private final String serverURL;
  private final String rtfAppName;
  private final Integer numberOfWeeks;
  private boolean testMessagePrinted = false; // not meant to be persistent

  private FestivalEnum(String festName, int numberOfWeeks, String URL, String webHost, String appName) {
    this.value = festName;
    this.numberOfWeeks = numberOfWeeks;
    this.serverURL = URL;
    this.webClientHost = webHost;
    this.rtfAppName = appName;
  }


  public static FestivalEnum fromValue(String value) {
    for (final FestivalEnum element : EnumSet.allOf(FestivalEnum.class)) {
      if (element.getName().equalsIgnoreCase(value)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + value + "'");
  }

  // Might be used in GWT and checked against any hostname, so allows failure by returning null
  public static FestivalEnum fromHostname(String hostname) {
    for (final FestivalEnum element : EnumSet.allOf(FestivalEnum.class)) {
      String hostNameLower = hostname.toLowerCase();
      String checkElementNameLower = element.getWebClientHostname().toLowerCase();
      if (hostNameLower.contains(checkElementNameLower)) {
        return element;
      }
    }
    return null;
    // throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + hostname + "'");
  }

  // Important to update this if fields change
  public boolean equals(FestivalEnum otherFest) {
    if (otherFest == null) {
      return false;
    }

    if (!this.value.equals(otherFest.value)) {
      return false;
    }

    // Not checking other values because they are all FINAL
    return true;
  }

  public String getName() {
    return this.value;
  }

  // Used by code I did not write, not going to delete
  // Fix this, rename all to getName?
  public String getValue() {
    return this.value;
  }

  public String getUrl() {
    return this.serverURL;
  }

  public String getWebClientHostname() {
    return this.webClientHost;
  }

  // public int getNumberOfWeeks() {
  // return numberOfWeeks;
  // }

  public String getRTFAppName() {
    return this.rtfAppName;
  }

  public void announceTestMessage() {
    if (!testMessagePrinted) {
      for (int i = 0; i < 5; i++) {
        System.out.println("TEST MODE - TEST MODE - TEST MODE - TEST MODE - TEST MODE - TEST MODE");
        // LogController.ERROR.logMessage("TEST MODE - TEST MODE - TEST MODE - TEST MODE - TEST MODE - TEST MODE");
      }
      this.testMessagePrinted = true;
    }

  }

  public int getNumberOfWeeks() {
    return this.numberOfWeeks;
  }

}
