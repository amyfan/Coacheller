package com.coacheller.server.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.googlecode.objectify.Key;

@Entity
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // internal PK
  private Integer score;
  private Integer weekend;
  private Key<Set> set;
  private Key<AppUser> rater;

  public Rating() {
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

  public Key<Set> getSet() {
    return set;
  }

  public void setSet(Key<Set> set) {
    this.set = set;
  }

  public Key<AppUser> getRater() {
    return rater;
  }

  public void setRater(Key<AppUser> rater) {
    this.rater = rater;
  }

}
