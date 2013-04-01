package com.ratethisfest.android.data;

import java.io.Serializable;
import java.util.StringTokenizer;

public class LoginData implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String DATA_LOGIN_INFO = "DATA_LOGIN_INFO";

  public long timeLoginIssued;
  public String loginType;
  public String accountIdentifier;
  public String emailAddress;
  public String accountToken;

  public String getTwitterToken() {
    StringTokenizer tokenizer = new StringTokenizer(accountToken, "|");
    return tokenizer.nextToken();
  }

  public String getTwitterSecret() {
    StringTokenizer tokenizer = new StringTokenizer(accountToken, "|");
    tokenizer.nextToken();
    return tokenizer.nextToken();
  }

  public void printDebug() {
    System.out.println("Login Type: " + loginType);
    System.out.println("Account Identifier: " + accountIdentifier);
    System.out.println("Email Address (May be unverified): " + emailAddress);
    int hoursAgo = ((int) (System.currentTimeMillis() - timeLoginIssued)) / 1000 / 60 / 60;
    System.out.println("Time Issued: " + hoursAgo + " hours ago at " + timeLoginIssued);
    System.out.println("Token Data: " + accountToken);
  }
}
