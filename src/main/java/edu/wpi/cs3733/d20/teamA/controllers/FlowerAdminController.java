package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerEditController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.ShowOrderController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.Order;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class FlowerAdminController extends AbstractController {
  @FXML private GridPane flowerTablePane;
  @FXML private GridPane orderTablePane;

  @FXML private StackPane dialogStackPane;

  @FXML private Label flowerTblLbl;
  @FXML private Label orderTblLbl;

  @FXML private JFXButton addFlowerButton;
  @FXML private JFXButton editFlowerButton;
  @FXML private JFXButton deleteFlowerButton;

  @FXML private AnchorPane flowerPane;

  private Order lastOrder;

  private SimpleTableView<Flower> tblFlowerView;
  private SimpleTableView<Order> tblOrderView;

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
    flowerTblLbl.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE));
    orderTblLbl.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BARCODE));

    // Setup button icons
    addFlowerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE));
    editFlowerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE));
    deleteFlowerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE));

    // Add tab switch update listener
    flowerPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    // Set up tables
    tblFlowerView = new SimpleTableView<>(new Flower("", "", 0, 0, 0), 80.0);
    flowerTablePane.getChildren().add(tblFlowerView);

    tblOrderView = new SimpleTableView<>(new Order(0, 0, "", 0, "", "", ""), 40.0);
    orderTablePane.getChildren().addAll(tblOrderView);

    tblOrderView.setRowFactory(
        tv -> {
          TreeTableRow<Order> row = new TreeTableRow<>();
          row.setOnMouseClicked(
              event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                  Order rowData = row.getItem();
                  ShowOrderController showController = new ShowOrderController();
                  showController.setOrder(row.getTreeItem().getValue());
                  DialogUtil.complexDialog(
                      dialogStackPane,
                      "Order",
                      "views/ViewDetailOrder.fxml",
                      false,
                      event2 -> update(),
                      showController);
                }
              });
          return row;
        });

    // Populate tables
    update();
  }

  public void addFlower() {
    DialogUtil.complexDialog(
        dialogStackPane,
        "Add Flower",
        "views/AddFlowerPopup.fxml",
        false,
        event -> update(),
        new FlowerEditController());
  }

  private boolean hasDependentOrder(Flower flower) {
    boolean constrained = false;
    /*try {
      for (Order order : flDatabase.orderOl()) {
        if ((order.getFlowerType().equals(flower.getTypeFlower()))
            && (order.getFlowerColor().equals(flower.getColor()))) {
          constrained = true;
        }
      }
    } catch (Exception e) {
      DialogUtil.simpleErrorDialog(
          dialogStackPane,
          "Database Failure",
          "Failed to verify that there were no outstanding orders for flower: "
              + flower.toString());
    }*/
    return constrained;
  }

  public void editFlower() {
    Flower flower = tblFlowerView.getSelected();
    if (flower != null) {
      // Figure out whether any outstanding orders depend on this flower type, in which case we
      // can't change the name / type
      boolean constrained = hasDependentOrder(flower);

      FlowerEditController controller = new FlowerEditController(flower, constrained);
      DialogUtil.complexDialog(
          dialogStackPane,
          "Edit Flower",
          "views/AddFlowerPopup.fxml",
          false,
          event -> update(),
          controller);
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Flower Selected",
          "Please select a flower by clicking a row in the table");
    }
  }

  public void deleteFlower() {
    Flower f = tblFlowerView.getSelected();
    if (f != null) {
      if (!hasDependentOrder(f)) {
        String name = f.getTypeFlower();
        String color = f.getColor();

        try {
          super.flDatabase.deleteFlower(name, color);
        } catch (Exception e) {
          e.printStackTrace();
          DialogUtil.simpleErrorDialog(
              dialogStackPane, "Error Deleting Flower", "Could not delete flower: " + f);
        }

        update();
      } else {
        DialogUtil.simpleInfoDialog(
            dialogStackPane,
            "Cannot Delete Flower",
            "This flower has an active order depending on it and thus cannot be removed");
      }
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Flower Selected",
          "Please select a flower by clicking a row in the table");
    }
  }

  public void update() {
    try {
      tblFlowerView.clear();
      tblOrderView.clear();

      tblFlowerView.add(flDatabase.flowerOl());
      tblOrderView.add(flDatabase.orderOl());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }
}
