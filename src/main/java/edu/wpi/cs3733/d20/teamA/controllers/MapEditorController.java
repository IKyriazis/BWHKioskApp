package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.NodeDialogController;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MapEditorController {
  @FXML private AnchorPane canvasPane;
  @FXML private StackPane dialogPane;
  @FXML private JFXSlider zoomSlider;
  @FXML private Label editorTipLabel;

  @FXML private JFXButton nodeInfoButton;
  @FXML private JFXButton panMapButton;
  @FXML private JFXButton addNodeButton;
  @FXML private JFXButton editNodeButton;
  @FXML private JFXButton deleteNodeButton;
  @FXML private JFXButton addEdgeButton;
  @FXML private JFXButton deleteEdgeButton;

  @FXML private AnchorPane infoPane;
  @FXML private JFXDrawer infoDrawer;
  private JFXRippler infoRippler;

  private MapCanvas canvas;
  private Graph graph;

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
    canvas.setDrawAllNodes(true);
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Setup zoom slider hook
    canvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());

    // Setup the canvasClicked function
    canvas.setOnMouseClicked(this::canvasClicked);

    // Setup zoom slider cursor
    zoomSlider.setCursor(Cursor.H_RESIZE);

    // Setup info pane rippler
    infoRippler = new JFXRippler(infoPane, JFXRippler.RipplerMask.RECT);
    infoRippler.setRipplerFill(Color.web("#78909C"));
    infoRippler.setEnabled(true);

    // Trigger ripple when label is updated
    editorTipLabel
        .textProperty()
        .addListener(observable -> Platform.runLater(infoRippler.createManualRipple()));

    // Setup info drawer
    infoDrawer.setSidePane(infoRippler);

    // Set up drawer transparency hooks
    infoDrawer.setOnDrawerOpened(event -> infoDrawer.setMouseTransparent(false));
    infoDrawer.setOnDrawerClosed(
        event -> {
          if (mode == Mode.PAN) {
            infoDrawer.setMouseTransparent(true);
          } else {
            // Bring the drawer back when people drag it closed when they shouldn't
            infoDrawer.open();
          }
        });

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

    // Try to get graph
    try {
      graph = Graph.getInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Platform.runLater(() -> canvas.draw(1));
  }

  public void toolPressed(ActionEvent event) {
    Mode startMode = mode;

    if (event.getTarget().equals(nodeInfoButton)) {
      mode = Mode.INFO;
      editorTipLabel.setText("Select Node");
    } else if (event.getTarget().equals(panMapButton)) {
      mode = Mode.PAN;
      canvas.setDragEnabled(true);
      infoDrawer.close();
    } else if (event.getTarget().equals(addNodeButton)) {
      mode = Mode.ADD_NODE;
      editorTipLabel.setText("Select Position");
    } else if (event.getTarget().equals(editNodeButton)) {
      mode = Mode.EDIT_NODE;
      editorTipLabel.setText("Select Node");
    } else if (event.getTarget().equals(deleteNodeButton)) {
      mode = Mode.DELETE_NODE;
      editorTipLabel.setText("Select Node");
    } else if (event.getTarget().equals(addEdgeButton)) {
      mode = Mode.ADD_EDGE;
      editorTipLabel.setText("Select First Node");
    } else if (event.getTarget().equals(deleteEdgeButton)) {
      mode = Mode.DELETE_EDGE;
      editorTipLabel.setText("Select First Node");
    }

    if (!event.getTarget().equals(panMapButton)) {
      canvas.setDragEnabled(false);
    }

    if (!event.getTarget().equals(panMapButton)) {
      infoDrawer.open();
    }

    if (mode != startMode) {
      canvas.setSelectedNode(null);
      canvas.draw(1);
    }
  }

  public void canvasClicked(MouseEvent mouse) {
    Point2D mousePos = new Point2D(mouse.getX(), mouse.getY());
    Point2D mouseGraphPos = canvas.canvasToGraph(mousePos);
    Optional<Node> optionalNode = canvas.getClosestNode(1, mousePos, 15);
    Node lastSelected = canvas.getSelectedNode();

    switch (mode) {
      case INFO:
        optionalNode.ifPresentOrElse(
            node -> {
              editorTipLabel.setText(
                  "Node ID: "
                      + node.getNodeID()
                      + "\nX: "
                      + node.getX()
                      + "\nY: "
                      + node.getY()
                      + "\nBuilding: "
                      + node.getBuilding()
                      + "\nNode Type: "
                      + node.getStringType()
                      + "\nLong Name: "
                      + node.getLongName()
                      + "\nShort Name: "
                      + node.getShortName());
              canvas.setSelectedNode(node);
            },
            () -> {
              editorTipLabel.setText("Select Node");
              canvas.setSelectedNode(null);
            });
        break;
      case ADD_NODE:
        openNodeModifyDialog(null, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY());
        break;
      case EDIT_NODE:
        optionalNode.ifPresent(
            node ->
                openNodeModifyDialog(node, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY()));
        break;
      case DELETE_NODE:
        optionalNode.ifPresent(
            node -> {
              try {
                graph.deleteNode(node);
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
        break;
      case ADD_EDGE:
        if (lastSelected == null) {
          optionalNode.ifPresent(canvas::setSelectedNode);
        } else {
          optionalNode.ifPresent(
              node -> {
                try {
                  graph.addEdge(lastSelected, node);
                } catch (Exception e) {
                  e.printStackTrace();
                }
                canvas.setSelectedNode(null);
              });
        }
        break;
      case DELETE_EDGE:
        if (lastSelected == null) {
          optionalNode.ifPresent(canvas::setSelectedNode);
        } else {
          optionalNode.ifPresent(
              node -> {
                try {
                  graph.deleteEdge(lastSelected, node);
                } catch (Exception e) {
                  e.printStackTrace();
                }
                canvas.setSelectedNode(null);
              });
        }
        break;
      default:
        break;
    }

    canvas.draw(1);
  }

  private void openNodeModifyDialog(Node node, int x, int y) {
    String heading = (node == null) ? "Add Node" : "Edit Node";
    NodeDialogController nodeDialogController = new NodeDialogController(node, x, y);
    DialogUtil.complexDialog(
        dialogPane,
        heading,
        "views/NodeModifyPopup.fxml",
        false,
        event -> canvas.draw(1),
        nodeDialogController);
  }
}
