package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.JanitorDatabase;
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

  ObservableList dropdownMenuItems = FXCollections.observableArrayList();
  Hashtable<String, Integer> activeRequestHash = new Hashtable<>();

  public void initialize() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    jDB = new JanitorDatabase(conn);
    gDB.createTables();
    jDB.createTables();
  }

  /**
   * Adds a service request to the database
   *
   * @throws SQLException
   */
  @FXML
  private void addServiceRequest() throws SQLException {

    labelSubmitRequest.setText("Request Submitted Successfully");
    System.out.println(jDB.addRequest(textfieldLocation.getText(), textfieldPriority.getText()));
  }

  /**
   * Deletes a request from the database
   *
   * @throws SQLException
   */
  @FXML
  private void removeServiceRequest() throws SQLException {
    String request = comboboxActiveRequests.getAccessibleText();
    jDB.deleteRequest(activeRequestHash.get(request));
    labelClearRequest.setText("Service Removed Successfully");
  }

  /**
   * Updates the list to show only active requests. Is called when combo box is clicked.
   *
   * @throws SQLException
   */
  @FXML
  private void refreshActiveRequests() throws SQLException {
    dropdownMenuItems.removeAll(dropdownMenuItems);
    activeRequestHash.clear();
    String Request;
    System.out.println(jDB.getLocation(1));
    int size = jDB.getRequestSize();
    for (int i = 1; i < size; i++) {
      Request = jDB.getLocation(i);
      if (Request == null) {
        System.out.println("null");
        continue;
      } else {
        System.out.println("worked");
        dropdownMenuItems.add(Request);
        activeRequestHash.put(Request, i);
        System.out.println(Request);
      }
    }
    comboboxActiveRequests.getItems().addAll(dropdownMenuItems);
  }
}
