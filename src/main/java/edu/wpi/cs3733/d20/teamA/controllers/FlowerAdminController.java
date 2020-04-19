package edu.wpi.cs3733.d20.teamA.controllers;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FlowerAdminController extends AbstractController {
  @FXML private TableView<Flower> tblFlowerView;
  @FXML private TableView<Order> tblOrderView;

  @FXML private AnchorPane flowerPane;

  public FlowerAdminController() throws SQLException {}

  public void initialize() throws SQLException {

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
    columnO1.setCellValueFactory(new PropertyValueFactory<>("flowerType"));

    TableColumn columnO4 = new TableColumn("Color");
    columnO4.setCellValueFactory(new PropertyValueFactory<>("flowerColor"));

    TableColumn columnO5 = new TableColumn("Price");
    columnO5.setCellValueFactory(new PropertyValueFactory<>("price"));

    TableColumn columnO6 = new TableColumn("Status");
    columnO6.setCellValueFactory(new PropertyValueFactory<>("status"));

    TableColumn columnO7 = new TableColumn("Location");
    columnO7.setCellValueFactory(new PropertyValueFactory<>("location"));

    tblOrderView.setItems(super.flDatabase.orderOl());
    tblOrderView
        .getColumns()
        .addAll(columnO1, columnO2, columnO3, columnO4, columnO5, columnO6, columnO7);
  }

  public void addFlower(ActionEvent actionEvent) throws IOException {
    FXMLLoader load = new FXMLLoader();
    load.setControllerFactory(
        param -> {
          return new FlowerModController(this);
        });

    toFlowerPopUp(load);
  }

  public void editFlower(ActionEvent actionEvent) throws IOException {
    Flower f = tblFlowerView.getSelectionModel().getSelectedItem();
    if (f == null) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No item selected");
      alert.setContentText("Please select an item by clicking a row in the table");
      alert.show();
    } else {
      FXMLLoader load = new FXMLLoader();
      load.setControllerFactory(
          param -> {
            return new FlowerModController(this, f);
          });

      toFlowerPopUp(load);
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
  }

  @FXML
  public void cancel(ActionEvent event) throws IOException {
    ((Button) (event.getSource()))
        .getScene()
        .getWindow()
        .hide(); // use existing stage to close current window
  }

  public Pane getPane() {
    return flowerPane;
  }
}
