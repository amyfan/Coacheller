package com.ratethisfest.android;

import java.util.Collection;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;

import com.ratethisfest.android.log.LogController;

public class AlertManager {
  private Application application;
  private HashMap<String, Alert> managedAlerts = new HashMap<String, Alert>();

  // Controller/Model style

  private AlertManager() {
  }

  public AlertManager(Application myApplication) {
    this.application = myApplication;
  }

  public boolean alertExistsForSet(JSONObject setData, int week) {

    Integer setID;
    try {
      setID = (Integer)setData.getInt(AndroidConstants.JSON_KEY_SETS__SET_ID);
      
    } catch (JSONException e) {
      LogController.ERROR.logMessage(e.getClass().getSimpleName() + " parsing selected set ID");
      e.printStackTrace();
      return false;
    }
    
    String hashKey = computeHashKey(setID, week);
    return this.managedAlerts.containsKey(hashKey);
  }

  private String computeHashKey(int setID, int week) {
    return setID + "-" + week;
  }

  // Consider only setId as input
  public void addAlertForSet(JSONObject setData, int week, int minutesBefore) {
    Integer setID;
    try {
      setID = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__SET_ID);
    } catch (JSONException e) {
      LogController.ERROR.logMessage(getClass().getSimpleName() + " - ERROR parsing set data, could not add alert");
      e.printStackTrace();
      return; // Give up!!!
    }

    if (alertExistsForSet(setData, week)) {
      // if alert exists, update it
    } else {
      // otherwise create it
      Alert newAlert = new Alert(this.application, null, 20);
      // Add to hash
      String hashKey = computeHashKey(setID, week);
      this.managedAlerts.put(hashKey, newAlert);
    }
  }

  public void removeAlertForSet(JSONObject setData, int week) {
    Integer setID;
    try {
      setID = (Integer) setData.get(AndroidConstants.JSON_KEY_SETS__SET_ID);
    } catch (JSONException e) {
      LogController.ERROR.logMessage(getClass().getSimpleName() + " - ERROR parsing set data, could not remove alert");
      e.printStackTrace();
      return; // Give up!!!
    }
    
    String hashKey = computeHashKey(setID, week);

    // Destroy alert
    Alert savedAlert = this.managedAlerts.get(hashKey);
    savedAlert.cancel();

    // Remove from hash
    this.managedAlerts.remove(hashKey);

  }

  public void removeAllAlerts() {
    Collection<Alert> alerts = this.managedAlerts.values();

    for (Alert thisAlert : alerts) {
      thisAlert.cancel();
    }

    // After they are really cancelled
    managedAlerts.clear();
  }

}
