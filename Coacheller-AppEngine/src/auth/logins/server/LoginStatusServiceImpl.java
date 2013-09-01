package auth.logins.server;


import java.util.Collection;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import auth.logins.client.LoginStatusService;
import auth.logins.data.AuthProviderAccount;
import auth.logins.other.LoginManager;
import auth.logins.other.LoginType;

import com.google.appengine.api.datastore.Entity;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.persistence.AppUserDAO;

public class LoginStatusServiceImpl extends RemoteServiceServlet implements LoginStatusService {
  
  public static final String PROPERTY_PERSON_NAME = "PROPERTY_PERSON_NAME";
  public static final String NOT_LOGGED_IN = "NOT_LOGGED_IN";


  @Override
  public HashMap<String, String> getLoginInfo() {
    ServletContext servletContext = this.getServletContext();
    HttpSession session = this.getThreadLocalRequest().getSession();
    boolean sessionLoggedIn = LoginManager.isSessionLoggedIn(session);
    
    
    HashMap<String, String> returnMap = new HashMap<String, String>();
    
    if (sessionLoggedIn) {  
      AppUser currentLogin = LoginManager.getCurrentLogin(session);
      long appEngineKeyLong = currentLogin.getId();
      String personName = currentLogin.getName();
      
      returnMap.put(Entity.KEY_RESERVED_PROPERTY, appEngineKeyLong+"");
      returnMap.put(PROPERTY_PERSON_NAME, personName);
      
      //Do we really need to provide info about all their accounts?
      Collection<AuthProviderAccount> apAccounts = AppUserDAO.getAuthProviderAccounts(currentLogin);
      for (AuthProviderAccount apAccount : apAccounts) {
        String providerName = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
        String description = apAccount.getDescription();

        returnMap.put(providerName, description);
      }
      
      
    } else {
      returnMap.put(NOT_LOGGED_IN, NOT_LOGGED_IN);
      
    }
    
    
    return returnMap;
  }

}
