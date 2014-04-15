package com.ratethisfest.client.ui;

import java.util.Collections;
import java.util.List;

import auth.logins.data.LoginStatus;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.ratethisfest.client.Coacheller_AppEngine;
import com.ratethisfest.client.ComparatorUtils;
import com.ratethisfest.client.FestivalService;
import com.ratethisfest.client.FestivalServiceAsync;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.RatingGwt;

/**
 * TODO: factor in multiple sets per artist in this logic
 * 
 * @author Amy
 * 
 */
public class RatingsComposite extends Composite {

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network " + "connection and try again.";

  private static final String ADMIN_EMAIL = "afan@coacheller.com";
  private static final String ADMIN_ERROR = "You do not have permission to do this. Sorry.";

  private Integer year = 2014;

  interface Binder extends UiBinder<Widget, RatingsComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  private final FestivalServiceAsync festivalService = GWT.create(FestivalService.class);
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
  com.google.gwt.user.client.ui.Button emailButton;

  @UiField
  RatingsTable ratingsTable;

  public RatingsComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();
  }

  public RatingsComposite(Integer year) {
    this();
    this.year = year;
    retrieveRatings();
  }

  private void initUiElements() {
    title.setText("My Ratings");

    ListDataProvider<RatingGwt> listDataProvider = new ListDataProvider<RatingGwt>();
    listDataProvider.addDataDisplay(ratingsTable);
    ratingsList = listDataProvider.getList();

    emailButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        infoBox.setText("");

        LoginStatus loginStatus = Coacheller_AppEngine.getLoginStatus();
        FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();

        festivalService.emailRatingsToUser(fest, new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            infoBox.setText(SERVER_ERROR);
          }

          @Override
          public void onSuccess(String result) {
            infoBox.setText(result);
          }
        });
      }
    });

    ratingsTable.deleteColumn.setFieldUpdater(new FieldUpdater<RatingGwt, String>() {
      @Override
      public void update(int index, RatingGwt rating, String value) {
        deleteRating(rating);
      }
    });
  }

  @Override
  public String getTitle() {
    return "Ratings";
  }

  private void retrieveRatings() {
    FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();
    festivalService.getRatingsByYear(fest, year, new AsyncCallback<List<RatingGwt>>() {

      @Override
      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      @Override
      public void onSuccess(List<RatingGwt> result) {
        ratingsList.clear();
        ratingsList.addAll(result);
        Collections.sort(ratingsList, ComparatorUtils.RATING_NAME_COMPARATOR);
      }
    });
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