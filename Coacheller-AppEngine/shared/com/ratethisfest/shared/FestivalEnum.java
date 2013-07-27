package com.ratethisfest.shared;

import java.util.EnumSet;

import com.ratethisfest.android.log.LogController;

public enum FestivalEnum {
  COACHELLA("Coachella", HttpConstants.readCommentgetCoachellerServerUrlReadComment()), LOLLAPALOOZA("Lollapalooza",
      HttpConstants.SERVER_URL_LOLLAPALOOZER), TESTFEST("TestFest", HttpConstants.SERVER_URL_TEST);

  private String value; // Fest Name
  private String serverURL;
  private boolean testMessagePrinted = false; // not meant to be persistent

  // private int numberOfWeeks;

  // Important to update this if fields change
  public boolean equals(FestivalEnum otherFest) {
    if (!this.value.equals(otherFest.value)) {
      return false;
    }

    if (!this.serverURL.equals(otherFest.serverURL)) {
      return false;
    }

    return true;
  }

  private FestivalEnum(String value, String URL) {
    this.value = value;
    this.serverURL = URL;
  }

  // private FestivalEnum(String value, int numberOfWeeks) {
  // this.value = value;
  // this.numberOfWeeks = numberOfWeeks;
  // }

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

  public void printTestMessage() {
    if (!testMessagePrinted) {
      for (int i = 0; i < 5; i++) {
        LogController.ERROR.logMessage("TEST MODE - TEST MODE - TEST MODE - TEST MODE - TEST MODE - TEST MODE");
      }
      this.testMessagePrinted = true;
    }

  }
}
