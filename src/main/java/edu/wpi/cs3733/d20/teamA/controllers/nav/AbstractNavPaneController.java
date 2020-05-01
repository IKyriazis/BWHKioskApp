package edu.wpi.cs3733.d20.teamA.controllers.nav;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;

public abstract class AbstractNavPaneController extends AbstractController {
  protected void addButton(GridPane gridPane, FontIcon icon, String fxmlPath, String label) {
    int row = Math.max(0, (gridPane.getChildren().size()) / 5);
    int col = (gridPane.getChildren().size() % 5);

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

    gridPane.getChildren().add(buttonBox);
  }

  protected void equalizeButtonGrid(GridPane gridPane) {
    int occupiedCols = Math.min(gridPane.getChildren().size(), gridPane.getRowCount());
    double percentWidth = 100.0 / occupiedCols;

    int occupiedRows =
        Math.min(((gridPane.getChildren().size() - 1) / 5) + 1, gridPane.getColumnCount());
    double percentHeight = 100.0 / occupiedRows;

    gridPane.getColumnConstraints().clear();
    gridPane.getRowConstraints().clear();

    ColumnConstraints colConstraint = new ColumnConstraints();
    colConstraint.setPercentWidth(percentWidth);
    for (int i = 0; i < gridPane.getColumnCount(); i++) {
      gridPane.getColumnConstraints().add(colConstraint);
    }

    RowConstraints rowConstraint = new RowConstraints();
    rowConstraint.setPercentHeight(percentHeight);
    for (int i = 0; i < gridPane.getRowCount(); i++) {
      gridPane.getRowConstraints().add(rowConstraint);
    }
  }
}
