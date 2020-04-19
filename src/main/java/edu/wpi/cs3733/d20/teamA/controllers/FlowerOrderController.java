package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import java.io.IOException;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javax.swing.*;

public class FlowerOrderController extends AbstractController {

  @FXML private JFXComboBox<String> choiceFlower;
  @FXML private JFXTextField txtNumber;

  public FlowerOrderController() throws SQLException {}

  public void initialize() throws SQLException {
    ObservableList<Flower> list = super.flDatabase.flowerOl(); // Get from FlowerDatabase @TODO
    for (Flower f : list) {
      choiceFlower.getItems().add(f.getTypeFlower() + ", " + f.getColor());
    }
  }

  @FXML
  public void cancel(ActionEvent event) throws IOException {
    Stage stage;
    Parent root;
    // putting the event's source in src var so it doesn't have to check it every time
    Object src = event.getSource();

    stage =
        (Stage)
            ((Button) (src)).getScene().getWindow(); // use existing stage to close current window

    root = FXMLLoader.load(App.class.getResource("views/FlowerDeliveryServiceHome.fxml"));
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
  }

  public void placeOrder(ActionEvent actionEvent) throws SQLException {
    if (choiceFlower.getSelectionModel().getSelectedItem() != null) {
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);

      int num = Integer.parseInt(txtNumber.getText());

      System.out.println(type);
      System.out.println(color);
      System.out.println("" + num);

      int i = super.flDatabase.addOrder(num, type, color, "LOACTION ADD LATER");

      if (i == 0) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unable to place order");
        alert.setContentText("Order not placed successfully, please try again");
        alert.showAndWait();
      } else {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order placed");
        alert.setContentText("Your order has been placed. Your order number is: " + i);
        alert.showAndWait();
        choiceFlower.getScene().getWindow().hide(); // use existing stage to close current window
      }
    }
  }

  public void setMaxNumber(ActionEvent actionEvent) {}
}
