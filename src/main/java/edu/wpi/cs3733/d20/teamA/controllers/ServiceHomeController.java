package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.controls.VSwitcherBox;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ServiceHomeController {
  @FXML private Pane rootPane;
  @FXML private Pane switcherPane;
  @FXML private Pane destPane;

  public void initialize() {
    // Setup switcher box
    VSwitcherBox vSwitcherBox = new VSwitcherBox(destPane, new FontIcon(FontAwesomeSolid.FILE_CSV));
    vSwitcherBox.addEntry(
        "Flowers", new FontIcon(FontAwesomeSolid.PAPER_PLANE), "views/flower/FlowerService.fxml");
    vSwitcherBox.addEntry(
        "Internal Transport",
        new FontIcon(FontAwesomeSolid.UNIVERSAL_ACCESS),
        "views/InternalTransportService.fxml");
    vSwitcherBox.setTransitionMillis(500);

    // Add switcher box to anchor pane and constrain it
    switcherPane.getChildren().add(vSwitcherBox);

    // Pass tab switch events through to current view
    rootPane.addEventFilter(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          if (event.getTarget().equals(rootPane)) {
            destPane.getChildren().forEach(node -> node.fireEvent(new TabSwitchEvent()));
          }
        });
  }
}
