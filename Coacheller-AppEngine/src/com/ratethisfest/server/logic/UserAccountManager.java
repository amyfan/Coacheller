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

  public Key<AppUser> getAppUserKeyByEmail(String email) {
    Key<AppUser> appUser = appUserDao.findAppUserKeyByEmail(email);
    return appUser;
  }

  @Deprecated
  public AppUser createAppUser(String email) {
    AppUser appUser = new AppUser();
    appUser.setEmail(email);
    appUser.setActive(true);
    appUser.setDateCreated(new Date());
    appUser.setDateModified(new Date());
    // initializes ID
    return appUserDao.updateAppUser(appUser);
  }

  public AppUser createAppUser(String authType, String authId, String email) {
    AppUser appUser = new AppUser();
    appUser.setAuthType(authType);
    appUser.setAuthId(authId);
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
