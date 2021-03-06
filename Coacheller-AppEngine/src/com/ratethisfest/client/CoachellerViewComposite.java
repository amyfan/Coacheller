package com.ratethisfest.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.BarChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.ratethisfest.shared.DateTimeUtils;
import com.ratethisfest.shared.Set;

public class CoachellerViewComposite extends Composite {

  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  interface Binder extends UiBinder<Widget, CoachellerViewComposite> {
  }

  private static Binder uiBinder = GWT.create(Binder.class);

  /**
   * Create a remote service proxy to talk to the server-side Coacheller
   * service.
   */
  private final CoachellerServiceAsync coachellerService = GWT.create(CoachellerService.class);
  private List<Set> setsList;

  //@UiField
  //Label subtitle;

  @UiField
  Anchor android;

  @UiField
  Anchor ios;

  @UiField
  Label infoBox;

  @UiField
  ListBox dayInput;

  @UiField
  ListBox yearInput;

  @UiField
  ListBox chartTypeInput;

  @UiField
  SimplePanel setsChartPanel;

  @UiField
  SetsTable setsTable;

  // @UiField
  // com.google.gwt.user.client.ui.Button queryButton;

  // @UiField
  // com.google.gwt.user.client.ui.Button rateButton;

  public CoachellerViewComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();

    retrieveSets();

