package com.coacheller.client;

import com.coacheller.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Coacheller_AppEngine implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";
  private static final String JSON_ERROR = "An error occurred while processing the JSON";

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    final Button populateButton = new Button("Populate");
    final Button calculateButton = new Button("Calculate Score Averages");
    final Button querySetButton = new Button("Query Sets");
    final Button addRatingButton = new Button("Add Rating");
    final Button queryRatingButton = new Button("Query Ratings");
    final Button clearRatingButton = new Button("Clear Ratings");
    final Button clearUserButton = new Button("Clear Users");
    final TextBox emailField = new TextBox();
    emailField.setText("amyfan@gmail.com");
    final TextBox yearField = new TextBox();
    yearField.setText("2012");
    final TextBox dayField = new TextBox();
    dayField.setText("Friday");
    final TextBox artistField = new TextBox();
    artistField.setText("Afrojack");
    final TextBox weekendField = new TextBox();
    weekendField.setText("1");
    final TextBox scoreField = new TextBox();
    scoreField.setText("5");
    final Label errorLabel = new Label();

    // We can add style names to widgets
    populateButton.addStyleName("populateButton");
    querySetButton.addStyleName("querySetButton");

    // Add the nameField and sendButton to the RootPanel
    // Use RootPanel.get() to get the entire body element
    RootPanel.get("emailFieldContainer").add(emailField);
    RootPanel.get("yearFieldContainer").add(yearField);
    RootPanel.get("dayFieldContainer").add(dayField);
    RootPanel.get("artistFieldContainer").add(artistField);
    RootPanel.get("weekendFieldContainer").add(weekendField);
    RootPanel.get("scoreFieldContainer").add(scoreField);
    RootPanel.get("populateButtonContainer").add(populateButton);
    RootPanel.get("calculateButtonContainer").add(calculateButton);
    RootPanel.get("querySetButtonContainer").add(querySetButton);
    RootPanel.get("addRatingButtonContainer").add(addRatingButton);
    RootPanel.get("queryRatingButtonContainer").add(queryRatingButton);
    RootPanel.get("clearRatingButtonContainer").add(clearRatingButton);
    RootPanel.get("clearUserButtonContainer").add(clearUserButton);
    RootPanel.get("errorLabelContainer").add(errorLabel);

    // Focus the cursor on the email field when the app loads
    emailField.setFocus(true);
    emailField.selectAll();

    // Create the popup dialog box
    final DialogBox dialogBox = new DialogBox();
    dialogBox.setText("Remote Procedure Call");
    dialogBox.setAnimationEnabled(true);
    final Button closeButton = new Button("Close");
    // We can set the id of a widget by accessing its Element
    closeButton.getElement().setId("closeButton");
    final Label textToServerLabel = new Label();
    final HTML serverResponseLabel = new HTML();
    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.addStyleName("dialogVPanel");
    dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
    dialogVPanel.add(textToServerLabel);
    dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
    dialogVPanel.add(serverResponseLabel);
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    dialogVPanel.add(closeButton);
    dialogBox.setWidget(dialogVPanel);

    // Add a handler to close the DialogBox
    closeButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
        querySetButton.setEnabled(true);
        querySetButton.setFocus(true);
        queryRatingButton.setEnabled(true);
        populateButton.setEnabled(true);
        calculateButton.setEnabled(true);
        addRatingButton.setEnabled(true);
        clearRatingButton.setEnabled(true);
        clearUserButton.setEnabled(true);
      }
    });

    // Create a handler for the sendButton and emailField
    class PopulateHandler implements ClickHandler, KeyUpHandler {
      /**
       * Fired when the user clicks on the sendButton.
       */
      public void onClick(ClickEvent event) {
        if (event.getSource() == populateButton) {
          populateDatabase();
        } else if (event.getSource() == calculateButton) {
          calculateScoreAverages();
        } else if (event.getSource() == addRatingButton) {
          addRating();
        }
      }

      /**
       * Fired when the user types in the nameField.
       */
      public void onKeyUp(KeyUpEvent event) {
        // if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        // populateDatabase();
        // }
      }

      /**
       * Send the name from the nameField to the server and wait for a response.
       */
      private void populateDatabase() {
        // First, we validate the input.
        errorLabel.setText("");

        // Then, we send the input to the server.
        populateButton.setEnabled(false);
        serverResponseLabel.setText("");
        greetingService.loadSetData(new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            dialogBox.setText("Remote Procedure Call - Failure");
            serverResponseLabel.addStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(SERVER_ERROR);
            dialogBox.center();
            closeButton.setFocus(true);
          }

          public void onSuccess(String result) {
            dialogBox.setText("Remote Procedure Call");
            serverResponseLabel.removeStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(result);
            dialogBox.center();
            closeButton.setFocus(true);
          }
        });
      }

      private void calculateScoreAverages() {
        // First, we validate the input.
        errorLabel.setText("");

        // Then, we send the input to the server.
        calculateButton.setEnabled(false);
        serverResponseLabel.setText("");
        greetingService.calculateSetRatingAverages(new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            dialogBox.setText("Remote Procedure Call - Failure");
            serverResponseLabel.addStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(SERVER_ERROR);
            dialogBox.center();
            closeButton.setFocus(true);
          }

          public void onSuccess(String result) {
            dialogBox.setText("Remote Procedure Call");
            serverResponseLabel.removeStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(result);
            dialogBox.center();
            closeButton.setFocus(true);
          }
        });
      }

      /**
       * Send the name from the nameField to the server and wait for a response.
       */
      private void addRating() {
        // First, we validate the input.
        errorLabel.setText("");
        String email = emailField.getText();
        String artist = artistField.getText();
        String weekend = weekendField.getText();
        String score = scoreField.getText();
        if (!FieldVerifier.isValidEmail(email)) {
          errorLabel.setText("Please enter valid email address");
          return;
        }

        // Then, we send the input to the server.
        addRatingButton.setEnabled(false);
        serverResponseLabel.setText("");
        greetingService.addRatingBySetArtist(email, artist, weekend, score,
            new AsyncCallback<String>() {
              public void onFailure(Throwable caught) {
                // Show the RPC error message to the user
                dialogBox.setText("Remote Procedure Call - Failure");
                serverResponseLabel.addStyleName("serverResponseLabelError");
                serverResponseLabel.setHTML(SERVER_ERROR);
                dialogBox.center();
                closeButton.setFocus(true);
              }

              public void onSuccess(String result) {
                dialogBox.setText("Remote Procedure Call");
                serverResponseLabel.removeStyleName("serverResponseLabelError");
                serverResponseLabel.setHTML(result);
                dialogBox.center();
                closeButton.setFocus(true);
              }
            });
      }
    }

    // Create a handler for the sendButton and emailField
    class QueryHandler implements ClickHandler, KeyUpHandler {
      /**
       * Fired when the user clicks on the sendButton.
       */
      public void onClick(ClickEvent event) {
        if (event.getSource() == querySetButton) {
          getSetsFromServer();
        } else if (event.getSource() == queryRatingButton) {
          getRatingsFromServer();
        }
      }

      /**
       * Fired when the user types in the nameField.
       */
      public void onKeyUp(KeyUpEvent event) {
      }

      /**
       * Send the name from the nameField to the server and wait for a response.
       */
      private void getSetsFromServer() {
        // First, we validate the input.
        errorLabel.setText("");
        String email = emailField.getText();
        String year = yearField.getText();
        String day = dayField.getText();
        if (!FieldVerifier.isValidEmail(email)) {
          errorLabel.setText("Please enter valid email address");
          return;
        }
        if (!FieldVerifier.isValidYear(year)) {
          errorLabel.setText("Please enter valid year");
          return;
        }
        if (day != null && !day.isEmpty()) {
          if (!FieldVerifier.isValidDay(day)) {
            errorLabel.setText("Please enter valid day");
            return;
          }
        }

        // Then, we send the input to the server.
        querySetButton.setEnabled(false);
        textToServerLabel.setText(email);
        serverResponseLabel.setText("");
        greetingService.getSets(email, year, day, new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            dialogBox.setText("Remote Procedure Call - Failure");
            serverResponseLabel.addStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(SERVER_ERROR);
            dialogBox.center();
            closeButton.setFocus(true);
          }

          public void onSuccess(String result) {
            dialogBox.setText("Remote Procedure Call");
            serverResponseLabel.removeStyleName("serverResponseLabelError");
            // String sets =
            // JSONUtils.convertJSONArrayStringToSetString(result);
            serverResponseLabel.setHTML(result);
            dialogBox.center();
            closeButton.setFocus(true);
          }
        });
      }

      /**
       * Send the name from the nameField to the server and wait for a response.
       */
      private void getRatingsFromServer() {
        // First, we validate the input.
        errorLabel.setText("");
        String email = emailField.getText();
        String artist = artistField.getText();
        if (!FieldVerifier.isValidEmail(email)) {
          errorLabel.setText("Please enter valid email address");
          return;
        }

        // Then, we send the input to the server.
        queryRatingButton.setEnabled(false);
        textToServerLabel.setText(email);
        serverResponseLabel.setText("");
        greetingService.getRatingsBySetArtist(email, artist, new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            dialogBox.setText("Remote Procedure Call - Failure");
            serverResponseLabel.addStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(SERVER_ERROR);
            dialogBox.center();
            closeButton.setFocus(true);
          }

          public void onSuccess(String result) {
            dialogBox.setText("Remote Procedure Call");
            serverResponseLabel.removeStyleName("serverResponseLabelError");
            // String sets =
            // JSONUtils.convertJSONArrayStringToSetString(result);
            serverResponseLabel.setHTML(result);
            dialogBox.center();
            closeButton.setFocus(true);
          }
        });
      }
    }

    // Create a handler for the sendButton and emailField
    class ClearHandler implements ClickHandler, KeyUpHandler {
      /**
       * Fired when the user clicks on the sendButton.
       */
      public void onClick(ClickEvent event) {
        if (event.getSource() == clearRatingButton) {
          clearRatingsFromServer();
        } else if (event.getSource() == clearUserButton) {
          clearUsersFromServer();
        }
      }

      /**
       * Fired when the user types in the nameField.
       */
      public void onKeyUp(KeyUpEvent event) {
      }

      /**
       * Send the name from the nameField to the server and wait for a response.
       */
      private void clearRatingsFromServer() {
        // First, we validate the input.
        errorLabel.setText("");

        // Then, we send the input to the server.
        clearRatingButton.setEnabled(false);
        serverResponseLabel.setText("");
        greetingService.deleteAllRatings(new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            dialogBox.setText("Remote Procedure Call - Failure");
            serverResponseLabel.addStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(SERVER_ERROR);
            dialogBox.center();
            closeButton.setFocus(true);
          }

          public void onSuccess(String result) {
            dialogBox.setText("Remote Procedure Call");
            serverResponseLabel.removeStyleName("serverResponseLabelError");
            // String sets =
            // JSONUtils.convertJSONArrayStringToSetString(result);
            serverResponseLabel.setHTML(result);
            dialogBox.center();
            closeButton.setFocus(true);
          }
        });
      }

      /**
       * Send the name from the nameField to the server and wait for a response.
       */
      private void clearUsersFromServer() {
        // First, we validate the input.
        errorLabel.setText("");

        // Then, we send the input to the server.
        clearUserButton.setEnabled(false);
        serverResponseLabel.setText("");
        greetingService.deleteAllUsers(new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            // Show the RPC error message to the user
            dialogBox.setText("Remote Procedure Call - Failure");
            serverResponseLabel.addStyleName("serverResponseLabelError");
            serverResponseLabel.setHTML(SERVER_ERROR);
            dialogBox.center();
            closeButton.setFocus(true);
          }

          public void onSuccess(String result) {
            dialogBox.setText("Remote Procedure Call");
            serverResponseLabel.removeStyleName("serverResponseLabelError");
            // String sets =
            // JSONUtils.convertJSONArrayStringToSetString(result);
            serverResponseLabel.setHTML(result);
            dialogBox.center();
            closeButton.setFocus(true);
          }
        });
      }
    }

    // Add a handler to send the name to the server
    PopulateHandler populateHandler = new PopulateHandler();
    populateButton.addClickHandler(populateHandler);
    calculateButton.addClickHandler(populateHandler);
    addRatingButton.addClickHandler(populateHandler);
    QueryHandler queryHandler = new QueryHandler();
    querySetButton.addClickHandler(queryHandler);
    queryRatingButton.addClickHandler(queryHandler);
    ClearHandler clearHandler = new ClearHandler();
    clearRatingButton.addClickHandler(clearHandler);
    clearUserButton.addClickHandler(clearHandler);
    // emailField.addKeyUpHandler(populateHandler);
  }
}
