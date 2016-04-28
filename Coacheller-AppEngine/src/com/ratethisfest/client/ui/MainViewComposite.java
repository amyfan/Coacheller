package com.ratethisfest.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;

import auth.logins.data.LoginStatus;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.ratethisfest.client.ClientResources;
import com.ratethisfest.client.Coacheller_AppEngine;
import com.ratethisfest.client.ComparatorUtils;
import com.ratethisfest.client.FestivalService;
import com.ratethisfest.client.FestivalServiceAsync;
import com.ratethisfest.client.LoginStatusEvent;
import com.ratethisfest.client.LoginStatusEventHandler;
import com.ratethisfest.client.PageToken;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.DateTimeUtils;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.Set;

public class MainViewComposite extends Composite implements ParentViewCallback {

  private static Binder uiBinder = GWT.create(Binder.class);

  interface Binder extends UiBinder<Widget, MainViewComposite> {
  }

  // The message displayed to the user when the server cannot be reached or returns an error.
  private static final String SERVER_ERROR = "An error occurred while attempting to contact the server. Please check your network connection and try again.";
  private static final Logger logger = Logger.getLogger(MainViewComposite.class.getName());

  // Create a remote service proxy to talk to the server-side Lollapaloozer service.
  private final FestivalServiceAsync festivalService = GWT.create(FestivalService.class);
  private List<Set> setsList;
  private Chart _chart = null;

  private FestivalEnum fest;

  @UiField
  Anchor androidUrl;

  @UiField
  Anchor iosUrl;

  @UiField
  Label infoBox;

  @UiField
  ListBox dayInput;

  @UiField
  ListBox chartDataSortInput;

  @UiField
  SimplePanel setsChartPanel;

  @UiField
  SetsTable setsTable;
  @UiField
  ListBox yearInput;

  // @UiField
  // com.google.gwt.user.client.ui.Button queryButton;

  // @UiField
  // com.google.gwt.user.client.ui.Button rateButton;

  public MainViewComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    fest = Coacheller_AppEngine.getFestFromSiteName();

    initUiElements();

    Coacheller_AppEngine.EVENT_BUS.addHandler(LoginStatusEvent.TYPE, new LoginStatusEventHandler() {

      @Override
      public void onLoginStatusChange(LoginStatusEvent event) {
        // LoginControlView.this.updateUI(event.getLoginStatus());
        MainViewComposite.this.onLoginStatusChange(Coacheller_AppEngine.getLoginStatus());
      }
    });

    retrieveSets();

