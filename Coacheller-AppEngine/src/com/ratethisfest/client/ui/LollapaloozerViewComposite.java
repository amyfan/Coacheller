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
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
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
import com.ratethisfest.client.Coacheller_AppEngine;
import com.ratethisfest.client.ComparatorUtils;
import com.ratethisfest.client.LoginStatusEvent;
import com.ratethisfest.client.LoginStatusEventHandler;
import com.ratethisfest.client.LollapaloozerService;
import com.ratethisfest.client.LollapaloozerServiceAsync;
import com.ratethisfest.client.PageToken;
import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.shared.DateTimeUtils;
import com.ratethisfest.shared.DayEnum;
import com.ratethisfest.shared.Set;

public class LollapaloozerViewComposite extends Composite {

  private static Binder uiBinder = GWT.create(Binder.class);

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

  interface Binder extends UiBinder<Widget, LollapaloozerViewComposite> {
  }

  // The message displayed to the user when the server cannot be reached or returns an error.
  private static final String SERVER_ERROR = "An error occurred while attempting to contact the server. Please check your network connection and try again.";
  private static final Logger logger = Logger.getLogger(LollapaloozerViewComposite.class.getName());

  // Create a remote service proxy to talk to the server-side Lollapaloozer service.
  private final LollapaloozerServiceAsync lollapaloozerService = GWT.create(LollapaloozerService.class);
  private List<Set> setsList;
  private Chart _chart = null;

  @UiField
  Anchor androidUrl;

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

  public LollapaloozerViewComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();

    Coacheller_AppEngine.EVENT_BUS.addHandler(LoginStatusEvent.TYPE, new LoginStatusEventHandler() {

      @Override
      public void onLoginStatusChange(LoginStatusEvent event) {
        // LoginControl.this.updateUI(event.getLoginStatus());
        LollapaloozerViewComposite.this.onLoginStatusChange(Coacheller_AppEngine.getLoginStatus());
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

  protected void onLoginStatusChange(LoginStatus loginStatus) {
    logger.info(this.getClass().getName() + " notified of change in login status, login?:" + loginStatus.isLoggedIn());
  }

  private void initUiElements() {
    setsChartPanel.setVisible(false);

    androidUrl.setHref("http://play.google.com/store/apps/details?id=com.lollapaloozer");
    androidUrl.setText("Download Lollapaloozer for Android");
    androidUrl.setTarget("_blank");

    ListDataProvider<Set> listDataProvider = new ListDataProvider<Set>();
    listDataProvider.addDataDisplay(setsTable);
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

    Element androidElement = getElement().getFirstChildElement().getFirstChildElement();
    final Animation androidAnimation = new AndroidAnimation(androidElement);

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
    FestivalEnum fest = Coacheller_AppEngine.getFestFromSiteName();
    String day = dayInput.getItemText(dayInput.getSelectedIndex());
    String year = yearInput.getItemText(yearInput.getSelectedIndex());
    lollapaloozerService.getSets(fest, year, DayEnum.fromValue(day), new AsyncCallback<List<Set>>() {

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
        Collections.sort(setsList, ComparatorUtils.SET_SCORE_COMPARATOR);

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
        Point point = new Point(set.getArtistName(), set.getAvgScoreOne()).setColor("#FF7800");
        pointsList.add(point);
      }
    } else if (chartDataSortInput.getItemText(chartDataSortInput.getSelectedIndex()).equals("Score")) {
      // sort first
      Collections.sort(setsList, ComparatorUtils.SET_SCORE_COMPARATOR);

      for (Set set : setsList) {
        artistsList.add(set.getArtistName());
        Point point = new Point(set.getArtistName(), set.getAvgScoreOne()).setColor("#FF7800");
        pointsList.add(point);
      }
    } else {
      // sort first
      Collections.sort(setsList, ComparatorUtils.SET_TIME_COMPARATOR);

      for (Set set : setsList) {
        String timeString = DateTimeUtils.militaryToCivilianTime(set.getTimeOne());
        String nameCombo = timeString + ": " + set.getArtistName();
        artistsList.add(nameCombo);
        Point point = new Point(set.getArtistName(), set.getAvgScoreOne()).setColor("#FF7800");
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

  public static class SetsTable extends CellTable<Set> {

    public Column<Set, String> dayColumn;
    public Column<Set, String> timeOneColumn;
    public Column<Set, String> artistNameColumn;
    public Column<Set, String> avgScoreOneColumn;
    public Column<Set, String> stageOneColumn;

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
            LollapaloozerRateComposite lollapaloozerRateComposite = new LollapaloozerRateComposite(targetSet);
            rateDialog.add(lollapaloozerRateComposite);
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

      String columnCount();

      String columnStage();
    }

    private static TasksTableResources resources = GWT.create(TasksTableResources.class);

    public SetsTable() {
      super(100, resources);

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

      avgScoreOneColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          String value = "";
          if (object.getAvgScoreOne() != null) {
            value = object.getAvgScoreOne().toString();
          }
          if (object.getNumRatingsOne() != null) {
            value += " (" + object.getNumRatingsOne().toString() + ")";
          }
          return value;
        }
      };
      addColumn(avgScoreOneColumn, "Average Score");
      addColumnStyleName(4, "columnFill");
      addColumnStyleName(4, resources.cellTableStyle().columnScore());

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