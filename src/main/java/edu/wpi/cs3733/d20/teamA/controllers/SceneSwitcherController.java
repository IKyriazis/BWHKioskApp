package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SceneSwitcherController {
  @FXML public TabPane tabPane;
  @FXML public Tab mapTab;
  @FXML public Tab serviceTab;
  @FXML public Tab employeeLoginTab;
  @FXML public JFXButton settingsButton;

  @FXML
  public void initialize() {
    // Setup tab icons
    mapTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MAP));
    serviceTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BELL));
    employeeLoginTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SIGN_IN));

    // Settings button icon
    settingsButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE));

    // Setup dynamic tab resizing
    tabPane
        .widthProperty()
        .addListener(
            observable -> {
              double newWidth = tabPane.getWidth() / tabPane.getTabs().size();
              newWidth -= 25;

              tabPane.setTabMinWidth(newWidth);
              tabPane.setTabMaxWidth(newWidth);
            });
  }
}
