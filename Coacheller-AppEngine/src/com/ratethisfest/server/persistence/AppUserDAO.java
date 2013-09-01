package com.ratethisfest.server.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import auth.logins.data.AuthProviderAccount;
import auth.logins.other.LoginType;
import auth.logins.other.RTFConstants;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import com.ratethisfest.server.domain.AppUser;

/**
 * 
 * @author Amy
 * 
 */
public class AppUserDAO {
  public static final String DATASTORE_ANCESTOR_ID = "AccountAncestor";

  private static com.google.appengine.api.datastore.Key _accountAncestorKey; // From class MasterAccount, needed for
                                                                             // querying APAccounts without Objectivy

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

    Iterable<Key<AppUser>> q = dao.getObjectify().query(AppUser.class).filter("authId", authId).fetchKeys();
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

    Iterable<Key<AppUser>> q = dao.getObjectify().query(AppUser.class).filter("email", email).fetchKeys();
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

  public static AuthProviderAccount getAuthProviderAccount(AppUser masterAccount, LoginType loginType) {
    HashMap<String, AuthProviderAccount> loginTypeToAccountsHash = AuthProviderAccount
        .loadAPAccountsByParentID(masterAccount.getId() + "");
    return loginTypeToAccountsHash.get(loginType.getName());
  }

  public static Collection<AuthProviderAccount> getAuthProviderAccounts(AppUser masterAccount) {
    HashMap<String, AuthProviderAccount> loginTypeToAccountsHash = AuthProviderAccount
        .loadAPAccountsByParentID(masterAccount.getId() + "");
    return loginTypeToAccountsHash.values();
  }

  public void updateAPAccount(AppUser masterAccount, AuthProviderAccount apAccountObj_DO_NOT_PERSIST) {
    // TODO refactor this
    log.info("Creating or updating APAccount record");
    String authProviderName = apAccountObj_DO_NOT_PERSIST.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    LoginType loginType = LoginType.fromString(authProviderName);

    AuthProviderAccount apAccountObjToPersist = getAuthProviderAccount(masterAccount, loginType);

    if (apAccountObjToPersist == null) {
      // No such APAccount in datastore, the instance created from login should be persisted
      log.info("There was no existing APAccount for id: "
          + apAccountObj_DO_NOT_PERSIST.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID));
      apAccountObjToPersist = apAccountObj_DO_NOT_PERSIST;
    } else {
      // Use instance created from login only to update the APAccount in datastore
      log.info("APAccount object key from this successful login: "
          + apAccountObj_DO_NOT_PERSIST.getDatastoreKeyDescription());
      log.info("Older APAccount object key description: " + apAccountObjToPersist.getDatastoreKeyDescription());
      apAccountObj_DO_NOT_PERSIST.copyDataTo(apAccountObjToPersist); // New gets copied to old, then save the existing
                                                                     // (old) including updates

    } 
    // Forces save to db
    apAccountObjToPersist.setProperty(AuthProviderAccount.RTFACCOUNT_OWNER_KEY, masterAccount.getId() + "");

    log.info("Properties copied and parent ID set, here is info again:");
    log.info("APAccount object key from this successful login: "
        + apAccountObj_DO_NOT_PERSIST.getDatastoreKeyDescription());
    log.info("Older APAccount object key description: " + apAccountObjToPersist.getDatastoreKeyDescription());

    // TODO this structure for the data is being gotten rid of but maybe will have to be fixed
    // _accounts.put(authProviderName, apAccountObjToPersist); //SAVE THE ONE TO BE PERSISTED IN HASHTABLE
  }

  // From class MasterAccount, needed for querying APAccounts without Objectivy
  public static com.google.appengine.api.datastore.Key getAncestorKey() {
    if (_accountAncestorKey == null) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity rtfAccountAncestor = new Entity(RTFConstants.DATASTORE_KIND_ANCESTOR, DATASTORE_ANCESTOR_ID);
      rtfAccountAncestor.setProperty(RTFConstants.ANCESTOR_TYPE, DATASTORE_ANCESTOR_ID);
      _accountAncestorKey = datastore.put(rtfAccountAncestor);
      log.info("Ancestor key of all RTFAccount objects determined to be: " + _accountAncestorKey);
    }

    return _accountAncestorKey;
  }
}