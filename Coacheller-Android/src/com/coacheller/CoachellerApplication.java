package com.coacheller;

import java.util.Calendar;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CoachellerApplication extends Application {
  
  private static Calendar _cal;

  public static void debug(Context context, String out) {
    Log.d(context.getString(R.string.app_name), out);
  }
  
  /**
   * populateSpinnerWithArray Populates dropdown boxes with options, based on
   * string arrays.
   * 
   * @param spinner
   * @param stringArrayResId
   */
  public static void populateSpinnerWithArray(Spinner spinner, int stringArrayResId) {
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
        android.R.layout.simple_spinner_item, spinner.getContext().getResources()
            .getStringArray(stringArrayResId));
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
  }
  
  public static void initCalendar() {
    if (_cal == null) {
      _cal = Calendar.getInstance();
    }
  }
  
  public static int whichWeekIsToday() {
    initCalendar();
    if (_cal.get(Calendar.DAY_OF_MONTH) < 19) {
      return 1;
    } else {
      return 2;
    }
  }

}
