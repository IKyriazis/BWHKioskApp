package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerOrderController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerTrackerController;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javax.swing.*;

public class FlowerServiceController extends AbstractController {
  @FXML private TreeTableView<Flower> flowerTable;
  @FXML private Label headerLabel;

  @FXML private JFXButton trackButton;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  public FlowerServiceController() {}

  public void initialize() throws Exception {
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

    // Set up tables
    JFXTreeTableColumn<Flower, String> column1 = new JFXTreeTableColumn<>("Type");
    column1.setCellValueFactory(param -> param.getValue().getValue().typeFlowerProperty());

    JFXTreeTableColumn<Flower, String> column2 = new JFXTreeTableColumn<>("Color");
    column2.setCellValueFactory(param -> param.getValue().getValue().colorProperty());

    JFXTreeTableColumn<Flower, Integer> column3 = new JFXTreeTableColumn<>("Quantity");
    column3.setCellValueFactory(param -> param.getValue().getValue().qtyProperty().asObject());

    JFXTreeTableColumn<Flower, Double> column4 = new JFXTreeTableColumn<>("Unit Price");
    column4.setCellValueFactory(param -> param.getValue().getValue().pricePerProperty().asObject());

    // Add the columns to the table
    //noinspection unchecked
    flowerTable.getColumns().addAll(column1, column2, column3, column4);

    // Setup column sizing
    flowerTable.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
    flowerTable
        .getColumns()
        .forEach(
            column -> {
              column.setMinWidth(80.0);
              column.setReorderable(false);
            });

    // Null root flower node
    TreeItem<Flower> rootFlower = new TreeItem<>(new Flower("", "", 0, 0.0));
    flowerTable.setRoot(rootFlower);
    flowerTable.setShowRoot(false);

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
      TreeItem<Flower> rootItem = flowerTable.getRoot();
      rootItem.getChildren().clear();
      flDatabase.flowerOl().forEach(flower -> rootItem.getChildren().add(new TreeItem<>(flower)));
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
