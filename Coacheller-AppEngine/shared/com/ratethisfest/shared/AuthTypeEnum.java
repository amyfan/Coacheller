package com.ratethisfest.shared;

import java.util.EnumSet;

public enum AuthTypeEnum {
  FACEBOOK("Facebook"), GOOGLE("Google"), TWITTER("Twitter");

  private String value;

  private AuthTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static DayEnum fromValue(String value) {
    for (final DayEnum element : EnumSet.allOf(DayEnum.class)) {
      if (element.getValue().equals(value)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + value + "'");
  }
}
