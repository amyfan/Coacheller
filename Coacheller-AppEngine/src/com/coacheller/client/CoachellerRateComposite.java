package com.coacheller.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.coacheller.shared.FieldVerifier;
import com.coacheller.shared.RatingGwt;
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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class CoachellerRateComposite extends Composite {

  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  private static final String ADMIN_EMAIL = "afan@coacheller.com";
  private static final String ADMIN_ERROR = "You do not have permission to do this. Sorry.";

  private String ownerEmail = "";

  interface Binder extends UiBinder<Widget, CoachellerRateComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  private final CoachellerServiceAsync coachellerService = GWT.create(CoachellerService.class);
  private List<RatingGwt> ratingsList;

  @UiField
  Label title;

  @UiField
  Label subtitle;

  @UiField
  Label infoBox;

  @UiField
  Label emailLabel;

  @UiField
  Label weekendLabel;

  @UiField
  Label scoreLabel;

  @UiField
  Label notesLabel;

  @UiField
  ListBox artistInput;

  @UiField
  RadioButton weekendOneRadioButton;

  // @UiField
  // RadioButton weekendTwoRadioButton;

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
  com.google.gwt.user.client.ui.Button addRatingButton;

  @UiField
  com.google.gwt.user.client.ui.Button backButton;

  // ADMIN PANEL:
  // @UiField
  // com.google.gwt.user.client.ui.Button reloadButton;
  //
  // @UiField
  // com.google.gwt.user.client.ui.Button recalculateButton;
  //
  // @UiField
  // com.google.gwt.user.client.ui.Button clearMyRatingButton;
  //
  // @UiField
  // com.google.gwt.user.client.ui.Button clearRatingButton;
  //
  // @UiField
  // com.google.gwt.user.client.ui.Button clearUserButton;

  @UiField
  RatingsTable ratingsTable;

  public CoachellerRateComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  public CoachellerRateComposite(String ownerEmail) {
    this();
    this.ownerEmail = ownerEmail;
    retrieveSets();
    retrieveRatings();
    emailLabel.setText(ownerEmail);
  }

  private void initUiElements() {
    title.setText("COACHELLER 2012");
    subtitle.setText("Rate This Set");

    ListDataProvider<RatingGwt> listDataProvider = new ListDataProvider<RatingGwt>();
    listDataProvider.addDataDisplay(ratingsTable);
    ratingsList = listDataProvider.getList();

    weekendLabel.setText("Weekend");
    weekendOneRadioButton.setText("1");
    weekendOneRadioButton.setValue(true);
    // weekendTwoRadioButton.setText("2");

    scoreLabel.setText("Score");
    scoreOneRadioButton.setText("1");
    scoreTwoRadioButton.setText("2");
    scoreThreeRadioButton.setText("3");
    scoreFourRadioButton.setText("4");
    scoreFiveRadioButton.setText("5");

    notesLabel.setText("Notes (optional)");

    artistInput.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        loadRatingContents();
      }
    });

    Element androidElement = getElement().getFirstChildElement().getFirstChildElement();
    final Animation androidAnimation = new AndroidAnimation(androidElement);

    addRatingButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        addRating();

        androidAnimation.run(400);
      }
    });

    backButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        FlowControl.go(new CoachellerViewComposite());
      }
    });

    // reloadButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // if (ownerEmail.equals(ADMIN_EMAIL)) {
    // infoBox.setText("");
    // coachellerService.loadSetData(new AsyncCallback<String>() {
    //
    // public void onFailure(Throwable caught) {
    // // Show the RPC error message to the user
    // infoBox.setText(SERVER_ERROR);
    // }
    //
    // public void onSuccess(String result) {
    // infoBox.setText(result);
    // }
    // });
    //
    // androidAnimation.run(400);
    // } else {
    // infoBox.setText(ADMIN_ERROR);
    // }
    // }
    // });
    //
    // recalculateButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // if (ownerEmail.equals(ADMIN_EMAIL)) {
    // infoBox.setText("");
    // coachellerService.recalculateSetRatingAverages(new
    // AsyncCallback<String>() {
    //
    // public void onFailure(Throwable caught) {
    // // Show the RPC error message to the user
    // infoBox.setText(SERVER_ERROR);
    // }
    //
    // public void onSuccess(String result) {
    // infoBox.setText(result);
    // }
    // });
    //
    // androidAnimation.run(400);
    // } else {
    // infoBox.setText(ADMIN_ERROR);
    // }
    // }
    // });
    //
    // clearMyRatingButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // infoBox.setText("");
    // coachellerService.deleteRatingsByUser(ownerEmail, new
    // AsyncCallback<String>() {
    // public void onFailure(Throwable caught) {
    // // Show the RPC error message to the user
    // infoBox.setText(SERVER_ERROR);
    // }
    //
    // public void onSuccess(String result) {
    // infoBox.setText(result);
    // }
    // });
    // androidAnimation.run(400);
    // }
    // });
    //
    // clearRatingButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // if (ownerEmail.equals(ADMIN_EMAIL)) {
    // infoBox.setText("");
    // coachellerService.deleteAllRatings(new AsyncCallback<String>() {
    // public void onFailure(Throwable caught) {
    // // Show the RPC error message to the user
    // infoBox.setText(SERVER_ERROR);
    // }
    //
    // public void onSuccess(String result) {
    // infoBox.setText(result);
    // }
    // });
    // androidAnimation.run(400);
    // } else {
    // infoBox.setText(ADMIN_ERROR);
    // }
    // }
    // });
    //
    // clearUserButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // if (ownerEmail.equals(ADMIN_EMAIL)) {
    // infoBox.setText("");
    // coachellerService.deleteAllUsers(new AsyncCallback<String>() {
    // public void onFailure(Throwable caught) {
    // // Show the RPC error message to the user
    // infoBox.setText(SERVER_ERROR);
    // }
    //
    // public void onSuccess(String result) {
    // infoBox.setText(result);
    // }
    // });
    //
    // androidAnimation.run(400);
    // } else {
    // infoBox.setText(ADMIN_ERROR);
    // }
    // }
    // });

    ratingsTable.deleteColumn.setFieldUpdater(new FieldUpdater<RatingGwt, String>() {
      public void update(int index, RatingGwt rating, String value) {
        deleteRating(rating);
      }
    });
  }

  @Override
  public String getTitle() {
    return PageToken.RATE.getValue() + "=" + ownerEmail;
  }

  private void retrieveSets() {
    infoBox.setText("");
    coachellerService.getSetArtists("2012", null, new AsyncCallback<List<String>>() {

      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      public void onSuccess(List<String> result) {
        ArrayList<String> sortedTasks = new ArrayList<String>(result);
        Collections.sort(sortedTasks, SET_NAME_COMPARATOR);

        artistInput.clear();
        for (String artist : sortedTasks) {
          artistInput.addItem(artist);
        }
      }
    });
  }

  private void retrieveRatings() {
    coachellerService.getRatingsByUserEmail(ownerEmail, new AsyncCallback<List<RatingGwt>>() {

      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      public void onSuccess(List<RatingGwt> result) {
        ratingsList.clear();
        ratingsList.addAll(result);
        Collections.sort(ratingsList, RATING_NAME_COMPARATOR);
      }
    });
  }

  private void loadRatingContents() {
    notesInput.setText("");
    Integer weekend;
    if (weekendOneRadioButton.getValue()) {
      weekend = 1;
    } else {
      weekend = 2;
    }
    String artist = artistInput.getItemText(artistInput.getSelectedIndex());
    for (RatingGwt rating : ratingsList) {
      if (artist.equals(rating.getArtistName()) && weekend == rating.getWeekend()) {
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
        break;
      }
    }
  }

  private void addRating() {
    infoBox.setText("");
    String artist = artistInput.getItemText(artistInput.getSelectedIndex());
    String weekend = null;
    if (weekendOneRadioButton.getValue()) {
      weekend = weekendOneRadioButton.getText();
    }
    // else if (weekendtwoRadioButton.getValue()) {
    // weekend = weekendtwoRadioButton.getText();
    // }
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
    String notes = notesInput.getText();
    if (!FieldVerifier.isValidEmail(ownerEmail)) {
      infoBox.setText(FieldVerifier.EMAIL_ERROR);
      return;
    }
    if (!FieldVerifier.isValidWeekend(weekend)) {
      infoBox.setText(FieldVerifier.WEEKEND_ERROR);
      return;
    }
    if (!FieldVerifier.isValidScore(score)) {
      infoBox.setText(FieldVerifier.SCORE_ERROR);
      return;
    }

    // Then, we send the input to the server.
    coachellerService.addRatingBySetArtist(ownerEmail, artist, "2012", weekend, score, notes,
        new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            infoBox.setText(SERVER_ERROR);
          }

          public void onSuccess(String result) {
            infoBox.setText(result);
            retrieveRatings();
          }
        });
  }

  private void deleteRating(RatingGwt rating) {
    infoBox.setText("");
    coachellerService.deleteRating(rating.getId(), new AsyncCallback<String>() {
      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      public void onSuccess(String result) {
        infoBox.setText(result);
        retrieveRatings();
      }
    });
    ratingsList.remove(rating);
  }

  public static final Comparator<? super String> SET_NAME_COMPARATOR = new Comparator<String>() {
    public int compare(String t0, String t1) {
      return t0.compareTo(t1);
    }
  };

  public static final Comparator<? super RatingGwt> RATING_NAME_COMPARATOR = new Comparator<RatingGwt>() {
    public int compare(RatingGwt t0, RatingGwt t1) {
      // Sort by set time first
      if (t0.getScore() < t1.getScore()) {
        return 1;
      } else if (t0.getScore() > t1.getScore()) {
        return -1;
      } else {
        // Sort items alphabetically within each group
        return t0.getArtistName().compareTo(t1.getArtistName());
      }
    }
  };

  public static class RatingsTable extends CellTable<RatingGwt> {

    public Column<RatingGwt, String> artistNameColumn;
    public Column<RatingGwt, String> weekendColumn;
    public Column<RatingGwt, String> scoreColumn;
    public Column<RatingGwt, String> notesColumn;
    public Column<RatingGwt, String> deleteColumn;

    interface TasksTableResources extends CellTable.Resources {
      @Source("RatingsTable.css")
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
        public SafeHtml render(String object) {
          return SafeHtmlUtils.fromTrustedString("<img src=\"delete.png\"></img>");
        }

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