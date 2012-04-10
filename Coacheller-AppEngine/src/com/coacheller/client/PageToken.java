package com.coacheller.client;

import java.util.EnumSet;

public enum PageToken {

  INDEX("index"), VIEW("view"), RATE("rate"), EMAIL("email");

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
