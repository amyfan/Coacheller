package com.coacheller.server.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Set {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // internal PK
  private String artistName;
  private Integer year;
  private String day;
  private Integer time;
  private Integer scoreSum; // stored for optimization purposes
  private Integer numRatings; // stored for optimization purposes
  private Double wkndOneAvgScore; // updated asynchronously for server quota purposes
  private Double wkndTwoAvgScore; // updated asynchronously for server quota purposes

  public Set() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getArtistName() {
    return artistName;
  }

  public void setArtistName(String artistName) {
    this.artistName = artistName;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public Integer getTime() {
    return time;
  }

  public void setTime(Integer time) {
    this.time = time;
  }

  public Integer getScoreSum() {
    return scoreSum;
  }

  public void setScoreSum(Integer scoreSum) {
    this.scoreSum = scoreSum;
  }

  public Integer getNumRatings() {
    return numRatings;
  }

  public void setNumRatings(Integer numRatings) {
    this.numRatings = numRatings;
  }

  public Double getWkndOneAvgScore() {
    return wkndOneAvgScore;
  }

  public void setWkndOneAvgScore(Double wkndOneAvgScore) {
    this.wkndOneAvgScore = wkndOneAvgScore;
  }

  public Double getWkndTwoAvgScore() {
    return wkndTwoAvgScore;
  }

  public void setWkndTwoAvgScore(Double wkndTwoAvgScore) {
    this.wkndTwoAvgScore = wkndTwoAvgScore;
  }

}
