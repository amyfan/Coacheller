package com.coacheller.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.coacheller.CoachellerApplication;
import com.coacheller.R;
import com.coacheller.R.id;
import com.coacheller.R.layout;
import com.coacheller.R.menu;
import com.ratethisfest.android.AlertListAdapter;
import com.ratethisfest.android.AlertManager;
import com.ratethisfest.android.log.LogController;

import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class AlertsActivity extends Activity implements OnItemClickListener, OnClickListener {

  private Button buttonDone;
  private Button buttonCancelAll;
  private CoachellerApplication application;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogController.LIFECYCLE_ACTIVITY.logMessage("AlertsActivity.onCreate()");
    this.application = (CoachellerApplication) getApplication();
    AlertManager alertManager = this.application.getAlertManager();
    AlertListAdapter alertListAdapter = new AlertListAdapter(alertManager, this);

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
  public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    LogController.USER_ACTION_UI.logMessage("AlertsActivity: item clicked in alert list");
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
  }

}
