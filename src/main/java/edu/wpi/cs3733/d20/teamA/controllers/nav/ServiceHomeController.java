package edu.wpi.cs3733.d20.teamA.controllers.nav;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ServiceHomeController {
  @FXML private AnchorPane rootPane;
  @FXML private GridPane buttonPane;

  public void initialize() {
    // Rebuild button pane when switching to this
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          buildButtonPane();
        });

    // Build initial button pane
    buildButtonPane();
  }

  private void buildButtonPane() {
    buttonPane.getChildren().clear();

    addButton(
        new FontIcon(FontAwesomeSolid.TOILET_PAPER), "views/flower/FlowerService.fxml", "Flowers");
    for (int i = 0; i < 9; i++) {
      addButton(
          new FontIcon(FontAwesomeSolid.WHEELCHAIR),
          "views/InternalTransportService.fxml",
          "Internal\nTransport");
    }

    equalizeButtonGrid();
  }

  private void addButton(FontIcon icon, String fxmlPath, String label) {
    int row = Math.max(0, (buttonPane.getChildren().size()) / 5);
    int col = (buttonPane.getChildren().size() % 5);

    VBox buttonBox = new VBox();
    buttonBox.setPadding(new Insets(25, 25, 25, 25));
    buttonBox.setAlignment(Pos.TOP_CENTER);
    buttonBox.setSpacing(10);

    JFXButton button = new JFXButton();
    button.getStyleClass().add("chonky-text");
    button.setGraphic(icon);
    button.setOnAction(
        event -> {
          SceneSwitcherController.pushScene(fxmlPath);
        });

    Label buttonLabel = new Label(label);
    buttonLabel.getStyleClass().add("medium-text");
    buttonLabel.setTextAlignment(TextAlignment.CENTER);
    buttonLabel.setWrapText(true);

    buttonBox.getChildren().addAll(button, buttonLabel);
    GridPane.setRowIndex(buttonBox, row);
    GridPane.setColumnIndex(buttonBox, col);

    buttonPane.getChildren().add(buttonBox);
  }

  private void equalizeButtonGrid() {
    int occupiedCols = Math.min(buttonPane.getChildren().size(), buttonPane.getRowCount());
    double percentWidth = 100.0 / occupiedCols;

    int occupiedRows =
        Math.min(((buttonPane.getChildren().size() - 1) / 5) + 1, buttonPane.getColumnCount());
    double percentHeight = 100.0 / occupiedRows;

    buttonPane.getColumnConstraints().clear();
    buttonPane.getRowConstraints().clear();

    ColumnConstraints colConstraint = new ColumnConstraints();
    colConstraint.setPercentWidth(percentWidth);
    for (int i = 0; i < buttonPane.getColumnCount(); i++) {
      buttonPane.getColumnConstraints().add(colConstraint);
    }

    RowConstraints rowConstraint = new RowConstraints();
    rowConstraint.setPercentHeight(percentHeight);
    for (int i = 0; i < buttonPane.getRowCount(); i++) {
      buttonPane.getRowConstraints().add(rowConstraint);
    }
  }
}
