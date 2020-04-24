package edu.wpi.cs3733.d20.teamA.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import edu.wpi.cs3733.d20.teamA.controls.VSwitcherBox;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class ServiceHomeController {
  @FXML private Pane rootPane;
  @FXML private AnchorPane switcherPane;
  @FXML private Pane destPane;

  public void initialize() {
    // Setup switcher box
    VSwitcherBox vSwitcherBox =
        new VSwitcherBox(destPane, new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT));
    vSwitcherBox.addEntry(
        "Flowers", new MaterialIconView(MaterialIcon.LOCAL_FLORIST), "views/FlowerService.fxml");
    vSwitcherBox.setTransitionMillis(500);

    // Add switcher box to anchor pane and constrain it
    switcherPane.getChildren().add(vSwitcherBox);
    AnchorPane.setBottomAnchor(vSwitcherBox, 0.0);
    AnchorPane.setTopAnchor(vSwitcherBox, 0.0);

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
