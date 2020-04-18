package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
  @FXML private VBox searchFields;

  public void initialize() {
    searchFields.setVisible(false);
  }

  @FXML
  public void toggleSearch(ActionEvent actionEvent) {
    searchFields.setVisible(!searchFields.isVisible());
  }

  @FXML
  public void showServices() throws IOException {
    Stage stage = new Stage();
    Parent root = FXMLLoader.load(App.class.getResource("views/FlowerDeliveryServiceHome.fxml"));

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void login() throws IOException {
    Stage stage = new Stage();
    Parent root = FXMLLoader.load(App.class.getResource("views/Login.fxml"));

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
