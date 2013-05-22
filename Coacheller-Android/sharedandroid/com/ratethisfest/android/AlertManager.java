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

  @Override
  public int getNumberOfItems() {
    return managedAlerts.size(); // Initialized on instantiation, safe
  }

  @Override
  public HashMap<String, Alert> getAlerts() {
    return managedAlerts;
  }

  //Called from the UI to add or update an existing alert
  public void addAlertForSet(FestivalEnum fest, JSONObject setData, int week, int minutesBefore) throws JSONException,
      FileNotFoundException, IOException {
    final Integer setID = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__SET_ID);
    final String hashKey = computeHashKey(fest, setID, week);
  
    if (alertExistsForSet(fest, setData, week)) {
      Alert existingAlert = managedAlerts.get(hashKey);
  
      // need to change existing alert
      LogController.ALERTS.logMessage("Warning: Code Missing, need to update existing alert");
  
    } else {
      // otherwise create it
      Alert newAlert = new Alert(fest, week, setData, hashKey, 8);
      // Add to hash
      this.managedAlerts.put(hashKey, newAlert);
    }
    saveAlerts();
    updateNextAlertWithAPI();
  }

  public void removeAlertForSet(FestivalEnum fest, JSONObject setData, int week) throws FileNotFoundException,
      IOException, JSONException {
    final Integer setID = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__SET_ID);
    final String hashKey = computeHashKey(fest, setID, week);
  
    LogController.ERROR.logMessage(getClass().getSimpleName() + " - ERROR parsing set data, could not remove alert");
  
    removeAlertWithHashKey(hashKey);
  }

  public boolean alertExistsForSet(FestivalEnum fest, JSONObject setData, int week) throws JSONException {
    Integer setID = (Integer) setData.getInt(AndroidConstants.JSON_KEY_SETS__SET_ID);
    String hashKey = computeHashKey(fest, setID, week);
    return this.managedAlerts.containsKey(hashKey);
  }

  private String computeHashKey(FestivalEnum fest, int setID, int week) {
    String hashKey = fest.getName() + "-" + setID + "-" + week;
    LogController.ALERTS.logMessage("Computed Hash Key: " + hashKey);
    return hashKey;
  }

  private void removeAlertWithHashKey(final String hashKey) throws FileNotFoundException, IOException {
    this.managedAlerts.remove(hashKey);
    saveAlerts();
    updateNextAlertWithAPI();
  }

  public void removeAllAlerts() throws FileNotFoundException, IOException {
    //Collection<Alert> alerts = this.managedAlerts.values();

    // After they are really cancelled
    managedAlerts.clear();
    saveAlerts();
    updateNextAlertWithAPI();  //Must be called to cancel any alerts with the API
  }

  public Alert getAlertWithHashKey(String hashKey) {
    return managedAlerts.get(hashKey);
    // Collection<Alert> alerts = this.managedAlerts.values();
    // for (Alert thisAlert : alerts) {
    // if (thisAlert.getHashKey().equals(hashKey)) {
    // return thisAlert;
    // }
    // }
    //
    // return null;
  }

  private Alert findNextAlert() {
    if (managedAlerts.isEmpty()) {
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

  // This should be the only function that sets and cancels alerts with the API
  private void updateNextAlertWithAPI() {
    Alert nextAlert = findNextAlert();
    String nextAlertDescription = "";
    if (nextAlert != null) {
      nextAlertDescription = nextAlert.getHashKey() + " " + nextAlert.getArtist() + " at "
          + nextAlert.getDayDateAsString();
    }
  
    String previousAlertDescription = "";
    if (this.scheduledAlert != null) {
      previousAlertDescription = this.scheduledAlert.getHashKey() + " " + this.scheduledAlert.getArtist() + " at "
          + this.scheduledAlert.getDayDateAsString();
    }
  
    if (nextAlert == null) { // there is no next alert
      LogController.ALERTS.logMessage("AlertManager.setNextAlert(): No next alert to schedule, previous was:"
          + previousAlertDescription);
    } else {
      if (this.scheduledAlert == null) { // there is a next alert and there was no previous alert
        LogController.ALERTS.logMessage("AlertManager.setNextAlert(): No previous alert, setting next alert:"
            + nextAlertDescription);
        nextAlert.setAlarm(application);
        this.scheduledAlert = nextAlert;
      } else if (this.scheduledAlert != nextAlert) { // there is a next alert and it is different from the previous
                                                     // alert
        LogController.ALERTS.logMessage("AlertManager.setNextAlert(): Scheduling next alert: " + nextAlertDescription
            + " previous alert was " + previousAlertDescription);
        scheduledAlert.cancel(this.application.getApplicationContext());
        nextAlert.setAlarm(application);
        this.scheduledAlert = nextAlert;
      } else if (this.scheduledAlert == nextAlert) { // there is a next alert and it is the same as the previous alert
        LogController.ALERTS.logMessage("AlertManager.setNextAlert(): No change, next alert same as current alert: "
            + nextAlertDescription);
      }
  
    }
  
  }

  private void saveAlerts() throws FileNotFoundException, IOException {
    FileOutputStream fos = this.application.openFileOutput(STORED_FILENAME, Context.MODE_PRIVATE);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(managedAlerts);
    File localFile = getLocalFile();
    LogController.ALERTS.logMessage("Saved local file " + localFile.getName() + " size:" + localFile.length());
  }

  public void loadAlerts() throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException {
    File localFile = getLocalFile(); // ???Throws NPE if file does not exist???
    if (localFile.exists()) {

      // Load from disk
      FileInputStream fis = this.application.openFileInput(STORED_FILENAME);
      ObjectInputStream ois = new ObjectInputStream(fis);
      managedAlerts = (HashMap<String, Alert>) ois.readObject();
      LogController.ALERTS.logMessage("AlertManager loaded file, size:" + localFile.length());
      updateNextAlertWithAPI();
    } else {

      LogController.ALERTS.logMessage("AlertManager save file does not yet exist");
    }

  }

  private File getLocalFile() {
    return this.application.getFileStreamPath(STORED_FILENAME);
  }

  public void exceptionLoadingAlerts(Exception e) {
    LogController.ERROR.logMessage("Error loading alerts from disk: " + e.getMessage());
    e.printStackTrace();
  }

  public void alertWentOff(String hashKey) {
    Alert alertWentOff = getAlertWithHashKey(hashKey);
    String alertMsg = "Go see " + alertWentOff.getArtist() + " at " + alertWentOff.getStage() + " for the set at "
        + alertWentOff.getSetTimeAsString() + " starting in " + alertWentOff.getTextIntervalUntilAlert();
    LogController.ALERTS.logMessage(alertMsg);

    // Here is where the application should alert the user to see the next set
    
    // Now remove the alert
    try {
      removeAlertWithHashKey(hashKey);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
