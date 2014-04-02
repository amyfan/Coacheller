package com.coacheller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.coacheller.data.CoachSetListAdapter;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.data.CustomSetListAdapter;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.android.ui.FestivalActivity;
import com.ratethisfest.data.AndroidConstants;

public class CoachellerActivity extends FestivalActivity {

	private CoachSetListAdapter coachSetListAdapter;

	@Override
	protected CustomSetListAdapter getSetListAdapter() {
		return coachSetListAdapter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LogController.LIFECYCLE_ACTIVITY
				.logMessage("CoachellerActivity Launched: " + this);
		_application = (CoachellerApplication) getApplication();
		_application.registerActivity(CoachellerActivity.this);

		sortMode = AndroidConstants.SORT_TIME;

		_selectedIdToValue.put(R.id.radio_button_week1, 1);
		_selectedIdToValue.put(R.id.radio_button_week2, 2);

		_selectedIdToValue.put(R.id.radio_button_score1, 1);
		_selectedIdToValue.put(R.id.radio_button_score2, 2);
		_selectedIdToValue.put(R.id.radio_button_score3, 3);
		_selectedIdToValue.put(R.id.radio_button_score4, 4);
		_selectedIdToValue.put(R.id.radio_button_score5, 5);

		_ratingSelectedValueToId.put("1", R.id.radio_button_score1);
		_ratingSelectedValueToId.put("2", R.id.radio_button_score2);
		_ratingSelectedValueToId.put("3", R.id.radio_button_score3);
		_ratingSelectedValueToId.put("4", R.id.radio_button_score4);
		_ratingSelectedValueToId.put("5", R.id.radio_button_score5);

		setContentView(R.layout.sets_list);
		coachSetListAdapter = new CoachSetListAdapter(this, _application,
				AndroidConstants.JSON_KEY_SETS__TIME_ONE,
				AndroidConstants.JSON_KEY_SETS__STAGE_ONE,
				_application.getUserRatingsJAHM());
		getSetListAdapter().setData(new JSONArray());

		ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
		viewSetsList.setAdapter(getSetListAdapter());
		viewSetsList.setOnItemClickListener(this);

		Button buttonSearchSets = (Button) this
				.findViewById(R.id.buttonChangeToSearchSets);
		buttonSearchSets.setOnClickListener(this);

		Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
		String[] searchTypeStringArray = _application.getResources()
				.getStringArray(R.array.search_types);
		AndroidUtils.populateSpinnerWithArray(spinnerSortType,
				android.R.layout.simple_spinner_item, searchTypeStringArray,
				android.R.layout.simple_spinner_dropdown_item);
		spinnerSortType.setOnItemSelectedListener(this);

		try {
			_application.getAlertManager().loadAlerts();
		} catch (StreamCorruptedException e) {
			_application.getAlertManager().exceptionLoadingAlerts(e);
		} catch (FileNotFoundException e) {
			_application.getAlertManager().exceptionLoadingAlerts(e);
		} catch (IOException e) {
			_application.getAlertManager().exceptionLoadingAlerts(e);
		} catch (ClassNotFoundException e) {
			_application.getAlertManager().exceptionLoadingAlerts(e);
		}

		// Above here is stuff to be done once

		_networkIOHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);

				if (msg.what == AndroidConstants.THREAD_UPDATE_UI) {
					LogController.LIFECYCLE_THREAD
							.logMessage("Executing update UI thread callback ");
					redrawUI();
				}

				if (msg.what == AndroidConstants.THREAD_SUBMIT_RATING) {
					LogController.LIFECYCLE_THREAD
							.logMessage("Executing submit rating thread callback ");

					try {
						_application.doSubmitRating(lastRating);

						// Don't need since we are refreshing
						// ListView viewSetsList = (ListView)
						// findViewById(R.id.viewSetsList);
						// viewSetsList.invalidateViews();

						// If this is removed, uncomment ListView and invalidate
						// above ^^^
						refreshData();

					} catch (JSONException je) {
						je.printStackTrace();
					} catch (Exception e) {
						showDialog(AndroidConstants.DIALOG_NETWORK_ERROR);
					}
				}
			}

		};

		// TODO: why is this deprecated?
		checkForExtraData();
		// refreshData();
	}

}
