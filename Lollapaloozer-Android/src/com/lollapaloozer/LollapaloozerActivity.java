package com.lollapaloozer;

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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lollapaloozer.data.LollaSetListAdapter;
import com.lollapaloozer.ui.SearchSetsActivity;
import com.ratethisfest.android.AndroidUtils;
import com.ratethisfest.android.auth.AuthModel;
import com.ratethisfest.android.data.CustomSetListAdapter;
import com.ratethisfest.android.data.SocialNetworkPost;
import com.ratethisfest.android.log.LogController;
import com.ratethisfest.android.ui.FestivalActivity;
import com.ratethisfest.data.AndroidConstants;

public class LollapaloozerActivity extends FestivalActivity {

  private LollaSetListAdapter lollaSetListAdapter;

  @Override
  protected CustomSetListAdapter getSetListAdapter() {
    return lollaSetListAdapter;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogController.LIFECYCLE_ACTIVITY.logMessage("LollapaloozerActivity Launched: " + this);
    _application = (LollapaloozerApplication) getApplication();
    _application.registerActivity(LollapaloozerActivity.this);

    sortMode = AndroidConstants.SORT_TIME;

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
    lollaSetListAdapter = new LollaSetListAdapter(this, _application, AndroidConstants.JSON_KEY_SETS__TIME_ONE,
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
    String day = intent.getExtras().getString(SearchSetsActivity.DAY);

    LogController.OTHER.logMessage("LollapaloozerActivity setting search suggestion of year[" + year + "] day[" + day + "]");
    _application.setYearToQuery(Integer.valueOf(year));
    _application.setDayToQuery(day);
    _application.setWeekToQuery(1);
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

  protected void rateDialogSubmitRating() {
    RadioGroup scoreGroup = (RadioGroup) dialogRate.findViewById(R.id.radio_pick_score);
    int scoreSelectedId = scoreGroup.getCheckedRadioButtonId();
    String submittedRating = _selectedIdToValue.get(scoreSelectedId).toString();

    EditText noteWidget = (EditText) dialogRate.findViewById(R.id.editText_commentsForSet);
    String submittedNote = noteWidget.getText().toString();

    dialogRate.dismiss();

    String weekNumber = "1";

    LogController.OTHER.logMessage("ScoreId[" + scoreSelectedId + "]");
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
