package auth.logins.client;

import auth.logins.data.LoginStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginStatusServiceAsync {

  void getLoginInfo(AsyncCallback<LoginStatus> callback);
}
