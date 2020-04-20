package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.Order;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    // Setup status change stuff
    txtNext.getItems().addAll("Order Sent", "Order Received", "Flowers Sent", "Flowers Delivered");
    txtNext.getSelectionModel().select(0);
  }

  public void addFlower(ActionEvent actionEvent) {
    /*FXMLLoader loader = new FXMLLoader();

    try {
      loader.setLocation(App.class.getResource("views/AddFlowerPopup.fxml"));

      JFXDialogLayout layout = new JFXDialogLayout();
      layout.setHeading(new Text("Add Flower"));

      JFXDialog dialog = new JFXDialog(dialogStackPane, layout, JFXDialog.DialogTransition.BOTTOM);
      contentBox.setEffect(blur);
      dialog.setOnDialogClosed(
          event -> {
            contentBox.setEffect(null);
            try {
              update();
            } catch (SQLException throwables) {
              throwables.printStackTrace();
            }
          });

      loader.setControllerFactory(
          param -> {
            return new FlowerModController(dialog);
          });

      // loader.setController(new FlowerModController(this));
      javafx.scene.Node rootPane = loader.load();
      layout.setBody(rootPane);

      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  public void editFlower(ActionEvent actionEvent) throws IOException {
    /*Flower f = tblFlowerView.getSelectionModel().getSelectedItem().getValue();
    if (f == null) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No item selected");
      alert.setContentText("Please select an item by clicking a row in the table");
      alert.show();
    } else {
      FXMLLoader loader = new FXMLLoader();

      try {
        loader.setLocation(App.class.getResource("views/AddFlowerPopup.fxml"));

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Text("Add Flower"));

        JFXDialog dialog =
            new JFXDialog(dialogStackPane, layout, JFXDialog.DialogTransition.BOTTOM);
        contentBox.setEffect(blur);
        dialog.setOnDialogClosed(
            event -> {
              contentBox.setEffect(null);
              try {
                update();
              } catch (SQLException throwables) {
                throwables.printStackTrace();
              }
            });

        loader.setControllerFactory(param -> new FlowerModController(dialog, f));

        // loader.setController(new FlowerModController(this));
        javafx.scene.Node rootPane = loader.load();
        layout.setBody(rootPane);

        dialog.show();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }*/
  }

  public void deleteFlower(ActionEvent actionEvent) throws SQLException {
    Flower f = tblFlowerView.getSelectionModel().getSelectedItem().getValue();
    if (f != null) {
      String name = f.getTypeFlower();
      String color = f.getColor();
      super.flDatabase.deleteFlower(name, color);
      update();
    } else {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No item selected");
      alert.setContentText("Please select an item by clicking a row in the table");
      alert.show();
    }
  }

  // Scene Switch
  @FXML
  public void toFlowerPopUp(FXMLLoader load) throws IOException {
    Stage stage = new Stage();

    Parent root;

    load.setLocation(App.class.getResource("views/AddFlowerPopup.fxml"));

    root = load.load();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void update() throws SQLException {
    TreeItem<Flower> rootItem = tblFlowerView.getRoot();
    rootItem.getChildren().clear();
    flDatabase.flowerOl().forEach(flower -> rootItem.getChildren().add(new TreeItem<>(flower)));

    TreeItem<Order> rootOrder = tblOrderView.getRoot();
    rootOrder.getChildren().clear();
    flDatabase.orderOl().forEach(order -> rootOrder.getChildren().add(new TreeItem<>(order)));
  }

  public Pane getPane() {
    return flowerPane;
  }

  public void setStatus(MouseEvent mouseEvent) {
    Order o = tblOrderView.getSelectionModel().getSelectedItem().getValue();
    if (o != null) {
      txtPrev.setText(o.getStatus());
      int nextStatus = statusStringToValue(o.getStatus()) + 1;
      if (nextStatus <= 3) txtNext.getSelectionModel().select(nextStatus);
    } else {
      txtPrev.clear();
    }
  }

  public void changeProgress(ActionEvent actionEvent) throws SQLException {
    Order o = tblOrderView.getSelectionModel().getSelectedItem().getValue();
    if (o != null) {
      String s = txtNext.getSelectionModel().getSelectedItem();
      super.flDatabase.changeOrderStatus(o.getOrderNumber(), s);
    } else {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No item selected");
      alert.setContentText("Please select an item by clicking a row in the table");
      alert.show();
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
