package com.ratethisfest.shared;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Cached;

@Entity
@Cached(expirationSeconds = 7200)
public class Set implements IsSerializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // internal PK
  private String festival;
  private String artistName;
  private Integer year;
  private String day;
  private Integer timeOne;
  private Integer timeTwo;
  private String stageOne;
  private String stageTwo;
  private Integer numRatingsOne; // stored for optimization purposes
  private Integer numRatingsTwo; // stored for optimization purposes
  private Integer scoreSumOne; // stored for optimization purposes
  private Integer scoreSumTwo; // stored for optimization purposes
  private Double avgScoreOne; // stored for optimization purposes
  private Double avgScoreTwo; // stored for optimization purposes
  private Date dateCreated;
  private Date dateModified;

  public Set() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFestival() {
    return festival;
  }

  public void setFestival(String festival) {
    this.festival = festival;
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

  public Integer getTimeOne() {
    return timeOne;
  }

  public void setTimeOne(Integer timeOne) {
    this.timeOne = timeOne;
  }

  public Integer getTimeTwo() {
    return timeTwo;
  }

  public void setTimeTwo(Integer timeTwo) {
    this.timeTwo = timeTwo;
  }

  public String getStageOne() {
    return stageOne;
  }

  public void setStageOne(String stageOne) {
    this.stageOne = stageOne;
  }

  public String getStageTwo() {
    return stageTwo;
  }

  public void setStageTwo(String stageTwo) {
    this.stageTwo = stageTwo;
  }

  public Integer getNumRatingsOne() {
    return numRatingsOne;
  }

  public void setNumRatingsOne(Integer numRatingsOne) {
    this.numRatingsOne = numRatingsOne;
  }

  public Integer getNumRatingsTwo() {
    return numRatingsTwo;
  }

  public void setNumRatingsTwo(Integer numRatingsTwo) {
    this.numRatingsTwo = numRatingsTwo;
  }

  public Integer getScoreSumOne() {
    return scoreSumOne;
  }

  public void setScoreSumOne(Integer scoreSumOne) {
    this.scoreSumOne = scoreSumOne;
  }

  public Integer getScoreSumTwo() {
    return scoreSumTwo;
  }

  public void setScoreSumTwo(Integer scoreSumTwo) {
    this.scoreSumTwo = scoreSumTwo;
  }

  public Double getAvgScoreOne() {
    return avgScoreOne;
  }

  public void setAvgScoreOne(Double avgScoreOne) {
    this.avgScoreOne = avgScoreOne;
  }

  public Double getAvgScoreTwo() {
    return avgScoreTwo;
  }

  public void setAvgScoreTwo(Double avgScoreTwo) {
    this.avgScoreTwo = avgScoreTwo;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Date getDateModified() {
    return dateModified;
  }

  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

}
