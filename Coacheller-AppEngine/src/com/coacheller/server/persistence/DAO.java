package com.coacheller.server.persistence;

import com.coacheller.server.domain.Set;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

/**
 * Per http://code.google.com/p/objectify-appengine/wiki/BestPractices
 * 
 * @author Amy
 * 
 */
public class DAO extends DAOBase {

  static {
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