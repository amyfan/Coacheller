package com.coacheller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coacheller.data.CoachSetListAdapter;
import com.coacheller.ui.SearchSetsActivity;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.CustomSetListAdapter;
import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.android.ui.FestivalActivity;
import com.ratethisfest.data.AndroidConstants;
import com.ratethisfest.shared.CalendarUtils;

public class CoachellerActivity extends FestivalActivity implements OnCheckedChangeListener {

  private CoachSetListAdapter coachSetListAdapter;

  @Override
  protected CustomSetListAdapter getSetListAdapter() {
    return coachSetListAdapter;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogController.LIFECYCLE_ACTIVITY.logMessage("CoachellerActivity Launched: " + this);
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
    coachSetListAdapter = new CoachSetListAdapter(this, _application, AndroidConstants.JSON_KEY_SETS__TIME_ONE,
        AndroidConstants.JSON_KEY_SETS__STAGE_ONE, _application.getUserRatingsJAHM());
    getSetListAdapter().setData(new JSONArray());

    ListView viewSetsList = (ListView) findViewById(R.id.viewSetsList);
    viewSetsList.setAdapter(getSetListAdapter());
    viewSetsList.setOnItemClickListener(this);

    Button buttonSearchSets = (Button) this.findViewById(R.id.buttonChangeToSearchSets);
    buttonSearchSets.setOnClickListener(this);

    Spinner spinnerSortType = (Spinner) findViewById(R.id.spinner_sort_by);
    String[] searchTypeStringArray = _application.getResources().getStringArray(R.array.search_types);
    AndroidUtils.populateSpinnerWithArray(spinnerSortType, android.R.layout.simple_spinner_item, searchTypeStringArray,
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
          LogController.LIFECYCLE_THREAD.logMessage("Executing update UI thread callback ");
          redrawUI();
        }

        if (msg.what == AndroidConstants.THREAD_SUBMIT_RATING) {
          LogController.LIFECYCLE_THREAD.logMessage("Executing submit rating thread callback ");

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

  @Override
  protected void checkForExtraData() {
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();

    if (bundle == null) {
      return;
    }

    String year = intent.getExtras().getString(SearchSetsActivity.YEAR);
    String week = intent.getExtras().getString(SearchSetsActivity.WEEK);
    String day = intent.getExtras().getString(SearchSetsActivity.DAY);

    LogController.OTHER.logMessage("CoachellerActivity setting search suggestion of year[" + year + "] week[" + week
        + "] day[" + day + "]");
    _application.setYearToQuery(Integer.valueOf(year));
    _application.setDayToQuery(day);
    _application.setWeekToQuery(Integer.valueOf(week));
  }

  // Any button in any view or dialog was clicked
  @Override
  public void onClick(View viewClicked) {

    // OK clicked on first use dialog
    if (viewClicked.getId() == R.id.button_firstuse_ok) {
      clickDialogFirstUseButtonOK();
    }

    // "OK" clicked to submit email address
    if (viewClicked.getId() == R.id.button_provideEmail) {
      clickDialogConfirmEmailButtonOK();
    }

    if (viewClicked.getId() == R.id.button_declineEmail) {
      dialogEmail.dismiss();
    }

    if (viewClicked.getId() == R.id.buttonChangeToSearchSets) {
      System.out.println("Button: Switch Day");
      Intent intent = new Intent();
      intent.setClass(this, SearchSetsActivity.class);
      startActivity(intent);
    }

    // Submit rating for a set
    if (viewClicked.getId() == R.id.button_go_rate_inline) { // Selections
      clickDialogSubmitRatingButtonOK();
    } // End rating dialog submitted

    // Submit rating for a set
    if (viewClicked.getId() == R.id.button_go_rate_above) { // Selections
      clickDialogSubmitRatingButtonOK();
    } // End rating dialog submitted

    // Submit rating for a set and do Facebook post
    if (viewClicked.getId() == R.id.button_go_fb) {
      try {
        clickDialogSubmitRatingFacebook();
      } catch (JSONException e) {
        System.out.println("JSONException gathering data for Facebook post");
        e.printStackTrace();
      }
    }

    // Submit rating for a set and do Twitter post
    if (viewClicked.getId() == R.id.button_go_tweet) {
      try {
        clickDialogSubmitRatingTwitter();
      } catch (JSONException e) {
        System.out.println("JSONException gathering data for Twitter post");
        e.printStackTrace();
      }
    }

    if (viewClicked.getId() == R.id.button_network_error_ok) {
      System.out.println("Clicked dismiss network error dialog");
      dialogNetworkError.dismiss();
    }

    if (dialogAlerts != null && dialogAlerts.isShowing()) {
      // This should work instead of having to give a globally unique ID
      // to each button
      handleClickDialogAlerts(viewClicked);
    }
  }

  @Override
  protected Dialog createDialogRate() {
    dialogRate = new Dialog(this);
    dialogRate.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialogRate.setContentView(R.layout.dialog_rate_set);

    RadioGroup weekGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_week);
    weekGroup.setOnCheckedChangeListener(this);

    // Setup 'X' close widget
    ImageView close_dialog = (ImageView) dialogRate.findViewById(R.id.imageView_custom_dialog_close);
    close_dialog.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        dialogRate.dismiss();
      }
    });

    Button buttonRateAbove = (Button) dialogRate.findViewById(R.id.button_go_rate_above);
    buttonRateAbove.setOnClickListener(this);

    Button buttonRateInline = (Button) dialogRate.findViewById(R.id.button_go_rate_inline);
    buttonRateInline.setOnClickListener(this);

    ImageButton buttonFB = (ImageButton) dialogRate.findViewById(R.id.button_go_fb);
    buttonFB.setOnClickListener(this);

    ImageButton buttonTweet = (ImageButton) dialogRate.findViewById(R.id.button_go_tweet);
    buttonTweet.setOnClickListener(this);

    return dialogRate;
  }

  @Override
  protected void prepareDialogRateSet() {
    // _lastRateDialog.setTitle("Rate this Set!");
    try {
      TextView subtitleText = (TextView) dialogRate.findViewById(R.id.text_rateBand_subtitle);
      subtitleText.setText(lastSetSelected.getString("artist")); // TODO
      // NPE
      // Here

    } catch (JSONException e) {
      LogController.OTHER.logMessage("JSONException assigning Artist name to Rating dialog");
      e.printStackTrace();
    }

    int festNumberOfWeeks = CalendarUtils.getFestivalMaxNumberOfWeeks(_application.getFestival());
    int weeksOver;
    if (festNumberOfWeeks == 1) {
      weeksOver = 0;
    } else {
      weeksOver = CalendarUtils.getlastFestWeekExpired(_application.getFestival());
    }

    RadioGroup weekGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_week);
    RadioButton buttonWeek1 = (RadioButton) dialogRate.findViewById(R.id.radio_button_week1);
    RadioButton buttonWeek2 = (RadioButton) dialogRate.findViewById(R.id.radio_button_week2);

    int idChanged = -1;

    if (weeksOver == 0) { // Still in week 1, disable rating week 2
      LogController.MULTIWEEK.logMessage(("Recommending week 1 for ratings based on schedule"));
      buttonWeek1.setClickable(true);
      buttonWeek2.setClickable(false);
      buttonWeek1.setChecked(true);
      idChanged = buttonWeek1.getId();

    } else if (weeksOver >= 1) { // Week 2 or later, enable rating week 2
      LogController.MULTIWEEK.logMessage(("Recommending week 2 for ratings based on schedule"));
      buttonWeek1.setClickable(true);
      buttonWeek2.setClickable(true);
      buttonWeek2.setChecked(true);
      idChanged = buttonWeek2.getId();

    } else {
      // Don't suggest a week
      LogController.MULTIWEEK.logMessage(("Unable to guess what week this Fest is in"));
      weekGroup.clearCheck();
    }

    int numWeeks = festNumberOfWeeks;
    if (numWeeks == 1) {
      weekGroup.setVisibility(View.INVISIBLE);
      TextView selectWeekText = (TextView) dialogRate.findViewById(R.id.layout_radio_choice_minutes_textline_text);
      selectWeekText.setVisibility(View.GONE);
    }

    // TODO pick user's last rating
    onCheckedChanged(weekGroup, idChanged);

    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)) {
      ImageButton buttonFB = (ImageButton) dialogRate.findViewById(R.id.button_go_fb);
      buttonFB.setImageResource(R.drawable.post_facebook_large);
      System.out.println(buttonFB.getPaddingTop() + " " + buttonFB.getPaddingLeft() + " " + buttonFB.getPaddingBottom()
          + " " + buttonFB.getPaddingRight());
      buttonFB.setPadding(7, 3, 7, 10);

    }

    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {
      ImageButton buttonTweet = (ImageButton) dialogRate.findViewById(R.id.button_go_tweet);
      buttonTweet.setImageResource(R.drawable.post_twitter_large);
      buttonTweet.setPadding(7, 3, 7, 10);
    }

    if (_application.getAuthModel().havePermission(AuthModel.PERMISSION_FACEBOOK_POSTWALL)
        && _application.getAuthModel().havePermission(AuthModel.PERMISSION_TWITTER_TWEET)) {

      Button buttonRateAbove = (Button) dialogRate.findViewById(R.id.button_go_rate_above);
      buttonRateAbove.setVisibility(View.VISIBLE);

      Button buttonRateInline = (Button) dialogRate.findViewById(R.id.button_go_rate_inline);
      buttonRateInline.setVisibility(View.GONE);
    }
  }

  @Override
  protected void rateDialogSubmitRating() {
    // TODO this is in the code in 2 places
    // RadioGroup weekGroup = (RadioGroup)
    // rateDialog.findViewById(R.id.radio_pick_week);
    int weekSelectedId = ((RadioGroup) dialogRate.findViewById(R.id.radio_pick_week)).getCheckedRadioButtonId();

    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();
    String submittedRating = _selectedIdToValue.get(scoreSelectedId).toString();

    EditText noteWidget = (EditText) dialogRate.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();

    dialogRate.dismiss();

    String weekNumber = _selectedIdToValue.get(weekSelectedId) + "";

    LogController.OTHER.logMessage("Selected Week[" + weekNumber + "] Score[" + submittedRating + "] WeekId["
        + weekSelectedId + "] ScoreId[" + scoreSelectedId + "]");
    try {

      lastRating = new JSONObject();

      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__SET_ID,
          lastSetSelected.get(AndroidConstants.JSON_KEY_SETS__SET_ID));
      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__WEEK, weekNumber);
      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__SCORE, submittedRating);
      lastRating.put(AndroidConstants.JSON_KEY_RATINGS__NOTES, submittedNote);

      LogController.OTHER.logMessage("State of last rating object before launching submit thread: "
          + lastRating.toString());
      launchSubmitRatingThread();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void onCheckedChanged(RadioGroup clickedGroup, int checkedId) {
    // todo should use switch
    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    EditText noteWidget = (EditText) dialogRate.findViewById(R.id.editText_commentsForSet);

    // Selection changed week 1<->2
    if (clickedGroup == dialogRate.findViewById(R.id.radio_pick_week)) {
      try {
        if (checkedId != R.id.radio_button_week1 && checkedId != R.id.radio_button_week2) {
          // Not sure what is selected, clear rating check
          scoreGroup.clearCheck();
          noteWidget.setText("");
        } else {
          JSONObject ratingToCheck = null;
          ;
          if (checkedId == R.id.radio_button_week1) {
            ratingToCheck = lastRatingPair.first;
          } else if (checkedId == R.id.radio_button_week2) {
            ratingToCheck = lastRatingPair.second;
          }
          if (ratingToCheck != null) {
            int buttonIdToCheck = _ratingSelectedValueToId.get(ratingToCheck
                .getString(AndroidConstants.JSON_KEY_RATINGS__SCORE));
            RadioButton buttonToCheck = (RadioButton) dialogRate.findViewById(buttonIdToCheck);
            buttonToCheck.setChecked(true);
            noteWidget.setText(ratingToCheck.getString(AndroidConstants.JSON_KEY_RATINGS__NOTES));
          } else {
            scoreGroup.clearCheck();
            noteWidget.setText("");
          }

        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      scoreGroup.invalidate();
    }
  }

  @Override
  protected SocialNetworkPost _buildSocialNetworkPost() throws JSONException {
    SocialNetworkPost post = new SocialNetworkPost();
    // Build data from dialog here
    // TODO this is in the code in 2 places
    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    String submittedRating = _selectedIdToValue.get(scoreGroup.getCheckedRadioButtonId()).toString();

    EditText noteWidget = (EditText) dialogRate.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();
    String artistName = lastSetSelected.getString("artist");

    post.rating = submittedRating;
    post.note = submittedNote;
    post.artistName = artistName;
    return post;
  }

  @Override
  protected boolean _lastRateDialogVerify() {
    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();
    if (scoreSelectedId == -1) {
      Toast selectEverything = Toast.makeText(this, "Please select a rating for this Set", 25);
      selectEverything.show();
      return false;
    }
    return true;
  }
}
