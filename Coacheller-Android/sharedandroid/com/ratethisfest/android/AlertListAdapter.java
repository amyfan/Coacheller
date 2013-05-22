package com.ratethisfest.android;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.coacheller.R;
import com.ratethisfest.android.log.LogController;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class AlertListAdapter implements ListAdapter {

  private AlertListAdapterDataSource dataSource;
  private Context context;
  private List<Entry<String, Alert>> localAlertList;

  public AlertListAdapter(AlertListAdapterDataSource dataSource, Context context) {
    this.dataSource = dataSource;
    this.context = context;
    reSort();
  }

  public void reSort() {
    HashMap<String, Alert> alerts = dataSource.getAlerts();
    this.localAlertList = new ArrayList<Entry<String, Alert>>(alerts.entrySet());
    java.util.Collections.sort(this.localAlertList, new AlertEntryComparator());
  }

  //Only sorts by time
  private class AlertEntryComparator implements Comparator<Entry<String, Alert>> {
    @Override
    public int compare(Entry<String, Alert> argLeft, Entry<String, Alert> argRight) {
      Date setTimeLeft = argLeft.getValue().getSetTime();
      Date setTimeRight = argRight.getValue().getSetTime();
      return setTimeLeft.compareTo(setTimeRight);
    }
  }

  @Override
  public boolean hasStableIds() {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.hasStableIds()");
    return false;
  }

  @Override
  public boolean areAllItemsEnabled() {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.areAllItemsEnabled()");
    return false;
  }

  @Override
  public int getCount() {
    // LogController.LIST_ADAPTER.logMessage("AlertListAdapter.getCount()");
    return dataSource.getNumberOfItems();
  }

  @Override
  public void registerDataSetObserver(DataSetObserver observer) {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.registerDataSetObserver()");

  }

  @Override
  public int getViewTypeCount() {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.getViewTypeCount()");
    // This describes how many different item views are managed by this list
    // If all items are similar enough that everything can use the same type of view, answer is 1
    return 1;
  }

  // convertView optimization NOT needed
  // the proportion of visible/total items estimated to be very high
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    //LogController.LIST_ADAPTER.logMessage("AlertListAdapter.getView()");
    LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.row_alert_info, parent, false);
    
    TextView textTime = (TextView) rowView.findViewById(R.id.text_set_time);
    TextView textDayDate = (TextView) rowView.findViewById(R.id.dayDate);
    TextView textStage = (TextView) rowView.findViewById(R.id.text_stage);
    TextView textTimeRemaining = (TextView) rowView.findViewById(R.id.text_timeUntilAlert);
    TextView textArtistName = (TextView) rowView.findViewById(R.id.text_artist_name);

    Entry<String, Alert> currentEntry = this.localAlertList.get(position);
    Alert currentAlert = currentEntry.getValue();
    textDayDate.setText(currentAlert.getDayDateAsString());
    textTime.setText(currentAlert.getSetTimeAsString());
    textStage.setText(currentAlert.getStage());
    textArtistName.setText(currentAlert.getArtist());
    textTimeRemaining.setText(currentAlert.getTextIntervalUntilAlert());


    return rowView;
  }

  @Override
  public Object getItem(int position) {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.getItem()");
    return null;
  }

  @Override
  public long getItemId(int position) {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.getItemId()");
    return 0;
  }

  @Override
  public int getItemViewType(int position) {
    //This one is called often
    //LogController.LIST_ADAPTER.logMessage("AlertListAdapter.getItemViewType()");
    return 0;
  }

  @Override
  public boolean isEmpty() {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.isEmpty()");
    return false;
  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver observer) {
    LogController.LIST_ADAPTER.logMessage("AlertListAdapter.unregisterDataSetObserver()");

  }

  @Override
  public boolean isEnabled(int position) {
    //This one is called a whole lot
    //LogController.LIST_ADAPTER.logMessage("AlertListAdapter.isEnabled()");
    return false;
  }

}
