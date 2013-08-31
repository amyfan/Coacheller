package auth.logins.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("../login_status_tester/loginStatus")
public interface LoginStatusService  extends RemoteService {
    public HashMap<String, String> getLoginInfo();
}


