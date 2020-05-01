package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.JanitorEditController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Employee;
import edu.wpi.cs3733.d20.teamA.database.JanitorService;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.io.IOException;
import java.sql.*;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class JanitorialController extends AbstractController {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";

  @FXML private JFXButton btnAddRequest;
  @FXML private JFXButton btnRemoveRequest;
  @FXML private JFXButton btnChangeStatus;

  @FXML private JFXComboBox<String> comboboxNextStatus;
  @FXML private JFXComboBox comboboxJanitorName;

  @FXML private GridPane gridTableView;

  @FXML private StackPane popupStackPane;

  private SimpleTableView tblServiceView;

  ObservableList statusItems = FXCollections.observableArrayList();
  ObservableList activeItems = FXCollections.observableArrayList();

  public JanitorialController() {}

  public void initialize() throws SQLException, IOException, CsvException {
    // initialize the database
    if (serviceDatabase.getSize() == -1) {
      serviceDatabase.dropTables();
      serviceDatabase.createTables();
    } else if (serviceDatabase.getSize() == 0) {
      // janitorDatabase.removeAll(); TODO ADD THIS METHOD
    }

    // Add the status items to the combobox
    statusItems.clear();
    String a = "Request Made";
    String b = "In Progress";
    String c = "Completed";
    statusItems.addAll(a, b, c);
    comboboxNextStatus.getItems().addAll(statusItems);

    ObservableList<Employee> allEmployeeList = eDB.getObservableList();
    allEmployeeList.sort(Comparator.comparing(Employee::toString));
    activeItems.addAll("Unassigned");
    comboboxJanitorName.getItems().addAll(allEmployeeList);
    comboboxJanitorName.getItems().add(activeItems);

    comboboxJanitorName.setOnMouseClicked(
        event -> {
          allEmployeeList.clear();

          allEmployeeList.addAll(eDB.getObservableList());
          allEmployeeList.sort(Comparator.comparing(Employee::toString));

          comboboxJanitorName.setItems(allEmployeeList);
          comboboxJanitorName.getItems().add(activeItems);
        });

    // Set up autofill for nodes
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));

    btnAddRequest.setGraphic(new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE));
    btnRemoveRequest.setGraphic(new FontIcon(FontAwesomeRegular.WINDOW_CLOSE));
    btnChangeStatus.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_CIRCLE));

    tblServiceView = new SimpleTableView<>(new JanitorService("", "", "", "", "", ""), 80.0);

    gridTableView.getChildren().add(tblServiceView);

    refreshActiveRequests();
  }

  /**
   * Adds a service request to the database
   *
   * @throws SQLException
   */
  @FXML
  private void addServiceRequest() throws SQLException {
    DialogUtil.complexDialog(
        popupStackPane,
        "Make a Janitorial Request",
        "views/AddJanitorPopup.fxml",
        false,
        event -> refreshActiveRequests(),
        new JanitorEditController());
    refreshActiveRequests();
  }

  /**
   * Deletes a request from the database
   *
   * @throws SQLException
   */
  @FXML
  private void removeServiceRequest() throws SQLException {
    if (tblServiceView.getSelectionModel().getSelectedItem() == null) {
      DialogUtil.simpleErrorDialog(popupStackPane, "Error", "Please select a request to remove");

    } else {
      JanitorService j =
          (((TreeItem<JanitorService>) (tblServiceView.getSelectionModel().getSelectedItem()))
              .getValue());
      serviceDatabase.deleteServReq(j.getIndex());
      refreshActiveRequests();
    }
  }

  // Update the status - this request uses unique status values, which will be put into the
  // additional field to avoid causing errors
  @FXML
  private void updateRequest() throws SQLException {
    if (tblServiceView.getSelectionModel().getSelectedItem() == null) {
      DialogUtil.simpleErrorDialog(popupStackPane, "Error", "Please select a request to update");
    } else {
      JanitorService j =
          (((TreeItem<JanitorService>) (tblServiceView.getSelectionModel().getSelectedItem()))
              .getValue());
      if (comboboxNextStatus.getValue() == null) {
        DialogUtil.simpleErrorDialog(
            popupStackPane, "Error", "Please select the status of the request");
      } else if (comboboxJanitorName.getValue() == null) {
        DialogUtil.simpleErrorDialog(
            popupStackPane, "Error", "Please select an employee to assign");
      } else {
        if (comboboxJanitorName.getValue().toString().equals("")) {
          serviceDatabase.setAssignedEmployee(j.getIndex(), j.getEmployeeName());
          serviceDatabase.setStatus(j.getIndex(), comboboxNextStatus.getValue());
        } else {
          serviceDatabase.setStatus(j.getIndex(), comboboxNextStatus.getValue());
        }
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
  private void refreshActiveRequests() {
    try {
      tblServiceView.clear();

      tblServiceView.add(serviceDatabase.getObservableListService(ServiceType.JANITOR));
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          popupStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }
}
