package com.ratethisfest.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.ratethisfest.android.log.LogController;
import com.ratethisfest.shared.FestivalEnum;

public class AlertManager implements AlertListAdapterDataSource {

  public static final String STORED_FILENAME = "Alerts.rtf.1";

  private Alert scheduledAlert;
  private Application application;
  private HashMap<String, Alert> managedAlerts = new HashMap<String, Alert>();

  // Controller/Model style

  private AlertManager() {
  }

  public AlertManager(Application myApplication) {
    this.application = myApplication;
  }

  public boolean alertExistsForSet(FestivalEnum fest, JSONObject setData, int week) throws JSONException {
    Integer setID = (Integer) setData.getInt(AndroidConstants.JSON_KEY_SETS__SET_ID);
    String hashKey = computeHashKey(fest, setID, week);
    return this.managedAlerts.containsKey(hashKey);
  }

  private String computeHashKey(FestivalEnum fest, int setID, int week) {
    return fest.getName() + "-" + setID + "-" + week;
  }

  public void addAlertForSet(FestivalEnum fest, JSONObject setData, int week, int minutesBefore) throws JSONException,
      FileNotFoundException, IOException {
    final Integer setID = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__SET_ID);
    final String hashKey = computeHashKey(fest, setID, week);

    if (alertExistsForSet(fest, setData, week)) {
      Alert existingAlert = managedAlerts.get(hashKey);

      // need to change existing alert

    } else {
      // otherwise create it
      Alert newAlert = new Alert(fest, week, setData, hashKey, 8);
      // Add to hash
      this.managedAlerts.put(hashKey, newAlert);
    }
    saveAlerts();
    setNextAlert();
  }

  public void removeAlertForSet(FestivalEnum fest, JSONObject setData, int week) throws FileNotFoundException,
      IOException, JSONException {
    final Integer setID = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__SET_ID);
    final String hashKey = computeHashKey(fest, setID, week);

    LogController.ERROR.logMessage(getClass().getSimpleName() + " - ERROR parsing set data, could not remove alert");

    // Destroy alert
    Alert savedAlert = this.managedAlerts.get(hashKey);
    savedAlert.cancel();

    // Remove from hash
    this.managedAlerts.remove(hashKey);
    saveAlerts();
    setNextAlert();
  }

  public void removeAllAlerts() throws FileNotFoundException, IOException {
    Collection<Alert> alerts = this.managedAlerts.values();

    for (Alert thisAlert : alerts) {
      thisAlert.cancel();
    }

    // After they are really cancelled
    managedAlerts.clear();
    saveAlerts();  //No need to call setnextalert...
  }

  public void saveAlerts() throws FileNotFoundException, IOException {
    FileOutputStream fos = this.application.openFileOutput(STORED_FILENAME, Context.MODE_PRIVATE);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(managedAlerts);
    File localFile = this.application.getFileStreamPath(STORED_FILENAME);
    LogController.ALERTS.logMessage("Saved local file " + localFile.getName() + " size:" + localFile.length());
  }

  public void loadAlerts() throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException {
    // Load from disk
    FileInputStream fis = this.application.openFileInput(STORED_FILENAME);
    ObjectInputStream ois = new ObjectInputStream(fis);
    managedAlerts = (HashMap<String, Alert>) ois.readObject();
    setNextAlert();
  }

  private void setNextAlert() {
    if (this.scheduledAlert != null) {
      scheduledAlert.cancel();
    }

    Alert nextAlert = findNextAlert();
    nextAlert.setAlarm(application);
    this.scheduledAlert = nextAlert;
  }

  public Alert findNextAlert() {
    if (managedAlerts == null) {
      return null;
    } else if (managedAlerts.isEmpty()) {
      return null;
    }

    // Guaranteed not empty
    Iterator<Entry<String, Alert>> alertIterator = managedAlerts.entrySet().iterator();
    Entry<String, Alert> earliestAlertEntry = alertIterator.next();

    while (alertIterator.hasNext()) {
      Entry<String, Alert> entry = alertIterator.next();
      String hashKey = entry.getKey();
      Alert alert = entry.getValue();

      if (alert.getSetTime().before(earliestAlertEntry.getValue().getSetTime())) {
        earliestAlertEntry = entry;
      }
    }
    return earliestAlertEntry.getValue();
  }

  @Override
  public int getNumberOfItems() {
    return managedAlerts.size();  //Initialized on instantiation, safe
  }

  @Override
  public HashMap<String, Alert> getAlerts() {
    return managedAlerts;
  }

}