    // TODO: see if we wanna auto refresh
    // Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
    // public boolean execute() {
    // retrieveSets();
    // return true;
    // }
    // }, DELAY_MS);
  }

  private void initUiElements() {
    //subtitle.setText("Your unofficial Coachella ratings guide");
    android.setHref("http://play.google.com/store/apps/details?id=com.coacheller");
    android.setText("Download Coacheller for Android!");
    android.setTarget("_blank");
    
    ios.setHref("https://itunes.apple.com/us/app/coacheller-unofficial/id634889261?ls=1&mt=8");
    ios.setText("Download Coacheller for iPhone!");
    ios.setTarget("_blank");

    ListDataProvider<Set> listDataProvider = new ListDataProvider<Set>();
    listDataProvider.addDataDisplay(setsTable);
    setsList = listDataProvider.getList();

    // Create a callback to be called when the visualization API
    // has been loaded.
    Runnable onLoadCallback = new Runnable() {
      @Override
      public void run() {
        // Create a bar chart
        setsChartPanel.add(createChart());
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
    // FlowControl.go(new CoachellerChartComposite(appUser.getEmail()));
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

    dayInput.addItem("Friday");
    dayInput.addItem("Saturday");
    dayInput.addItem("Sunday");

    dayInput.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        retrieveSets();
        // androidAnimation.run(400);
      }
    });

    yearInput.addItem("2013");
    yearInput.addItem("2012");

    yearInput.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        retrieveSets();
        // androidAnimation.run(400);
      }
    });

    chartTypeInput.addItem("Score");
    chartTypeInput.addItem("Set Time");
    chartTypeInput.addItem("Artist Name");

    chartTypeInput.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        final DataTable dataTable = (DataTable) createChartDataTable();

        // Create a callback to be called when the visualization API
        // has been loaded.
        Runnable onLoadCallback = new Runnable() {
          @Override
          public void run() {
            // Create a bar chart
            setsChartPanel.clear();
            setsChartPanel.add(createChart());
          }
        };

        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
        VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);
      }
    });

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
    // FlowControl.go(new CoachellerEmailComposite());
    // }
    // });
  }

  @Override
  public String getTitle() {
    return PageToken.VIEW.getValue();
  }

  private void retrieveSets() {
    infoBox.setText("");
    String day = dayInput.getItemText(dayInput.getSelectedIndex());
    String year = yearInput.getItemText(yearInput.getSelectedIndex());
    coachellerService.getSets(year, day, new AsyncCallback<List<Set>>() {

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
            // Create a bar chart
            setsChartPanel.clear();
            setsChartPanel.add(createChart());
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
    // options.setTitle("Coachella Set Ratings");
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
      data.addColumn(ColumnType.NUMBER, "Week 1 Average Score");
      data.addColumn(ColumnType.NUMBER, "Week 2 Average Score");
    } else {
      data.addColumn(ColumnType.STRING, "Artist Name");
      data.addColumn(ColumnType.NUMBER, "Week 1 Average Score");
      data.addColumn(ColumnType.NUMBER, "Week 2 Average Score");
      data.addRows(setsList.size());
      if (chartTypeInput.getItemText(chartTypeInput.getSelectedIndex()).equals("Artist Name")) {
        // sort first
        Collections.sort(setsList, ComparatorUtils.SET_NAME_COMPARATOR);

        int setNum = 0;
        for (Set set : setsList) {
          data.setValue(setNum, 0, set.getArtistName());
          data.setValue(setNum, 1, set.getAvgScoreOne());
          data.setValue(setNum, 2, set.getAvgScoreTwo());
          setNum++;
        }
      } else if (chartTypeInput.getItemText(chartTypeInput.getSelectedIndex()).equals("Score")) {
        // sort first
        Collections.sort(setsList, ComparatorUtils.DOUBLE_SET_SCORE_COMPARATOR);

        int setNum = 0;
        for (Set set : setsList) {
          data.setValue(setNum, 0, set.getArtistName());
          data.setValue(setNum, 1, set.getAvgScoreOne());
          data.setValue(setNum, 2, set.getAvgScoreTwo());
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
          data.setValue(setNum, 2, set.getAvgScoreTwo());
          setNum++;
        }
      }
    }
    return data;
  }

  private Chart createChart() {
    Chart chart = new Chart().setType(Series.Type.BAR)
        .setChartTitleText(yearInput.getItemText(yearInput.getSelectedIndex()) + " RATING RESULTS")
        .setMarginRight(10);
    chart.getXAxis().setAxisTitleText("Artist");
    chart.getYAxis().setAxisTitleText("Score").setMin(0).setMax(5);
    Series series = chart.createSeries().setName("Average Score");
    if (setsList != null) {
      chart.setHeight(setsList.size() * 35);
      List<String> artistsList = new ArrayList<String>();
      List<Point> pointsList = new ArrayList<Point>();

      if (chartTypeInput.getItemText(chartTypeInput.getSelectedIndex()).equals("Artist Name")) {
        // sort first
        Collections.sort(setsList, ComparatorUtils.SET_NAME_COMPARATOR);

        for (Set set : setsList) {
          artistsList.add(set.getArtistName() + "- 1");
          artistsList.add("2");
          Point point = new Point(set.getArtistName(), set.getAvgScoreOne()).setColor("#000033");
          pointsList.add(point);
          point = new Point(set.getArtistName(), set.getAvgScoreTwo()).setColor("#900070");
          pointsList.add(point);
        }
      } else if (chartTypeInput.getItemText(chartTypeInput.getSelectedIndex()).equals("Score")) {
        // sort first
        Collections.sort(setsList, ComparatorUtils.SET_SCORE_COMPARATOR);

        for (Set set : setsList) {
          artistsList.add(set.getArtistName() + "- 1");
          artistsList.add("2");
          Point point = new Point(set.getArtistName(), set.getAvgScoreOne()).setColor("#000033");
          pointsList.add(point);
          point = new Point(set.getArtistName(), set.getAvgScoreTwo()).setColor("#900070");
          pointsList.add(point);
        }
      } else {
        // sort first
        Collections.sort(setsList, ComparatorUtils.SET_TIME_COMPARATOR);

        for (Set set : setsList) {
          String timeString = DateTimeUtils.militaryToCivilianTime(set.getTimeOne());
          String nameCombo = timeString + ": " + set.getArtistName() + " - 1";
          artistsList.add(nameCombo);
          timeString = DateTimeUtils.militaryToCivilianTime(set.getTimeTwo());
          nameCombo = timeString + ": " + set.getArtistName() + " - 2";
          // artistsList.add(nameCombo);
          artistsList.add("2");
          Point point = new Point(set.getArtistName(), set.getAvgScoreOne()).setColor("#000033");
          pointsList.add(point);
          point = new Point(set.getArtistName(), set.getAvgScoreTwo()).setColor("#900070");
          pointsList.add(point);
        }
      }

      String[] artistsArray = artistsList.toArray(new String[artistsList.size()]);
      chart.getXAxis().setCategories(artistsArray);

      Point[] pointsArray = pointsList.toArray(new Point[pointsList.size()]);
      series.setPoints(pointsArray);

      chart.addSeries(series);
    }
    return chart;
  }

  public static class SetsTable extends CellTable<Set> {

    public Column<Set, String> dayColumn;
    public Column<Set, String> timeOneColumn;
    // public Column<Set, String> timeTwoColumn;
    public Column<Set, String> artistNameColumn;
    public Column<Set, String> avgScoreOneColumn;
    public Column<Set, String> avgScoreTwoColumn;
    // public Column<Set, String> numRatingsOneColumn;
    // public Column<Set, String> numRatingsTwoColumn;
    public Column<Set, String> stageOneColumn;

    // public Column<Set, String> stageTwoColumn;

    interface TasksTableResources extends CellTable.Resources {
      @Override
      @Source("SetsTable.css")
      TableStyle cellTableStyle();
    }

    interface TableStyle extends CellTable.Style {

      String columnTime();

      String columnDay();

      String columnName();

      String columnScore();

      String columnStage();
    }

    private static TasksTableResources resources = GWT.create(TasksTableResources.class);

    public SetsTable() {
      super(100, resources);

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
      addColumn(avgScoreOneColumn, "Week 1 Score");
      addColumnStyleName(4, "columnFill");
      addColumnStyleName(4, resources.cellTableStyle().columnScore());

      avgScoreTwoColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          String value = "";
          if (object.getAvgScoreTwo() != null) {
            value = object.getAvgScoreTwo().toString();
          }
          if (object.getNumRatingsOne() != null) {
            value += " (" + object.getNumRatingsTwo().toString() + ")";
          }
          return value;
        }
      };
      addColumn(avgScoreTwoColumn, "Week 2 Score");
      addColumnStyleName(5, "columnFill");
      addColumnStyleName(5, resources.cellTableStyle().columnScore());
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