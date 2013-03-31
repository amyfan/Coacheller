package com.ratethisfest.android;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ratethisfest.android.data.LoginData;
import com.ratethisfest.shared.HttpConstants;

public class AndroidUtils {

  public static String bundleValues(Bundle inputBundle) {
    StringBuilder returnString = new StringBuilder();
    int count = 0;
    for (String s : inputBundle.keySet()) {
      returnString.append(s + ": " + inputBundle.get(s));
      count++;
    }
    return "[" + count + "]: " + returnString.toString();
  }

  /**
   * populateSpinnerWithArray Populates dropdown boxes with options, based on
   * string arrays.
   * 
   * @param spinner
   * @param stringArrayResId
   */
  public static void populateSpinnerWithArray(Spinner spinner, int textViewResId,
      int stringArrayResId, int dropDownViewResId) {
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(), textViewResId,
        spinner.getContext().getResources().getStringArray(stringArrayResId));
    adapter.setDropDownViewResource(dropDownViewResId);
    spinner.setAdapter(adapter);
  }

  /**
   * TODO: account for weekend for coachella
   * 
   * @param year
   * @param dayToQuery
   * @param loginData
   * @return
   */
  public static List<NameValuePair> createGetQueryParamsArrayList(String year, String dayToQuery,
      LoginData loginData) {
    List<NameValuePair> params = new ArrayList<NameValuePair>();

    params.add(new BasicNameValuePair(HttpConstants.PARAM_YEAR, year));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_DAY, dayToQuery));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TYPE, loginData.loginType + ""));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_ID, loginData.accountIdentifier));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TOKEN, loginData.accountToken));
    if (loginData.emailAddress != null) {
      params.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, loginData.emailAddress));
    }

    return params;
  }

  /**
   * 
   * @param year
   * @param set_id
   * @param scoreSelectedValue
   * @param notes
   * @param loginData
   * @param weekend
   * @return
   */
  public static List<NameValuePair> createSubmitRatingParamsArrayList(String year, String set_id,
      String scoreSelectedValue, String notes, LoginData loginData, String weekend) {
    List<NameValuePair> params = new ArrayList<NameValuePair>(1);

    params.add(new BasicNameValuePair(HttpConstants.PARAM_SET_ID, set_id));
    if (weekend != null) {
      params.add(new BasicNameValuePair(HttpConstants.PARAM_WEEKEND, weekend));
    }
    params.add(new BasicNameValuePair(HttpConstants.PARAM_SCORE, scoreSelectedValue));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_NOTES, notes));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TYPE, loginData.loginType + ""));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_ID, loginData.accountIdentifier));
    params.add(new BasicNameValuePair(HttpConstants.PARAM_AUTH_TOKEN, loginData.accountToken));
    if (loginData.emailAddress != null) {
      params.add(new BasicNameValuePair(HttpConstants.PARAM_EMAIL, loginData.emailAddress));
    }

    return params;
  }

}
