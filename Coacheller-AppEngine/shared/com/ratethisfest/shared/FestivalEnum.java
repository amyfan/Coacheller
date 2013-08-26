package com.ratethisfest.shared;

import java.util.EnumSet;

public enum FestivalEnum {
  COACHELLA("Coachella", HttpConstants.readCommentgetCoachellerServerUrlReadComment(),
      HttpConstants.CLIENT_HOST_COACHELLER, "Coacheller"),
  /* */
  LOLLAPALOOZA("Lollapalooza", HttpConstants.SERVER_URL_LOLLAPALOOZER, HttpConstants.CLIENT_HOST_LOLLAPALOOZER, "Lollapaloozer"),
  /* */
  TESTFEST("TestFest", HttpConstants.SERVER_URL_TEST, HttpConstants.CLIENT_HOST_TESTFEST, "TestFestER");

  private final String value; // Fest Name
  private final String webClientHost; // Web hostname for
  private final String serverURL;
  private final String rtfAppName;
  private boolean testMessagePrinted = false; // not meant to be persistent

  private FestivalEnum(String festName, String URL, String webHost, String appName) {
    this.value = festName;
    this.serverURL = URL;
    this.webClientHost = webHost;
    this.rtfAppName = appName;
  }

  // private int numberOfWeeks;

  // public int getNumberOfWeeks() {
  // return numberOfWeeks;
  // }

  public static FestivalEnum fromValue(String value) {
    for (final FestivalEnum element : EnumSet.allOf(FestivalEnum.class)) {
      if (element.getName().equalsIgnoreCase(value)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + value + "'");
  }

  //Might be used in GWT and checked against any hostname, so allows failure by returning null
  public static FestivalEnum fromHostname(String hostname) {
    for (final FestivalEnum element : EnumSet.allOf(FestivalEnum.class)) {
      String hostNameLower = hostname.toLowerCase();
      String checkElementNameLower = element.getWebClientHostname().toLowerCase();
      if (hostNameLower.contains(checkElementNameLower)) {
        return element;
      }
    }
    return null;
    //throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + hostname + "'");
  }

  // private FestivalEnum(String value, int numberOfWeeks) {
  // this.value = value;
  // this.numberOfWeeks = numberOfWeeks;
  // }

  // private int numberOfWeeks;

  // Important to update this if fields change
  public boolean equals(FestivalEnum otherFest) {
    if (otherFest == null) {
      return false;
    }
    
    if (!this.value.equals(otherFest.value)) {
      return false;
    }
    
    //Not checking other values because they are all FINAL
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

}
