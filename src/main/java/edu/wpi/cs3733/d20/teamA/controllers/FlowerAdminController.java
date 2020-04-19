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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FlowerAdminController extends AbstractController {
  @FXML private TableView<Flower> tblFlowerView;
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
