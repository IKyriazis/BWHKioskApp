package edu.wpi.cs3733.d20.teamA.controllers.nav;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SettingsController extends AbstractNavPaneController {
  @FXML private GridPane buttonPane;

  public void initialize() {
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.MAP_SIGNS),
        "views/Settings/MapEditor.fxml",
        "Map\nEditor");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.PALETTE),
        "views/Settings/ThemeSettings.fxml",
        "Theme\nSettings");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.USER_PLUS),
        "views/Settings/CreateAcct.fxml",
        "Create\nAccount");
  }
}
