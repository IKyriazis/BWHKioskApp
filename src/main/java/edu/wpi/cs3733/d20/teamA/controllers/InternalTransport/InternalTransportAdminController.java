package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.InternalTransportRequest;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class InternalTransportAdminController extends AbstractController {

  @FXML private GridPane orderTablePane;

  @FXML private JFXTextField txtPrev;
  @FXML private JFXComboBox<String> txtNext;

  @FXML private StackPane dialogStackPane;

  @FXML private Label orderTblLbl;
  @FXML private JFXButton changeProgressButton;

  @FXML private AnchorPane flowerPane;

  private InternalTransportRequest lastOrder;

  private SimpleTableView<InternalTransportRequest> tblOrderView;

  public void initialize() {
    if (flDatabase.getSizeFlowers() == -1 || flDatabase.getSizeFlowers() == -1) {
      flDatabase.dropTables();
      flDatabase.createTables();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    } else if (flDatabase.getSizeFlowers() == 0 || flDatabase.getSizeOrders() == 0) {
      flDatabase.removeAllOrders();
      flDatabase.removeAllFlowers();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    }

    // Setup label icons
    orderTblLbl.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BARCODE));

    // Setup button icons
    changeProgressButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCHANGE));

    // Add tab switch update listener
    flowerPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    // Set up tables

    tblOrderView = new SimpleTableView<>(new InternalTransportRequest(0, "", "", "", "", ""), 40.0);
    orderTablePane.getChildren().addAll(tblOrderView);

    // Populate tables
    update();

    // Hook up txtPrev to show status of selected order
    tblOrderView.setOnMouseClicked(this::updateStatus);

    // Setup status change stuff
    txtNext.getItems().addAll("Reported", "Dispatched", "Done");
    txtNext.getSelectionModel().select(0);
  }

  public void update() {
    try {
      tblOrderView.clear();

      tblOrderView.add(itDatabase.requestOl());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }

  public void updateStatus(MouseEvent mouseEvent) {
    InternalTransportRequest selected = tblOrderView.getSelected();
    if (selected != null) {
      // track the last selected order
      lastOrder = selected;

      // Update status text display
      txtPrev.setText(lastOrder.getProgress());

      // Update combobox selection (automagically select next status)
      int i = statusStringToValue(lastOrder.getProgress()) + 1; // next status
      if (i <= 2) txtNext.getSelectionModel().select(i);
    } else {
      txtPrev.setText("");
      txtNext.getSelectionModel().select(0);
    }
  }

  private int statusStringToValue(String status) {
    switch (status) {
      case "Reported":
        return 0;
      case "Dispatched":
        return 1;
      case "Done":
        return 2;
      default:
        return 999;
    }
  }
}
