package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.io.IOException;
import java.sql.*;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class JanitorialController extends AbstractController {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";

  @FXML private JFXButton btnClearRequest;
  @FXML private JFXButton btnSubmitRequest;
  @FXML private JFXButton btnNotStarted;
  @FXML private JFXButton btnCompleted;
  @FXML private JFXButton btnInProgress;

  @FXML private JFXComboBox<Node> roomList;

  // @FXML private JFXTextField textfieldLocation;
  @FXML private JFXTextField textfieldPriority;

  @FXML private JFXTextField textfieldEmployeeName;

  @FXML private Label labelClearRequest;
  @FXML private Label labelSubmitRequest;
  @FXML private Label labelStatus;

  @FXML private JFXListView<String> listviewActiveRequests;
  @FXML private JFXComboBox<String> comboboxPriority;

  ObservableList priorityItems = FXCollections.observableArrayList();
  ObservableList activeItems = FXCollections.observableArrayList();
  Hashtable<String, Integer> activeRequestHash = new Hashtable<>();
  Hashtable<String, String> statusHash = new Hashtable<>();

  public JanitorialController() {}

  public void initialize() throws SQLException, IOException, CsvException {
    refreshActiveRequests();
    statusHash.clear();
    priorityItems.clear();
    String a = "High";
    String b = "Medium";
    String c = "Low";
    priorityItems.addAll(a, b, c);
    comboboxPriority.getItems().addAll(priorityItems);

    // Set up autofill for nodes
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));

    roomList.setItems(allNodeList);

    roomList
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(roomList, roomList, allNodeList));

    janitorDatabase.readFromCSV();
  }

  /**
   * Adds a service request to the database
   *
   * @throws SQLException
   */
  @FXML
  private void addServiceRequest() throws SQLException {
    Node node = roomList.getSelectionModel().getSelectedItem();
    String loc = "";
    if (node != null && !comboboxPriority.getValue().equals("")) {
      loc = node.getNodeID();
      janitorDatabase.addRequest(loc, comboboxPriority.getValue());
      statusHash.put(loc, "Not Started");
      comboboxPriority.getSelectionModel().clearSelection();
      labelSubmitRequest.setText("Request Submitted Successfully");
    } else if (comboboxPriority.getValue() != null && node == null) {
      labelSubmitRequest.setText("Please enter a location");
    } else if (comboboxPriority.getValue() == null && node != null) {
      labelSubmitRequest.setText("Please enter a priority");
    } else if (comboboxPriority.getValue() == null && node == null) {
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
    if (listviewActiveRequests.getSelectionModel().getSelectedIndex() == -1) {
      return;
    }

    String request = listviewActiveRequests.getSelectionModel().getSelectedItem();
    if (request != null) {
      if (janitorDatabase.deleteRequest(activeRequestHash.get(request))) {
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
    int size = janitorDatabase.getRequestSize();
    for (int i = 3; i < size + 3; i++) {
      Request = janitorDatabase.getLocation(i);
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
  private void elementSelect() {
    String Request = listviewActiveRequests.getSelectionModel().getSelectedItem();
    if (Request != null) {
      btnCompleted.setVisible(true);
      btnInProgress.setVisible(true);
      btnNotStarted.setVisible(true);
      labelStatus.setText(statusHash.get(Request));
      labelStatus.setVisible(true);
      textfieldEmployeeName.setVisible(true);
    }
  }

  @FXML
  private void markInProgress() {
    if (listviewActiveRequests.getSelectionModel().getSelectedIndex() == -1) {
      return;
    }
    if (textfieldEmployeeName.getText().equals("")) {
      labelStatus.setText("Please enter the employee name below");
    } else if (!textfieldEmployeeName.getText().equals("")) {
      statusHash.replace(
          listviewActiveRequests.getSelectionModel().getSelectedItem(),
          textfieldEmployeeName.getText() + " is currently working");
      labelStatus.setText(
          statusHash.get(listviewActiveRequests.getSelectionModel().getSelectedItem()));
    }
  }

  @FXML
  private void markNotStarted() {
    if (listviewActiveRequests.getSelectionModel().getSelectedIndex() == -1) {
      return;
    }
    if (textfieldEmployeeName.getText().equals("")) {
      labelStatus.setText("Please enter the employee name below");
    } else if (!textfieldEmployeeName.getText().equals("")) {
      statusHash.replace(
          listviewActiveRequests.getSelectionModel().getSelectedItem(), "Not Started");
      labelStatus.setText(
          statusHash.get(listviewActiveRequests.getSelectionModel().getSelectedItem()));
    }
  }

  @FXML
  private void markCompleted() {
    if (listviewActiveRequests.getSelectionModel().getSelectedIndex() == -1) {
      return;
    }
    if (textfieldEmployeeName.getText().equals("")) {
      labelStatus.setText("Please enter the employee name below");
    } else if (!textfieldEmployeeName.getText().equals("")) {
      statusHash.replace(
          listviewActiveRequests.getSelectionModel().getSelectedItem(),
          "Completed by " + textfieldEmployeeName.getText());
      labelStatus.setText(
          statusHash.get(listviewActiveRequests.getSelectionModel().getSelectedItem()));
    }
  }
}
