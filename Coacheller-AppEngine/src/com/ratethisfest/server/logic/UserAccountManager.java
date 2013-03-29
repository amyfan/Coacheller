package com.ratethisfest.server.logic;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.persistence.AppUserDAO;

/**
 * Contains some logic formerly located in DeviceInfo.java and
 * RegistrationInfo.java, as well as custom logic for managing our own AppUsers
 * 
 * @author Amy
 * 
 */
public class UserAccountManager {

  private static final Logger log = Logger.getLogger(UserAccountManager.class.getName());

  private AppUserDAO appUserDao;

  // Private constructor prevents instantiation from other classes
  private UserAccountManager() {
    appUserDao = new AppUserDAO();
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final UserAccountManager instance = new UserAccountManager();
  }

  /**
   * TODO: maybe remove singleton pattern if always referenced statically by
   * service class anyway
   * 
   * @return
   */
  public static UserAccountManager getInstance() {
    return SingletonHolder.instance;
  }

  // private String getAccountName() {
  // UserService userService = UserServiceFactory.getUserService();
  // User user = userService.getCurrentUser();
  // if (user == null) {
  // throw new RuntimeException("No one logged in");
  // }
  // return user.getEmail();
  // }

  /**
   * 
   * @param appUserKey
   * @return
   */
  public AppUser getAppUserByKey(Key<AppUser> appUserKey) {
    AppUser appUser = appUserDao.findAppUserByKey(appUserKey);
    return appUser;
  }

  /**
   * @param email
   * @return
   */
  public AppUser getAppUserByEmail(String email) {
    AppUser appUser = appUserDao.findAppUserByEmail(email);
    return appUser;
  }

  public Key<AppUser> getAppUserKeyById(Long id) {
    Key<AppUser> appUser = appUserDao.findAppUserKeyById(id);
    return appUser;
  }

  public Key<AppUser> getAppUserKeyByAuthId(String authId) {
    Key<AppUser> appUser = appUserDao.findAppUserKeyByAuthId(authId);
    return appUser;
  }

  public Key<AppUser> getAppUserKeyByEmail(String email) {
    Key<AppUser> appUser = appUserDao.findAppUserKeyByEmail(email);
    return appUser;
  }

  public AppUser createAppUser(String authType, String authId, String authToken, String email) {
    // TODO: authenticate user
    AppUser appUser = new AppUser();
    appUser.setAuthType(authType);
    appUser.setAuthId(authId);
    appUser.setAuthToken(authToken);
    appUser.setEmail(email);
    appUser.setActive(true);
    appUser.setDateCreated(new Date());
    appUser.setDateModified(new Date());
    // initializes ID
    return appUserDao.updateAppUser(appUser);
  }

  public AppUser findAppUser(Long id) {
    return appUserDao.findAppUser(id);
  }

  public AppUser updateAppUser(AppUser appUser) {
    appUser.setDateModified(new Date());
    return appUserDao.updateAppUser(appUser);
  }

  /**
   * This method assumed all parameters already authenticated
   * 
   * @param authType
   * @param authId
   * @param authToken
   * @param email
   * @return
   */
  public Key<AppUser> manageAppUser(String authType, String authId, String authToken, String email) {
    Key<AppUser> userKey = getAppUserKeyByAuthId(authId);
    if (userKey == null) {
      userKey = getAppUserKeyByEmail(email);
      if (userKey == null) {
        // create brand new user
        createAppUser(authType, authId, authToken, email);
        userKey = getAppUserKeyByAuthId(authId);
      } else {
        // link new login auth to existing user email sans auth info
        // TODO: this should only be implemented after email verification
        // implemented for twitter account users)
        AppUser user = getAppUserByKey(userKey);
        user.setAuthType(authType);
        user.setAuthId(authId);
        user.setAuthToken(authToken);
        updateAppUser(user);
        userKey = getAppUserKeyByAuthId(authId); // prolly not necessary
      }
    } else if (authToken != null && !authToken.isEmpty()) {
      // TODO: this should eventually be switched to "else" statement once
      // server side auth implemented
      // update verified token
      AppUser user = getAppUserByKey(userKey);
      if (!authToken.equals(user.getAuthToken())) {
        user.setAuthType(authType);
        user.setAuthId(authId);
        user.setAuthToken(authToken);
        updateAppUser(user);
      }
    }
    return userKey;
  }

  public void deleteAppUser(AppUser appUser) {
    appUserDao.deleteAppUser(appUser.getId());
  }

  public void deleteAllAppUsers() {
    appUserDao.deleteAllAppUsers();
  }

  public List<AppUser> findAllAppUsers() {
    return appUserDao.findAllAppUsers();
  }

}
