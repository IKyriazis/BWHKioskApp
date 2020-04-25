package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerEditController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.Order;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class FlowerAdminController extends AbstractController {
  @FXML private GridPane flowerTablePane;
  @FXML private GridPane orderTablePane;

  @FXML private JFXTextField txtPrev;
  @FXML private JFXComboBox<String> txtNext;

  @FXML private StackPane dialogStackPane;

  @FXML private Label flowerTblLbl;
  @FXML private Label orderTblLbl;

  @FXML private JFXButton addFlowerButton;
  @FXML private JFXButton editFlowerButton;
  @FXML private JFXButton deleteFlowerButton;
  @FXML private JFXButton changeProgressButton;

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
    changeProgressButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCHANGE));

    // Add tab switch update listener
    flowerPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    // Set up tables
    tblFlowerView = new SimpleTableView<>(new Flower("", "", 0, 0), 80.0);
    flowerTablePane.getChildren().add(tblFlowerView);

    tblOrderView = new SimpleTableView<>(new Order(0, 0, "", "", 0, "", ""), 40.0);
    orderTablePane.getChildren().addAll(tblOrderView);

    // Populate tables
    update();
    // Hook up txtPrev to show status of selected order
    /*tblOrderView
    .getSelectionModel()
    .selectedItemProperty()
    .addListener(
        (observable, oldValue, newValue) -> {
          try {
            Order o = newValue.getValue();
            if (o != null) txtPrev.setText(o.getStatus());
            else txtPrev.setText("");
          } catch (Exception e) {
            // do nothing, this keeps throwing an exception when updating table
          }
        });*/

    // Setup status change stuff
    txtNext.getItems().addAll("Order Sent", "Order Received", "Flowers Sent", "Flowers Delivered");
    txtNext.getSelectionModel().select(0);
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
    try {
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
    }

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
      tblFlowerView.add(flDatabase.flowerOl());
      tblOrderView.add(flDatabase.orderOl());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }

  public void changeProgress() {
    if (lastOrder != null) {
      String s = txtNext.getSelectionModel().getSelectedItem();
      super.flDatabase.changeOrderStatus(lastOrder.getOrderNumber(), s);
      lastOrder = null;
      update();
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Order Selected",
          "Please select an order by clicking a row in the table");
    }
  }

  public void updateStatus(MouseEvent mouseEvent) {
    Order selected = tblOrderView.getSelected();
    if (selected != null) {
      // track the last selected order
      lastOrder = selected;

      int i = statusStringToValue(lastOrder.getStatus() + 1); // next status

      if (i <= 3) txtNext.getSelectionModel().select(i);
    } else {
      txtNext.getSelectionModel().select(0);
    }
  }

  private int statusStringToValue(String status) {
    switch (status) {
      case "Order Sent":
        return 0;
      case "Order Received":
        return 1;
      case "Flowers Sent":
        return 2;
      case "Flowers Delivered":
        return 3;
      default:
        return 999;
    }
  }
}
