package com.coacheller;

import com.google.gson.annotations.SerializedName;

public class Set {
  @SerializedName("id")
  private String id;
  @SerializedName("artistName")
  private String artistName;
  @SerializedName("year")
  private String year;
  @SerializedName("day")
  private String day;
  @SerializedName("time")
  private String time;
  @SerializedName("scoreSum")
  private String scoreSum;
  @SerializedName("numRatings")
  private String numRatings;
  @SerializedName("wkndOneAvgScore")
  private String wkndOneAvgScore;
  @SerializedName("wkndTwoAvgScore")
  private String wkndTwoAvgScore;
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getArtistName() {
    return artistName;
  }
  public void setArtistName(String artistName) {
    this.artistName = artistName;
  }
  public String getYear() {
    return year;
  }
  public void setYear(String year) {
    this.year = year;
  }
  public String getDay() {
    return day;
  }
  public void setDay(String day) {
    this.day = day;
  }
  public String getTime() {
    return time;
  }
  public void setTime(String time) {
    this.time = time;
  }
  public String getScoreSum() {
    return scoreSum;
  }
  public void setScoreSum(String scoreSum) {
    this.scoreSum = scoreSum;
  }
  public String getNumRatings() {
    return numRatings;
  }
  public void setNumRatings(String numRatings) {
    this.numRatings = numRatings;
  }
  public String getWkndOneAvgScore() {
    return wkndOneAvgScore;
  }
  public void setWkndOneAvgScore(String wkndOneAvgScore) {
    this.wkndOneAvgScore = wkndOneAvgScore;
  }
  public String getWkndTwoAvgScore() {
    return wkndTwoAvgScore;
  }
  public void setWkndTwoAvgScore(String wkndTwoAvgScore) {
    this.wkndTwoAvgScore = wkndTwoAvgScore;
  }

}
