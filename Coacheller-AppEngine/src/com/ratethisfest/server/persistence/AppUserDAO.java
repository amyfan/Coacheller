package com.ratethisfest.server.persistence;

import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import com.ratethisfest.server.domain.AppUser;

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

  public AppUser findAppUserByKey(Key<AppUser> key) {
    if (key == null) {
      return null;
    }

    AppUser incident = dao.getObjectify().get(key);
    return incident;
  }

  public AppUser findAppUserByEmail(String email) {
    if (email == null) {
      return null;
    }

    Query<AppUser> q = dao.getObjectify().query(AppUser.class).filter("email", email);
    if (q.list().size() > 0) {
      return q.list().get(0);
    } else {
      return null;
    }
  }

  public Key<AppUser> findAppUserKeyById(Long id) {
    if (id == null) {
      return null;
    }

    Iterable<Key<AppUser>> q = dao.getObjectify().query(AppUser.class).filter("id", id).fetchKeys();
    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public Key<AppUser> findAppUserKeyByAuthId(String authId) {
    if (authId == null) {
      return null;
    }

    Iterable<Key<AppUser>> q = dao.getObjectify().query(AppUser.class).filter("authId", authId)
        .fetchKeys();
    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public Key<AppUser> findAppUserKeyByEmail(String email) {
    if (email == null) {
      return null;
    }

    Iterable<Key<AppUser>> q = dao.getObjectify().query(AppUser.class).filter("email", email)
        .fetchKeys();
    if (q.iterator().hasNext()) {
      return q.iterator().next();
    } else {
      return null;
    }
  }

  public List<AppUser> findAllAppUsers() {
    Query<AppUser> q = dao.getObjectify().query(AppUser.class);
    return q.list();
  }

  public AppUser updateAppUser(AppUser user) {
    dao.getObjectify().put(user); // id populated in this statement
    System.out.println("Updated AppUser to datastore: " + user.toString());
    return user;
  }

  public void deleteAppUser(Long id) {
    System.out.println("Deleting AppUser from datastore: " + id);
    dao.getObjectify().delete(AppUser.class, id);
  }

  public void deleteAllAppUsers() {
    System.out.println("Deleting all AppUsers from datastore: ");
    dao.getObjectify().delete(dao.getObjectify().query(AppUser.class).fetchKeys());
  }

  public int getAppUserCount() {
    return dao.getObjectify().query(AppUser.class).count();
  }
}