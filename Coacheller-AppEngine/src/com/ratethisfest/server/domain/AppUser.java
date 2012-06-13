package com.ratethisfest.server.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // internal PK
  private String email;
  private Boolean active;
  private String name;
  private Date dateCreated;
  private Date dateModified;

  public AppUser() {
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public Date getDateModified() {
    return dateModified;
  }

  public String getEmail() {
    return this.email;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Boolean isActive() {
    return active;
  }

  public void setActive(Boolean done) {
    this.active = done;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AppUser [dateCreated=");
    builder.append(dateCreated);
    builder.append(", active=");
    builder.append(active);
    builder.append(", name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }
}