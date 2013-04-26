package com.ratethisfest.shared;

import java.util.EnumSet;

public enum FestivalEnum {
  COACHELLA("Coachella",2), LOLLAPALOOZA("Lollapalooza",1);

  private String value;
  private int numberOfWeeks;

  private FestivalEnum(String value, int numberOfWeeks) {
    this.value = value;
    this.numberOfWeeks = numberOfWeeks;
  }

  
  public String getName() {
    return value;
  }
  //Fix this, rename all to getName
  public String getValue() {
    return value;
  }
  
  public int getNumberOfWeeks() {
    return numberOfWeeks;
  }

  public static FestivalEnum fromValue(String value) {
    for (final FestivalEnum element : EnumSet.allOf(FestivalEnum.class)) {
      if (element.getName().equalsIgnoreCase(value)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + value + "'");
  }
}
