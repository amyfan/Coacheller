package auth.logins.test;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import auth.logins.client.LoginStatusService;
import auth.logins.client.LoginStatusServiceAsync;
import auth.logins.data.LoginStatus;

import com.google.appengine.api.datastore.Entity;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ratethisfest.shared.LoginType;

public class LoginStatusTester implements EntryPoint {

  private VerticalPanel mainPanel = new VerticalPanel();
  private Label lastUpdatedLabel = new Label();
  private FlexTable loginStatusTable = new FlexTable();

  private LoginStatusServiceAsync loginStatusSvc = GWT.create(LoginStatusService.class);
  private Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public void onModuleLoad() {
    RootPanel.get("loginStatusTestDiv").add(mainPanel);
    mainPanel.add(lastUpdatedLabel);
    mainPanel.add(loginStatusTable);

    lastUpdatedLabel.setTitle("It's not polite to point");

    // Setup timer to refresh list automatically.
    Timer refreshTimer = new Timer() {

      @Override
      public void run() {
        lastUpdatedLabel.setText("Timer Started");
        logger.log(Level.SEVERE, "this message should get logged");

        // Initialize the service proxy.
        // TODO is this necessary? Tutorial app has it in 2 places
        if (loginStatusSvc == null) {
          loginStatusSvc = GWT.create(LoginStatusService.class);
        }

        // Set up the callback object.
        AsyncCallback<LoginStatus> callback = new AsyncCallback<LoginStatus>() {
          public void onFailure(Throwable caught) {
            // TODO: Do something with errors.
            lastUpdatedLabel.setText("Got exception result");
          }

          public void onSuccess(LoginStatus result) {
            lastUpdatedLabel.setText("Got successful result");
            int currentRow = 0;
            loginStatusTable.setText(currentRow, 0, "Logged in?");
            loginStatusTable.setText(currentRow, 1, result.isLoggedIn() + "");
            currentRow++;
            loginStatusTable.setText(currentRow, 0, "Key (" + Entity.KEY_RESERVED_PROPERTY + ")");
            loginStatusTable.setText(currentRow, 1, result.getProperty(Entity.KEY_RESERVED_PROPERTY));
            currentRow++;
            loginStatusTable.setText(currentRow, 0, "Google Logged In?");
            loginStatusTable.setText(currentRow, 1, result.isLoggedIn(LoginType.GOOGLE)+"");
            currentRow++;
            loginStatusTable.setText(currentRow, 0, "Google info:");
            loginStatusTable.setText(currentRow, 1, result.getProperty(LoginType.GOOGLE.getName()));
            currentRow++;
            loginStatusTable.setText(currentRow, 0, "Facebook Logged In?");
            loginStatusTable.setText(currentRow, 1, result.isLoggedIn(LoginType.FACEBOOK)+"");
            currentRow++;
            loginStatusTable.setText(currentRow, 0, "Facebook info:");
            loginStatusTable.setText(currentRow, 1, result.getProperty(LoginType.FACEBOOK.getName()));
            currentRow++;
            loginStatusTable.setText(currentRow, 0, "Twitter Logged In?");
            loginStatusTable.setText(currentRow, 1, result.isLoggedIn(LoginType.TWITTER)+"");
            currentRow++;
            loginStatusTable.setText(currentRow, 0, "Twitter info:");
            loginStatusTable.setText(currentRow, 1, result.getProperty(LoginType.TWITTER.getName()));
            currentRow++;

          }
        };

        // Make the call to the stock price service.
        loginStatusSvc.getLoginInfo(callback);
      }
    };
    refreshTimer.scheduleRepeating(5000);
  }

}
