package com.coacheller.server.persistence;

import java.util.List;
import java.util.logging.Logger;

import com.coacheller.server.domain.AppUser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;

/**
 * 
 * @author Amy
 * 
 */
public class AppUserDAO {
  private static final Logger log = Logger.getLogger(AppUserDAO.class.getName());

  private DAO dao;

  public AppUserDAO() {
    dao = new DAO();
  }

  /**
   * Find a {@link AppUser} by id.
   * 
   * @param id
   *          the {@link AppUser} id
   * @return the associated {@link AppUser}, or null if not found
   */
  public AppUser findAppUser(Long id) {
    if (id == null) {
      return null;
    }

    AppUser incident = dao.getObjectify().get(AppUser.class, id);
    return incident;
  }

  public AppUser findAppUserByKey(Key<AppUser> incidentKey) {
    if (incidentKey == null) {
      return null;
    }

    AppUser incident = dao.getObjectify().get(incidentKey);
    return incident;
  }

  public List<AppUser> findAllAppUsers() {
    Query<AppUser> q = dao.getObjectify().query(AppUser.class);
    return q.list();
  }

  public AppUser updateAppUser(AppUser incident) {
    dao.getObjectify().put(incident); // id populated in this statement
    System.out.println("Updated AppUser to datastore: " + incident.toString());
    return incident;
  }

  public void deleteAppUser(Long id) {
    System.out.println("Deleting AppUser from datastore: " + id);
    dao.getObjectify().delete(AppUser.class, id);
  }

  /**
   * TODO: verify that this works
   */
  public void deleteAllAppUsers() {
    System.out.println("Deleting all AppUsers from datastore: ");
    dao.getObjectify().delete(dao.getObjectify().query(AppUser.class).fetchKeys());
  }

  public int getAppUserCount() {
    return dao.getObjectify().query(AppUser.class).count();
  }
}