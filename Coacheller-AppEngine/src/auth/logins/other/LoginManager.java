package auth.logins.other;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import auth.logins.data.AuthProviderAccount;
import auth.logins.data.MasterAccount;

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

public class LoginManager {
  private static final Logger log = Logger.getLogger(new Object() {
  }.getClass().getEnclosingClass().getName());

  public static void authProviderLoginAccomplished(HttpSession session, LoginType loginType,
      AuthProviderAccount newAPAccountLogin) throws RTFAccountException {
    MasterAccount RTFAccountToLoginOrUpdate = null;
    MasterAccount currentSessionLogin = getCurrentLogin(session); // Get logged in RTF acct from session
    MasterAccount ownerOfAddedAPAccount = LoginManager.findRTFAccount(newAPAccountLogin); // See who owns AP account, may
                                                                                       // be null

    if (currentSessionLogin == null) {// if it is not saved in the current session attribute
      RTFAccountToLoginOrUpdate = ownerOfAddedAPAccount; // Assign AP account owner from datastore, might be null
      if (ownerOfAddedAPAccount == null) { // if it is not in datastore either
        RTFAccountToLoginOrUpdate = MasterAccount.createNewRTFAccount(); // Create a new one for the brand new user
        String personName = newAPAccountLogin.getProperty(AuthProviderAccount.LOGIN_PERSON_NAME);
        RTFAccountToLoginOrUpdate.setProperty(MasterAccount.PROPERTY_PERSON_NAME, personName);
      }
    } else {
      // Session is already logged in
      if (ownerOfAddedAPAccount != null && currentSessionLogin.getAppEngineKeyLong() != ownerOfAddedAPAccount.getAppEngineKeyLong()) {
        String newAPTypeName = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
        String newAPDescription = newAPAccountLogin.getDescription();

        log.info("Auth Provider Account ownership conflict: " + newAPTypeName + " account " + newAPDescription
            + " is already owned by RTF Account:" + ownerOfAddedAPAccount.getAppEngineKeyLong()
            + " but user attempted to add it to RTF Account:" + currentSessionLogin.getAppEngineKeyLong());
        RTFAccountException ex = new RTFAccountException(currentSessionLogin, ownerOfAddedAPAccount, newAPAccountLogin);
        throw ex;  //Before anything gets modified
      }
      RTFAccountToLoginOrUpdate = currentSessionLogin;
    }

    long rtfAccountId = RTFAccountToLoginOrUpdate.getAppEngineKeyLong();
    String apAccountProviderName = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    String apAccountID = newAPAccountLogin.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);

    if (RTFAccountToLoginOrUpdate.isLoggedInAPType(loginType)) {
      // APAccount already registered with RTFAccount
      // Welcome back!!!!!

      log.info("Current RTF Account[" + rtfAccountId + "] already owns APAccount type[" + apAccountProviderName
          + "] id[" + apAccountID + "]");
      RTFAccountToLoginOrUpdate.updateAPAccount(newAPAccountLogin);
    } else {
      log.info("Current RTF Account[" + rtfAccountId + "] does not own APAccount type[" + apAccountProviderName
          + "] id[" + apAccountID + "], adding AP Account");
      RTFAccountToLoginOrUpdate.addAPAccount(newAPAccountLogin);
    }

    // Must save to GAE after // object modification
    session.setAttribute(MasterAccount.LOGIN_HTTPSESSION_ATTRIBUTE, RTFAccountToLoginOrUpdate);
  }

  // Find RTF Account owning this APAccount
  public static MasterAccount findRTFAccount(AuthProviderAccount apAccount) {
    // Get the Datastore Service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    String authProviderName = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    String authProviderID = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);

    Filter nameFilter = new FilterPredicate(AuthProviderAccount.AUTH_PROVIDER_NAME, FilterOperator.EQUAL,
        authProviderName);
    Filter apIDFilter = new FilterPredicate(AuthProviderAccount.AUTH_PROVIDER_ID, FilterOperator.EQUAL, authProviderID);

    // Use CompositeFilter to combine multiple filters
    Filter nameAndIDFilter = CompositeFilterOperator.and(nameFilter, apIDFilter);

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

    return MasterAccount.fromID(RTFAccountID);
  }

  // May return NULL
  public static MasterAccount getCurrentLogin(HttpSession session) {
    return (MasterAccount) session.getAttribute(MasterAccount.LOGIN_HTTPSESSION_ATTRIBUTE);
  }

  public static boolean isSessionLoggedIn(HttpSession session) {
    MasterAccount currentRTFAccountLoggedIn = getCurrentLogin(session);
    if (currentRTFAccountLoggedIn == null) {
      return false;
    } else {
      return true;
    }
  }

  // Removes every session attribute starting with the login prefix
  public static void logOutUser(HttpSession session) {
    session.removeAttribute(MasterAccount.LOGIN_HTTPSESSION_ATTRIBUTE);
  }

  public static void destroyRTFAccount(HttpSession session) {
    MasterAccount currentRTFAccountLoggedIn = getCurrentLogin(session);
    if (currentRTFAccountLoggedIn == null) {
      log.info("No RTF account is logged in");
      return;
    }

    long rtfAccountId = currentRTFAccountLoggedIn.getAppEngineKeyLong();
    log.info("Destroying RTF Account ID=" + rtfAccountId);

    // Query RTFAccounts for all id's matching account ID
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key targetKey = KeyFactory.createKey(MasterAccount.getAncestorKey(), MasterAccount.DATASTORE_KIND, rtfAccountId);
    Filter RTFIdFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, targetKey);

    Query rtfQuery = new Query("RTFAccount").setFilter(RTFIdFilter);// .setKeysOnly()

    PreparedQuery pq = datastore.prepare(rtfQuery);

    for (Entity result : pq.asIterable()) {
      log.info("FOUND RTFAccount ID=" + result.getKey().getId());
      datastore.delete(result.getKey());
    }

    Filter ownerFilter = new FilterPredicate(AuthProviderAccount.RTFACCOUNT_OWNER_KEY, FilterOperator.EQUAL,
        rtfAccountId + "");
    Query apQuery = new Query("AuthProviderAccount").setFilter(ownerFilter).setKeysOnly();
    PreparedQuery apAccountPQ = datastore.prepare(apQuery);
    HashSet<Key> hashSet = new HashSet<Key>();
    for (Entity result : apAccountPQ.asIterable()) {
      log.info("queried APAccount ID=" + result.getKey().getId());
      hashSet.add(result.getKey());
    }
    datastore.delete(hashSet);

    // Query APAccounts for all APAccounts with Owner equal to RTF Account ID
  }

}
