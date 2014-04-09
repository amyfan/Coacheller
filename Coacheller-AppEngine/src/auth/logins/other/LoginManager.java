package auth.logins.other;

import java.util.HashSet;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import auth.logins.data.AuthProviderAccount;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.logic.UserAccountManager;
import com.ratethisfest.server.persistence.AppUserDAO;
import com.ratethisfest.shared.LoginType;

public class LoginManager {
  private static final Logger log = Logger.getLogger(new Object() {
  }.getClass().getEnclosingClass().getName());

  private static AppUserDAO appUserDao = new AppUserDAO();

  public static void authProviderLoginAccomplished(HttpSession session, LoginType loginType,
      AuthProviderAccount newAPAccountLogin) throws RTFAccountException {
    AppUser appUserToLoginOrUpdate = null;
    AppUser currentSessionLogin = getCurrentLogin(session); // Get logged in RTF acct from session
    AppUser ownerOfAddedAPAccount = LoginManager.findAppUserAccount(newAPAccountLogin); // See who owns AP account,
    // may
    // be null

    if (currentSessionLogin == null) {
      // if it is not saved in the current session attribute
      log.info("No session is currently logged in");
      // Assign AP account owner from datastore, should not be null
      appUserToLoginOrUpdate = ownerOfAddedAPAccount;
    } else {
      log.info("Session is already logged in");
      if (ownerOfAddedAPAccount != null
          && currentSessionLogin.getId().longValue() != ownerOfAddedAPAccount.getId().longValue()) {
        String newAPTypeName = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
        String newAPDescription = newAPAccountLogin.getDescription();

        log.info("Auth Provider Account ownership conflict: " + newAPTypeName + " account " + newAPDescription
            + " is already owned by RTF Account:" + ownerOfAddedAPAccount.getId()
            + " but user attempted to add it to RTF Account:" + currentSessionLogin.getId());
        RTFAccountException ex = new RTFAccountException(currentSessionLogin, ownerOfAddedAPAccount, newAPAccountLogin);
        throw ex; // Before anything gets modified
      }
      appUserToLoginOrUpdate = currentSessionLogin;
    }

    long rtfAccountId = appUserToLoginOrUpdate.getId();
    String apAccountProviderName = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    String apAccountID = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);

    AuthProviderAccount existingOwnedAuthProviderAccount = appUserDao.getAuthProviderAccount(appUserToLoginOrUpdate,
        loginType);

    if (existingOwnedAuthProviderAccount != null) {
      // APAccount already registered with RTFAccount
      log.info("Current RTF Account[" + rtfAccountId + "] already owns APAccount type[" + apAccountProviderName
          + "] id[" + apAccountID + "]");
    } else {
      log.info("Current RTF Account[" + rtfAccountId + "] does not own APAccount type[" + apAccountProviderName
          + "] id[" + apAccountID + "]");
    }

    appUserDao.updateAPAccount(appUserToLoginOrUpdate, newAPAccountLogin);

    // Must save to GAE session object modification, forces distributed session update
    log.info("Setting session attribute for RTF Account: " + appUserToLoginOrUpdate.getId());
    session.setAttribute(AppUser.LOGIN_HTTPSESSION_ATTRIBUTE, appUserToLoginOrUpdate);
  }

  // Find RTF Account owning this APAccount
  public static AppUser findAppUserAccount(AuthProviderAccount apAccount) {
    // Get the Datastore Service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    String authProviderName = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    String authProviderID = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);
    String email = apAccount.getProperty(AuthProviderAccount.LOGIN_EMAIL);

    // TODO: stop storing auth tokens in db?
    String authToken = null;

    com.googlecode.objectify.Key<AppUser> appUserKey = UserAccountManager.getInstance().manageAppUser(authProviderName,
        authProviderID, authToken, email);
    return UserAccountManager.getInstance().getAppUserByKey(appUserKey);

    // AF: SO not necessary thanks to my existing UserAccountManager.manageAppUser() logic
    // AF: Also, the following code was failing because it was too specific & AUTH_PROVIDER_NAME format was NOT matching
    // w/ LOGIN_TYPE formats used everywhere else in the system

    // // Prepare single filters
    // Filter apNamefilter = new FilterPredicate(AuthProviderAccount.AUTH_PROVIDER_NAME, FilterOperator.EQUAL,
    // authProviderName);
    // Filter apIDFilter = new FilterPredicate(AuthProviderAccount.AUTH_PROVIDER_ID, FilterOperator.EQUAL,
    // authProviderID);
    //
    // // Use CompositeFilter to combine filters
    // Filter nameAndIDFilter = CompositeFilterOperator.and(apNamefilter, apIDFilter);
    //
    // // Use class Query to assemble a query
    // Query q = new Query(AuthProviderAccount.DATASTORE_KIND).setFilter(nameAndIDFilter);
    //
    // // Use PreparedQuery interface to retrieve results
    // PreparedQuery pq = datastore.prepare(q);
    //
    // log.info("Query Results for AuthProvider:" + authProviderName + " ID:" + authProviderID);
    // String RTFAccountID = null;
    // for (Entity result : pq.asIterable()) {
    // StringBuilder resultBuilder = new StringBuilder();
    // for (String propertyName : result.getProperties().keySet()) {
    // Object propertyValue = result.getProperty(propertyName);
    // String valueString;
    // if (propertyValue != null) {
    // valueString = propertyValue.toString();
    // } else {
    // valueString = "null";
    // }
    // resultBuilder.append(propertyName + "=" + valueString + " ");
    // RTFAccountID = result.getProperty(AuthProviderAccount.APPUSER_KEY).toString();
    // }
    // log.info(resultBuilder.toString());
    // }
    //
    // log.info("Parent RTFAccount has ID:" + RTFAccountID);
    //
    // if (RTFAccountID == null) {
    // return null;
    // } else {
    // return appUserDao.findAppUser(Long.valueOf(RTFAccountID));
    // }
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
    Filter ownerFilter = new FilterPredicate(AuthProviderAccount.APPUSER_KEY, FilterOperator.EQUAL,
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
