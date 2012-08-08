package com.ratethisfest.client;

import java.util.EnumSet;

public enum PageToken {

  INDEX("index"),
  FESTIVAL_INDEX("festival_index"),
  VIEW("view"),
  RATE("rate"),
  EMAIL("email"),
  LOGIN("login"),
  LOGIN_FACEBOOK("login_facebook"),
  LOGIN_GOOGLE("login_google"),
  LOGIN_TWITTER("login_twitter");

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
