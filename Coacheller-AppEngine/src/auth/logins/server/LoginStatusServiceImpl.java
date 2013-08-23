package auth.logins.server;


import java.util.Collection;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import auth.logins.client.LoginStatusService;
import auth.logins.data.AuthProviderAccount;
import auth.logins.data.MasterAccount;
import auth.logins.other.LoginManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginStatusServiceImpl extends RemoteServiceServlet implements LoginStatusService {

  @Override
  public HashMap<String, String> getLoginInfo() {
    ServletContext servletContext = this.getServletContext();
    HttpSession session = this.getThreadLocalRequest().getSession();
    boolean sessionLoggedIn = LoginManager.isSessionLoggedIn(session);
    
    
    HashMap<String, String> returnMap = new HashMap<String, String>();
    
    if (sessionLoggedIn) {  
      MasterAccount currentLogin = LoginManager.getCurrentLogin(session);
      long appEngineKeyLong = currentLogin.getAppEngineKeyLong();
      String personName = currentLogin.getProperty(MasterAccount.PROPERTY_PERSON_NAME);
      
      returnMap.put("AppEngine Datastore Key", appEngineKeyLong+"");
      returnMap.put("PROPERTY_PERSON_NAME", personName);
      
      //Do we really need to provide info about all their accounts?
      //Collection<AuthProviderAccount> apAccounts = currentLogin.getAPAccounts();
      
      
    } else {
      //TODO should throw an exception if the user is not logged in
      returnMap.put("NOT", "LOGGED-IN");
      
    }
    
    
    return returnMap;
  }

}
