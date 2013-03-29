package com.ratethisfest.auth.verify;

public interface AuthVerifier {

  public boolean verify(String authToken, String identifier);

  public void simulateFailure(int failures);
}
