package com.ratethisfest.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import auth.logins.ServletConfig;
import auth.logins.ServletInterface;
import auth.logins.data.LoginStatus;
import auth.logins.other.LoginType;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.ratethisfest.client.ClientResources;
import com.ratethisfest.client.Coacheller_AppEngine;
import com.ratethisfest.client.ComparatorUtils;
import com.ratethisfest.client.FlowControl;
import com.ratethisfest.client.FestivalService;
import com.ratethisfest.client.FestivalServiceAsync;
import com.ratethisfest.client.PageToken;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.Base64Coder;
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

public class MainRateComposite extends Composite {

  private static final Logger logger = Logger.getLogger(MainRateComposite.class.getName());
  private final FestivalServiceAsync festivalService = GWT.create(FestivalService.class);

  private static final String ADMIN_NAME = "Amy Fan";
  private static final String ADMIN_ERROR = "You do not have permission to do this. Sorry.";
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network " + "connection and try again.";

  private List<Set> setsList = new ArrayList<Set>();
  private List<RatingGwt> ratingsList;
  private Set _targetSet;

  private static Binder uiBinder = GWT.create(Binder.class);

  interface Binder extends UiBinder<Widget, MainRateComposite> {
  }

  @UiField
  Label title;

  @UiField
  Label subtitle;

  @UiField
  Label infoBox;

  @UiField
  Label emailLabel;

  @UiField
  Label scoreLabel;

  @UiField
  Label notesLabel;

  @UiField
  ListBox weekInput;

  @UiField
  RadioButton scoreOneRadioButton;

  @UiField
  RadioButton scoreTwoRadioButton;

  @UiField
  RadioButton scoreThreeRadioButton;

  @UiField
  RadioButton scoreFourRadioButton;

  @UiField
  RadioButton scoreFiveRadioButton;

  @UiField
  TextBox notesInput;

  @UiField
  com.google.gwt.user.client.ui.Button buttonRate;
  @UiField
  Button buttonRateFacebook;
  @UiField
  Button buttonRateTwitter;

  // DISABLED FOR NOW
  @UiField
  com.google.gwt.user.client.ui.Button emailButton;

  @UiField
  com.google.gwt.user.client.ui.Button backButton;

  // ADMIN PANEL:
  @UiField
  com.google.gwt.user.client.ui.Button updateSetButton;

  @UiField
  com.google.gwt.user.client.ui.Button recalculateButton;

  @UiField
  com.google.gwt.user.client.ui.Button clearMyRatingButton;

  @UiField
  com.google.gwt.user.client.ui.Button clearAllRatingButton;

  @UiField
  RatingsTable ratingsTable;

  public MainRateComposite(Set targetSet) {
    _targetSet = targetSet;
    initWidget(uiBinder.createAndBindUi(this));
    initUiElements();
    retrieveRatings();
  }

  // public MainRateComposite(String ownerEmail) {
  // this();
  // this.ownerEmail = ownerEmail;
  // retrieveSets();
  // retrieveRatings();
  // emailLabel.setText(ownerEmail);
  // }

