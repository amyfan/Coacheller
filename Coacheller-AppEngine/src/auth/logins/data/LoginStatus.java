package auth.logins.data;

import java.io.Serializable;
import java.util.HashMap;

import com.google.appengine.api.datastore.Entity;

import auth.logins.other.LoginType;

public class LoginStatus implements Serializable {
  
  private HashMap<String, String> _properties = new HashMap<String, String>();
  private static final String NOT_LOGGED_IN = "NOT_LOGGED_IN";
  public static final String PROPERTY_PERSON_NAME = "PROPERTY_PERSON_NAME";
  public static final String PROPERTY_ACCOUNT_ID = Entity.KEY_RESERVED_PROPERTY;
  
  //Obtain a LoginStatus object representing a user who has not logged in
  public static LoginStatus notLoggedIn() {
    LoginStatus loginStatus = new LoginStatus();
    loginStatus.setProperty(NOT_LOGGED_IN, NOT_LOGGED_IN);
    return loginStatus;
  }
  
  //Set a general property
  public void setProperty (String name, String value) {
    _properties.put(name, value);
  }
  
  //Get a general property
  public String getProperty(String name) {
    return _properties.get(name);
  }
  
  //Set the property associated with an authprovider
  public void setAPAccountProperty(LoginType type, String value) {
     _properties.put(type.getName(), value);
  }
  
  //Is the user logged in under any master account? (Implying at least one authprovider account)
  public boolean isLoggedIn() {
    return !_properties.containsKey(NOT_LOGGED_IN);
  }
  
  //Is the user logged in under the specified type of authprovider?
  public boolean isLoggedIn(LoginType type) {
    return _properties.containsKey(type.getName());
  }
  
  
  

}
