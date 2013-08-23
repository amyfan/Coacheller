package auth.logins.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginStatusServiceAsync {

  void getLoginInfo(AsyncCallback<HashMap<String, String>> callback);
}
