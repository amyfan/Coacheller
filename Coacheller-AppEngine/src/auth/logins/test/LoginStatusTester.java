package auth.logins.test;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


import auth.logins.client.LoginStatusService;
import auth.logins.client.LoginStatusServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
        // TODO is this necessary?  Tutorial app has it in 2 places
        if (loginStatusSvc == null) {
          loginStatusSvc = GWT.create(LoginStatusService.class);
        }

        // Set up the callback object.
        AsyncCallback<HashMap<String, String>> callback = new AsyncCallback<HashMap<String, String>>() {
          public void onFailure(Throwable caught) {
            // TODO: Do something with errors.
            lastUpdatedLabel.setText("Got exception result");
          }

          public void onSuccess(HashMap<String, String> result) {
            lastUpdatedLabel.setText("Got successful result");
            int currentRow = 0;
            loginStatusTable.setText(currentRow, 0, "KEY");
            loginStatusTable.setText(currentRow, 1, "VALUE");
            currentRow++;
            
            for (String keyName : result.keySet()) {
              String valueName = result.get(keyName);
              loginStatusTable.setText(currentRow, 0, keyName);
              loginStatusTable.setText(currentRow, 1, valueName);
              currentRow++;
            }
          }
        };

        // Make the call to the stock price service.
        loginStatusSvc.getLoginInfo(callback);
      }
    };
    refreshTimer.scheduleRepeating(5000);
  }

}
