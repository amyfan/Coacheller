package com.coacheller.client;

import java.util.EnumSet;

public enum PageToken {

  INDEX("index"), LIST("list"), CHART("chart");

  private String value;

  private PageToken(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static PageToken fromValue(String value) {
    for (final PageToken element : EnumSet.allOf(PageToken.class)) {
      if (element.getValue().equals(value)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + value + "'");
  }
}
