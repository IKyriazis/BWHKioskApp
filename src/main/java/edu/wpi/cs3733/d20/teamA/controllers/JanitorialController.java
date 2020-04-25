package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.database.JanitorService;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import java.io.IOException;
import java.sql.*;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class JanitorialController extends AbstractController {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";

  @FXML private JFXButton btnAddRequest;
  @FXML private JFXButton btnRemoveRequest;
  @FXML private JFXButton btnChangeStatus;

  @FXML private JFXComboBox<String> comboboxNextStatus;

  @FXML private JFXTextField textfieldCurrentStatus;

  @FXML private JFXTreeTableView<JanitorService> tblServiceView;

  ObservableList priorityItems = FXCollections.observableArrayList();
  ObservableList activeItems = FXCollections.observableArrayList();

  public JanitorialController() {}

  public void initialize() throws SQLException, IOException, CsvException {
    refreshActiveRequests();
    priorityItems.clear();
    String a = "Not Started";
    String b = "In Progress";
    String c = "Finished";
    priorityItems.addAll(a, b, c);
    comboboxNextStatus.getItems().addAll(priorityItems);

    // Set up autofill for nodes
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));

    janitorDatabase.readFromCSV();
  }

  /**
   * Adds a service request to the database
   *
   * @throws SQLException
   */
  @FXML
  private void addServiceRequest() throws SQLException {

    refreshActiveRequests();
  }

  /**
   * Deletes a request from the database
   *
   * @throws SQLException
   */
  @FXML
  private void removeServiceRequest() throws SQLException {

    refreshActiveRequests();
  }

  /**
   * Updates the list to show only active requests. Is called when combo box is clicked.
   *
   * @throws SQLException
   */
  @FXML
  private void refreshActiveRequests() throws SQLException {}
}
