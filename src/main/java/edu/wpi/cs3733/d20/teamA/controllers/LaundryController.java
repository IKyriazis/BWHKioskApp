package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Employee;
import edu.wpi.cs3733.d20.teamA.database.Laundry;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.graph.NodeType;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class LaundryController extends AbstractController {

  @FXML private JFXButton addRequestButton;
  @FXML private JFXButton removeRequestButton;
  @FXML private JFXButton seeCompletedButton;
  @FXML private JFXButton updateCleanerButton;
  @FXML private JFXButton updateProgressButton;

  @FXML private Label serviceLabel;
  @FXML private Label requestTableLabel;

  @FXML private JFXComboBox<Employee> cleanerComboBox;
  @FXML private JFXComboBox<String> progressComboBox;
  @FXML private JFXComboBox<Node> roomList;

  @FXML private GridPane orderTablePane;
  @FXML private StackPane dialogStackPane;
  @FXML private AnchorPane laundryPane;

  private SimpleTableView tblLaundryView;

  public void initialize() {
    primaryDB.createTables();

    serviceLabel.setGraphic(new MaterialIconView(MaterialIcon.LOCAL_LAUNDRY_SERVICE));
    requestTableLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LIST));

    addRequestButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE));
    removeRequestButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_CIRCLE));
    updateCleanerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_UP));
    seeCompletedButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));

    laundryPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    tblLaundryView =
        new SimpleTableView<>(
            new Laundry("", "", "", "", "", new Timestamp(System.currentTimeMillis())), 80.0);
    orderTablePane.getChildren().add(tblLaundryView);

    tblLaundryView.setOnMouseClicked(
        event -> {
          Laundry l = (Laundry) tblLaundryView.getSelected();
          if (l != null) {
            updateCleanerButton.disableProperty().setValue(false);
            removeRequestButton.disableProperty().setValue(false);
            progressComboBox.getSelectionModel().select(l.getProgress());
            if (l.getEmployeeWash() != null) {
              cleanerComboBox.getSelectionModel().select(eDB.findFromUsername(l.getEmployeeWash()));
            } else {
              cleanerComboBox.getSelectionModel().select(null);
            }
          }
        });

    update();

    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(
                    node ->
                        node.getType() != NodeType.HALL
                            && node.getType() != NodeType.STAI
                            && node.getType() != NodeType.ELEV)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));

    roomList.setItems(allNodeList);

    roomList
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(roomList, roomList, allNodeList));

    roomList.setOnMouseClicked(
        event -> {
          allNodeList.clear();

          allNodeList.addAll(
              FXCollections.observableArrayList(
                  Graph.getInstance().getNodes().values().stream()
                      .filter(
                          node ->
                              node.getType() != NodeType.HALL
                                  && node.getType() != NodeType.STAI
                                  && node.getType() != NodeType.ELEV)
                      .collect(Collectors.toList())));
          allNodeList.sort(Comparator.comparing(Node::getLongName));

          roomList.setItems(allNodeList);
        });

    progressComboBox.getItems().addAll("Requested", "Collected", "Washing", "Drying", "Returned");

    ObservableList<Employee> allEmployeeList = eDB.employeeOl();
    allEmployeeList.sort(Comparator.comparing(Employee::toString));

    cleanerComboBox.setItems(allEmployeeList);
    cleanerComboBox.setOnMouseClicked(
        event -> {
          allEmployeeList.clear();

          allEmployeeList.addAll(eDB.employeeOl());
          allEmployeeList.sort(Comparator.comparing(Employee::toString));

          cleanerComboBox.setItems(allEmployeeList);
        });
  }

  @FXML
  private void addRequest() {
    Node node = roomList.getSelectionModel().getSelectedItem();
    String loc = "";
    if (node != null) {
      loc = node.getLongName();
      String l = primaryDB.addServiceReq(ServiceType.LAUNDRY, loc, "", "");
      if (l == null) {
        DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Cannot add request");
      } else {
        DialogUtil.simpleInfoDialog(
            dialogStackPane, "Requested", "Request " + l + " Has Been Added");
      }
    } else if (node == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a location");
    }
    disableAdd();
    roomList.getSelectionModel().clearSelection();
    update();
  }

  @FXML
  private void removeRequest() {
    Laundry l = (Laundry) tblLaundryView.getSelected();
    if (l != null) {
      if (!primaryDB.deleteServReq(l.getRequestNum())) {
        DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Cannot remove request");
      }
    } else if (l == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a request");
    }
    tblLaundryView.getSelectionModel().clearSelection();
    update();
  }

  @FXML
  private void updateCleaner() {
    /*Laundry l = tblLaundryView.getSelected();
    Employee e = cleanerComboBox.getSelectionModel().getSelectedItem();
    if (l != null) {
      if (progressComboBox.getValue() != null) {
        lDB.setProg(l.getRequestNum(), progressComboBox.getValue());
      }
      if (cleanerComboBox.getValue() != null) {
        if (!lDB.setEmpW(l.getRequestNum(), eDB.getUsername(e.getId()))) {
          DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Cannot set Employee");
        }
      }
    } else if (l == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a request");
    }
    progressComboBox.getSelectionModel().clearSelection();
    cleanerComboBox.getSelectionModel().clearSelection();
    tblLaundryView.getSelectionModel().clearSelection();
    update();*/
  }

  @FXML
  private void seeCompleted() {
    DialogUtil.complexDialog(
        dialogStackPane, "Completed Requests", "views/LaundryCompleted.fxml", true, null, null);
  }

  @FXML
  private void disableButtons() {
    updateCleanerButton.disableProperty().setValue(true);
    removeRequestButton.disableProperty().setValue(true);
  }

  @FXML
  private void disableAdd() {
    addRequestButton.disableProperty().setValue(true);
  }

  @FXML
  private void enableAdd() {
    if (roomList.getSelectionModel().getSelectedItem() != null) {
      addRequestButton.disableProperty().setValue(false);
    }
  }

  public void update() {
    try {
      tblLaundryView.clear();

      tblLaundryView.add(primaryDB.observableList(ServiceType.LAUNDRY));
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update laundry request table");
    }
  }
}
