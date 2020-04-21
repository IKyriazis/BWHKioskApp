package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public class SceneSwitcherController {
  @FXML private TabPane tabPane;
  @FXML private Tab mapTab;
  @FXML private Tab serviceTab;
  @FXML private Tab employeeLoginTab;
  @FXML private JFXButton informationButton;
  @FXML private StackPane dialogPane;

  @FXML
  public void initialize() {
    // Setup tab icons
    mapTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MAP));
    serviceTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BELL));
    employeeLoginTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SIGN_IN));

    // Settings button icon
    informationButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE));

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

    tabPane
        .getTabs()
        .forEach(
            tab -> {
              tab.setOnSelectionChanged(
                  event -> {
                    if (tab.isSelected()) {
                      Node node = mapTab.getContent();
                      node.fireEvent(new TabSwitchEvent());
                    }
                  });
            });
  }

  @FXML
  public void informationButtonPressed() {
    DialogUtil.complexDialog(
        dialogPane, "Announcements", "views/NotificationWall.fxml", true, null, null);
  }
}
