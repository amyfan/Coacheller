package com.ratethisfest.android.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.coacheller.CoachellerApplication;
import com.coacheller.R;
import com.ratethisfest.android.Alert;
import com.ratethisfest.android.AlertListAdapter;
import com.ratethisfest.android.AlertManager;
import com.ratethisfest.android.AndroidConstants;
import com.ratethisfest.android.log.LogController;

public class AlertsActivity extends Activity implements OnItemClickListener, OnClickListener {

  private Button buttonDone;
  private Button buttonCancelAll;
  private CoachellerApplication application;
  private Dialog dialogAlerts;
  private AlertListAdapter alertListAdapter;
  private Alert lastAlertSelected;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogController.LIFECYCLE_ACTIVITY.logMessage("AlertsActivity.onCreate()");
    this.application = (CoachellerApplication) getApplication();
    AlertManager alertManager = this.application.getAlertManager();
    alertListAdapter = new AlertListAdapter(alertManager, this);

    setContentView(R.layout.activity_alerts);

    this.buttonDone = (Button) findViewById(R.id.button_Done);
    this.buttonDone.setOnClickListener(this);

    this.buttonCancelAll = (Button) findViewById(R.id.button_cancelAllAlerts);
    this.buttonCancelAll.setOnClickListener(this);

    ListView listViewAlerts = (ListView) findViewById(R.id.listView_alerts);
    listViewAlerts.setAdapter(alertListAdapter);
    listViewAlerts.setOnItemClickListener(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.alerts, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  // Alert clicked in listView
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    LogController.USER_ACTION_UI.logMessage("AlertsActivity: item clicked in alert list: " + position);

    // Item we wanted
    Entry<String, Alert> item = this.alertListAdapter.getItem(position);
    String key = item.getKey();
    lastAlertSelected = item.getValue();
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter identifies set clicked as: "
        + lastAlertSelected.getArtist() + " at " + lastAlertSelected.getSetDateTime() + " hashKey:" + key);

