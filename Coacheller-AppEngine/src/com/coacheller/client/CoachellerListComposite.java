package com.coacheller.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.coacheller.shared.Set;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class CoachellerListComposite extends Composite {

  interface Binder extends UiBinder<Widget, CoachellerListComposite> {
  }

  private static final int DELAY_MS = 1000;

  private static Binder uiBinder = GWT.create(Binder.class);

  @UiField
  Label infoBox;

  @UiField
  ListBox dayInput;

  @UiField
  TextBox userEmailAddressInput;

  @UiField
  SetsTable setsTable;

  @UiField
  SimplePanel setsChartPanel;

  @UiField
  com.google.gwt.user.client.ui.Button queryButton;

  // @UiField
  // com.google.gwt.user.client.ui.Button viewChartButton;

  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  /**
   * Create a remote service proxy to talk to the server-side Coacheller
   * service.
   */
  private final CoachellerServiceAsync coachellerService = GWT.create(CoachellerService.class);
  private List<Set> usersList;

  public CoachellerListComposite() {
    initWidget(uiBinder.createAndBindUi(this));

    initUiElements();

    // TODO: see if we wanna auto refresh
    // Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
    // public boolean execute() {
    // retrieveSets();
    // return true;
    // }
    // }, DELAY_MS);
  }

  private void initUiElements() {
    ListDataProvider<Set> listDataProvider = new ListDataProvider<Set>();
    listDataProvider.addDataDisplay(setsTable);
    usersList = listDataProvider.getList();

    // Create a callback to be called when the visualization API
    // has been loaded.
    Runnable onLoadCallback = new Runnable() {
      public void run() {
        // Create a pie chart visualization.
        PieChart pie = new PieChart(createTable(), createOptions());
        setsChartPanel.add(pie);
      }
    };

    // Load the visualization api, passing the onLoadCallback to be called
    // when loading is done.
    VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);

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
    // usersList.remove(appUser);
    // }
    // });

    dayInput.addItem("Friday");
    dayInput.addItem("Saturday");
    dayInput.addItem("Sunday");
    userEmailAddressInput.getElement().setPropertyString("placeholder", "Enter email address here");

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

    queryButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String day = dayInput.getItemText(dayInput.getSelectedIndex());
        String email = userEmailAddressInput.getText();
        retrieveSets(email, day);

        androidAnimation.run(400);
      }
    });

    // viewChartButton.addClickHandler(new ClickHandler() {
    // @Override
    // public void onClick(ClickEvent event) {
    // FlowControl.go(new CoachellerChartComposite());
    // }
    // });
  }

  @Override
  public String getTitle() {
    return PageToken.LIST.getValue();
  }

  private void retrieveSets(String email, String day) {
    coachellerService.getSets(email, "2012", day, new AsyncCallback<List<Set>>() {

      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        infoBox.setText(SERVER_ERROR);
      }

      public void onSuccess(List<Set> result) {
        // sort first
        ArrayList<Set> sortedTasks = new ArrayList<Set>(result);
        Collections.sort(sortedTasks, SET_COMPARATOR);

        usersList.clear();
        for (Set appUser : sortedTasks) {
          usersList.add(appUser);
        }
      }
    });
  }

  // @UiFactory
  // PieChart makePieChart() { // method name is insignificant
  // return new PieChart(createTable(), createOptions());
  // }

  private Options createOptions() {
    Options options = Options.create();
    options.setWidth(400);
    options.setHeight(240);
    options.setTitle("My Daily Activities");
    return options;
  }

  private AbstractDataTable createTable() {
    DataTable data = DataTable.create();
    data.addColumn(ColumnType.STRING, "Task");
    data.addColumn(ColumnType.NUMBER, "Hours per Day");
    data.addRows(2);
    data.setValue(0, 0, "Work");
    data.setValue(0, 1, 14);
    data.setValue(1, 0, "Sleep");
    data.setValue(1, 1, 10);
    return data;
  }

  public static final Comparator<? super Set> SET_COMPARATOR = new Comparator<Set>() {
    public int compare(Set t0, Set t1) {
      // Sort by set time first
      if (t0.getTime() < t1.getTime()) {
        return 1;
      } else if (t0.getTime() > t1.getTime()) {
        return -1;
      } else {
        // Sort items alphabetically within each group
        return t0.getArtistName().compareTo(t1.getArtistName());
      }
    }
  };

  public static class SetsTable extends CellTable<Set> {

    public Column<Set, Boolean> activeColumn;
    public Column<Set, String> dayColumn;
    public Column<Set, String> timeColumn;
    public Column<Set, String> artistNameColumn;
    public Column<Set, String> avgScoreOneColumn;
    public Column<Set, String> avgScoreTwoColumn;

    interface TasksTableResources extends CellTable.Resources {
      @Source("CoachellerTable.css")
      TableStyle cellTableStyle();
    }

    interface TableStyle extends CellTable.Style {
      String columnCheckbox();

      String columnText();

      String columnDate();

      String columnTrash();
    }

    private static TasksTableResources resources = GWT.create(TasksTableResources.class);

    public SetsTable() {
      super(20, resources);

      activeColumn = new Column<Set, Boolean>(new CheckboxCell()) {
        @Override
        public Boolean getValue(Set object) {
          return false;
          // return object.isActive() == Boolean.TRUE;
        }
      };
      addColumn(activeColumn, "\u2713"); // Checkmark
      addColumnStyleName(0, resources.cellTableStyle().columnCheckbox());

      dayColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          return object.getDay();
        }
      };
      addColumn(dayColumn, "Day");
      addColumnStyleName(1, "columnFill");
      addColumnStyleName(1, resources.cellTableStyle().columnText());

      timeColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          return object.getTime().toString();
        }
      };
      addColumn(timeColumn, "Set Time");
      addColumnStyleName(2, "columnFill");
      addColumnStyleName(2, resources.cellTableStyle().columnText());

      artistNameColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          return object.getArtistName();
        }
      };
      // TODO: figure out why the hell I don't see column headers
      addColumn(artistNameColumn, "Artist Name");
      addColumnStyleName(3, "columnFill");
      addColumnStyleName(3, resources.cellTableStyle().columnText());

      avgScoreOneColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          if (object.getAvgScoreOne() != null) {
            return object.getAvgScoreOne().toString();
          }
          return "";
        }
      };
      addColumn(avgScoreOneColumn, "Weekend 1 Average Score");
      addColumnStyleName(4, "columnFill");
      addColumnStyleName(4, resources.cellTableStyle().columnDate());

      avgScoreTwoColumn = new Column<Set, String>(new TextCell()) {
        @Override
        public String getValue(Set object) {
          if (object.getAvgScoreTwo() != null) {
            return object.getAvgScoreTwo().toString();
          }
          return "";
        }
      };
      addColumn(avgScoreTwoColumn, "Weekend 2 Average Score");
      addColumnStyleName(5, "columnFill");
      addColumnStyleName(5, resources.cellTableStyle().columnDate());

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