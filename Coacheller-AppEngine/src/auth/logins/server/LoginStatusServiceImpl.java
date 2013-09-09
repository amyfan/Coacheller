package auth.logins.server;


import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import auth.logins.client.LoginStatusService;
import auth.logins.data.AuthProviderAccount;
import auth.logins.data.LoginStatus;
import auth.logins.other.LoginManager;

import com.google.appengine.api.datastore.Entity;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ratethisfest.server.domain.AppUser;
import com.ratethisfest.server.persistence.AppUserDAO;

public class LoginStatusServiceImpl extends RemoteServiceServlet implements LoginStatusService {
  




  @Override
  public LoginStatus getLoginInfo() {
    ServletContext servletContext = this.getServletContext();
    HttpSession session = this.getThreadLocalRequest().getSession();
    boolean sessionLoggedIn = LoginManager.isSessionLoggedIn(session);
     
    if (sessionLoggedIn) {  
      LoginStatus returnMap = new LoginStatus();
      AppUser currentLogin = LoginManager.getCurrentLogin(session);
      long appEngineKeyLong = currentLogin.getId();
      String personName = currentLogin.getName();
      
      returnMap.setProperty(LoginStatus.PROPERTY_ACCOUNT_ID, appEngineKeyLong+"");
      returnMap.setProperty(LoginStatus.PROPERTY_PERSON_NAME, personName);
      
      //Do we really need to provide info about all their accounts?
      Collection<AuthProviderAccount> apAccounts = AppUserDAO.getAuthProviderAccounts(currentLogin);
      for (AuthProviderAccount apAccount : apAccounts) {
        //String providerName = apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
        String description = apAccount.getDescription();
        returnMap.setAPAccountProperty(apAccount.getLoginType(), description);
      }
 
      return returnMap;
      
    } else {
      return LoginStatus.notLoggedIn();  
    }

  }

}
