package auth.logins.data;


import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import auth.logins.other.LoginType;
import auth.logins.other.RTFConstants;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class MasterAccount implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final String LOGIN_HTTPSESSION_ATTRIBUTE = "LOGIN_HTTPSESSION_ATTRIBUTE";
  public static final String APPENGINE_KEY_LONG = "APPENGINE_KEY_LONG";
  public static final String DATASTORE_KIND = "RTFAccount";
  public static final String DATASTORE_ANCESTOR_ID = "AccountAncestor";
  public static final String PROPERTY_PERSON_NAME = "PROPERTY_PERSON_NAME";

  private HashMap<String, AuthProviderAccount> _accounts;
  private HashMap<String, String> _masterAccountProperties = new HashMap<String, String>();
  private static Key _accountAncestorKey;
  
  private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass().getName());

  public static MasterAccount fromID(String RTFAccountID) {
    if (RTFAccountID == null) {
      return null;
    }
    Key RTFAccountKey = KeyFactory.createKey(getAncestorKey(), DATASTORE_KIND, Long.valueOf(RTFAccountID));
    return new MasterAccount(RTFAccountKey);
  }

  private MasterAccount(Key useKey) {
    _masterAccountProperties.put(APPENGINE_KEY_LONG, useKey.getId()+"");
    loadFromDataStore();
    _accounts = AuthProviderAccount.loadAPAccountsByParentID(getAppEngineKeyLong()+"");
  }
  
  private void loadFromDataStore() {  //Assumes appengine key already set in hashmap
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    log.info("Obtaining properties for Master Account ID:"+ this.getAppEngineKeyLong());
    Key targetKey = KeyFactory.createKey(this.getAncestorKey(), DATASTORE_KIND, this.getAppEngineKeyLong());
    Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, targetKey);
    Query q = new Query(DATASTORE_KIND).setFilter(keyFilter);
    PreparedQuery pq = datastore.prepare(q);
    
    for (Entity result : pq.asIterable()) {
      StringBuilder resultBuilder = new StringBuilder();
      for (String propertyName : result.getProperties().keySet()) {
        Object propValue = result.getProperty(propertyName);
        String valueStr;
        if (propValue != null) {
           valueStr = propValue.toString();
        } else {
          valueStr = "null";
        }
        _masterAccountProperties.put(propertyName, (String)propValue);
        resultBuilder.append(propertyName + "=" + valueStr + " ");
      }
      log.info(resultBuilder.toString());
    }
  }
  
  private void saveToDataStore() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity rtfAccountAncestor = new Entity(RTFConstants.DATASTORE_KIND_ANCESTOR, DATASTORE_ANCESTOR_ID);
    
    Entity thisObject = new Entity(DATASTORE_KIND, getAppEngineKeyLong(), getAncestorKey());
     
    for (String propertyName : _masterAccountProperties.keySet()) {
      String propertyValue = _masterAccountProperties.get(propertyName);
      
      if (propertyName.equals(APPENGINE_KEY_LONG)) {
        //No need to save this to datastore - same as the datastore ID value
      } else {
        thisObject.setProperty(propertyName, propertyValue);        
      }
    }
    
    datastore.put(thisObject);
  }

  public static Key getAncestorKey() {
    if (_accountAncestorKey == null) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity rtfAccountAncestor = new Entity(RTFConstants.DATASTORE_KIND_ANCESTOR, DATASTORE_ANCESTOR_ID);
      rtfAccountAncestor.setProperty(RTFConstants.ANCESTOR_TYPE, DATASTORE_ANCESTOR_ID);
      _accountAncestorKey = datastore.put(rtfAccountAncestor);
      log.info("Ancestor key of all RTFAccount objects determined to be: "+ _accountAncestorKey);
    }
    
    return _accountAncestorKey;
  }

  public static MasterAccount createNewRTFAccount() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key accountAncestorKey = getAncestorKey();
    log.info("RTFAccount.createRTFAccount: Ancestor Key=" + accountAncestorKey.getName() + " "
        + accountAncestorKey.toString());

    KeyRange allocatedIds = datastore.allocateIds(accountAncestorKey, DATASTORE_KIND, 1); // Ask for a new ID now
    Key allocatedId = allocatedIds.getStart();

    // All RTFAccounts will be under the same ancestor subject to strict consistency
    Entity rtfAccount = new Entity(DATASTORE_KIND, allocatedId.getId(), accountAncestorKey); // Specify ID and parent
    Key accountKey = datastore.put(rtfAccount);

    return new MasterAccount(allocatedId);
  }

  public static Key getKey(String RTFAccountID) {
    Long idToLookup = Long.valueOf(RTFAccountID);
    return KeyFactory.createKey(getAncestorKey(), DATASTORE_KIND, idToLookup);
  }

  public long getAppEngineKeyLong() {
    return Long.valueOf(_masterAccountProperties.get(APPENGINE_KEY_LONG));
  }

  public void addAPAccount(AuthProviderAccount apAccount) {
    if (_accounts.containsValue(apAccount)) {
      throw new RuntimeException("Attempt to add an apAccount already owned by this RTFAccount");
    }

    _accounts.put(apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME), apAccount);
    apAccount.setProperty(AuthProviderAccount.RTFACCOUNT_OWNER_KEY, this.getAppEngineKeyLong()+"");
  }

  public void updateAPAccount(AuthProviderAccount newAPAccountObj) {
    String authProviderName = newAPAccountObj.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    
    AuthProviderAccount oldAPAccountObj = _accounts.get(authProviderName);
    oldAPAccountObj.copyDataTo(newAPAccountObj);
    oldAPAccountObj.setProperty(AuthProviderAccount.RTFACCOUNT_OWNER_KEY, this.getAppEngineKeyLong()+"");  //Forces save to db
    
    _accounts.put(authProviderName, newAPAccountObj);
  }

  public Collection<AuthProviderAccount> getAPAccounts() {
    return _accounts.values();
  }

  public boolean isLoggedInAPType(LoginType loginType) {
    Collection<AuthProviderAccount> providerAccounts = getAPAccounts();
    
    for (AuthProviderAccount apAccount : providerAccounts) {
      if (loginType.getName().equals(apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME))) {
        return true;
      }
    }
    return false;
  }
  
  public String getProperty(String propertyName) {
      return _masterAccountProperties.get(propertyName);
  }
  
  public void setProperty(String propertyName, String propertyValue) {
    _masterAccountProperties.put(propertyName, propertyValue);
    this.saveToDataStore();
  }


}
