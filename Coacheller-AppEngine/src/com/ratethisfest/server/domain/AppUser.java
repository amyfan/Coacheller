package com.ratethisfest.server.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;

@Entity
@Cached(expirationSeconds = 7200)
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // internal PK
  private String email; // populate automatically from fb/goog authId, ask
                        // separately for twitter users
  private Boolean active;
  private String name; // alias/handle
  private String authType; // facebook, google, or twitter
  private String authId; // unique id for the auth account; if fb/goog, then
                         // looks like email; otherwise, #
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

  public String getAuthType() {
    return authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public String getAuthId() {
    return authId;
  }

  public void setAuthId(String authId) {
    this.authId = authId;
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