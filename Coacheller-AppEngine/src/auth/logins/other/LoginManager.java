package auth.logins.other;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import auth.logins.data.AuthProviderAccount;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.persistence.AppUserDAO;

public class LoginManager {
  private static final Logger log = Logger.getLogger(new Object() {
  }.getClass().getEnclosingClass().getName());

  private static AppUserDAO appUserDao = new AppUserDAO();

  public static void authProviderLoginAccomplished(HttpSession session, LoginType loginType,
      AuthProviderAccount newAPAccountLogin) throws RTFAccountException {
    AppUser RTFAccountToLoginOrUpdate = null;
    AppUser currentSessionLogin = getCurrentLogin(session); // Get logged in RTF acct from session
    AppUser ownerOfAddedAPAccount = LoginManager.findRTFAccount(newAPAccountLogin); // See who owns AP account,
                                                                                    // may
    // be null

    if (currentSessionLogin == null) {// if it is not saved in the current session attribute
      log.info("No session is currently logged in");
      RTFAccountToLoginOrUpdate = ownerOfAddedAPAccount; // Assign AP account owner from datastore, might be null
      if (ownerOfAddedAPAccount == null) { // if it is not in datastore either
        log.info("No master account owning this APAccount was found");
        RTFAccountToLoginOrUpdate = new AppUser(); // Create new account
        RTFAccountToLoginOrUpdate.setName(newAPAccountLogin.getProperty(AuthProviderAccount.LOGIN_PERSON_NAME));
        RTFAccountToLoginOrUpdate.setDateCreated(new Date());
        RTFAccountToLoginOrUpdate.setDateModified(new Date());
        appUserDao.updateAppUser(RTFAccountToLoginOrUpdate);
      }
    } else {
      log.info("Session is already logged in");
      if (ownerOfAddedAPAccount != null && currentSessionLogin.getId().longValue() != ownerOfAddedAPAccount.getId().longValue()) {
        String newAPTypeName = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
        String newAPDescription = newAPAccountLogin.getDescription();

        log.info("Auth Provider Account ownership conflict: " + newAPTypeName + " account " + newAPDescription
            + " is already owned by RTF Account:" + ownerOfAddedAPAccount.getId()
            + " but user attempted to add it to RTF Account:" + currentSessionLogin.getId());
        RTFAccountException ex = new RTFAccountException(currentSessionLogin, ownerOfAddedAPAccount, newAPAccountLogin);
        throw ex; // Before anything gets modified
      }
      RTFAccountToLoginOrUpdate = currentSessionLogin;
    }

    long rtfAccountId = RTFAccountToLoginOrUpdate.getId();
    String apAccountProviderName = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    String apAccountID = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);

    AuthProviderAccount existingOwnedAuthProviderAccount = appUserDao.getAuthProviderAccount(RTFAccountToLoginOrUpdate,
        loginType);

    if (existingOwnedAuthProviderAccount != null) {
      // APAccount already registered with RTFAccount
      log.info("Current RTF Account[" + rtfAccountId + "] already owns APAccount type[" + apAccountProviderName
          + "] id[" + apAccountID + "]");
    } else {
      log.info("Current RTF Account[" + rtfAccountId + "] does not own APAccount type[" + apAccountProviderName
          + "] id[" + apAccountID + "]");
    }

    appUserDao.updateAPAccount(RTFAccountToLoginOrUpdate, newAPAccountLogin);

    // Must save to GAE session object modification, forces distributed session update
    log.info("Setting session attribute for RTF Account: " + RTFAccountToLoginOrUpdate.getId());
    session.setAttribute(AppUser.LOGIN_HTTPSESSION_ATTRIBUTE, RTFAccountToLoginOrUpdate);
  }

  // Find RTF Account owning this APAccount
  public static AppUser findRTFAccount(AuthProviderAccount apAccount) {
    // Get the Datastore Service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    String authProviderName = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    String authProviderID = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);

    // Prepare single filters
    Filter apNamefilter = new FilterPredicate(AuthProviderAccount.AUTH_PROVIDER_NAME, FilterOperator.EQUAL,
        authProviderName);
    Filter apIDFilter = new FilterPredicate(AuthProviderAccount.AUTH_PROVIDER_ID, FilterOperator.EQUAL, authProviderID);

    // Use CompositeFilter to combine filters
    Filter nameAndIDFilter = CompositeFilterOperator.and(apNamefilter, apIDFilter);

    // Use class Query to assemble a query
    Query q = new Query(AuthProviderAccount.DATASTORE_KIND).setFilter(nameAndIDFilter);

    // Use PreparedQuery interface to retrieve results
    PreparedQuery pq = datastore.prepare(q);

    log.info("Query Results for AuthProvider:" + authProviderName + " ID:" + authProviderID);
    String RTFAccountID = null;
    for (Entity result : pq.asIterable()) {
      StringBuilder resultBuilder = new StringBuilder();
      for (String propertyName : result.getProperties().keySet()) {
        Object propertyValue = result.getProperty(propertyName);
        String valueString;
        if (propertyValue != null) {
          valueString = propertyValue.toString();
        } else {
          valueString = "null";
        }
        resultBuilder.append(propertyName + "=" + valueString + " ");
        RTFAccountID = result.getProperty(AuthProviderAccount.RTFACCOUNT_OWNER_KEY).toString();
      }
      log.info(resultBuilder.toString());
    }

    log.info("Parent RTFAccount has ID:" + RTFAccountID);

    if (RTFAccountID == null) {
      return null;
    } else {
      return appUserDao.findAppUser(Long.valueOf(RTFAccountID));
    }
  }

  // May return NULL
  public static AppUser getCurrentLogin(HttpSession session) {
    return (AppUser) session.getAttribute(AppUser.LOGIN_HTTPSESSION_ATTRIBUTE);
  }

  public static boolean isSessionLoggedIn(HttpSession session) {
    AppUser currentRTFAccountLoggedIn = getCurrentLogin(session);
    if (currentRTFAccountLoggedIn == null) {
      return false;
    } else {
      return true;
    }
  }

  // Removes every session attribute starting with the login prefix
  public static void logOutUser(HttpSession session) {
    session.removeAttribute(AppUser.LOGIN_HTTPSESSION_ATTRIBUTE);
  }

  public static void destroyRTFAccount(HttpSession session) {
    AppUser currentRTFAccountLoggedIn = getCurrentLogin(session);
    if (currentRTFAccountLoggedIn == null) {
      log.info("No RTF account is logged in");
      return;
    }

    long rtfAccountId = currentRTFAccountLoggedIn.getId();
    log.info("Destroying RTF Account ID=" + rtfAccountId);

    // Deleted a bunch of crap just to query for this record

    // Query APAccounts for all APAccounts with Owner equal to RTF Account ID
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter ownerFilter = new FilterPredicate(AuthProviderAccount.RTFACCOUNT_OWNER_KEY, FilterOperator.EQUAL,
        currentRTFAccountLoggedIn.getId() + "");
    Query apQuery = new Query("AuthProviderAccount").setFilter(ownerFilter).setKeysOnly();
    PreparedQuery apAccountPQ = datastore.prepare(apQuery);
    HashSet<Key> hashSet = new HashSet<Key>();
    for (Entity result : apAccountPQ.asIterable()) {
      log.info("APAccount to be deleted, ID=" + result.getKey().getId());
      hashSet.add(result.getKey());
    }
    datastore.delete(hashSet);

    appUserDao.deleteAppUser(currentRTFAccountLoggedIn.getId());
  }

}
