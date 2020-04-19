package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
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
  }

  @FXML
  public void informationButtonPressed() {
    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setHeading(new Text("Info"));
    layout.setBody(
        new Text(
            "Built by Yash Patel, Tyler Looney, Dyllan Cole, Brennan Aubuchon, Eva Labbe, Gabriel Dudlicek, Maddison Caten, Will Engdahl, Cory Helmuth, and Ioannis Kyriazis"));

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
