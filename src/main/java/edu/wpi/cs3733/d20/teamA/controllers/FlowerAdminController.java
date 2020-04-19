package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.database.Flower;
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
import javafx.stage.Stage;

public class FlowerAdminController extends AbstractController {
  @FXML private TableView<Flower> tblFlowerView;

  public FlowerAdminController() throws SQLException {}

  public void initialize() throws SQLException {

    // Setup columns in table
    TableColumn column1 = new TableColumn("Type");
    column1.setCellValueFactory(new PropertyValueFactory<>("typeFlower"));

    TableColumn column2 = new TableColumn("Color");
    column2.setCellValueFactory(new PropertyValueFactory<>("color"));

    TableColumn column3 = new TableColumn("Y Coordinate");
    column3.setCellValueFactory(new PropertyValueFactory<>("qty"));

    TableColumn column4 = new TableColumn("Unit Price");
    column4.setCellValueFactory(new PropertyValueFactory<>("pricePer"));

    tblFlowerView.setItems(super.flDatabase.flowerOl());
    tblFlowerView.getColumns().addAll(column1, column2, column3, column4);
  }

  public void addFlower(ActionEvent actionEvent) throws IOException {
    FXMLLoader load = new FXMLLoader();
    load.setControllerFactory(
        param -> {
          return new FlowerModController(super.flDatabase, this);
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
            return new FlowerModController(super.flDatabase, this, f);
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
}
