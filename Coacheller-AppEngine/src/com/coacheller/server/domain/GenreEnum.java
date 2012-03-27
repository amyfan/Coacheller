package com.coacheller.server.domain;

import java.util.EnumSet;

public enum GenreEnum {
  INDIE("Indie");

  private String code;

  private GenreEnum(String code) {
    this.code = code;
  }

  public String getName() {
    return code;
  }

  public static GenreEnum fromCode(String code) {
    for (final GenreEnum element : EnumSet.allOf(GenreEnum.class)) {
      if (element.getName().equals(code)) {
        return element;
      }
    }
    throw new IllegalArgumentException("Cannot be parsed into an enum element : '" + code + "'");
  }
}
