package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerOrderController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerTrackerController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class FlowerServiceController extends AbstractController {
  @FXML private GridPane flowerServiceTablePane;
  @FXML private Label headerLabel;

  @FXML private JFXButton trackButton;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  private SimpleTableView<Flower> flowerTable;

  public FlowerServiceController() {}

  public void initialize() {
    if (flDatabase.getSizeFlowers() == -1 || flDatabase.getSizeOrders() == -1) {
      flDatabase.dropTables();
      flDatabase.createTables();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    } else if (flDatabase.getSizeFlowers() == 0 || flDatabase.getSizeOrders() == 0) {
      flDatabase.removeAllFlowers();
      flDatabase.removeAllOrders();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    }

    // Set up icons
    headerLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE));
    trackButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRUCK));
    orderButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_RIGHT));

    // Set up table
    flowerTable = new SimpleTableView<>(new Flower("", "", 0, 0), 80.0);
    flowerServiceTablePane.getChildren().add(flowerTable);

    // Listen for tab switch events to update flower list
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          updateTable();
        });
  }

  @FXML
  public void updateTable() {
    try {
      flowerTable.clear();
      flowerTable.add(flDatabase.flowerOl());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleInfoDialog(
          dialogPane,
          "Database Error",
          "Failed to pull flower list from database. Please try again later.");
    }
  }

  @FXML
  public void placeOrder() {
    DialogUtil.complexDialog(
        dialogPane,
        "Place Order",
        "views/FlowerOrderDialog.fxml",
        false,
        null,
        new FlowerOrderController());
  }

  @FXML
  public void trackOrder() {
    DialogUtil.complexDialog(
        dialogPane,
        "Track Order",
        "views/FlowerTrackerDialog.fxml",
        false,
        null,
        new FlowerTrackerController());
  }
}
