package com.ratethisfest.shared;

import java.util.EnumSet;

public enum FestivalEnum {
  COACHELLA("Coachella"), LOLLAPALOOZA("Lollapalooza");

  private String value;

  private FestivalEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static FestivalEnum fromValue(String value) {
    for (final FestivalEnum element : EnumSet.allOf(FestivalEnum.class)) {
      if (element.getValue().equalsIgnoreCase(value)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + value + "'");
  }
}
