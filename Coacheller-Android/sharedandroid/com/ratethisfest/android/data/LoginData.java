package com.ratethisfest.android.data;

import java.io.Serializable;
import java.util.StringTokenizer;

import com.ratethisfest.android.log.LogController;

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
    
    LogController.MODEL.logMessage("Login Type: " + loginType);
    LogController.MODEL.logMessage("Account Identifier: " + accountIdentifier);
    LogController.MODEL.logMessage("Email Address (May be unverified): " + emailAddress);
    int hoursAgo = ((int) (System.currentTimeMillis() - timeLoginIssued)) / 1000 / 60 / 60;
    LogController.MODEL.logMessage("Time Issued: " + hoursAgo + " hours ago at " + timeLoginIssued);
    LogController.MODEL.logMessage("Token Data: " + accountToken);
  }
}
