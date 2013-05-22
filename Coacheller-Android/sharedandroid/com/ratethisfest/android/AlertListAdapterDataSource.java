package com.ratethisfest.android;

import java.util.HashMap;

public interface AlertListAdapterDataSource {

  int getNumberOfItems();
  HashMap<String, Alert> getAlerts();

}
