package edu.wpi.cs3733.d20.teamA.controllers.nav;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainMenuController {
  @FXML private JFXButton mapButton;
  @FXML private JFXButton transitButton;
  @FXML private JFXButton serviceButton;

  public void initialize() {
    mapButton.setGraphic(new FontIcon(FontAwesomeSolid.MAP));
    transitButton.setGraphic(new FontIcon(FontAwesomeSolid.TRAIN));
    serviceButton.setGraphic(new FontIcon(FontAwesomeSolid.HAND_HOLDING_HEART));

    // Set up listeners to resize buttons to be equiradial circles
    JFXButton[] buttons = {mapButton, transitButton, serviceButton};
    for (JFXButton button : buttons) {
      button.widthProperty().addListener(observable -> resizeButtons());
      button.heightProperty().addListener(observable -> resizeButtons());
    }

    // Set up scene switching callbacks
    mapButton.setOnAction(
        event -> {
          SceneSwitcherController.pushScene("views/SimpleMap.fxml");
        });

    transitButton.setOnAction(
        event -> {
          SceneSwitcherController.pushScene("views/ServiceHome.fxml");
        });

    serviceButton.setOnAction(
        event -> {
          SceneSwitcherController.pushScene("views/EmployeeHome.fxml");
        });

    // Fix for weird button layout issues
    Platform.runLater(
        () -> {
          GridPane buttonPane = (GridPane) mapButton.getParent();
          buttonPane.requestLayout();
        });
  }

  private void resizeButtons() {
    JFXButton[] buttons = {mapButton, transitButton, serviceButton};

    // Find maximum button dimension
    double maxWidth = 0.0;
    double maxHeight = 0.0;
    for (JFXButton button : buttons) {
      maxWidth = Math.max(maxWidth, button.getWidth());
      maxHeight = Math.max(maxHeight, button.getHeight());
    }

    // Turn square
    maxWidth = Math.max(maxWidth, maxHeight);
    maxHeight = Math.max(maxWidth, maxHeight);

    // Set new button sizes
    for (JFXButton button : buttons) {
      button.setMinWidth(maxWidth);
      button.setPrefWidth(maxWidth);
      button.setMaxWidth(maxWidth);

      button.setMinHeight(maxHeight);
      button.setPrefHeight(maxHeight);
      button.setMaxHeight(maxHeight);
    }
  }
}
