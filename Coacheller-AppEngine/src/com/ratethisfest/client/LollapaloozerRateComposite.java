package com.ratethisfest.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.ratethisfest.shared.FieldVerifier;
import com.ratethisfest.shared.RatingGwt;
import com.ratethisfest.shared.Set;

public class LollapaloozerRateComposite extends Composite {

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

  interface Binder extends UiBinder<Widget, LollapaloozerRateComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  private final LollapaloozerServiceAsync lollapaloozerService = GWT
      .create(LollapaloozerService.class);
  private List<Set> setsList = new ArrayList<Set>();
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
  Label scoreLabel;

  @UiField
  Label notesLabel;

  @UiField
  ListBox setInput;

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

  public LollapaloozerRateComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  public LollapaloozerRateComposite(String ownerEmail) {
    this();
    this.ownerEmail = ownerEmail;
    retrieveSets();
    retrieveRatings();
    emailLabel.setText(ownerEmail);
  }

  private void initUiElements() {
    title.setText("LOLLAPALOOZER 2012");
    subtitle.setText("Rate This Set");

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

    setInput.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        loadRatingContents();
      }
    });

    Element androidElement = getElement().getFirstChildElement().getFirstChildElement();
    final Animation androidAnimation = new AndroidAnimation(androidElement);

    notesInput.addKeyPressHandler(new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        if (((int) event.getCharCode()) == 13) {
          addRating();

          androidAnimation.run(400);
        }
      }
    });

    addRatingButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        addRating();

        androidAnimation.run(400);
      }
    });

    emailButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        infoBox.setText("");
        lollapaloozerService.emailRatingsToUser(ownerEmail, new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            infoBox.setText(SERVER_ERROR);
          }

          public void onSuccess(String result) {
            infoBox.setText(result);
          }
        });
      }
    });

    backButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        FlowControl.go(new LollapaloozerViewComposite());
      }
    });

    updateSetButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (ownerEmail.equals(ADMIN_EMAIL)) {
          infoBox.setText("");
          lollapaloozerService.insertSetData(new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
              // Show the RPC error message to the user
              // infoBox.setText(SERVER_ERROR);
              infoBox.setText(caught.getMessage());
            }

            public void onSuccess(String result) {
              infoBox.setText(result);
            }
          });

          androidAnimation.run(400);
        } else {
          infoBox.setText(ADMIN_ERROR);
        }
      }
    });

    recalculateButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (ownerEmail.equals(ADMIN_EMAIL)) {
          infoBox.setText("");
          lollapaloozerService.recalculateSetRatingAverages(new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
              // Show the RPC error message to the user
              // infoBox.setText(SERVER_ERROR);
              infoBox.setText(caught.getMessage());
            }

            public void onSuccess(String result) {
              infoBox.setText(result);
            }
          });

          androidAnimation.run(400);
        } else {
          infoBox.setText(ADMIN_ERROR);
        }
      }
    });

    clearMyRatingButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        infoBox.setText("");
        lollapaloozerService.deleteRatingsByUser(ownerEmail, new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            // infoBox.setText(SERVER_ERROR);
            infoBox.setText(caught.getMessage());
          }

          public void onSuccess(String result) {
            infoBox.setText(result);
          }
        });
        androidAnimation.run(400);
      }
    });

    clearAllRatingButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (ownerEmail.equals(ADMIN_EMAIL)) {
          infoBox.setText("");
          lollapaloozerService.deleteRatingsByYear(2012, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
              // Show the RPC error message to the user
              // infoBox.setText(SERVER_ERROR);
              infoBox.setText(caught.getMessage());
            }

            public void onSuccess(String result) {
              infoBox.setText(result);
            }
          });
          androidAnimation.run(400);
        } else {
          infoBox.setText(ADMIN_ERROR);
        }
      }
    });

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
    lollapaloozerService.getSets("2012", null, new AsyncCallback<List<Set>>() {

      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      public void onSuccess(List<Set> result) {
        ArrayList<Set> sortedItems = new ArrayList<Set>(result);
        Collections.sort(sortedItems, ComparatorUtils.SET_NAME_COMPARATOR);
        setsList.clear();
        setsList.addAll(sortedItems);

        setInput.clear();
        for (Set set : sortedItems) {
          setInput.addItem(set.getDay() + " " + set.getTimeOne() + " - " + set.getArtistName(), set
              .getId().toString());
        }
      }
    });
  }

  private void retrieveRatings() {
    // TODO: year input eventually
    lollapaloozerService.getRatingsByUserEmail(ownerEmail, 2012,
        new AsyncCallback<List<RatingGwt>>() {

          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            infoBox.setText(SERVER_ERROR);
          }

          public void onSuccess(List<RatingGwt> result) {
            ratingsList.clear();
            ratingsList.addAll(result);
            Collections.sort(ratingsList, ComparatorUtils.RATING_NAME_COMPARATOR);
          }
        });
  }

  private void loadRatingContents() {
    notesInput.setText("");
    Set set = setsList.get(setInput.getSelectedIndex());
    for (RatingGwt rating : ratingsList) {
      if (set.getId().equals(rating.getSetId())) {
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
    Set set = setsList.get(setInput.getSelectedIndex());
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
    if (!FieldVerifier.isValidScore(score)) {
      infoBox.setText(FieldVerifier.SCORE_ERROR);
      return;
    }

    // Then, we send the input to the server.
    lollapaloozerService.addRating(ownerEmail, set.getId(), score, notes,
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
    lollapaloozerService.deleteRating(rating.getId(), new AsyncCallback<String>() {
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