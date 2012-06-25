package com.lollapaloozer;

import java.io.Serializable;
import java.util.StringTokenizer;

public class LoginData implements Serializable {

  public long timeLoginIssued;
  public int loginType;
  public String accountIdentifier;
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
}
