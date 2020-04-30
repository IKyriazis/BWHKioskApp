package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.NotificationController;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

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
    mapTab.setGraphic(new FontIcon(FontAwesomeRegular.MAP));
    serviceTab.setGraphic(new FontIcon(FontAwesomeSolid.CUBES));
    employeeLoginTab.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_IN_ALT));

    // Settings button icon
    informationButton.setGraphic(new FontIcon(FontAwesomeRegular.BELL));

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
                      Node node = tab.getContent();
                      node.fireEvent(new TabSwitchEvent());
                    }
                  });
            });
  }

  @FXML
  public void informationButtonPressed() {
    DialogUtil.complexDialog(
        dialogPane,
        "Announcements",
        "views/NotificationWall.fxml",
        true,
        null,
        new NotificationController());
  }
}
