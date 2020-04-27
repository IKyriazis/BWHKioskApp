package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.flowerTableItems.Flower;
import edu.wpi.cs3733.d20.teamA.database.flowerTableItems.FlowerEmployee;
import edu.wpi.cs3733.d20.teamA.database.flowerTableItems.Order;
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
  @FXML private GridPane employeeTablePane;

  @FXML private StackPane dialogStackPane;

  @FXML private Label flowerTblLbl;
  @FXML private Label orderTblLbl;

  @FXML private JFXButton addFlowerButton;
  @FXML private JFXButton editFlowerButton;
  @FXML private JFXButton deleteFlowerButton;

  @FXML private AnchorPane flowerPane;

  @FXML private Label lblEmployees;

  private Order lastOrder;

  private SimpleTableView<Flower> tblFlowerView;
  private SimpleTableView<Order> tblOrderView;
  private SimpleTableView<FlowerEmployee> tblEmployeeView;

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
    lblEmployees.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.USERS));

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
    tblFlowerView = new SimpleTableView<>(new Flower("", "", 0, 0, 0), 40.0);
    flowerTablePane.getChildren().add(tblFlowerView);

    tblOrderView = new SimpleTableView<>(new Order(0, 0, "", 0, "", "", "", "", ""), 40.0);
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
                      "views/flower/ViewDetailOrder.fxml",
                      false,
                      event2 -> update(),
                      showController);
                }
              });
          return row;
        });

    tblEmployeeView = new SimpleTableView<>(new FlowerEmployee("", ""), 30.0);
    employeeTablePane.getChildren().addAll(tblEmployeeView);

    // Populate tables
    update();
  }

  public void addFlower() {
    DialogUtil.complexDialog(
        dialogStackPane,
        "Add Flower",
        "views/flower/AddFlowerPopup.fxml",
        false,
        event -> update(),
        new FlowerEditController());
  }

  public void addEmployee() {
    DialogUtil.complexDialog(
        dialogStackPane,
        "Add Employee",
        "views/flower/AddFlowerEmployee.fxml",
        false,
        event -> update(),
        new EmployeeDialogController());
  }

  private boolean hasDependentOrder(Flower flower) {
    boolean constrained = false;
    try {
      for (Order order : flDatabase.orderOl()) {
        if (order.getFlowerString().contains(flower.getFlowerID() + "/")) {
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
          "views/flower/AddFlowerPopup.fxml",
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

  public void deleteEmployee() {
    FlowerEmployee e = tblEmployeeView.getSelected();
    if (e != null) {
      String fName = e.getFirstName();
      String lName = e.getLastName();

      /*try {
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
      }*/
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Employee Selected",
          "Please select an employee by clicking a row in the table");
    }
  }

  public void update() {
    try {
      tblFlowerView.clear();
      tblOrderView.clear();
      tblEmployeeView.clear();

      tblFlowerView.add(flDatabase.flowerOl());
      tblOrderView.add(flDatabase.orderOl());
      tblEmployeeView.add(flDatabase.employeeOl());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }
}
