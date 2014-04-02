package com.ratethisfest.android.alert;

import java.util.HashMap;

public interface AlertListAdapterDataSource {

  int getNumberOfItems();
  HashMap<String, Alert> getAlerts();

}
