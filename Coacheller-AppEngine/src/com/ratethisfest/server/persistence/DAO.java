package com.ratethisfest.server.persistence;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.Set;

/**
 * Per http://code.google.com/p/objectify-appengine/wiki/BestPractices
 * 
 * @author Amy
 * 
 */
public class DAO extends DAOBase {

  static {
    ObjectifyService.register(AppUser.class);
    ObjectifyService.register(Rating.class);
    ObjectifyService.register(Set.class);
  }

  /**
   * afan: clearer naming convention
   * 
   * @return
   */
  public Objectify getObjectify() {
    return ofy();
  }

}