    // TODO: see if we wanna auto refresh
    // Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
    // public boolean execute() {
    // retrieveSets();
    // return true;
    // }
    // }, DELAY_MS);
  }
  
  @Override
  public void updateUI() {
    retrieveSets();
  }

  protected void onLoginStatusChange(LoginStatus loginStatus) {
    logger.info(this.getClass().getName() + " notified of change in login status, login?:" + loginStatus.isLoggedIn());
  }

  private void initUiElements() {
    setsChartPanel.setVisible(false);

    final ClientResources clientResources = GWT.create(ClientResources.class);

    if (fest.equals(FestivalEnum.COACHELLA)) {
      androidUrl.setHref("http://play.google.com/store/apps/details?id=com.coacheller");
      androidUrl.setTarget("_blank");
      // androidUrl.setText("Download Coacheller for Android!");
      Image androidImage = new Image(clientResources.download_android());
      androidUrl.getElement().appendChild(androidImage.getElement());

      iosUrl.setHref("https://itunes.apple.com/us/app/coacheller-unofficial/id634889261?ls=1&mt=8");
      iosUrl.setTarget("_blank");
      // iosUrl.setText("Download Coacheller for iPhone!");
      Image iosImage = new Image(clientResources.download_ios());
      iosUrl.getElement().appendChild(iosImage.getElement());
    } else {
      androidUrl.setHref("http://play.google.com/store/apps/details?id=com.lollapaloozer");
      androidUrl.setTarget("_blank");
      // androidUrl.setText("Download Lollapaloozer for Android");
      Image androidImage = new Image(clientResources.download_android());
      androidUrl.getElement().appendChild(androidImage.getElement());
    }

    ListDataProvider<Set> listDataProvider = new ListDataProvider<Set>();
    listDataProvider.addDataDisplay(setsTable);
    setsTable.setParentViewCallback(this);
    setsList = listDataProvider.getList();

    // Create a callback to be called when the visualization API
    // has been loaded.
    Runnable onLoadCallback = new Runnable() {
      @Override
      public void run() {
        logger.info("Init UI visualization API loaded handler");

        if (_chart != null) {
          changeChart(_chart);
          chartShowLoading("Loading (2)");
        }
      }
    };

    // Load the visualization api, passing the onLoadCallback to be called
    // when loading is done.
    VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);

    // usersTable.setRowStyles(new RowStyles<Set>() {
    // public String getStyleNames(Set row, int rowIndex) {
    // return isActive(row) ? "Active" : "Inactive";
    // }
    // });
    //
    // usersTable.activeColumn.setFieldUpdater(new FieldUpdater<Set, Boolean>()
    // {
    // public void update(int index, Set appUser, Boolean value) {
    // FlowControl.go(new LollapaloozerChartComposite(appUser.getEmail()));
    // }
    // });
    //
    // usersTable.dateColumn.setFieldUpdater(new FieldUpdater<Set, Date>() {
    // public void update(int index, Set appUser, Date value) {
    // AppUserRequest request = requestFactory.appUserRequest();
    // Set updatedTask = request.edit(appUser);
    // updatedTask.setDateModified(value);
    // request.updateAppUser(updatedTask).fire();
    // }
    // });
    //
    // usersTable.deleteColumn.setFieldUpdater(new FieldUpdater<Set, String>() {
    // public void update(int index, Set appUser, String value) {
    // AppUserRequest request = requestFactory.appUserRequest();
    // request.deleteAppUser(appUser).fire();
    // setsList.remove(appUser);
    // }
    // });

    DropdownChangeHandler dropdownHandler = new DropdownChangeHandler();
    yearInput.addItem("2016");
    yearInput.addItem("2015");
    yearInput.addItem("2014");
    yearInput.addItem("2013");
    yearInput.addItem("2012");
    yearInput.addChangeHandler(dropdownHandler);

    dayInput.addItem(DayEnum.FRIDAY.getValue());
    dayInput.addItem(DayEnum.SATURDAY.getValue());
    dayInput.addItem(DayEnum.SUNDAY.getValue());
    dayInput.addChangeHandler(dropdownHandler);

    chartDataSortInput.addItem("Score");
    chartDataSortInput.addItem("Set Time");
    chartDataSortInput.addItem("Artist Name");
    chartDataSortInput.addChangeHandler(dropdownHandler);

    // userNameInput.addKeyUpHandler(new KeyUpHandler() {
    // public void onKeyUp(KeyUpEvent event) {
    // if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
    // String message = userNameInput.getText();
    // userNameInput.setText("");
    // sendNewUserToServer(message);
    //
    // androidAnimation.run(400);
    // }
    // }
    // });

    // TODO: DEPRECATED
    // queryButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // retrieveSets();
    //
    // androidAnimation.run(400);
    // }
    // });

    // rateButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // FlowControl.go(new LollapaloozerEmailComposite());
    // }
    // });
  }

  public void changeChart(Chart chart) {
    setsChartPanel.setVisible(true);
    setsChartPanel.clear();
    setsChartPanel.add(chart);
    chartHideLoading();
  }

  public void chartShowLoading(String message) {
    if (_chart == null) {
      logger.info("Unexpected, chart is null (" + message + ")");
      return;
    }
    logger.info("Showing chart loading (" + message + ")");
    _chart.showLoading(message);
  }

  public void chartHideLoading() {
    if (_chart == null) {
      logger.info("Unexpected, chart is null (tried to hide chart)");
      return;
    }
    logger.info("Hiding chart loading");
    _chart.hideLoading();
  }

  @Override
  public String getTitle() {
    return PageToken.VIEW.getValue();
  }

  private void retrieveSets() {
    infoBox.setText("");
    chartShowLoading("Loading...");
    String day = dayInput.getItemText(dayInput.getSelectedIndex());
    String year = yearInput.getItemText(yearInput.getSelectedIndex());
    festivalService.getSets(fest, year, DayEnum.fromValue(day), new AsyncCallback<List<Set>>() {

      @Override
      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      @Override
      public void onSuccess(List<Set> result) {
        setsList.clear();
        setsList.addAll(result);

        // final DataTable dataTable = (DataTable) createChartDataTable();

        // Create a callback to be called when the visualization API
        // has been loaded.
        Runnable onLoadCallback = new Runnable() {
          @Override
          public void run() {
            logger.info("Retrieve sets completed callback handler");
            _chart = createChart();
            changeChart(_chart);

          }
        };

        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.

        VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);

      }
    });
  }

  @Deprecated
  private Options createOptions() {
    Options options = Options.create();
    options.setHeight((setsList.size() * 25) + 20);
    // options.setTitle("Lollapalooza Set Ratings");
    options.setColors("blue", "green");
    AxisOptions axisOptions = AxisOptions.create();
    axisOptions.setMinValue(0);
    axisOptions.setMaxValue(5);
    options.setHAxisOptions(axisOptions);
    ChartArea chartArea = ChartArea.create();
    // chartArea.setTop(10);
    chartArea.setHeight(setsList.size() * 25);
    options.setChartArea(chartArea);
    return options;
  }

  @Deprecated
  private AbstractDataTable createChartDataTable() {
    DataTable data = DataTable.create();
    if (setsList == null) {
      data.addColumn(ColumnType.STRING, "Artist Name");
      data.addColumn(ColumnType.NUMBER, "Average Score");
    } else {
      data.addColumn(ColumnType.STRING, "Artist Name");
      data.addColumn(ColumnType.NUMBER, "Average Score");
      data.addRows(setsList.size());
      if (chartDataSortInput.getItemText(chartDataSortInput.getSelectedIndex()).equals("Artist Name")) {
        // sort first
        Collections.sort(setsList, ComparatorUtils.SET_NAME_COMPARATOR);

        int setNum = 0;
        for (Set set : setsList) {
          data.setValue(setNum, 0, set.getArtistName());
          data.setValue(setNum, 1, set.getAvgScoreOne());
          setNum++;
        }
      } else if (chartDataSortInput.getItemText(chartDataSortInput.getSelectedIndex()).equals("Score")) {
        // sort first
        if (fest.equals(FestivalEnum.COACHELLA)) {
          Collections.sort(setsList, ComparatorUtils.DOUBLE_SET_SCORE_COMPARATOR);
        } else {
          Collections.sort(setsList, ComparatorUtils.SET_SCORE_COMPARATOR);
        }

        int setNum = 0;
        for (Set set : setsList) {
          data.setValue(setNum, 0, set.getArtistName());
          data.setValue(setNum, 1, set.getAvgScoreOne());
          setNum++;
        }
      } else {
        // sort first
        Collections.sort(setsList, ComparatorUtils.SET_TIME_COMPARATOR);

        int setNum = 0;
        for (Set set : setsList) {
          String timeString = DateTimeUtils.militaryToCivilianTime(set.getTimeOne());
          String nameCombo = timeString + ": " + set.getArtistName();
          data.setValue(setNum, 0, nameCombo);
          data.setValue(setNum, 1, set.getAvgScoreOne());
          setNum++;
        }
      }
    }
    return data;
  }

  private Chart createChart() {
    if (setsList == null) {
      Chart chart = new Chart();
      chart.setHeight(setsTable.getOffsetHeight() * 1.07);

      return chart;
    }

    String title = "RATING RESULTS";
    String year = yearInput.getItemText(yearInput.getSelectedIndex());
    if (year != null) {
      title = year + " " + title;
    }
    Chart chart = new Chart().setType(Series.Type.BAR).setChartTitleText(title).setMarginRight(10);
    chart.getXAxis().setAxisTitleText("Artist");
    chart.getYAxis().setAxisTitleText("Score").setMin(0).setMax(5);
    Series series = chart.createSeries().setName("Average Score");
    // chart.setHeight(setsList.size() * 22);
    List<String> artistsList = new ArrayList<String>();
    List<Point> pointsList = new ArrayList<Point>();

    if (chartDataSortInput.getItemText(chartDataSortInput.getSelectedIndex()).equals("Artist Name")) {
      // sort first
      Collections.sort(setsList, ComparatorUtils.SET_NAME_COMPARATOR);

      for (Set set : setsList) {
        artistsList.add(set.getArtistName());
        Point point = new Point(set.getArtistName(), averageScore(set)).setColor("#FF7800");
        pointsList.add(point);
      }
    } else if (chartDataSortInput.getItemText(chartDataSortInput.getSelectedIndex()).equals("Score")) {
      // sort first
      if (fest.equals(FestivalEnum.COACHELLA)) {
        Collections.sort(setsList, ComparatorUtils.DOUBLE_SET_SCORE_COMPARATOR);
      } else {
        Collections.sort(setsList, ComparatorUtils.SET_SCORE_COMPARATOR);
      }

      for (Set set : setsList) {
        artistsList.add(set.getArtistName());
        Point point = new Point(set.getArtistName(), averageScore(set)).setColor("#FF7800");
        pointsList.add(point);
      }
    } else {
      // sort first
      Collections.sort(setsList, ComparatorUtils.SET_TIME_COMPARATOR);

      for (Set set : setsList) {
        String timeString = DateTimeUtils.militaryToCivilianTime(set.getTimeOne());
        String nameCombo = timeString + ": " + set.getArtistName();
        artistsList.add(nameCombo);
        Point point = new Point(set.getArtistName(), averageScore(set)).setColor("#FF7800");
        pointsList.add(point);
      }
    }

    String[] artistsArray = artistsList.toArray(new String[artistsList.size()]);
    chart.getXAxis().setCategories(artistsArray);

    Point[] pointsArray = pointsList.toArray(new Point[pointsList.size()]);
    series.setPoints(pointsArray);

    chart.addSeries(series);

    chart.setHeight(setsTable.getOffsetHeight() * 1.07);
    return chart;
  }
  
  private double averageScore(Set set) {
    double sumOne = set.getAvgScoreOne() * set.getNumRatingsOne();
    double sumTwo = set.getAvgScoreTwo() * set.getNumRatingsTwo();
    double average = sumOne + sumTwo;

    double totalNumRatings = set.getNumRatingsOne() + set.getNumRatingsTwo();
    if (totalNumRatings > 0) {
      average = average / totalNumRatings;

      // TODO: this is very rudimentary, but going to downgrade single-rating scores
      if (totalNumRatings == 1) {
        average -= 0.75;
      }
    }
    return average;
  }

  private final class DropdownChangeHandler implements ChangeHandler {
    @Override
    public void onChange(ChangeEvent event) {
      if (event.getSource() == dayInput) {
        logger.info("Dropdown change of Day");
        retrieveSets();
        // androidAnimation.run(400);

      } else if (event.getSource() == yearInput) {
        logger.info("Dropdown change of Year");
        retrieveSets();
        // androidAnimation.run(400);

      } else if (event.getSource() == chartDataSortInput) {
        logger.info("Dropdown change of chart data sort type");
        chartShowLoading("Loading...");

        _chart = createChart(); // Probably not the best solution but I have not refactored the sorting code yet -MA
        // final DataTable dataTable = (DataTable) createChartDataTable();

        // Create a callback to be called when the visualization API
        // has been loaded.
        Runnable onLoadCallback = new Runnable() {
          @Override
          public void run() {
            logger.info("Chart type change handler running");
            changeChart(_chart);
          }
        };

        // Load the visualization api, passing the onLoadCallback to be called when loading is done.
        VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);

      } else {
        logger.info("Unexpected:  Dropdown change from unknown source");
      }

    }
  }

  public static class SetsTable extends CellTable<Set> {

    public Column<Set, String> dayColumn;
    public Column<Set, String> timeOneColumn;
    public Column<Set, String> artistNameColumn;
    public Column<Set, String> avgScoreColumn;
    public Column<Set, Set> coachAvgScoreColumn;
    public Column<Set, String> stageOneColumn;
    public Column<Set, Set> coachellaStarsColumn;
    public Column<Set, ImageResource> lollaStarsColumn;
    private ParentViewCallback parentViewCallback;

    public void setParentViewCallback(ParentViewCallback parentViewCallback) {
      this.parentViewCallback = parentViewCallback; 
    }

    private final class SetClickHandler implements Handler<Set> {

      private SetsTable _table;

      public void setOwner(SetsTable owner) {
        _table = owner;
      }
      
      @Override
      public void onCellPreview(CellPreviewEvent<Set> event) {
        // logger.info("CellPreviewHandler called");
        if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType())) {
          _table.getRowElement(event.getIndex()).getCells().getItem(event.getColumn())
              .setTitle("Click to Rate this set!");

          // Element cellElement = event.getNativeEvent().getEventTarget().cast();
          // if (cellElement.getParentElement()
          // .getFirstChildElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))
          // && cellElement.getTagName().equalsIgnoreCase("span")) {
          // }
        }

        if (BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
          Element cellElement = event.getNativeEvent().getEventTarget().cast();
          // play with element
          int column = event.getColumn();
          int index = event.getIndex();
          Set targetSet = event.getValue();

          logger.info("CellPreviewHandler found browser click column=" + column + " index=" + index);
          logger.info("Set ID:" + targetSet.getId() + " Artist Name[" + targetSet.getArtistName() + "]");

          if (Coacheller_AppEngine.getLoginStatus().isLoggedIn()) {
            logger.info("User is logged in, navigating to rate UI"); // We should already have user's ratings if logged
                                                                     // in

            String accountId = Coacheller_AppEngine.getLoginStatus().getProperty(LoginStatus.PROPERTY_ACCOUNT_ID);
            // RatingDAO ratingDAO = new RatingDAO(); //should not be accessed in gwt
            // Key<AppUser> userKey = new Key<AppUser>(AppUser.class,accountId);

            RateDialogBox rateDialog = new RateDialogBox();
            rateDialog.clear();
            // If there is an existing rating for this set,
            // Preconfigure Rate dialog
            MainRateComposite rateComposite = new MainRateComposite(parentViewCallback, targetSet);
            rateDialog.add(rateComposite);
            FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();

            rateDialog.setTitle(fest.getRTFAppName()); // Sets tooltip, go figure
            rateDialog.setText(fest.getRTFAppName()); // Sets title, go figure
            rateDialog.show();
          } else {
            logger.info("User is not logged in: Not doing anything on click");
            RateDialogBox rateDialog = new RateDialogBox();
            rateDialog.setText("Please log in first.");
          }
        }
      }
    }

    interface TasksTableResources extends CellTable.Resources {
      @Override
      @Source("../SetsTable.css")
      TableStyle cellTableStyle();
    }

    interface TableStyle extends CellTable.Style {

      String columnTime();

      String columnDay();

      String columnName();

      String columnScore();

      String columnStage();

      String columnStars();
    }

    private static TasksTableResources resources = GWT.create(TasksTableResources.class);

    public SetsTable() {
      super(100, resources);

      FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();

      SetClickHandler handler = new SetClickHandler();
      this.addCellPreviewHandler(handler);
      handler.setOwner(this);
      dayColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          return object.getDay();
        }
      };
      addColumn(dayColumn, "Day");
      addColumnStyleName(0, "columnFill");
      addColumnStyleName(0, resources.cellTableStyle().columnDay());

      timeOneColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          return DateTimeUtils.militaryToCivilianTime(object.getTimeOne());
        }
      };
      addColumn(timeOneColumn, "Set Time");
      addColumnStyleName(1, "columnFill");
      addColumnStyleName(1, resources.cellTableStyle().columnTime());

      stageOneColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          return object.getStageOne();
        }
      };
      addColumn(stageOneColumn, "Stage");
      addColumnStyleName(2, "columnFill");
      addColumnStyleName(2, resources.cellTableStyle().columnStage());

      artistNameColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          return object.getArtistName();
        }
      };
      addColumn(artistNameColumn, "Artist Name");
      addColumnStyleName(3, "columnFill");
      addColumnStyleName(3, resources.cellTableStyle().columnName());

      if (fest.equals(FestivalEnum.LOLLAPALOOZA)) {

        avgScoreColumn = new Column<Set, String>(new TextCell()) {
          @Override
          public String getValue(Set object) {
            String value = "";
            if (object.getAvgScoreOne() != null) {
              value += object.getAvgScoreOne().toString();
            }
            if (object.getNumRatingsOne() != null) {
              value += " (" + object.getNumRatingsOne().toString() + ")";
            }
            return value;
          }
        };
        addColumn(avgScoreColumn, "Average Score");
        addColumnStyleName(4, "columnFill");
        addColumnStyleName(4, resources.cellTableStyle().columnScore());

        lollaStarsColumn = new Column<Set, ImageResource>(new ImageResourceCell()) {
          @Override
          public ImageResource getValue(Set object) {
            return getStarImage(object.getAvgScoreOne());
          }
        };
        addColumn(lollaStarsColumn, " ");
        addColumnStyleName(5, "columnFill");
        addColumnStyleName(5, resources.cellTableStyle().columnStars());
      } else {

        // avgScoreColumn = new Column<Set, String>(new TextCell()) {
        // @Override
        // public String getValue(Set object) {
        //
        // String value = "Week 1: ";
        // if (object.getAvgScoreOne() != null) {
        // value += object.getAvgScoreOne().toString();
        // }
        // if (object.getNumRatingsOne() != null) {
        // value += " (" + object.getNumRatingsOne().toString() + ")";
        // }
        //
        // value += "<br>Week 2: ";
        // if (object.getAvgScoreTwo() != null) {
        // value += object.getAvgScoreTwo().toString();
        // }
        // if (object.getNumRatingsTwo() != null) {
        // value += " (" + object.getNumRatingsTwo().toString() + ")";
        // }
        // return value;
        // }
        // };

        final ArrayList<HasCell<Set, ?>> textCells = new ArrayList<HasCell<Set, ?>>();

        // then define the cells and add them to the list
        HasCell<Set, String> weekOne = new HasCell<Set, String>() {
          @Override
          public Cell<String> getCell() {
            return new TextCell();
          }

          @Override
          public FieldUpdater<Set, String> getFieldUpdater() {
            return null;
          }

          @Override
          public String getValue(Set object) {
            String value = "Week 1: ";
            if (object.getAvgScoreOne() != null) {
              value += object.getAvgScoreOne().toString();
            }
            if (object.getNumRatingsOne() != null) {
              value += " (" + object.getNumRatingsOne().toString() + ")";
            }
            return value;
          }
        };
        textCells.add(weekOne);

        HasCell<Set, String> weekTwo = new HasCell<Set, String>() {
          @Override
          public Cell<String> getCell() {
            return new TextCell();
          }

          @Override
          public FieldUpdater<Set, String> getFieldUpdater() {
            return null;
          }

          @Override
          public String getValue(Set object) {
            String value = "Week 2: ";
            if (object.getAvgScoreTwo() != null) {
              value += object.getAvgScoreTwo().toString();
            }
            if (object.getNumRatingsTwo() != null) {
              value += " (" + object.getNumRatingsTwo().toString() + ")";
            }
            return value;
          }
        };
        textCells.add(weekTwo);

        coachAvgScoreColumn = new Column<Set, Set>(new MyCompositeCell<Set>(textCells)) {
          @Override
          public Set getValue(Set object) {
            return object;
          }
        };

        addColumn(coachAvgScoreColumn, "Average Score");
        addColumnStyleName(4, "columnFill");
        addColumnStyleName(4, resources.cellTableStyle().columnScore());

        // first make a list to store the cells, you want to combine
        final ArrayList<HasCell<Set, ?>> imgCells = new ArrayList<HasCell<Set, ?>>();

        // then define the cells and add them to the list
        HasCell<Set, ImageResource> weekOneImg = new HasCell<Set, ImageResource>() {
          @Override
          public Cell<ImageResource> getCell() {
            return new ImageResourceCell();
          }

          @Override
          public FieldUpdater<Set, ImageResource> getFieldUpdater() {
            return null;
          }

          @Override
          public ImageResource getValue(Set object) {
            return getStarImage(object.getAvgScoreOne());
          }
        };
        imgCells.add(weekOneImg);

        HasCell<Set, ImageResource> weekTwoImg = new HasCell<Set, ImageResource>() {
          @Override
          public Cell<ImageResource> getCell() {
            return new ImageResourceCell();
          }

          @Override
          public FieldUpdater<Set, ImageResource> getFieldUpdater() {
            return null;
          }

          @Override
          public ImageResource getValue(Set object) {
            return getStarImage(object.getAvgScoreTwo());
          }
        };
        imgCells.add(weekTwoImg);

        coachellaStarsColumn = new Column<Set, Set>(new MyCompositeCell<Set>(imgCells)) {
          @Override
          public Set getValue(Set object) {
            return object;
          }
        };
        addColumn(coachellaStarsColumn, " ");
        addColumnStyleName(5, "columnFill");
        addColumnStyleName(5, resources.cellTableStyle().columnStars());
      }
    }

    private ImageResource getStarImage(Double score) {
      final ClientResources clientResources = GWT.create(ClientResources.class);
      if (score != null) {
        if (score > 4.87)
          return clientResources.five_stars_100();
        if (score > 4.62)
          return clientResources.five_stars_95();
        if (score > 4.37)
          return clientResources.five_stars_90();
        if (score > 4.12)
          return clientResources.five_stars_85();
        if (score > 3.87)
          return clientResources.five_stars_80();
        if (score > 3.62)
          return clientResources.five_stars_75();
        if (score > 3.37)
          return clientResources.five_stars_70();
        if (score > 3.12)
          return clientResources.five_stars_65();
        if (score > 2.87)
          return clientResources.five_stars_60();
        if (score > 2.62)
          return clientResources.five_stars_55();
        if (score > 2.37)
          return clientResources.five_stars_50();
        if (score > 2.12)
          return clientResources.five_stars_45();
        if (score > 1.87)
          return clientResources.five_stars_40();
        if (score > 1.62)
          return clientResources.five_stars_35();
        if (score > 1.37)
          return clientResources.five_stars_30();
        if (score > 1.12)
          return clientResources.five_stars_25();
        if (score > 0.87)
          return clientResources.five_stars_20();
        if (score > 0.62)
          return clientResources.five_stars_15();
        if (score > 0.37)
          return clientResources.five_stars_10();
        if (score > 0.12)
          return clientResources.five_stars_5();
      }
      return clientResources.five_stars_0();
    }

    static class MyCompositeCell<C> extends CompositeCell<C> {
      private List<HasCell<C, ?>> myCells;

      public MyCompositeCell(List<HasCell<C, ?>> hasCells) {
        super(hasCells);
        myCells = hasCells;
      }

      // override methods to render the weekends vertically
      @Override
      public void render(Context context, C value, SafeHtmlBuilder sb) {
        for (HasCell<C, ?> hasCell : myCells) {
          render(context, value, sb, hasCell);
        }
      }

      protected <X> void render(Context context, C value, SafeHtmlBuilder sb, HasCell<C, X> hasCell) {
        Cell<X> cell = hasCell.getCell();
        sb.appendHtmlConstant("<div style='display:block;padding-bottom:5px;'>");
        cell.render(context, hasCell.getValue(value), sb);
        sb.appendHtmlConstant("</div>");
      }
    }
  }

}