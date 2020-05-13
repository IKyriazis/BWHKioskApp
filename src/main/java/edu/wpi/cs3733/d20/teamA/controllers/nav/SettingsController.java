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
        "views/MapEditor.fxml",
        "Map\nEditor");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.PALETTE),
        "views/ThemeSettings.fxml",
        "Application\nOptions");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.USER),
        "views/ViewEmployees.fxml",
        "Manage\nEmployees");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.BULLHORN),
        "views/AnnouncementAdmin.fxml",
        "Announcements");
  }
}
