package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Laundry;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
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
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

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
    serviceDatabase.createTables();

    serviceLabel.setGraphic(new FontIcon(FontAwesomeSolid.TINT));
    requestTableLabel.setGraphic(new FontIcon(FontAwesomeRegular.LIST_ALT));

    addRequestButton.setGraphic(new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
    removeRequestButton.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_CIRCLE));
    updateCleanerButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));
    seeCompletedButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

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

    progressComboBox.getItems().addAll("Request Made", "In Progress", "Completed");

    ObservableList<Employee> allEmployeeList = eDB.getObservableList();
    allEmployeeList.sort(Comparator.comparing(Employee::toString));

    cleanerComboBox.setItems(allEmployeeList);
    cleanerComboBox.setOnMouseClicked(
        event -> {
          allEmployeeList.clear();

          allEmployeeList.addAll(eDB.getObservableList());
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
      String l = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, loc, "", "");
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
      if (!serviceDatabase.deleteServReq(l.getRequestNum())) {
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
    Laundry l = (Laundry) (tblLaundryView.getSelected());
    Employee e = cleanerComboBox.getSelectionModel().getSelectedItem();
    if (l != null) {
      if (progressComboBox.getValue() != null) {
        serviceDatabase.editStatus(
            l.getRequestNum(), progressComboBox.getSelectionModel().getSelectedItem());
      }
      serviceDatabase.setAssignedEmployee(l.getRequestNum(), e.getUsername());
    } else if (l == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a request");
    }
    progressComboBox.getSelectionModel().clearSelection();
    cleanerComboBox.getSelectionModel().clearSelection();
    tblLaundryView.getSelectionModel().clearSelection();
    update();
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

      tblLaundryView.add(serviceDatabase.getObservableListService(ServiceType.LAUNDRY));
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update laundry request table");
    }
  }
}
