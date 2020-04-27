package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.database.flowerTableItems.FlowerEmployee;
import edu.wpi.cs3733.d20.teamA.database.flowerTableItems.Order;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

public class ShowOrderController extends AbstractController implements IDialogController {

  @FXML private JFXTreeTableView<TempFlower> tblFlowers;
  @FXML private JFXTextField txtLocation;
  @FXML private JFXTextField txtMessage;
  @FXML private JFXTextField txtTotalCost;

  @FXML private JFXTextField txtPrevStat;
  @FXML private JFXComboBox<String> txtNextStat;

  @FXML private JFXTextField txtPrevEmp;
  @FXML private JFXComboBox<FlowerEmployee> txtNextEmp;

  @FXML private JFXButton changeProgressButton;
  @FXML private JFXButton changeEmployeeButton;

  private Order myOrder;

  private JFXDialog dialog;

  public void initialize() {
    String s = myOrder.getFlowerString();

    // Set up table
    TreeItem rootItem = new TreeItem<>(new TempFlower("", 0));

    // Setup root item in table
    tblFlowers.setRoot(rootItem);
    tblFlowers.setShowRoot(false);
    tblFlowers.setEditable(false);

    JFXTreeTableColumn<TempFlower, String> columnName = new JFXTreeTableColumn<>("Flower");
    columnName.setCellValueFactory(param -> param.getValue().getValue().type);
    columnName.setMinWidth(250);
    columnName.setResizable(false);

    JFXTreeTableColumn<TempFlower, Integer> columnNum = new JFXTreeTableColumn<>("Number in Order");
    columnNum.setCellValueFactory(param -> param.getValue().getValue().num.asObject());
    columnNum.setMinWidth(250);
    columnNum.setResizable(false);

    tblFlowers.getColumns().addAll(columnName, columnNum);
    while (s.indexOf('|') != -1) {
      int flNum = Integer.parseInt(s.substring(0, s.indexOf("/")));
      int num = Integer.parseInt(s.substring(s.indexOf("/") + 1, s.indexOf("|")));

      String flType = flDatabase.getFlowerTypeID(flNum);
      String flColor = flDatabase.getFlowerColorID(flNum);

      TempFlower flow = new TempFlower(flColor + "   " + flType, flNum);
      tblFlowers.getRoot().getChildren().add(new TreeItem<>(flow));
      s = s.substring(s.indexOf("|") + 1);
    }

    txtTotalCost.setText(String.format("$%.2f", myOrder.getPrice()));
    txtMessage.setText(myOrder.getMessage());
    txtLocation.setText(graphDatabase.getLongName(myOrder.getLocation()));

    changeProgressButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCHANGE));
    changeEmployeeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ID_CARD));

    txtPrevStat.setText(myOrder.getStatus());
    // Setup status change stuff
    txtNextStat
        .getItems()
        .addAll("Order Sent", "Order Received", "Flowers Sent", "Flowers Delivered");
    txtNextStat
        .getSelectionModel()
        .select(Math.min(statusStringToValue(myOrder.getStatus()) + 1, 3));

    if (myOrder.employeeAssigned()) {
      FlowerEmployee e = myOrder.getEmployee();
      txtPrevEmp.setText(e.getFirstName() + " " + e.getLastName());
    } else {
      txtPrevEmp.setText("No employee assigned");
    }
    txtNextEmp.getItems().addAll(flDatabase.employeeOl());
  }

  public void setOrder(Order value) {
    myOrder = value;
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }

  public void changeProgress() {
    String s = txtNextStat.getSelectionModel().getSelectedItem();
    super.flDatabase.changeOrderStatus(myOrder.getOrderNumber(), s);
    // Set fields to reflect this
    txtPrevStat.setText(s);
    txtNextStat
        .getSelectionModel()
        .select(
            Math.min(
                statusStringToValue(flDatabase.getOrderStatus(myOrder.getOrderNumber())) + 1,
                3)); // Make sure to grab status from database because the locally stored order does
    // not reflect that
  }

  public void changeEmployee() {
    FlowerEmployee e = txtNextEmp.getSelectionModel().getSelectedItem();

    if (e != null) {
      super.flDatabase.assignEmployee(myOrder.getOrderNumber(), e);
      // Set fields to reflect this
      txtPrevEmp.setText(e.toString());
      txtNextEmp.getSelectionModel().clearSelection();
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

  // Temporary flower object used to create a treetable
  private class TempFlower extends RecursiveTreeObject<TempFlower> {
    public SimpleStringProperty type;
    public SimpleIntegerProperty num;

    public TempFlower(String tp, int num) {
      type = new SimpleStringProperty(tp);
      this.num = new SimpleIntegerProperty(num);
    }
  }
}
