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

public class JanitorialController {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase gDB;
  JanitorDatabase jDB;

  @FXML private JFXComboBox<String> comboboxActiveRequests;
  @FXML private JFXButton btnClearRequest;
  @FXML private JFXButton btnSubmitRequest;
  @FXML private JFXTextField textfieldLocation;
  @FXML private JFXTextField textfieldPriority;
  @FXML private Label labelClearRequest;
  @FXML private Label labelSubmitRequest;
  @FXML private JFXListView<String> listviewActiveRequests;

  ObservableList activeItems = FXCollections.observableArrayList();
  Hashtable<String, Integer> activeRequestHash = new Hashtable<>();

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
  }

  /**
   * Adds a service request to the database
   *
   * @throws SQLException
   */
  @FXML
  private void addServiceRequest() throws SQLException {
    if ((!textfieldLocation.getText().equals("")) && !textfieldPriority.getText().equals("")) {
      jDB.addRequest(textfieldLocation.getText(), textfieldPriority.getText());
      textfieldPriority.clear();
      textfieldLocation.clear();
      labelSubmitRequest.setText("Request Submitted Successfully");
    } else if (!textfieldPriority.getText().equals("") && textfieldLocation.getText().equals("")) {
      labelSubmitRequest.setText("Please enter a location");
    } else if (textfieldPriority.getText().equals("") && !textfieldLocation.getText().equals("")) {
      labelSubmitRequest.setText("Please enter a priority");
    } else if (textfieldPriority.getText().equals("") && textfieldLocation.getText().equals("")) {
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
    for (int i = 1; i < size + 1; i++) {
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
}
