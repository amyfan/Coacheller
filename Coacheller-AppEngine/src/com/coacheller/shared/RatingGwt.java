package com.coacheller.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RatingGwt implements IsSerializable {

  private Long id; // internal PK
  private Integer score;
  private Integer weekend;
  private String notes;
  private Long setId;
  private Long raterId;
  private String artistName;

  public RatingGwt() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public Integer getWeekend() {
    return weekend;
  }

  public void setWeekend(Integer weekend) {
    this.weekend = weekend;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Long getSetId() {
    return setId;
  }

  public void setSetId(Long setId) {
    this.setId = setId;
  }

  public Long getRaterId() {
    return raterId;
  }

  public void setRaterId(Long raterId) {
    this.raterId = raterId;
  }

  public String getArtistName() {
    return artistName;
  }

  public void setArtistName(String artistName) {
    this.artistName = artistName;
  }

}
