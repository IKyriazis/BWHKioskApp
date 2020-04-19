package edu.wpi.cs3733.d20.teamA.controllers;

import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginController {
  @FXML private VBox loginBox;

  @FXML
  public void initialize() {
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(12.0);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    dropShadow.setColor(Color.GREY);

    loginBox.setEffect(dropShadow);
  }
}
