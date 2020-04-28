package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.EquipRequest;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class EquipReqController extends AbstractController {

  @FXML JFXButton addReqbttn;
  @FXML JFXButton delReqbttn;
  @FXML JFXComboBox<String> priCombo;
  @FXML JFXComboBox<Node> locCombo;
  @FXML JFXTextField itemField;
  @FXML JFXTextField qtyField;
  @FXML GridPane gridReq;
  @FXML StackPane reqStackPane;

  private EquipRequest selected;
  private SimpleTableView<EquipRequest> tblEquipView;

  public void initialize() {

    if (eDB.getSizeReq() == -1) {
      eDB.dropTables();
      eDB.createTables();
    } else if (eDB.getSizeReq() == 0) {
      eDB.removeAllReqs();
    }

    qtyField.setTextFormatter(InputFormatUtil.getIntFilter());

    // initialize choices for combo
    priCombo.getItems().addAll("High", "Medium", "Low");
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));
    locCombo.setItems(allNodeList);

    tblEquipView =
        new SimpleTableView<>(
            new EquipRequest("", "", 0, "", "", new Timestamp(System.currentTimeMillis()), ""),
            80.0);
    gridReq.getChildren().add(tblEquipView);

    tblEquipView.setOnMouseClicked(
        event -> {
          delReqbttn.disableProperty().setValue(false);
        });

    updateTable();
  }

  public void updateTable() {
    try {

      tblEquipView.clear();
      tblEquipView.add(eDB.ReqOl());

    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(reqStackPane, "Error", "Failed to update table");
    }
  }

  public void disableBttns(MouseEvent mouseEvent) {
    delReqbttn.disableProperty().set(true);
  }

  public void addReq(ActionEvent actionEvent) {
    String priority = priCombo.getSelectionModel().getSelectedItem();
    Node location = locCombo.getSelectionModel().getSelectedItem();
    String item = itemField.getText();
    int qty = Integer.parseInt(qtyField.getText());
    boolean a = eDB.addReq(item, qty, location.getNodeID(), priority);
    priCombo.getSelectionModel().clearSelection();
    locCombo.getSelectionModel().clearSelection();
    itemField.clear();
    qtyField.clear();
    updateTable();
  }

  public void delReq(ActionEvent actionEvent) {

    selected = tblEquipView.getSelected();
    if (selected != null) {
      boolean b = eDB.deleteReq(selected.getUsername(), selected.getTime());
      updateTable();
    }
  }

  public void checkSubmittableCombo(ActionEvent actionEvent) {
    checkAdd();
  }

  public void checkSubmittableField(KeyEvent keyEvent) {
    checkAdd();
  }

  public void checkAdd() {
    if (locCombo.getSelectionModel().getSelectedItem() != null
        && priCombo.getSelectionModel().getSelectedItem() != null
        && !itemField.getText().isEmpty()
        && !qtyField.getText().isEmpty()) {
      addReqbttn.disableProperty().setValue(false);
    } else {
      addReqbttn.disableProperty().setValue(true);
    }
  }
}
