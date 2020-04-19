package edu.wpi.cs3733.d20.teamA.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginController {
  @FXML private VBox loginBox;
  @FXML private Button loginButton;

  @FXML
  public void initialize() {
    // Add drop shadow to login box.
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(12.0);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    dropShadow.setColor(Color.GREY);
    loginBox.setEffect(dropShadow);

    // Setup login button icon
    loginButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK));
  }
}
