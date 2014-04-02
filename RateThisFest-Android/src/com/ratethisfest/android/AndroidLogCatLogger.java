package com.ratethisfest.android;

import android.util.Log;

import com.ratethisfest.android.log.LogInterface;

public class AndroidLogCatLogger implements LogInterface {

  private String _projectName;

  public AndroidLogCatLogger(String projectName) {
    _projectName = projectName;
  }

  @Override
  public void logStandard(String message) {
    // TODO Auto-generated method stub
    Log.i(_projectName, message);
  }

  @Override
  public void logError(String message) {
    // TODO Auto-generated method stub
    Log.e(_projectName, message);
  }

}
