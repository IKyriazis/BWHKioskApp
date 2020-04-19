package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MapEditorController {
  @FXML private AnchorPane canvasPane;
  @FXML private JFXSlider zoomSlider;
  @FXML private Label nodeInfoLbl;

  @FXML private JFXButton nodeInfoButton;
  @FXML private JFXButton panMapButton;
  @FXML private JFXButton addNodeButton;
  @FXML private JFXButton editNodeButton;
  @FXML private JFXButton deleteNodeButton;
  @FXML private JFXButton addEdgeButton;
  @FXML private JFXButton deleteEdgeButton;

  @FXML private JFXDrawer infoDrawer;

  private MapCanvas canvas;

  enum Mode {
    INFO,
    PAN,
    ADD_NODE,
    EDIT_NODE,
    DELETE_NODE,
    ADD_EDGE,
    DELETE_EDGE
  }

  // Setup the Mode
  private Mode mode = Mode.PAN;

  @FXML
  public void initialize() {
    // Setup map canvas
    canvas = new MapCanvas(true);
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Setup zoom slider hook
    canvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());

    // Setup the canvasClicked function
    canvas.setOnMouseClicked(this::canvasClicked);

    // Setup zoom slider cursor
    zoomSlider.setCursor(Cursor.H_RESIZE);

    // Setup info drawer
    infoDrawer.setSidePane(nodeInfoLbl);

    // Set up drawer transparency hooks
    infoDrawer.setOnDrawerOpened(event -> infoDrawer.setMouseTransparent(false));
    infoDrawer.setOnDrawerClosed(event -> infoDrawer.setMouseTransparent(true));

    // Setup button icons
    nodeInfoButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.INFO));
    panMapButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROWS_ALT));
    addNodeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE));
    editNodeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
    deleteNodeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
    addEdgeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
    deleteEdgeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT));

    // Setup tool button press handler
    nodeInfoButton.setOnAction(this::toolPressed);
    panMapButton.setOnAction(this::toolPressed);
    addNodeButton.setOnAction(this::toolPressed);
    editNodeButton.setOnAction(this::toolPressed);
    deleteNodeButton.setOnAction(this::toolPressed);
    addEdgeButton.setOnAction(this::toolPressed);
    deleteEdgeButton.setOnAction(this::toolPressed);

    Platform.runLater(() -> canvas.draw(1));
  }

  public void toolPressed(ActionEvent event) {
    if (event.getTarget().equals(nodeInfoButton)) {
      mode = Mode.INFO;
      infoDrawer.open();
    } else if (event.getTarget().equals(panMapButton)) {
      mode = Mode.PAN;
      canvas.setDragEnabled(true);
    } else if (event.getTarget().equals(addNodeButton)) {
      mode = Mode.ADD_NODE;
    } else if (event.getTarget().equals(editNodeButton)) {
      mode = Mode.EDIT_NODE;
    } else if (event.getTarget().equals(deleteNodeButton)) {
      mode = Mode.DELETE_NODE;
    } else if (event.getTarget().equals(addEdgeButton)) {
      mode = Mode.ADD_EDGE;
    } else if (event.getTarget().equals(deleteEdgeButton)) {
      mode = Mode.DELETE_EDGE;
    }

    if (!event.getTarget().equals(panMapButton)) {
      canvas.setDragEnabled(false);
    }

    if (!event.getTarget().equals(nodeInfoButton)) {
      infoDrawer.close();
    }
  }

  public void canvasClicked(MouseEvent mouse) {
    Point2D mousePos = new Point2D(mouse.getX(), mouse.getY());

    if (mode == Mode.INFO) {
      Optional<Node> node = canvas.getClosestNode(mousePos, 15);
      if (node.isPresent()) {
        Node closestNode = node.get();
        nodeInfoLbl.setText(
            "Node ID: "
                + closestNode.getNodeID()
                + "\nX: "
                + closestNode.getX()
                + "\nY: "
                + closestNode.getY());
      }
    }
  }
}
