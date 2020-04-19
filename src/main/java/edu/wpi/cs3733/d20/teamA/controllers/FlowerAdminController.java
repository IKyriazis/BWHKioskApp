package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FlowerAdminController {
  private FlowerDatabase database;

  @FXML private TableView tblFlowerView;

  public void initialize() throws SQLException {
    database = new FlowerDatabase(database.getConnection());
    database.createTables();
    database.addFlower("Flower1", "Red", 8, 1.25);

    database.deleteFlower("Flower1", "Red");
    database.addFlower("Flower1", "Red", 9, 1.25);

    System.out.println("init");
    // Setup columns in table
    TableColumn column1 = new TableColumn("Type");
    column1.setCellValueFactory(new PropertyValueFactory<>("typeFlower"));

    TableColumn column2 = new TableColumn("Color");
    column2.setCellValueFactory(new PropertyValueFactory<>("color"));

    TableColumn column3 = new TableColumn("Y Coordinate");
    column3.setCellValueFactory(new PropertyValueFactory<>("qty"));

    TableColumn column4 = new TableColumn("Unit Price");
    column4.setCellValueFactory(new PropertyValueFactory<>("pricePer"));

    tblFlowerView.setItems(database.flowerOl());
    tblFlowerView.getColumns().addAll(column1, column2, column3, column4);
  }

  public void addFlower(ActionEvent actionEvent) throws IOException {
    toFlowerPopUp();
  }

  // Scene Switch
  @FXML
  public void toFlowerPopUp() throws IOException {

    Stage stage = new Stage();
    Parent root;
    FXMLLoader load = new FXMLLoader();
    load.setControllerFactory(
        param -> {
          return new FlowerModController(database, this);
        });
    load.setLocation(App.class.getResource("views/AddFlowerPopup.fxml"));

    root = load.load();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void update() throws SQLException {
    tblFlowerView.setItems(database.flowerOl());
  }

  @FXML
  public void cancel(ActionEvent event) throws IOException {
    ((Button) (event.getSource()))
        .getScene()
        .getWindow()
        .hide(); // use existing stage to close current window
  }
}
