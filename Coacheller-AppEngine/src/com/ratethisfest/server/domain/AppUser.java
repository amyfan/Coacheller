package com.ratethisfest.server.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import auth.logins.data.AuthProviderAccount;

import com.googlecode.objectify.annotation.Cached;
import com.ratethisfest.shared.LoginType;

@Entity
@Cached
public class AppUser implements Serializable {
  //Implementing Serializable allows this object to be saved as a property of the servlet Session (HttpSession)
  //Hopefully this doesn't break anything with Objectify...

  
  @Transient
  public static final String LOGIN_HTTPSESSION_ATTRIBUTE = "LOGIN_HTTPSESSION_ATTRIBUTE";
  //Decided not to use this
  //@Transient
  //private HashMap<String, AuthProviderAccount> _accounts = new HashMap<String, AuthProviderAccount>();

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
  private String authToken; // storing verified token to save on performance
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

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
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
    builder.append(", dateModified=");
    builder.append(dateModified);
    builder.append(", id=");
    builder.append(id);
    builder.append(", email=");
    builder.append(email);
    builder.append(", active=");
    builder.append(active);
    builder.append(", name=");
    builder.append(name);
    builder.append(", authType=");
    builder.append(authType);
    builder.append(", authId=");
    builder.append(authId);
    builder.append(", authToken=");
    builder.append(authToken);
    builder.append("]");
    return builder.toString();
  }
  

}