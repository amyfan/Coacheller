package auth.logins.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import auth.logins.other.LoginType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.ratethisfest.server.persistence.AppUserDAO;

public class AuthProviderAccount implements Serializable {
  private static final long serialVersionUID = 1L;

  private static final Logger log = Logger.getLogger(new Object() {
  }.getClass().getEnclosingClass().getName());

  public static final String DATASTORE_KIND = "AuthProviderAccount";

  // public static final String LOGIN_SETTING_PREFIX = "LOGIN"; //No longer needed, using AuthProviderAccount object

  // After data is live, ONLY change the name, not the value
  public static final String RTFACCOUNT_OWNER_KEY = "RTFACCOUNT_OWNER"; // datastore ID of RTFAccount that owns this
                                                                        // APAccount
  public static final String AUTH_PROVIDER_NAME = "AUTH_PROVIDER_NAME";
  public static final String AUTH_PROVIDER_ID = "AUTH_PROVIDER_ID";
  public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
  public static final String LOGIN_SCREEN_NAME = "LOGIN_SCREEN_NAME";
  public static final String LOGIN_PERSON_NAME = "LOGIN_PERSON_NAME";
  public static final String LOGIN_GENDER = "LOGIN_GENDER";
  public static final String LOGIN_PICTURE_URL = "LOGIN_PICTURE_URL";
  public static final String LOGIN_SOCIAL_URL = "LOGIN_SOCIAL_URL";
  public static final String LOGIN_LOCALE = "LOGIN_LOCALE";
  public static final String LOGIN_TIMEZONE = "LOGIN_TIMEZONE";
  public static final String LOGIN_UTC_OFFSET = "LOGIN_UTC_OFFSET";
  public static final String LOGIN_LOCATION = "LOGIN_LOCATION";
  public static final String LOGIN_DESCRIPTION = "LOGIN_DESCRIPTION";
  public static final String LOGIN_FOLLOWERS_COUNT = "LOGIN_FOLLOWERS_COUNT";
  public static final String LOGIN_FRIENDS_COUNT = "LOGIN_FRIENDS_COUNT";
  public static final String LOGIN_LISTED_COUNT = "LOGIN_LISTED_COUNT";

  private static final ArrayList<String> _indexedProperties = new ArrayList<String>();

  private HashMap<String, String> _loginHash = new HashMap<String, String>();

  private Key _datastoreKey;

  // Be careful adding anything here, only new indexed Elements will be returned in queries
  private ArrayList<String> getIndexedProperties() {
    if (_indexedProperties.isEmpty()) {
      _indexedProperties.add(AUTH_PROVIDER_NAME);
      _indexedProperties.add(AUTH_PROVIDER_ID);
      _indexedProperties.add(RTFACCOUNT_OWNER_KEY);
    }

    return _indexedProperties;

  }

  public boolean equals(Object otherAPAccount) {
    if (!(otherAPAccount instanceof AuthProviderAccount)) {
      return false;
    }
    return this._loginHash.equals(((AuthProviderAccount) otherAPAccount)._loginHash);
  }

