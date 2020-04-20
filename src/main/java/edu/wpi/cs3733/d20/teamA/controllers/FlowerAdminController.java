package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FlowerAdminController extends AbstractController {

  @FXML private TableView<Flower> tblFlowerView;
  @FXML private TableView<Order> tblOrderView;

  @FXML private JFXTextField txtPrev;
  @FXML private JFXComboBox<String> txtNext;

  @FXML private AnchorPane flowerPane;
  @FXML private StackPane dialogStackPane;
  @FXML private HBox contentBox;

  private GaussianBlur blur;

  public FlowerAdminController() throws SQLException {}

  public void initialize() throws SQLException {
    blur = new GaussianBlur();
    blur.setRadius(15.0);

    // Setup columns in flower table
    TableColumn column1 = new TableColumn("Type");
    column1.setCellValueFactory(new PropertyValueFactory<>("typeFlower"));
    column1.setMinWidth(100);

    TableColumn column2 = new TableColumn("Color");
    column2.setCellValueFactory(new PropertyValueFactory<>("color"));
    column2.setMinWidth(100);

    TableColumn column3 = new TableColumn("Quantity");
    column3.setCellValueFactory(new PropertyValueFactory<>("qty"));
    column3.setMinWidth(80);

    TableColumn column4 = new TableColumn("Unit Price");
    column4.setCellValueFactory(new PropertyValueFactory<>("pricePer"));
    column4.setMinWidth(80);

    tblFlowerView.setItems(super.flDatabase.flowerOl());
    tblFlowerView.getColumns().addAll(column1, column2, column3, column4);

    // Setup columns in order table
    TableColumn columnO1 = new TableColumn("Order Number");
    columnO1.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));

    TableColumn columnO2 = new TableColumn("Number of Flowers");
    columnO2.setCellValueFactory(new PropertyValueFactory<>("numFlowers"));

    TableColumn columnO3 = new TableColumn("Type");
    columnO3.setCellValueFactory(new PropertyValueFactory<>("flowerType"));

    TableColumn columnO4 = new TableColumn("Color");
    columnO4.setCellValueFactory(new PropertyValueFactory<>("flowerColor"));

    TableColumn columnO5 = new TableColumn("Price");
    columnO5.setCellValueFactory(new PropertyValueFactory<>("price"));

    TableColumn columnO6 = new TableColumn("Status");
    columnO6.setCellValueFactory(new PropertyValueFactory<>("status"));

    TableColumn columnO7 = new TableColumn("Location");
    columnO7.setCellValueFactory(new PropertyValueFactory<>("location"));

    tblOrderView
        .getColumns()
        .addAll(columnO1, columnO2, columnO3, columnO4, columnO5, columnO6, columnO7);
    tblOrderView.setItems(super.flDatabase.orderOl());
    // Setup status change stuff
    txtPrev.setText("Select an order");
    txtNext.getItems().addAll("Order Sent", "Order Received", "Flowers Sent", "Flowers Delivered");
    txtNext.getSelectionModel().select(0);
  }

  public void addFlower(ActionEvent actionEvent) throws IOException {
    FXMLLoader loader = new FXMLLoader();

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
    }
  }

  public void editFlower(ActionEvent actionEvent) throws IOException {
    Flower f = tblFlowerView.getSelectionModel().getSelectedItem();
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

        loader.setControllerFactory(
            param -> {
              return new FlowerModController(dialog, f);
            });

        // loader.setController(new FlowerModController(this));
        javafx.scene.Node rootPane = loader.load();
        layout.setBody(rootPane);

        dialog.show();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void deleteFlower(ActionEvent actionEvent) throws SQLException {
    Flower f = tblFlowerView.getSelectionModel().getSelectedItem();
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
    tblFlowerView.setItems(super.flDatabase.flowerOl());
    tblOrderView.setItems(super.flDatabase.orderOl());
  }

  public Pane getPane() {
    return flowerPane;
  }

  public void setStatus(MouseEvent mouseEvent) {
    Order o = tblOrderView.getSelectionModel().getSelectedItem();
    if (o != null) {
      txtPrev.setText(o.getStatus());
      int nextStatus = statusStringToValue(o.getStatus()) + 1;
      if (nextStatus <= 3) txtNext.getSelectionModel().select(nextStatus);
    } else {
      txtPrev.setText("Select an order");
    }
  }

  public void changeProgress(ActionEvent actionEvent) throws SQLException {
    Order o = tblOrderView.getSelectionModel().getSelectedItem();
    if (o != null) {
      String s = txtNext.getSelectionModel().getSelectedItem();
      super.flDatabase.changeOrderStatus(o.getOrderNumber(), s);
    } else {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No item selected");
      alert.setContentText("Please select an item by clicking a row in the table");
      alert.show();
    }

    tblOrderView.setItems(super.flDatabase.orderOl());
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
  /*private String statusValueToString(int status)
  {
    switch(status)
    {
      case 1:
        return "Order Sent";
      case 2:
        return "Order Received";
      case 3:
        return "Flowers Sent";
      case 4:
        return "Flowers Delivered";
      default:
        return "BAD";//Should never occur
    }
  }*/
}
