package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXSlider;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MapEditorController {
  @FXML private AnchorPane canvasPane;
  @FXML private JFXSlider zoomSlider;
  @FXML private Label nodeInfoLbl;

  private MapCanvas canvas;

  enum Mode {
    nodeInfo,
    add,
    edit,
    delete,
    uploadCSV,
    downloadCSV
  }

  // Setup the Mode
  Mode mode = Mode.nodeInfo;

  @FXML
  public void initialize() {
    // Setup map canvas
    canvas = new MapCanvas();
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Setup zoom slider hook
    canvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());

    // Setup zoom slider cursor
    zoomSlider.setCursor(Cursor.H_RESIZE);

    Platform.runLater(() -> canvas.draw(1));
  }

  public void nodeInfoPressed(ActionEvent actionEvent) {
    mode = Mode.nodeInfo;
    nodeInfoLbl.setText("Info for nodes goes here");
  }
}