  public AuthProviderAccount(String authResponse, LoginType loginType) throws JsonProcessingException, IOException {
    ObjectMapper m = new ObjectMapper();
    JsonNode rootNode = m.readTree(authResponse);

    if (LoginType.GOOGLE.equals(loginType)) {
      _loginHash.put(AuthProviderAccount.AUTH_PROVIDER_NAME, loginType.getName());
      _loginHash.put(AuthProviderAccount.AUTH_PROVIDER_ID, rootNode.path("id").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_EMAIL, rootNode.path("email").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_PERSON_NAME, rootNode.path("name").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_PICTURE_URL, rootNode.path("picture").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_SOCIAL_URL, rootNode.path("link").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_GENDER, rootNode.path("gender").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_LOCALE, rootNode.path("locale").textValue());

    } else if (LoginType.FACEBOOK.equals(loginType)) {
      _loginHash.put(AuthProviderAccount.AUTH_PROVIDER_NAME, loginType.getName());
      _loginHash.put(AuthProviderAccount.AUTH_PROVIDER_ID, rootNode.path("id").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_SCREEN_NAME, rootNode.path("username").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_EMAIL, rootNode.path("email").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_PERSON_NAME, rootNode.path("name").textValue());
      // _loginHash.put(AuthProviderAccount.LOGIN_PICTURE_URL, rootNode.path("picture").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_SOCIAL_URL, rootNode.path("link").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_GENDER, rootNode.path("gender").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_LOCALE, rootNode.path("locale").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_UTC_OFFSET, rootNode.path("timezone").textValue());

    } else if (LoginType.TWITTER.equals(loginType)) {
      _loginHash.put(AuthProviderAccount.AUTH_PROVIDER_NAME, loginType.getName());
      _loginHash.put(AuthProviderAccount.AUTH_PROVIDER_ID, rootNode.path("id_str").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_PERSON_NAME, rootNode.path("name").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_SCREEN_NAME, rootNode.path("screen_name").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_LOCATION, rootNode.path("location").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_DESCRIPTION, rootNode.path("description").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_SOCIAL_URL, rootNode.path("url").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_FOLLOWERS_COUNT, rootNode.path("followers_count").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_FRIENDS_COUNT, rootNode.path("friends_count").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_LISTED_COUNT, rootNode.path("listed_count").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_TIMEZONE, rootNode.path("time_zone").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_UTC_OFFSET, rootNode.path("utc_offset").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_LOCALE, rootNode.path("lang").textValue());
      _loginHash.put(AuthProviderAccount.LOGIN_PICTURE_URL, rootNode.path("profile_image_url").textValue());

      // _loginHash.put(AuthProviderAccount.LOGIN_EMAIL, rootNode.path("email").textValue());
      // _loginHash.put(AuthProviderAccount.LOGIN_PICTURE_URL, rootNode.path("picture").textValue());
      // _loginHash.put(AuthProviderAccount.LOGIN_GENDER, rootNode.path("gender").textValue());
      // _loginHash.put(AuthProviderAccount.LOGIN_LOCALE, rootNode.path("locale").textValue());
    } else {
      throw new RuntimeException("Login Type is null or unknown");
    }

  }

  public AuthProviderAccount(Entity queriedEntity) {

    _datastoreKey = queriedEntity.getKey();
    log.info("Recovered APAccount from queried Entity with key: " + _datastoreKey.toString() + " id: "
        + _datastoreKey.getId());
    for (String key : queriedEntity.getProperties().keySet()) {
      String value = (String) queriedEntity.getProperty(key);
      _loginHash.put(key, value);
    }
    log.info("Describing again the APAccount recovered from queried Entity: " + this.getDatastoreKeyDescription());
  }

  public String getProperty(String propName) {
    return _loginHash.get(propName);
  }

  public String getDescription() {
    StringBuilder sBuilder = new StringBuilder();
    String loginTypeName = this.getProperty(AUTH_PROVIDER_NAME);

    if (loginTypeName.equals(LoginType.GOOGLE.getName())) {
      sBuilder.append(this.getProperty(AuthProviderAccount.LOGIN_PERSON_NAME)).append(" (")
          .append(this.getProperty(AuthProviderAccount.LOGIN_EMAIL)).append(")");
    } else if (loginTypeName.equals(LoginType.FACEBOOK.getName())) {
      sBuilder.append(this.getProperty(AuthProviderAccount.LOGIN_PERSON_NAME)).append(" (")
          .append(this.getProperty(AuthProviderAccount.LOGIN_EMAIL)).append(")");
    } else if (loginTypeName.equals(LoginType.TWITTER.getName())) {
      sBuilder.append(this.getProperty(AuthProviderAccount.LOGIN_PERSON_NAME)).append(" (@")
          .append(this.getProperty(AuthProviderAccount.LOGIN_SCREEN_NAME)).append(")");
    }

    return sBuilder.toString();
  }

  public void setProperty(String propertyName, String value) {
    _loginHash.put(propertyName, value);
    this.saveToDatastore();
  }
  
  private Key getParentKey(String parentIdStr) {
  Long idToLookup = Long.valueOf(parentIdStr);
  return KeyFactory.createKey(AppUserDAO.getAncestorKey(), DATASTORE_KIND, idToLookup);
  }

