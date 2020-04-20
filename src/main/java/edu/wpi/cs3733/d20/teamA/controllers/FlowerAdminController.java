package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerDialogController;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.Order;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class FlowerAdminController extends AbstractController {

  @FXML private TreeTableView<Flower> tblFlowerView;
  @FXML private TreeTableView<Order> tblOrderView;

  @FXML private JFXTextField txtPrev;
  @FXML private JFXComboBox<String> txtNext;

  @FXML private AnchorPane flowerPane;
  @FXML private StackPane dialogStackPane;

  @FXML private Label flowerTblLbl;
  @FXML private Label orderTblLbl;

  @FXML private JFXButton addFlowerButton;
  @FXML private JFXButton editFlowerButton;
  @FXML private JFXButton deleteFlowerButton;
  @FXML private JFXButton changeProgressButton;

  private GaussianBlur blur;

  public FlowerAdminController() throws SQLException {}

  public void initialize() throws SQLException {
    blur = new GaussianBlur();
    blur.setRadius(15.0);

    // Setup label icons
    flowerTblLbl.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE));
    orderTblLbl.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BARCODE));

    // Setup button icons
    addFlowerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE));
    editFlowerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE));
    deleteFlowerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE));
    changeProgressButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCHANGE));

    // Setup columns in flower table
    JFXTreeTableColumn<Flower, String> column1 = new JFXTreeTableColumn<>("Type");
    column1.setCellValueFactory(param -> param.getValue().getValue().typeFlowerProperty());

    JFXTreeTableColumn<Flower, String> column2 = new JFXTreeTableColumn<>("Color");
    column2.setCellValueFactory(param -> param.getValue().getValue().colorProperty());

    JFXTreeTableColumn<Flower, Integer> column3 = new JFXTreeTableColumn<>("Quantity");
    column3.setCellValueFactory(param -> param.getValue().getValue().qtyProperty().asObject());

    JFXTreeTableColumn<Flower, Double> column4 = new JFXTreeTableColumn<>("Unit Price");
    column4.setCellValueFactory(param -> param.getValue().getValue().pricePerProperty().asObject());

    // Add columns to table
    //noinspection unchecked
    tblFlowerView.getColumns().addAll(column1, column2, column3, column4);

    // Setup column sizing
    tblFlowerView.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
    tblFlowerView
        .getColumns()
        .forEach(
            column -> {
              column.setMinWidth(80.0);
              column.setReorderable(false);
            });

    // Null root flower node
    TreeItem<Flower> rootFlower = new TreeItem<>(new Flower("", "", 0, 0.0));

    // Add other tree items below root item
    flDatabase.flowerOl().forEach(flower -> rootFlower.getChildren().add(new TreeItem<>(flower)));

    // Set root table item
    tblFlowerView.setRoot(rootFlower);
    tblFlowerView.setShowRoot(false);

    // Setup columns in order table
    JFXTreeTableColumn<Order, Integer> columnO1 = new JFXTreeTableColumn<>("Order #");
    columnO1.setCellValueFactory(
        param -> param.getValue().getValue().orderNumberProperty().asObject());

    JFXTreeTableColumn<Order, Integer> columnO2 = new JFXTreeTableColumn<>("Quantity");
    columnO2.setCellValueFactory(
        param -> param.getValue().getValue().numFlowersProperty().asObject());

    JFXTreeTableColumn<Order, String> columnO3 = new JFXTreeTableColumn<>("Type");
    columnO3.setCellValueFactory(param -> param.getValue().getValue().flowerTypeProperty());

    JFXTreeTableColumn<Order, String> columnO4 = new JFXTreeTableColumn<>("Color");
    columnO4.setCellValueFactory(param -> param.getValue().getValue().flowerColorProperty());

    JFXTreeTableColumn<Order, Double> columnO5 = new JFXTreeTableColumn<>("Price");
    columnO5.setCellValueFactory(param -> param.getValue().getValue().priceProperty().asObject());

    JFXTreeTableColumn<Order, String> columnO6 = new JFXTreeTableColumn<>("Status");
    columnO6.setCellValueFactory(param -> param.getValue().getValue().statusProperty());

    JFXTreeTableColumn<Order, String> columnO7 = new JFXTreeTableColumn<>("Location");
    columnO7.setCellValueFactory(param -> param.getValue().getValue().locationProperty());

    // Add columns to order table
    //noinspection unchecked
    tblOrderView
        .getColumns()
        .addAll(columnO1, columnO2, columnO3, columnO4, columnO5, columnO6, columnO7);

    // Setup column sizing
    tblOrderView.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
    tblOrderView
        .getColumns()
        .forEach(
            column -> {
              column.setReorderable(false);
            });

    // Null root order node
    TreeItem<Order> rootOrder = new TreeItem<>(new Order(0, 0, "", "", 0.0, "", ""));

    // Add other tree items below root item
    flDatabase.orderOl().forEach(order -> rootOrder.getChildren().add(new TreeItem<>(order)));

    // Set root table item
    tblOrderView.setRoot(rootOrder);
    tblOrderView.setShowRoot(false);

    // Hook up txtPrev to show status of selected order
    tblOrderView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              txtPrev.setText(newValue.getValue().toString());
            });

    // Setup status change stuff
    txtNext.getItems().addAll("Order Sent", "Order Received", "Flowers Sent", "Flowers Delivered");
    txtNext.getSelectionModel().select(0);
  }

  public void addFlower(ActionEvent actionEvent) {
    DialogUtil.complexDialog(
        dialogStackPane,
        "Add Flower",
        "views/AddFlowerPopup.fxml",
        false,
        event -> update(),
        new FlowerDialogController());
  }

  public void editFlower(ActionEvent actionEvent) {
    TreeItem<Flower> selection = tblFlowerView.getSelectionModel().getSelectedItem();
    if (selection != null) {
      Flower flower = selection.getValue();
      FlowerDialogController controller = new FlowerDialogController(flower);
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

  public void deleteFlower(ActionEvent actionEvent) throws SQLException {
    TreeItem<Flower> selected = tblFlowerView.getSelectionModel().getSelectedItem();
    if (selected != null) {
      Flower f = selected.getValue();
      String name = f.getTypeFlower();
      String color = f.getColor();
      super.flDatabase.deleteFlower(name, color);
      update();
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Flower Selected",
          "Please select a flower by clicking a row in the table");
    }
  }

  public void update() {
    try {
      TreeItem<Flower> rootItem = tblFlowerView.getRoot();
      rootItem.getChildren().clear();
      flDatabase.flowerOl().forEach(flower -> rootItem.getChildren().add(new TreeItem<>(flower)));

      TreeItem<Order> rootOrder = tblOrderView.getRoot();
      rootOrder.getChildren().clear();
      flDatabase.orderOl().forEach(order -> rootOrder.getChildren().add(new TreeItem<>(order)));
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }

  public void changeProgress(ActionEvent actionEvent) throws SQLException {
    TreeItem<Order> selected = tblOrderView.getSelectionModel().getSelectedItem();
    if (selected != null) {
      Order o = selected.getValue();
      String s = txtNext.getSelectionModel().getSelectedItem();
      super.flDatabase.changeOrderStatus(o.getOrderNumber(), s);
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Order Selected",
          "Please select an order by clicking a row in the table");
    }

    TreeItem<Order> rootOrder = tblOrderView.getRoot();
    rootOrder.getChildren().clear();
    flDatabase.orderOl().forEach(order -> rootOrder.getChildren().add(new TreeItem<>(order)));
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
        return 999; // Should never occur
    }
  }

  private String statusValueToString(int status) {
    switch (status) {
      case 1:
        return "Order Sent";
      case 2:
        return "Order Received";
      case 3:
        return "Flowers Sent";
      case 4:
        return "Flowers Delivered";
      default:
        return "BAD"; // Should never occur
    }
  }
}
