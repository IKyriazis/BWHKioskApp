package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.LaundryEditController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Laundry;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
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

  @FXML private JFXComboBox<String> cleanerComboBox;
  @FXML private JFXComboBox<String> progressComboBox;
  @FXML private JFXComboBox<Node> roomList;

  @FXML private GridPane orderTablePane;
  @FXML private StackPane dialogStackPane;
  @FXML private AnchorPane laundryPane;

  private SimpleTableView<Laundry> tblLaundryView;

  public void initialize() {
    lDB.dropTables();
    lDB.createTables();
    lDB.removeAll();

    lDB.addLaundry("admin", "Emergency Department");
    lDB.addLaundry("admin", "Admitting");

    serviceLabel.setGraphic(new MaterialIconView(MaterialIcon.LOCAL_LAUNDRY_SERVICE));
    requestTableLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LIST));

    addRequestButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE));
    removeRequestButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_CIRCLE));

    laundryPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    tblLaundryView =
        new SimpleTableView<>(
            new Laundry(0, "", "", "", "", new Timestamp(System.currentTimeMillis())), 80.0);
    orderTablePane.getChildren().add(tblLaundryView);

    update();

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
  }

  @FXML
  private void addRequest() {
    Node node = roomList.getSelectionModel().getSelectedItem();
    String loc = "";
    if (node != null) {
      loc = node.getLongName();
      if (!lDB.addLaundry("admin", loc)) {
        DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Cannot add request");
      }
    } else if (node == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a location");
    }
    roomList.getSelectionModel().clearSelection();
    update();
  }

  @FXML
  private void removeRequest() {
    Laundry l = tblLaundryView.getSelected();
    if (l != null) {
      if (!lDB.deleteLaundry(l.getRequestNum())) {
        DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Cannot remove request");
      }
    } else if (l == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a request");
    }
    update();
  }

  @FXML
  private void updateCleaner() {
    Laundry l = tblLaundryView.getSelected();
    if (l != null) {
      LaundryEditController controller = new LaundryEditController(l);
      DialogUtil.complexDialog(
          dialogStackPane,
          "Update Request",
          "views/LaundryEdit.fxml",
          false,
          event -> update(),
          controller);
    } else if (l == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a request");
    }
    update();
  }

  @FXML
  private void seeCompleted() {
    DialogUtil.complexDialog(
        dialogStackPane, "Completed Requests", "views/LaundryCompleted.fxml", true, null, null);
  }

  public void update() {
    try {
      tblLaundryView.clear();

      tblLaundryView.add(lDB.laundryOLNotComplete());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update laundry request table");
    }
  }
}