  private void saveToDatastore() {

    // If we are saving it, we must know what RTFAccount owns it
    String parentIdStr = this.getProperty(RTFACCOUNT_OWNER_KEY);
    // Long parentIdLong = Long.valueOf(parentIdStr);
    
    Key RTFAccountKey = getParentKey(parentIdStr);

    Entity apAccountEntity;
    // here we need to re-use the existing ID if possible
    if (_datastoreKey == null) {
      log.info("Saving new APAccount, so key is unknown");
      // This is being saved for the first time, we don't have an ID yet
      apAccountEntity = new Entity(DATASTORE_KIND, RTFAccountKey);

    } else {
      log.info("Updating existing APAccount, using existing key: " + _datastoreKey.getId());
      // Save using key we found when we loaded this object from the datastore
      // Otherwise a whole new record gets created
      apAccountEntity = new Entity(DATASTORE_KIND, _datastoreKey.getId(), RTFAccountKey);

    }

    for (String key : _loginHash.keySet()) {
      String value = _loginHash.get(key);

      if (getIndexedProperties().contains(key)) {
        apAccountEntity.setProperty(key, value);
      } else {
        apAccountEntity.setUnindexedProperty(key, value);
      }
    }

    if (_datastoreKey == null) {
      log.info("Saving APAccount, no existing key information.  Parent key: " + RTFAccountKey);
    } else {
      log.info("About to save AuthProviderAccount: " + apAccountEntity.toString() + " Key:" + _datastoreKey
          + " Parent Key: " + _datastoreKey.getParent());
    }
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // This object is the one tracked in Sessions, must store the key here
    _datastoreKey = datastore.put(apAccountEntity);

    log.info("AuthProviderAccount saved: " + apAccountEntity.toString() + " Key:" + _datastoreKey + " Parent Key: "
        + _datastoreKey.getParent());

  }

  public static HashMap<String, AuthProviderAccount> loadAPAccountsByParentID(String RTFAccountID) {
    HashMap<String, AuthProviderAccount> returnHash = new HashMap<String, AuthProviderAccount>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // String authProviderName = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    // String authProviderID = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_ID);

    Filter idFilter = new FilterPredicate(AuthProviderAccount.RTFACCOUNT_OWNER_KEY, FilterOperator.EQUAL, RTFAccountID);

    // Use CompositeFilter to combine multiple filters

    // Use class Query to assemble a query
    Query q = new Query(AuthProviderAccount.DATASTORE_KIND).setFilter(idFilter);

    // Use PreparedQuery interface to retrieve results
    PreparedQuery pq = datastore.prepare(q);

    log.info("Query Results for AuthProviders owned by " + idFilter + ":");

    for (Entity result : pq.asIterable()) {
      StringBuilder resultBuilder = new StringBuilder();
      AuthProviderAccount authProviderAccount = new AuthProviderAccount(result);
      returnHash.put(authProviderAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME), authProviderAccount);
      for (String propertyName : result.getProperties().keySet()) {
        Object propValue = result.getProperty(propertyName);
        String valueStr;
        if (propValue != null) {
          valueStr = propValue.toString();
        } else {
          valueStr = "null";
        }
        resultBuilder.append(propertyName + "=" + valueStr + " ");
      }
      log.info(resultBuilder.toString());
    }
    return returnHash;
  }

  // Used for updating an existing AuthProviderAccount object reconstructed from the datastore,
  // Using new information from a more recent OAuth login.
  // Properties existing in new object are copied to old object, overwriting existing values
  // This allows us to capture when a user updates their info with the auth provider
  // Note, does not copy the datastore key
  public void copyDataTo(AuthProviderAccount overwriteThisAPAccountData) {
    overwriteThisAPAccountData._loginHash.putAll(this._loginHash);
  }

  // Meant for debugging only and not for extracting the datastore key
  public String getDatastoreKeyDescription() {
    if (_datastoreKey == null) {
      return "NULL DATASTORE KEY";
    } else {

      return "This key: " + _datastoreKey.toString() + " id: " + _datastoreKey.getId() + " parent: "
          + _datastoreKey.getParent().toString();
    }
  }

}
