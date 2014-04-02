package com.lollapaloozer;

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
