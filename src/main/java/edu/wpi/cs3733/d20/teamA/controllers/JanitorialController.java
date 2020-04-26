package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import com.opencsv.exceptions.CsvException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.JanitorEditController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.JanitorService;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.io.IOException;
import java.sql.*;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class JanitorialController extends AbstractController {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";

  @FXML private JFXButton btnAddRequest;
  @FXML private JFXButton btnRemoveRequest;
  @FXML private JFXButton btnChangeStatus;

  @FXML private JFXComboBox<String> comboboxNextStatus;

  @FXML private JFXTextField textfieldCurrentStatus;

  @FXML private GridPane gridTableView;

  @FXML private StackPane popupStackPane;

  private SimpleTableView<JanitorService> tblServiceView;

  ObservableList statusItems = FXCollections.observableArrayList();
  ObservableList activeItems = FXCollections.observableArrayList();

  public JanitorialController() {}

  public void initialize() throws SQLException, IOException, CsvException {
    // initialize the database
    if (janitorDatabase.getRequestSize() == -1) {
      janitorDatabase.dropTables();
      janitorDatabase.createTables();
      //      janitorDatabase.readFromCSV();
    } else if (janitorDatabase.getRequestSize() == 0) {
      janitorDatabase.removeAll();
      //      janitorDatabase.readFromCSV();
    }

    // Add the status items to the combobox
    statusItems.clear();
    String a = "Reported";
    String b = "Dispatched";
    String c = "Done";
    statusItems.addAll(a, b, c);
    comboboxNextStatus.getItems().addAll(statusItems);

    // Set up autofill for nodes
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));

    btnAddRequest.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE));
    btnRemoveRequest.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
    btnChangeStatus.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_CIRCLE));

    tblServiceView = new SimpleTableView<>(new JanitorService("", "", "", "", 0, ""), 80.0);
    tblServiceView.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {

      }
    });
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
      janitorDatabase.deleteRequest(j.getIndex());
      refreshActiveRequests();
    }
  }

  @FXML
  private void updateRequest() throws SQLException {
    if (tblServiceView.getSelectionModel().getSelectedItem() == null) {
      DialogUtil.simpleErrorDialog(popupStackPane, "Error", "Please select a request to update");
    } else {
      JanitorService j =
          (((TreeItem<JanitorService>) (tblServiceView.getSelectionModel().getSelectedItem()))
              .getValue());
      janitorDatabase.updateRequest(
          j.getIndex(), j.getLongName(), comboboxNextStatus.getValue(), j.getEmployeeName());
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

      tblServiceView.add(janitorDatabase.janitor01());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          popupStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }
}
