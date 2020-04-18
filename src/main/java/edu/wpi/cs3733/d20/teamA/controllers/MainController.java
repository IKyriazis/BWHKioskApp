package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
  @FXML private VBox searchFields;
  @FXML private AnchorPane canvasPane;

  private MapCanvas canvas;

  public void initialize() {
    searchFields.setVisible(false);

    // Make canvas occupy the full width / height of its parent anchor pane. Couldn't set in FXML.
    canvas = new MapCanvas();
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Draw background asap
    Platform.runLater(() -> canvas.drawFloorBackground(1));
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
