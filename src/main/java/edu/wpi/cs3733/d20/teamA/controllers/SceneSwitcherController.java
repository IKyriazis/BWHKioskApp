package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class SceneSwitcherController {
  @FXML private TabPane tabPane;
  @FXML private Tab mapTab;
  @FXML private Tab serviceTab;
  @FXML private Tab employeeLoginTab;
  @FXML private JFXButton informationButton;
  @FXML private StackPane dialogPane;
  @FXML private JFXDrawer anouncementDrawer;

  @FXML
  public void initialize() throws IOException {
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
  public void informationButtonPressed() throws IOException {
    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setHeading(new Text("Announcements"));
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(App.class.getResource("views/NotificationWall.fxml"));
    layout.setBody(loader.load());
    JFXDialog dialog = new JFXDialog(dialogPane, layout, JFXDialog.DialogTransition.TOP);

    JFXButton closeButton = new JFXButton("Close");
    closeButton.setButtonType(JFXButton.ButtonType.RAISED);
    closeButton.setStyle("-fx-background-color: #78909C");
    closeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
    closeButton.setOnAction(
        event -> {
          dialog.close();
        });

    layout.setActions(closeButton);
    dialog.show();
  }
}