  private void initUiElements() {
    LoginStatus loginStatus = Coacheller_AppEngine.getLoginStatus();

    title.setText(_targetSet.getArtistName());
    subtitle.setText("Rate This Set");

    // Compute number of weeks in this fest
    FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();
    int festivalMaxNumberOfWeeks = fest.getNumberOfWeeks();

    for (int i = 0; i < festivalMaxNumberOfWeeks; i++) { // Populate weekend selector
      weekInput.insertItem("Week " + (i + 1), i);
    }

    if (festivalMaxNumberOfWeeks == 1) {
      weekInput.setVisible(false); // Render weekend selector invisible, value is still needed
    }

    ListDataProvider<RatingGwt> listDataProvider = new ListDataProvider<RatingGwt>();
    listDataProvider.addDataDisplay(ratingsTable);
    ratingsList = listDataProvider.getList();

    scoreLabel.setText("Score");
    scoreOneRadioButton.setText("1");
    scoreTwoRadioButton.setText("2");
    scoreThreeRadioButton.setText("3");
    scoreFourRadioButton.setText("4");
    scoreFiveRadioButton.setText("5");

    notesLabel.setText("Notes (optional)");

    // Set up rate facebook/twitter image buttons
    ClientResources resources = GWT.create(ClientResources.class);

    buttonRate.setTitle("Add Rating");

    ImageResource facebookResource;
    if (loginStatus.isLoggedIn(LoginType.FACEBOOK)) {
      facebookResource = resources.post_facebook_large();
    } else {
      facebookResource = resources.post_facebook_small();
    }
    Image facebookImage = new Image(facebookResource);
    facebookImage.setHeight("42");
    buttonRateFacebook.getElement().getStyle().setProperty("padding", "0px 0px");
    buttonRateFacebook.getElement().appendChild(facebookImage.getElement());
    buttonRateFacebook.setTitle("Share on Facebook");

    ImageResource twitterResource;
    if (loginStatus.isLoggedIn(LoginType.TWITTER)) {
      twitterResource = resources.post_twitter_large();
    } else {
      twitterResource = resources.post_twitter_small();
    }
    Image twitterImage = new Image(twitterResource);
    twitterImage.setHeight("42");
    buttonRateTwitter.getElement().getStyle().setProperty("padding", "0px 0px");
    buttonRateTwitter.getElement().appendChild(twitterImage.getElement());
    buttonRateTwitter.setTitle("Share on Twitter");

    weekInput.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        loadRatingContents();
      }
    });

    Element androidElement = getElement().getFirstChildElement().getFirstChildElement();
    final Animation androidAnimation = new AndroidAnimation(androidElement);

    notesInput.addKeyPressHandler(new KeyPressHandler() {
      @Override
      public void onKeyPress(KeyPressEvent event) {
        if ((event.getCharCode()) == 13) {
          addRating();

          androidAnimation.run(400);
        }
      }
    });

    // Handlers
    RateClickHandler multiButtonClickHandler = new RateClickHandler(androidAnimation);
    buttonRate.addClickHandler(multiButtonClickHandler);
    buttonRateFacebook.addClickHandler(multiButtonClickHandler);
    buttonRateTwitter.addClickHandler(multiButtonClickHandler);

    ratingsTable.deleteColumn.setFieldUpdater(new FieldUpdater<RatingGwt, String>() {
      @Override
      public void update(int index, RatingGwt rating, String value) {
        deleteRating(rating);
      }
    });

    emailButton.setVisible(false);
    backButton.setVisible(false);
    clearAllRatingButton.setVisible(false);
    ratingsTable.setVisible(false);

    updateSetButton.setVisible(false);
    recalculateButton.setVisible(false);
    clearMyRatingButton.setVisible(false);

    // admin functions
    if (loginStatus.getProperty(LoginStatus.PROPERTY_PERSON_NAME).equals(ADMIN_NAME)) {
      MiscClickHandler miscButtonClickHandler = new MiscClickHandler(androidAnimation);
      updateSetButton.setVisible(true);
      updateSetButton.addClickHandler(miscButtonClickHandler);
      recalculateButton.setVisible(true);
      recalculateButton.addClickHandler(miscButtonClickHandler);
      clearMyRatingButton.setVisible(true);
      clearMyRatingButton.addClickHandler(miscButtonClickHandler);
    }
  }

  @Override
  public String getTitle() {
    return PageToken.RATE.getValue();
  }

  private void retrieveRatings() {
    // TODO: year input eventually
    festivalService.getRatingsForSet(_targetSet, new AsyncCallback<List<RatingGwt>>() {

      @Override
      public void onFailure(Throwable caught) {
        logger.info("Failed to get ratings for set: " + _targetSet.getId());
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      @Override
      public void onSuccess(List<RatingGwt> result) {
        logger.info("Got " + result.size() + " ratings for set: " + _targetSet.getId());
        ratingsList.clear();
        ratingsList.addAll(result);
        Collections.sort(ratingsList, ComparatorUtils.RATING_NAME_COMPARATOR);
        loadRatingContents();
      }
    });
  }

  // The value of the week input selector is automatically considered
  // This works for Coacheller also as the selector still exists in a hidden state
  private RatingGwt getRatingForSetAndWeekInput(Set set) {
    for (RatingGwt rating : ratingsList) {
      if (_targetSet.getId().equals(rating.getSetId()) // Same set
          && getSelectedWeekFromUi() == rating.getWeekend()) { // And same week
        return rating;
      }
    }
    return null;
  }

  // Returns the fest week the user has selected on the UI, NOT THE [zero-based] INDEX
  // For fests with 1 week, the control will be set to week 1 and hidden so it cannot be changed
  private int getSelectedWeekFromUi() {
    return weekInput.getSelectedIndex() + 1;
  }

  // Returns the score the user has selected on the UI
  private String getSelectedScoreFromUi() {
    String score = null;
    if (scoreOneRadioButton.getValue()) {
      score = scoreOneRadioButton.getText();
    } else if (scoreTwoRadioButton.getValue()) {
      score = scoreTwoRadioButton.getText();
    } else if (scoreThreeRadioButton.getValue()) {
      score = scoreThreeRadioButton.getText();
    } else if (scoreFourRadioButton.getValue()) {
      score = scoreFourRadioButton.getText();
    } else if (scoreFiveRadioButton.getValue()) {
      score = scoreFiveRadioButton.getText();
    }
    return score;
  }

  private void loadRatingContents() {
    logger.info("Configuring dialog with rating info");
    notesInput.setText("");
    // Set set = setsList.get(weekInput.getSelectedIndex());
    boolean matchFound = false;
    RatingGwt rating = getRatingForSetAndWeekInput(_targetSet);
    if (rating != null) {
      logger.info("Matched rating and set");
      matchFound = true;
      if (rating.getScore() == 1) {
        scoreOneRadioButton.setValue(true);
      } else if (rating.getScore() == 2) {
        scoreTwoRadioButton.setValue(true);
      } else if (rating.getScore() == 3) {
        scoreThreeRadioButton.setValue(true);
      } else if (rating.getScore() == 4) {
        scoreFourRadioButton.setValue(true);
      } else if (rating.getScore() == 5) {
        scoreFiveRadioButton.setValue(true);
      }
      if (rating.getNotes() != null) {
        notesInput.setText(rating.getNotes());
      }
    } else {
      logger.info("Could not find existing rating, clearing inputs");
      scoreOneRadioButton.setValue(false);
      scoreTwoRadioButton.setValue(false);
      scoreThreeRadioButton.setValue(false);
      scoreFourRadioButton.setValue(false);
      scoreFiveRadioButton.setValue(false);
      notesInput.setValue("");
    }
  }

  private boolean verifyInput() {
    infoBox.setText("");

    if (!FieldVerifier.isValidScore(getSelectedScoreFromUi())) {
      infoBox.setText(FieldVerifier.SCORE_ERROR);
      return false;
    }
    return true;
  }

  // Add rating and do nothing else
  private void addRating() {
    addRating(null);
  }

  // Add rating and redirect browser after rating is successfully added
  private void addRating(final String redirectUrl) {

    // Set set = setsList.get(weekInput.getSelectedIndex());
    Set set = _targetSet;
    String score = getSelectedScoreFromUi();
    String notes = notesInput.getText();

    // if (!FieldVerifier.isValidEmail(ownerEmail)) {
    // infoBox.setText(FieldVerifier.EMAIL_ERROR);
    // return;
    // }

    // Then, we send the input to the server.
    int weekValue = getSelectedWeekFromUi();
    festivalService.addRating(set.getId(), weekValue + "", score, notes, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        logger.info("Failed to add rating");
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      @Override
      public void onSuccess(String result) {
        logger.info("Add rating success");
        infoBox.setText(result);

        if (redirectUrl != null) {
          Window.Location.replace(redirectUrl);
        }
        // TODO Maybe don't do this if we are hiding
        // retrieveRatings();
      }
    });

    Widget parent = MainRateComposite.this.getParent().getParent();
    if (parent instanceof RateDialogBox) {
      ((RateDialogBox) parent).hide();
    } else {
      logger.log(Level.INFO, "Unexpected - parent is not a RateDialogBox, not hiding");
    }
  }

  private void deleteRating(RatingGwt rating) {
    infoBox.setText("");
    festivalService.deleteRating(rating.getId(), new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      @Override
      public void onSuccess(String result) {
        infoBox.setText(result);
        retrieveRatings();
      }
    });
    ratingsList.remove(rating);
  }

  // Maybe don't need to use this?
  private void retrieveSets() {
    infoBox.setText("");
    FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();
    festivalService.getSets(fest, "2012", null, new AsyncCallback<List<Set>>() {

      @Override
      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      @Override
      public void onSuccess(List<Set> result) {
        ArrayList<Set> sortedItems = new ArrayList<Set>(result);
        Collections.sort(sortedItems, ComparatorUtils.SET_NAME_COMPARATOR);
        setsList.clear();
        setsList.addAll(sortedItems);

        weekInput.clear();
        // Not yet sure what this does or why, good for now because we are not using this code
        for (Set set : sortedItems) {
          weekInput.addItem(set.getDay() + " " + set.getTimeOne() + " - " + set.getArtistName(), set.getId().toString());
        }
      }
    });
  }

  public static class RatingsTable extends CellTable<RatingGwt> {

    public Column<RatingGwt, String> artistNameColumn;
    public Column<RatingGwt, String> weekendColumn;
    public Column<RatingGwt, String> scoreColumn;
    public Column<RatingGwt, String> notesColumn;
    public Column<RatingGwt, String> deleteColumn;

    interface TasksTableResources extends CellTable.Resources {
      @Override
      @Source("../RatingsTable.css")
      TableStyle cellTableStyle();
    }

    interface TableStyle extends CellTable.Style {

      String columnName();

      String columnWeekend();

      String columnScore();

      String columnTrash();
    }

    private static TasksTableResources resources = GWT.create(TasksTableResources.class);

    public RatingsTable() {
      super(100, resources);

      artistNameColumn = new Column<RatingGwt, String>(new TextCell()) {
        @Override
        public String getValue(RatingGwt object) {
          return object.getArtistName();
        }
      };
      addColumn(artistNameColumn, "Artist Name");
      addColumnStyleName(0, "columnFill");
      addColumnStyleName(0, resources.cellTableStyle().columnName());

      weekendColumn = new Column<RatingGwt, String>(new TextCell()) {
        @Override
        public String getValue(RatingGwt object) {
          if (object.getWeekend() != null) {
            return object.getWeekend().toString();
          }
          return "";
        }
      };
      addColumn(weekendColumn, "Weekend");
      addColumnStyleName(1, "columnFill");
      addColumnStyleName(1, resources.cellTableStyle().columnWeekend());

      scoreColumn = new Column<RatingGwt, String>(new TextCell()) {
        @Override
        public String getValue(RatingGwt object) {
          if (object.getScore() != null) {
            return object.getScore().toString();
          }
          return "";
        }
      };
      addColumn(scoreColumn, "Score");
      addColumnStyleName(2, "columnFill");
      addColumnStyleName(2, resources.cellTableStyle().columnScore());

      notesColumn = new Column<RatingGwt, String>(new TextCell()) {
        @Override
        public String getValue(RatingGwt object) {
          return object.getNotes();
        }
      };
      addColumn(notesColumn, "Notes");
      addColumnStyleName(3, "columnFill");
      addColumnStyleName(3, resources.cellTableStyle().columnName());

      ButtonCell buttonCell = new ButtonCell(new SafeHtmlRenderer<String>() {
        @Override
        public SafeHtml render(String object) {
          return SafeHtmlUtils.fromTrustedString("<img src=\"delete.png\"></img>");
        }

        @Override
        public void render(String object, SafeHtmlBuilder builder) {
          builder.append(render(object));
        }
      });

      deleteColumn = new Column<RatingGwt, String>(buttonCell) {
        @Override
        public String getValue(RatingGwt object) {
          return "\u2717"; // Ballot "X" mark
        }
      };
      addColumn(deleteColumn, "\u2717");
      addColumnStyleName(4, "columnFill");
      addColumnStyleName(4, resources.cellTableStyle().columnTrash());
    }
  }

  private final class RateClickHandler implements ClickHandler {
    private final Animation androidAnimation;

    private RateClickHandler(Animation androidAnimation) {
      this.androidAnimation = androidAnimation;
    }

    @Override
    public void onClick(ClickEvent event) {
      logger.info("Add Rating button clicked!");

      if (verifyInput() == false) {
        return;
      }

      if (event.getSource() == buttonRate) {
        logger.info("Rate button clicked");
        addRating();
      } else if (event.getSource() == buttonRateFacebook || event.getSource() == buttonRateTwitter) {
        // Twitter and facebook common vars
        FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();
        String linkToRTFApp = ServletConfig.HTTP + fest.getWebClientHostname();

        String score = getSelectedScoreFromUi();
        int week = getSelectedWeekFromUi();
        String rtfAppName = fest.getRTFAppName();
        String festName = fest.getName();
        String caption = "I saw " + _targetSet.getArtistName();
        String description = notesInput.getText();
        if (fest.getNumberOfWeeks() == 1) {
          caption += " at " + festName;
        } else {
          caption += " during " + festName + " (Week " + week + ")";
        }
        caption += " and rated it a " + score + " (out of 5)";

        caption = URL.encode(caption);
        description = URL.encode(description);
        linkToRTFApp = URL.encode(linkToRTFApp);

        if (event.getSource() == buttonRateFacebook) {
          logger.info("Facebook+Rate button clicked");

          if (Coacheller_AppEngine.getLoginStatus().isLoggedIn(LoginType.FACEBOOK)) { // If facebook is logged in

            String redirectTarget = ServletConfig.HTTP + "ratethisfest.appspot.com/sessionsTest" + "?"
                + ServletInterface.SPECIAL_ACTION_REDIRECTURL + "="
                + Base64Coder.encodeStringRTFSpecial(fest.getWebClientHostname());
            redirectTarget = URL.encode(redirectTarget);

            String finalRedirect = "https://www.facebook.com/dialog/feed?app_id=" + ServletConfig.FACEBOOK_ID
                + "&link=" + linkToRTFApp + "&name=" + rtfAppName + "&caption=" + caption;

            if (description != null && !description.equals("")) {
              finalRedirect += "&description=" + description;
            }
            finalRedirect += "&redirect_uri=" + redirectTarget;

            logger.info("User wants to rate with Facebook: " + finalRedirect);
            addRating(finalRedirect);
          } else { // If facebook is not logged in
            Window.Location.replace(LoginControl.getUrlLoginFacebook());
          }

        } else if (event.getSource() == buttonRateTwitter) { // Twitter-only block
          logger.info("Twitter+Rate button clicked");
          if (Coacheller_AppEngine.getLoginStatus().isLoggedIn(LoginType.TWITTER)) { // If twitter is logged in
            String redirectTarget = ServletConfig.HTTPS + "twitter.com/intent/tweet?" + "url=" + linkToRTFApp + "&"
                + "text=" + caption;
            addRating(); // Hides this dialog
            Window.open(redirectTarget, "_blank", "enabled");
            // Window.Location.replace("http://www.msnbc.com");
          } else { // If twitter is not logged in
            Window.Location.replace(LoginControl.getUrlLoginTwitter());
          }

        } else { // End twitter + facebook common block
          logger.info("Unexpected: Facebook or Twitter post was supposed to execute");
        }
      } else {
        logger.info("Click event source does not work as expected");
      }

      androidAnimation.run(400);
    }
  }

  private final class MiscClickHandler implements ClickHandler {
    private final Animation androidAnimation;

    public MiscClickHandler(Animation androidAnimation2) {
      androidAnimation = androidAnimation2;
    }

    @Override
    public void onClick(ClickEvent event) {
      logger.info("Misc click handler called!");

      LoginStatus loginStatus = Coacheller_AppEngine.getLoginStatus();

      if (event.getSource() == emailButton) {
        logger.info("emailButton was clicked");

        infoBox.setText("");
        // TODO: Update code!
        // festivalService.emailRatingsToUser(ownerEmail, new AsyncCallback<String>() {
        // @Override
        // public void onFailure(Throwable caught) {
        // // Show the RPC error message to the user
        // infoBox.setText(SERVER_ERROR);
        // }
        //
        // @Override
        // public void onSuccess(String result) {
        // infoBox.setText(result);
        // }
        // });

      } else if (event.getSource() == backButton) {
        logger.info("backButton was clicked");
        FlowControl.go(new MainViewComposite());

      } else if (event.getSource() == updateSetButton) {
        logger.info("updateSetButton was clicked");
        if (loginStatus.getProperty(LoginStatus.PROPERTY_PERSON_NAME).equals(ADMIN_NAME)) {
          infoBox.setText("");
          festivalService.updateSetData(Coacheller_AppEngine.getFestFromSiteName(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
              // Show the RPC error message to the user
              // infoBox.setText(SERVER_ERROR);
              infoBox.setText(caught.getMessage());
            }

            @Override
            public void onSuccess(String result) {
              infoBox.setText(result);
            }
          });

          androidAnimation.run(400);
        } else {
          infoBox.setText(ADMIN_ERROR);
        }

      } else if (event.getSource() == recalculateButton) {
        logger.info("recalculateButton was clicked");
        if (loginStatus.getProperty(LoginStatus.PROPERTY_PERSON_NAME).equals(ADMIN_NAME)) {
          infoBox.setText("");
          festivalService.recalculateSetRatingAverages(Coacheller_AppEngine.getFestFromSiteName(),
              new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                  // Show the RPC error message to the user
                  // infoBox.setText(SERVER_ERROR);
                  infoBox.setText(caught.getMessage());
                }

                @Override
                public void onSuccess(String result) {
                  infoBox.setText(result);
                }
              });

          androidAnimation.run(400);
        } else {
          infoBox.setText(ADMIN_ERROR);
        }

      } else if (event.getSource() == clearMyRatingButton) {
        logger.info("clearMyRatingButton was clicked");
        infoBox.setText("");
        // TODO: Update code!
        // festivalService.deleteRatingsByUser(ownerEmail, new AsyncCallback<String>() {
        // @Override
        // public void onFailure(Throwable caught) {
        // // Show the RPC error message to the user
        // // infoBox.setText(SERVER_ERROR);
        // infoBox.setText(caught.getMessage());
        // }
        //
        // @Override
        // public void onSuccess(String result) {
        // infoBox.setText(result);
        // }
        // });
        androidAnimation.run(400);

      } else if (event.getSource() == clearAllRatingButton) {
        logger.info("clearAllRatingButton was clicked");
        // Commented this because we have real live ratings in the datastore
        // And also the app is being changed to handle any year

        // if (ownerEmail.equals(ADMIN_NAME)) {
        // infoBox.setText("");
        // festivalService.deleteRatingsByYear(2012, new AsyncCallback<String>() {
        // @Override
        // public void onFailure(Throwable caught) {
        // // Show the RPC error message to the user
        // // infoBox.setText(SERVER_ERROR);
        // infoBox.setText(caught.getMessage());
        // }
        //
        // @Override
        // public void onSuccess(String result) {
        // infoBox.setText(result);
        // }
        // });
        // androidAnimation.run(400);
        // } else {
        // infoBox.setText(ADMIN_ERROR);
        // }

        // } else if (event.getSource() == backButton) {
        // logger.info("backButton was clicked");
        // } else if (event.getSource() == backButton) {
        // logger.info("backButton was clicked");
      } else {
        logger.info("Click event was from unknown source!");
      }

    }
  }

  class AndroidAnimation extends Animation {
    private static final int TOP = -50;
    private static final int BOTTOM = 150;
    Element element;

    public AndroidAnimation(Element element) {
      this.element = element;
    }

    @Override
    protected void onStart() {
      element.getStyle().setTop(TOP, Unit.PX);
    }

    @Override
    protected void onUpdate(double progress) {
      element.getStyle().setTop(TOP + (BOTTOM - TOP) * interpolate(progress), Unit.PX);
    }

    @Override
    protected void onComplete() {
      element.getStyle().setTop(TOP, Unit.PX);
    }
  }

}