    showDialog(AndroidConstants.DIALOG_ALERTS);
  }

  @Override
  public void onClick(View viewClicked) {

    if (viewClicked.getId() == R.id.button_Done) {
      LogController.USER_ACTION_UI.logMessage("Button Clicked - Done with Alerts Activity");
      Intent intent = new Intent();
      intent.setClass(this, CoachellerActivity.class);
      startActivity(intent);
    }

    if (viewClicked.getId() == R.id.button_cancelAllAlerts) {
      LogController.USER_ACTION_UI.logMessage("Button Clicked - Cancel All Alerts");
      clickedButtonCancelAllAlerts();
    }

    if (dialogAlerts != null && dialogAlerts.isShowing()) {
      handleAlertDialogClick(viewClicked);
    }
  }

  private void handleAlertDialogClick(View viewClicked) {
    RadioButton radioNearNumbers = (RadioButton) dialogAlerts.findViewById(R.id.radioNearNumberfield);
    RadioButton radioWithText = (RadioButton) dialogAlerts.findViewById(R.id.radioWithText);
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter identifies set clicked as: "
        + lastAlertSelected.getArtist() + " at " + lastAlertSelected.getSetDateTime() + " hashKey:"
        + lastAlertSelected.getHashKey());

    if (viewClicked.getId() == R.id.button_ok) {
      LogController.USER_ACTION_UI.logMessage("AlertsActivity: Clicked Alert Dialog OK");
      // Figure out which radio is selected
      if (radioNearNumbers.isChecked()) {
        LogController.USER_ACTION_UI.logMessage("User selected to update existing alert");
        EditText numberBox = (EditText) dialogAlerts.findViewById(R.id.numberBox);
        int minutesBefore = Integer.parseInt(numberBox.getText().toString());
        lastAlertSelected.setMinutesBeforeSet(minutesBefore);
        application.getAlertManager().alertWasChanged();

      } else if (radioWithText.isChecked()) {
        LogController.USER_ACTION_UI.logMessage("User chose to cancel existing alert");
        try {
          application.getAlertManager().removeAlert(lastAlertSelected);
        } catch (FileNotFoundException e) {
          LogController.ERROR.logMessage("AlertsActivity: " + e.getClass() + " trying to remove alert");
          e.printStackTrace();
        } catch (IOException e) {
          LogController.ERROR.logMessage("AlertsActivity: " + e.getClass() + " trying to remove alert");
          e.printStackTrace();
        }
      }
      this.dialogAlerts.dismiss();
      redrawEverything();
    }
    if (viewClicked.getId() == R.id.button_cancel) {
      LogController.USER_ACTION_UI.logMessage("AlertsActivity: Clicked Alert Dialog Cancel");
      this.dialogAlerts.dismiss();
    }
    if (viewClicked.getId() == R.id.radioNearNumberfield) {
      LogController.USER_ACTION_UI.logMessage("AlertsActivity: Clicked Alert Dialog Top Radio");
      radioWithText.setChecked(false); // Deselect bottom radio

    }
    if (viewClicked.getId() == R.id.radioWithText) {
      LogController.USER_ACTION_UI.logMessage("AlertsActivity: Clicked Alert Dialog Bottom Radio");
      radioNearNumbers.setChecked(false); // Deselect top radio
    }
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    if (id == AndroidConstants.DIALOG_ALERTS) {
      return createDialogAlerts();
    } else {
      LogController.ERROR.logMessage("Error, unexpected Dialog ID");
      return createDialogAlerts(); // Supposed to not crash the program O.o
    }
  }

  private Dialog createDialogAlerts() {
    LogController.USER_ACTION_UI.logMessage("AlertsActivity.onPrepareDialog");
    dialogAlerts = new Dialog(this);
    dialogAlerts.setContentView(R.layout.dialog_alert_multipurpose);
    // alertsDialog.setTitle(AuthConstants.DIALOG_TITLE_GET_EMAIL);
    dialogAlerts.setTitle("Alert Dialog Title");
    Button buttonOK = (Button) dialogAlerts.findViewById(R.id.button_ok);
    buttonOK.setOnClickListener(this);
    Button buttonCancel = (Button) dialogAlerts.findViewById(R.id.button_cancel);
    buttonCancel.setOnClickListener(this);
    RadioButton radioNearNumbers = (RadioButton) dialogAlerts.findViewById(R.id.radioNearNumberfield);
    radioNearNumbers.setOnClickListener(this);
    RadioButton radioWithText = (RadioButton) dialogAlerts.findViewById(R.id.radioWithText);
    radioWithText.setOnClickListener(this);
    radioWithText.setText("Cancel this alert");
    return dialogAlerts;
  }

  @Override
  protected void onPrepareDialog(int id, Dialog dialog) {
    super.onPrepareDialog(id, dialog);
    LogController.USER_ACTION_UI.logMessage("AlertsActivity.onPrepareDialog");
    if (id == AndroidConstants.DIALOG_ALERTS) {

      RadioButton radioWithText = (RadioButton) dialogAlerts.findViewById(R.id.radioWithText);
      RadioButton radioNearNumbers = (RadioButton) dialogAlerts.findViewById(R.id.radioNearNumberfield);
      radioNearNumbers.setChecked(true);
      radioWithText.setChecked(false);

      EditText numberBox = (EditText) dialogAlerts.findViewById(R.id.numberBox);
      numberBox.setText(lastAlertSelected.getMinutesBeforeSet() + "");
      numberBox.selectAll();

    } else {
      LogController.ERROR.logMessage("Error, unexpected Dialog ID");
    }

  }

  private void clickedButtonCancelAllAlerts() {
    try {
      this.application.getAlertManager().removeAllAlerts();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    this.redrawEverything();

  }

  private void redrawEverything() {
    alertListAdapter.reSort();
    ListView listViewAlerts = (ListView) findViewById(R.id.listView_alerts);
    listViewAlerts.invalidateViews();
  }

}
