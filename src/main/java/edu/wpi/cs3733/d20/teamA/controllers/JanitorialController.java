package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.database.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.JanitorDatabase;
import java.io.IOException;
import java.sql.*;
import java.util.Hashtable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class JanitorialController extends AbstractController {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase gDB;
  JanitorDatabase jDB;

  @FXML private JFXButton btnClearRequest;
  @FXML private JFXButton btnSubmitRequest;
  @FXML private JFXButton btnNotStarted;
  @FXML private JFXButton btnCompleted;
  @FXML private JFXButton btnInProgress;

  @FXML private JFXTextField textfieldLocation;
  @FXML private JFXTextField textfieldPriority;

  @FXML private Label labelClearRequest;
  @FXML private Label labelSubmitRequest;
  @FXML private Label labelStatus;

  @FXML private JFXListView<String> listviewActiveRequests;
  @FXML private JFXComboBox<String> comboboxPriority;

  ObservableList priorityItems = FXCollections.observableArrayList();
  ObservableList activeItems = FXCollections.observableArrayList();
  Hashtable<String, Integer> activeRequestHash = new Hashtable<>();
  Hashtable<String, String> statusHash = new Hashtable<>();

  public void initialize() throws SQLException, IOException, CsvException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    jDB = new JanitorDatabase(conn);
    gDB.createTables();
    jDB.createTables();
    jDB.readFromCSV();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("biscuit1", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    refreshActiveRequests();
    statusHash.clear();
    priorityItems.clear();
    String a = "High";
    String b = "Medium";
    String c = "Low";
    priorityItems.addAll(a, b, c);
    comboboxPriority.getItems().addAll(priorityItems);
  }

  /**
   * Adds a service request to the database
   *
   * @throws SQLException
   */
  @FXML
  private void addServiceRequest() throws SQLException {
    if ((!textfieldLocation.getText().equals("")) && !comboboxPriority.getValue().equals("")) {
      jDB.addRequest(textfieldLocation.getText(), comboboxPriority.getValue());
      statusHash.put(textfieldLocation.getText(), "Not Started");
      comboboxPriority.getSelectionModel().clearSelection();
      textfieldLocation.clear();
      labelSubmitRequest.setText("Request Submitted Successfully");
    } else if (comboboxPriority.getValue() != null && textfieldLocation.getText().equals("")) {
      labelSubmitRequest.setText("Please enter a location");
    } else if (comboboxPriority.getValue() == null && !textfieldLocation.getText().equals("")) {
      labelSubmitRequest.setText("Please enter a priority");
    } else if (comboboxPriority.getValue() == null && textfieldLocation.getText().equals("")) {
      labelSubmitRequest.setText("Please enter data");
    }
    refreshActiveRequests();
  }

  /**
   * Deletes a request from the database
   *
   * @throws SQLException
   */
  @FXML
  private void removeServiceRequest() throws SQLException {
    String request = listviewActiveRequests.getSelectionModel().getSelectedItem();
    if (request != null) {
      if (jDB.deleteRequest(activeRequestHash.get(request))) {
        labelClearRequest.setText("Service Removed Successfully");
        listviewActiveRequests.getItems().removeAll(activeItems);
      } else {
        labelClearRequest.setText("Please select an active request");
      }
    }
    refreshActiveRequests();
    statusHash.remove(request);
  }

  /**
   * Updates the list to show only active requests. Is called when combo box is clicked.
   *
   * @throws SQLException
   */
  @FXML
  private void refreshActiveRequests() throws SQLException {
    listviewActiveRequests.getItems().clear();
    activeItems.clear();
    activeRequestHash.clear();
    String Request;
    int size = jDB.getRequestSize();
    for (int i = 3; i < size + 3; i++) {
      Request = jDB.getLocation(i);
      if (Request == null) {
        continue;
      } else {
        activeItems.add(Request);
        activeRequestHash.put(Request, i);
      }
    }
    listviewActiveRequests.getItems().addAll(activeItems);
  }

  @FXML
  private void elementSelect() throws SQLException {
    String Request = listviewActiveRequests.getSelectionModel().getSelectedItem();
    if (Request != null) {
      btnCompleted.setVisible(true);
      btnInProgress.setVisible(true);
      btnNotStarted.setVisible(true);
      labelStatus.setText(statusHash.get(Request));
      labelStatus.setVisible(true);
    }
  }

  @FXML
  private void markInProgress() {
    statusHash.replace(listviewActiveRequests.getSelectionModel().getSelectedItem(), "In Progress");
    labelStatus.setText(
        statusHash.get(listviewActiveRequests.getSelectionModel().getSelectedItem()));
  }

  @FXML
  private void markNotStarted() {
    statusHash.replace(listviewActiveRequests.getSelectionModel().getSelectedItem(), "Not Started");
    labelStatus.setText(
        statusHash.get(listviewActiveRequests.getSelectionModel().getSelectedItem()));
  }

  @FXML
  private void markCompleted() {
    statusHash.replace(listviewActiveRequests.getSelectionModel().getSelectedItem(), "Completed");
    labelStatus.setText(
        statusHash.get(listviewActiveRequests.getSelectionModel().getSelectedItem()));
  }
}
