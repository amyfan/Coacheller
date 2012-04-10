package com.coacheller.client;

import com.google.gwt.user.client.ui.Composite;

public class CoachellerChartComposite extends Composite {
  //
  // private String ownerEmail;
  //
  // interface Binder extends UiBinder<Widget, CoachellerChartComposite> {
  // }
  //
  // private static final int DELAY_MS = 1000;
  //
  // private static Binder uiBinder = GWT.create(Binder.class);
  //
  // @UiField
  // TextBox voterEmailInput;
  //
  // @UiField
  // UserPollsTable usersTable;
  //
  // @UiField
  // com.google.gwt.user.client.ui.Button addButton;
  //
  // @UiField
  // com.google.gwt.user.client.ui.Button backButton;
  //
  // private final EventBus eventBus = new SimpleEventBus();
  // private final FashionMeRequestFactory requestFactory =
  // GWT.create(FashionMeRequestFactory.class);
  // private List<PollProxy> usersList;
  //
  // public CoachellerChartComposite() {
  // initWidget(uiBinder.createAndBindUi(this));
  //
  // requestFactory.initialize(eventBus);
  //
  // initUiElements();
  //
  // Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
  // public boolean execute() {
  // if (ownerEmail != null) {
  // retrievePolls(ownerEmail);
  // }
  // return true;
  // }
  // }, DELAY_MS);
  // }
  //
  // public CoachellerChartComposite(String ownerEmail) {
  // this();
  // this.ownerEmail = ownerEmail;
  // }
  //
  // private void initUiElements() {
  //
  // ListDataProvider<PollProxy> listDataProvider = new
  // ListDataProvider<PollProxy>();
  // listDataProvider.addDataDisplay(usersTable);
  // usersList = listDataProvider.getList();
  //
  // Element androidElement =
  // getElement().getFirstChildElement().getFirstChildElement();
  // final Animation androidAnimation = new AndroidAnimation(androidElement);
  //
  // // usersTable.setRowStyles(new RowStyles<PollProxy>() {
  // // public String getStyleNames(PollProxy row, int rowIndex) {
  // // return isActive(row) ? "Active" : "Inactive";
  // // }
  // // });
  //
  // usersTable.activeColumn.setFieldUpdater(new FieldUpdater<PollProxy,
  // Boolean>() {
  // public void update(int index, PollProxy poll, Boolean value) {
  // FlowControl.go(new PollPhotoWidget(poll));
  // }
  // });
  //
  // usersTable.dateColumn.setFieldUpdater(new FieldUpdater<PollProxy, Date>() {
  // public void update(int index, PollProxy poll, Date value) {
  // PollRequest request = requestFactory.pollRequest();
  // PollProxy updatedTask = request.edit(poll);
  // updatedTask.setDateModified(value);
  // request.updatePoll(updatedTask).fire();
  // }
  // });
  //
  // usersTable.deleteColumn.setFieldUpdater(new FieldUpdater<PollProxy,
  // String>() {
  // public void update(int index, PollProxy poll, String value) {
  // PollRequest request = requestFactory.pollRequest();
  // request.deletePoll(poll).fire();
  // usersList.remove(poll);
  // }
  // });
  //
  // voterEmailInput.getElement().setPropertyString("placeholder",
  // "Enter voter email here");
  //
  // // userNameInput.addKeyUpHandler(new KeyUpHandler() {
  // // public void onKeyUp(KeyUpEvent event) {
  // // if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
  // // String message = userNameInput.getText();
  // // userNameInput.setText("");
  // // sendNewUserToServer(message);
  // //
  // // androidAnimation.run(400);
  // // }
  // // }
  // // });
  //
  // addButton.addClickHandler(new ClickHandler() {
  // @Override
  // public void onClick(ClickEvent event) {
  // String name = voterEmailInput.getText();
  // voterEmailInput.setText("");
  // sendNewPollToServer(name);
  //
  // androidAnimation.run(400);
  // }
  // });
  //
  // backButton.addClickHandler(new ClickHandler() {
  // @Override
  // public void onClick(ClickEvent event) {
  // FlowControl.go(new CoachellerListComposite());
  // }
  // });
  // }
  //
  // @Override
  // public String getTitle() {
  // return PageToken.POLL.getValue() + "=" + ownerEmail;
  // }
  //
  // boolean isActive(PollProxy t) {
  // Boolean active = t.isActive();
  // return active != null && active;
  // }
  //
  // private void retrievePolls(String userId) {
  // requestFactory.pollRequest().queryPollsByOwner(userId).fire(new
  // Receiver<List<PollProxy>>() {
  // @Override
  // public void onSuccess(List<PollProxy> polls) {
  // // sort first
  // ArrayList<PollProxy> sortedTasks = new ArrayList<PollProxy>(polls);
  // Collections.sort(sortedTasks, POLL_COMPARATOR);
  //
  // usersList.clear();
  // for (PollProxy poll : sortedTasks) {
  // usersList.add(poll);
  // }
  // }
  // });
  // }
  //
  // /**
  // * Send a poll to the server.
  // */
  // private void sendNewPollToServer(String voterEmail) {
  // PollRequest request = requestFactory.pollRequest();
  // PollProxy poll = request.create(PollProxy.class);
  // poll.setOwnerEmail(ownerEmail);
  // poll.setVoterEmail(voterEmail);
  // poll.setDateCreated(new Date());
  // request.updatePoll(poll).fire();
  // usersList.add(poll);
  // }
  //
  // public static final Comparator<? super PollProxy> POLL_COMPARATOR = new
  // Comparator<PollProxy>() {
  // public int compare(PollProxy t0, PollProxy t1) {
  // // Sort uncompleted polls above completed polls
  // if (isActive(t0) && !isActive(t1)) {
  // return 1;
  // } else if (!isActive(t0) && isActive(t1)) {
  // return -1;
  // } else {
  // // Sort items by date modified within each group
  // return compareArtistName(t0, t1);
  // }
  // }
  //
  // boolean isActive(PollProxy t) {
  // Boolean done = t.isActive();
  // return done != null && done;
  // }
  //
  // int compareDateModified(PollProxy t0, PollProxy t1) {
  // Date d0 = t0.getDateModified();
  // Date d1 = t1.getDateModified();
  //
  // if (d0 == null) {
  // if (d1 == null) {
  // return 0;
  // } else {
  // return -1;
  // }
  // } else if (d1 == null) {
  // return 1;
  // }
  // long delta = d0.getTime() - d1.getTime();
  // if (delta < 0) {
  // return -1;
  // } else if (delta > 0) {
  // return 1;
  // } else {
  // return 0;
  // }
  // }
  // };
  //
  // public static class UserPollsTable extends CellTable<PollProxy> {
  //
  // public Column<PollProxy, Date> dateColumn;
  // public Column<PollProxy, String> deleteColumn;
  // public Column<PollProxy, Boolean> activeColumn;
  // public Column<PollProxy, String> voterEmailColumn;
  // public Column<PollProxy, String> emailColumn;
  //
  // interface TasksTableResources extends CellTable.Resources {
  // @Source("FashionMeTable.css")
  // TableStyle cellTableStyle();
  // }
  //
  // interface TableStyle extends CellTable.Style {
  // String columnCheckbox();
  //
  // String columnText();
  //
  // String columnDate();
  //
  // String columnTrash();
  // }
  //
  // private static TasksTableResources resources =
  // GWT.create(TasksTableResources.class);
  //
  // public UserPollsTable() {
  // super(20, resources);
  //
  // activeColumn = new Column<PollProxy, Boolean>(new CheckboxCell()) {
  // @Override
  // public Boolean getValue(PollProxy object) {
  // return object.isActive() == Boolean.TRUE;
  // }
  // };
  //
  // addColumn(activeColumn, "\u2713"); // Checkmark
  // addColumnStyleName(0, resources.cellTableStyle().columnCheckbox());
  //
  // voterEmailColumn = new Column<PollProxy, String>(new TextCell()) {
  // @Override
  // public String getValue(PollProxy object) {
  // return object.getVoterEmail();
  // }
  // };
  // addColumn(voterEmailColumn, "Voter Email");
  // addColumnStyleName(1, "columnFill");
  // addColumnStyleName(1, resources.cellTableStyle().columnText());
  //
  // dateColumn = new Column<PollProxy, Date>(new DatePickerCell(
  // DateTimeFormat.getFormat(PredefinedFormat.MONTH_ABBR_DAY))) {
  // @Override
  // public Date getValue(PollProxy poll) {
  // Date dateModified = poll.getDateModified();
  // return dateModified == null ? new Date() : dateModified;
  // }
  // };
  // addColumn(dateColumn, "DateModified");
  // addColumnStyleName(2, resources.cellTableStyle().columnDate());
  //
  // ButtonCell buttonCell = new ButtonCell(new SafeHtmlRenderer<String>() {
  // public SafeHtml render(String object) {
  // return SafeHtmlUtils.fromTrustedString("<img src=\"delete.png\"></img>");
  // }
  //
  // public void render(String object, SafeHtmlBuilder builder) {
  // builder.append(render(object));
  // }
  // });
  //
  // deleteColumn = new Column<PollProxy, String>(buttonCell) {
  // @Override
  // public String getValue(PollProxy object) {
  // return "\u2717"; // Ballot "X" mark
  // }
  // };
  // addColumn(deleteColumn, "\u2717");
  // addColumnStyleName(3, resources.cellTableStyle().columnTrash());
  // }
  // }
  //
  // class AndroidAnimation extends Animation {
  // private static final int TOP = -50;
  // private static final int BOTTOM = 150;
  // Element element;
  //
  // public AndroidAnimation(Element element) {
  // this.element = element;
  // }
  //
  // @Override
  // protected void onStart() {
  // element.getStyle().setTop(TOP, Unit.PX);
  // }
  //
  // @Override
  // protected void onUpdate(double progress) {
  // element.getStyle().setTop(TOP + (BOTTOM - TOP) * interpolate(progress),
  // Unit.PX);
  // }
  //
  // @Override
  // protected void onComplete() {
  // element.getStyle().setTop(TOP, Unit.PX);
  // }
  // }

}