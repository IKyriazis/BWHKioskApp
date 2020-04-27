package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.NodeDialogController;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.CSVLoader;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class MapEditorController {
  @FXML private AnchorPane canvasPane;
  @FXML private StackPane dialogPane;
  @FXML private JFXSlider zoomSlider;
  @FXML private Label editorTipLabel;

  @FXML private JFXButton floorUpButton;
  @FXML private JFXButton floorDownButton;
  @FXML private JFXTextField floorField;

  @FXML private AnchorPane infoPane;
  @FXML private JFXDrawer infoDrawer;
  private JFXRippler infoRippler;

  private ArrayList<Node> selections;
  private MapCanvas canvas;
  private Graph graph;
  private int floor = 1;

  private Point2D dragStart;
  private Point2D dragCurr;
  private Point2D lastPressedPos = new Point2D(0, 0);
  private boolean draggingNodes = false;

  private final EventHandler<MouseEvent> dragStartHandler =
      event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
          dragStart = new Point2D(event.getX(), event.getY());
        }
        canvas.getDragStartHandler().handle(event);
      };

  private final EventHandler<MouseEvent> dragHandler =
      event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
          dragCurr = new Point2D(event.getX(), event.getY());
          if (draggingNodes) {
            canvas.setHighlightOffset(dragCurr.subtract(dragStart));
          } else {
            canvas.setSelectionBox(dragStart, dragCurr);
          }

          canvas.draw(floor);
        }

        canvas.getDragHandler().handle(event);
      };

  private final EventHandler<MouseEvent> dragEndHandler =
      event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
          if (dragCurr == null) {
            return;
          }

          if (draggingNodes) {
            Point2D offset =
                canvas.canvasToGraph(dragCurr).subtract(canvas.canvasToGraph(dragStart));
            selections.forEach(
                node -> {
                  graph.moveNode(
                      node,
                      (int) (node.getX() + offset.getX()),
                      (int) (node.getY() + offset.getY()));
                });

            canvas.setHighlightOffset(null);
            draggingNodes = false;
          } else {
            double startX = Math.min(dragStart.getX(), dragCurr.getX());
            double startY = Math.min(dragStart.getY(), dragCurr.getY());

            double width = Math.max(dragStart.getX(), dragCurr.getX()) - startX;
            double height = Math.max(dragStart.getY(), dragCurr.getY()) - startY;

            if (width < 5 || height < 5) {
              return;
            }
            lastPressedPos = new Point2D(event.getX(), event.getY());

            BoundingBox bounds = new BoundingBox(startX, startY, width, height);
            selections.clear();
            graph.getNodes().values().stream()
                .filter(node -> node.getFloor() == floor)
                .forEach(
                    node -> {
                      Point2D nodePos = canvas.graphToCanvas(new Point2D(node.getX(), node.getY()));
                      if (bounds.contains(nodePos) && !selections.contains(node)) {
                        selections.add(node);
                      }
                    });
            canvas.setSelectionBox(null, null);
          }
          canvas.draw(floor);
          dragStart = dragCurr = null;
        }
        canvas.getDragEndHandler().handle(event);
      };

  enum Action {
    NONE,
    ADDING_EDGE,
    DELETING_EDGE
  }

  // Setup the Mode
  private Action mode = Action.NONE;

  @FXML
  public void initialize() {
    // Setup map canvas
    canvas = new MapCanvas(false);
    canvas.setDrawAllNodes(true);
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Setup zoom slider hook
    canvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());

    // Set canvas to pan with middle mouse button, drag nodes with left mouse click
    canvas.setDragMapButton(MouseButton.MIDDLE);

    // Setup canvas click handler
    canvas.setOnMouseClicked(this::canvasClicked);

    // Setup canvas drag handlers
    canvas.setOnMousePressed(dragStartHandler);
    canvas.setOnMouseDragged(dragHandler);
    canvas.setOnMouseReleased(dragEndHandler);

    // Set canvas highlights
    selections = new ArrayList<>();
    canvas.setHighlights(selections);

    // Setup zoom slider cursor
    zoomSlider.setCursor(Cursor.H_RESIZE);

    // Setup info pane rippler
    infoRippler = new JFXRippler(infoPane, JFXRippler.RipplerMask.RECT);
    infoRippler.setRipplerFill(Color.web("#607D8B"));
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
          if (mode == Action.NONE) {
            infoDrawer.setMouseTransparent(true);
          } else {
            // Bring the drawer back when people drag it closed when they shouldn't
            infoDrawer.open();
          }
        });

    // Setup button icons
    floorUpButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
    floorDownButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));

    // Try to get graph
    try {
      graph = Graph.getInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Platform.runLater(() -> canvas.draw(floor));
  }

  public void canvasClicked(MouseEvent event) {
    if (lastPressedPos.getX() == event.getX() && lastPressedPos.getY() == event.getY()) {
      return;
    }
    Point2D mousePos = new Point2D(event.getX(), event.getY());
    Point2D mouseGraphPos = canvas.canvasToGraph(mousePos);
    Optional<Node> optionalNode = canvas.getClosestNode(floor, mousePos, 15);

    if (event.getButton() == MouseButton.PRIMARY) {
      if (optionalNode.isPresent()) {
        Node node = optionalNode.get();
        selections.add(node);
      } else {
        selections.clear();
      }
    } else if (event.getButton() == MouseButton.SECONDARY) {
      JFXPopup popup = new JFXPopup();
      VBox buttonBox = new VBox();
      String buttonStyle = "-fx-font-size: 18pt; -fx-background-radius: 0px";

      double popX = event.getX();
      double popY = event.getY();

      if (selections.size() == 0) {
        JFXButton newNodeButton = new JFXButton("New Node");
        newNodeButton.setStyle(buttonStyle);
        newNodeButton.setOnAction(
            e -> {
              popup.hide();
              openNodeModifyDialog(
                  null, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor);
            });
        buttonBox.getChildren().add(newNodeButton);
      } else if (selections.size() == 1) {
        JFXButton infoButton = new JFXButton("Node Info");
        infoButton.setStyle(buttonStyle);
        infoButton.setOnAction(
            e -> {
              buttonBox.getChildren().clear();

              Label infoLabel = new Label(getNodeInfo(selections.get(0)));
              infoLabel.setStyle("-fx-font-size: 18pt");
              buttonBox.getChildren().add(infoLabel);
            });

        JFXButton editButton = new JFXButton("Edit Node");
        editButton.setStyle(buttonStyle);
        editButton.setOnAction(
            e -> {
              popup.hide();
              openNodeModifyDialog(
                  selections.get(0), (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor);
            });

        buttonBox.getChildren().addAll(infoButton, editButton);
      } else if (selections.size() == 2) {
        JFXButton addEdgeButton = new JFXButton("Add Edge");
        addEdgeButton.setStyle(buttonStyle);
        addEdgeButton.setOnAction(
            e -> {
              popup.hide();
              graph.addEdge(selections.get(0), selections.get(1));
              canvas.draw(floor);
            });

        JFXButton deleteEdgeButton = new JFXButton("Delete Edge");
        deleteEdgeButton.setStyle(buttonStyle);
        deleteEdgeButton.setOnAction(
            e -> {
              popup.hide();
              graph.deleteEdge(selections.get(0), selections.get(1));
              canvas.draw(floor);
            });
        buttonBox.getChildren().addAll(addEdgeButton, deleteEdgeButton);
      }

      if (selections.size() >= 1) {
        JFXButton moveButton = new JFXButton("Move " + (selections.size() > 1 ? "Nodes" : "Node"));
        moveButton.setStyle(buttonStyle);
        moveButton.setOnAction(
            e -> {
              popup.hide();
              draggingNodes = true;
            });

        //TODO; Find a way to not delete constrained nodes
        JFXButton deleteButton =
            new JFXButton("Delete " + (selections.size() > 1 ? "Nodes" : "Node"));
        deleteButton.setStyle(buttonStyle);
        deleteButton.setOnAction(
            e -> {
              popup.hide();
              selections.forEach(node -> graph.deleteNode(node));
              canvas.draw(floor);
            });

        buttonBox.getChildren().addAll(deleteButton, moveButton);
      }

      buttonBox
          .widthProperty()
          .addListener(
              observable -> {
                buttonBox
                    .getChildren()
                    .forEach(
                        node -> {
                          if (node instanceof JFXButton) {
                            ((JFXButton) node).setPrefWidth(buttonBox.getWidth());
                          }
                        });
              });
      popup.setPopupContent(buttonBox);
      popup.setHideOnEscape(true);
      popup.show(dialogPane, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, popX, popY);
    }

    canvas.draw(floor);
  }

  private void openNodeModifyDialog(Node node, int x, int y, int floor) {
    String heading = (node == null) ? "Add Node" : "Edit Node";
    NodeDialogController nodeDialogController = new NodeDialogController(node, x, y, floor);
    DialogUtil.complexDialog(
        dialogPane,
        heading,
        "views/NodeModifyPopup.fxml",
        false,
        event -> canvas.draw(floor),
        nodeDialogController);
  }

  @FXML
  public void exportClicked(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Node CSV");

    FileChooser.ExtensionFilter filter =
        new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv");
    fileChooser.getExtensionFilters().add(filter);
    File nodeFile = fileChooser.showSaveDialog(canvas.getScene().getWindow());
    if (nodeFile != null) {
      CSVLoader.exportNodes(graph, nodeFile);
    } else {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Export Error", "No file / invalid file selected for node export");
      return;
    }

    fileChooser.setTitle("Save Edge CSV");
    File edgeFile = fileChooser.showSaveDialog(canvas.getScene().getWindow());
    if (edgeFile != null) {
      CSVLoader.exportEdges(graph, edgeFile);
    } else {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Export Error", "No file / invalid file selected for edge export");
    }
  }

  @FXML
  public void floorUp() {
    floor = Math.min(5, floor + 1);
    canvas.draw(floor);
    floorField.setText(String.valueOf(floor));
  }

  @FXML
  public void floorDown() {
    floor = Math.max(1, floor - 1);
    canvas.draw(floor);
    floorField.setText(String.valueOf(floor));
  }

  private String getNodeInfo(Node node) {
    return "Node ID: "
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
        + node.getShortName();
  }
}
