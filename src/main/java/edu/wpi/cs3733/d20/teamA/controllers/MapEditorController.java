package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.DialogMaker;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.NodeDialogController;
import edu.wpi.cs3733.d20.teamA.graph.*;
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
import javafx.geometry.Insets;
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
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MapEditorController {
  @FXML private AnchorPane canvasPane;
  @FXML private StackPane dialogPane;
  @FXML private JFXSlider zoomSlider;
  @FXML private Label editorTipLabel;

  @FXML private JFXButton floorUpButton;
  @FXML private JFXButton floorDownButton;
  @FXML private JFXButton exportCSVButton;
  @FXML private JFXButton helpButton;
  @FXML private JFXTextField floorField;
  @FXML private JFXRadioButton mainRadioButton;

  @FXML private AnchorPane infoPane;
  @FXML private JFXDrawer infoDrawer;
  private JFXRippler infoRippler;

  private ArrayList<Node> selections;

  private MapCanvas mainCanvas;
  private MapCanvas faulknerCanvas;
  private MapCanvas currCanvas;

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
        currCanvas.getDragStartHandler().handle(event);
      };

  private final EventHandler<MouseEvent> dragHandler =
      event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
          dragCurr = new Point2D(event.getX(), event.getY());
          if (draggingNodes) {
            currCanvas.setHighlightOffset(dragCurr.subtract(dragStart));
          } else {
            currCanvas.setSelectionBox(dragStart, dragCurr);
          }

          currCanvas.draw(floor);
        }

        currCanvas.getDragHandler().handle(event);
      };

  private final EventHandler<MouseEvent> dragEndHandler =
      event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
          if (dragCurr == null) {
            return;
          }

          if (draggingNodes) {
            Point2D offset =
                currCanvas.canvasToGraph(dragCurr).subtract(currCanvas.canvasToGraph(dragStart));
            selections.forEach(
                node -> {
                  graph.moveNode(
                      node,
                      (int) (node.getX() + offset.getX()),
                      (int) (node.getY() + offset.getY()));
                });

            updateTipLabel();
            currCanvas.setHighlightOffset(null);
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
                      Point2D nodePos =
                          currCanvas.graphToCanvas(new Point2D(node.getX(), node.getY()));
                      if (bounds.contains(nodePos) && !selections.contains(node)) {
                        selections.add(node);
                      }
                    });
            currCanvas.setSelectionBox(null, null);
          }
          updateTipLabel();
          currCanvas.draw(floor);
          dragStart = dragCurr = null;
        }
        currCanvas.getDragEndHandler().handle(event);
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
    // Setup highlights
    selections = new ArrayList<>();

    // Setup map canvas'
    mainCanvas = new MapCanvas(false, Campus.MAIN);
    currCanvas = mainCanvas;
    setupCanvas(mainCanvas);

    faulknerCanvas = new MapCanvas(false, Campus.FAULKNER);
    faulknerCanvas.setVisible(false);
    setupCanvas(faulknerCanvas);

    // Setup zoom slider cursor
    zoomSlider.setCursor(Cursor.H_RESIZE);

    // Setup info pane rippler
    infoRippler = new JFXRippler(infoPane, JFXRippler.RipplerMask.RECT);
    infoRippler.setRipplerFill(Color.web("#9E9E9E"));
    infoRippler.setEnabled(true);

    // Trigger drawer open & ripple when label is updated
    editorTipLabel
        .textProperty()
        .addListener(
            observable -> {
              Platform.runLater(infoRippler.createManualRipple());
            });

    // Setup info drawer
    infoDrawer.setSidePane(infoRippler);

    // Fix info drawer height to info rippler height
    infoRippler
        .widthProperty()
        .addListener(
            observable -> {
              infoDrawer.setPrefHeight(infoRippler.getHeight());
            });

    // Setup tip label
    updateTipLabel();

    // Set up drawer transparency hooks
    infoDrawer.setOnDrawerOpened(
        event -> {
          infoDrawer.setMouseTransparent(false);
          infoDrawer.setVisible(true);
        });
    infoDrawer.setOnDrawerClosed(
        event -> {
          if (mode == Action.NONE) {
            infoDrawer.setMouseTransparent(true);
            infoDrawer.setVisible(false);
          } else {
            // Bring the drawer back when people drag it closed when they shouldn't
            infoDrawer.open();
          }
        });

    // Setup button icons
    floorUpButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_UP));
    floorDownButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_DOWN));
    exportCSVButton.setGraphic(new FontIcon(FontAwesomeSolid.SAVE));
    helpButton.setGraphic(new FontIcon(FontAwesomeSolid.QUESTION_CIRCLE));

    // Try to get graph
    try {
      graph = Graph.getInstance(Campus.MAIN);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String c;
    if (currCanvas == faulknerCanvas) {
      c = "Faulkner";
    } else {
      c = "MAIN";
    }
    floorField.setText(graph.getFloorString(floor, c));

    Platform.runLater(() -> currCanvas.draw(floor));
  }

  public void canvasClicked(MouseEvent event) {
    if (lastPressedPos.getX() == event.getX() && lastPressedPos.getY() == event.getY()) {
      return;
    }
    Point2D mousePos = new Point2D(event.getX(), event.getY());
    Point2D mouseGraphPos = currCanvas.canvasToGraph(mousePos);
    Optional<Node> optionalNode = currCanvas.getClosestNode(floor, mousePos, 15);

    if (event.getButton() == MouseButton.PRIMARY) {
      if (optionalNode.isPresent()) {
        Node node = optionalNode.get();
        if (!selections.contains(node)) {
          selections.add(node);
        }
      } else {
        selections.clear();
      }
    } else if (event.getButton() == MouseButton.SECONDARY) {
      JFXPopup popup = new JFXPopup();
      VBox buttonBox = new VBox();

      double popX = event.getX();
      double popY = event.getY();

      if (selections.size() == 0) {
        JFXButton newNodeButton = new JFXButton("New Node");
        newNodeButton.getStyleClass().add("popup-button");
        newNodeButton.setOnAction(
            e -> {
              popup.hide();
              openNodeModifyDialog(
                  null, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor);
            });
        buttonBox.getChildren().add(newNodeButton);
      } else if (selections.size() == 1) {
        JFXButton infoButton = new JFXButton("Node Info");
        infoButton.getStyleClass().add("popup-button");
        infoButton.setOnAction(
            e -> {
              buttonBox.getChildren().clear();

              Label infoLabel = new Label(getNodeInfo(selections.get(0)));
              infoLabel.setStyle("-fx-font-size: 18pt");
              infoLabel.setPadding(new Insets(5, 5, 5, 5));
              buttonBox.getChildren().add(infoLabel);
            });

        JFXButton editButton = new JFXButton("Edit Node");
        editButton.getStyleClass().add("popup-button");
        editButton.setOnAction(
            e -> {
              popup.hide();
              openNodeModifyDialog(
                  selections.get(0), (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor);
            });

        buttonBox.getChildren().addAll(infoButton, editButton);
      } else if (selections.size() == 2) {
        JFXButton addEdgeButton = new JFXButton("Add Edge");
        addEdgeButton.getStyleClass().add("popup-button");
        addEdgeButton.setOnAction(
            e -> {
              popup.hide();
              graph.addEdge(selections.get(0), selections.get(1));
              currCanvas.draw(floor);
            });

        JFXButton deleteEdgeButton = new JFXButton("Delete Edge");
        deleteEdgeButton.getStyleClass().add("popup-button");
        deleteEdgeButton.setOnAction(
            e -> {
              popup.hide();
              graph.deleteEdge(selections.get(0), selections.get(1));
              currCanvas.draw(floor);
            });
        buttonBox.getChildren().addAll(addEdgeButton, deleteEdgeButton);
      }

      if (selections.size() >= 1) {
        JFXButton moveButton = new JFXButton("Move " + (selections.size() > 1 ? "Nodes" : "Node"));
        moveButton.getStyleClass().add("popup-button");
        moveButton.setOnAction(
            e -> {
              popup.hide();
              draggingNodes = true;
              editorTipLabel.setText("Click and Drag to Move Selection");
            });

        JFXButton deleteButton =
            new JFXButton("Delete " + (selections.size() > 1 ? "Nodes" : "Node"));
        deleteButton.getStyleClass().add("popup-button");
        deleteButton.setOnAction(
            e -> {
              popup.hide();
              selections.forEach(
                  node -> {
                    boolean success = graph.deleteNode(node);
                    if (!success) {
                      DialogUtil.simpleErrorDialog(
                          dialogPane,
                          "Database Error",
                          "Failed to delete node "
                              + node.getNodeID()
                              + ", likely due to indeterminate service constraints. Please try again later.");
                    }
                  });
              currCanvas.draw(floor);
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

    updateTipLabel();
    currCanvas.draw(floor);
  }

  public void updateTipLabel() {
    String info;
    if (selections.size() == 0) {
      info =
          "Middle Click to Pan\nClick on Node / Click & Drag to Select\nClick Blank Area to Deselect\nRight Click for New Node";
    } else if (selections.size() == 1) {
      info =
          "Middle Click to Pan\nClick on Node / Click & Drag to Select\nClick Blank Area to Deselect\nRight Click for Info/Move/Edit/Delete";
    } else if (selections.size() == 2) {
      info =
          "Middle Click to Pan\nClick on Node / Click & Drag to Select\nClick Blank Area to Deselect\nRight Click for Move/Delete/Edge";
    } else {
      info =
          "Middle Click to Pan\nClick on Node / Click & Drag to Select\nClick Blank Area to Deselect\nRight Click for Move/Delete";
    }
    editorTipLabel.setText(info);
  }

  private void openNodeModifyDialog(Node node, int x, int y, int floor) {
    String heading = (node == null) ? "Add Node" : "Edit Node";
    NodeDialogController nodeDialogController =
        new NodeDialogController(
            currCanvas == mainCanvas ? Campus.MAIN : Campus.FAULKNER, node, x, y, floor);

    DialogMaker maker = new DialogMaker();
    maker.makeNodeDialog(
        currCanvas == mainCanvas ? Campus.MAIN : Campus.FAULKNER,
        node,
        x,
        y,
        floor,
        currCanvas,
        dialogPane);

    /* DialogUtil.complexDialog(
        dialogPane,
        heading,
        "views/NodeModifyPopup.fxml",
        false,
        event -> currCanvas.draw(floor),
        nodeDialogController);

    */
  }

  @FXML
  public void exportClicked(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    if (graph.getCampus().equals(Campus.FAULKNER)) {
      fileChooser.setTitle("Save Faulkner Node CSV");
    } else {
      fileChooser.setTitle("Save Main Campus Node CSV");
    }
    FileChooser.ExtensionFilter filter =
        new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv");
    fileChooser.getExtensionFilters().add(filter);
    File nodeFile = fileChooser.showSaveDialog(currCanvas.getScene().getWindow());
    if (nodeFile != null) {
      CSVLoader.exportNodes(graph, nodeFile);
    } else {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Export Error", "No file / invalid file selected for node export");
      return;
    }

    if (graph.getCampus().equals(Campus.FAULKNER)) {
      fileChooser.setTitle("Save Faulkner Edge CSV");
    } else {
      fileChooser.setTitle("Save Main Campus Edge CSV");
    }
    File edgeFile = fileChooser.showSaveDialog(currCanvas.getScene().getWindow());
    if (edgeFile != null) {
      CSVLoader.exportEdges(graph, edgeFile);
    } else {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Export Error", "No file / invalid file selected for edge export");
    }
  }

  @FXML
  public void floorUp() {
    floor = Math.min(currCanvas == mainCanvas ? 6 : 5, floor + 1);
    currCanvas.draw(floor);
    String c;
    if (currCanvas == faulknerCanvas) {
      c = "Faulkner";
    } else {
      c = "MAIN";
    }
    floorField.setText(graph.getFloorString(floor, c));
  }

  @FXML
  public void floorDown() {
    floor = Math.max(1, floor - 1);
    currCanvas.draw(floor);
    String c;
    if (currCanvas == faulknerCanvas) {
      c = "Faulkner";
    } else {
      c = "MAIN";
    }
    floorField.setText(graph.getFloorString(floor, c));
  }

  @FXML
  public void toggleDrawBelow(ActionEvent event) {
    currCanvas.setDrawBelow(((JFXCheckBox) event.getSource()).isSelected());
    currCanvas.draw(floor);
  }

  @FXML
  public void helpClicked(ActionEvent actionEvent) {
    if (infoDrawer.isClosed()) {
      infoDrawer.setVisible(true);
    }
    infoDrawer.toggle();
  }

  public void setupCanvas(MapCanvas canvas) {
    canvas.setDrawAllNodes(true);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());
    canvasPane.getChildren().add(0, canvas);

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
    canvas.setHighlights(selections);
  }

  @FXML
  public void toggleDisplayedMap() {
    MapCanvas newCanvas = mainRadioButton.isSelected() ? mainCanvas : faulknerCanvas;
    graph =
        mainRadioButton.isSelected()
            ? Graph.getInstance(Campus.MAIN)
            : Graph.getInstance(Campus.FAULKNER);
    if (newCanvas != currCanvas) {
      currCanvas.setVisible(false);
      newCanvas.setVisible(true);
      String c;
      if (newCanvas == faulknerCanvas) {
        c = "Faulkner";
      } else {
        c = "MAIN";
      }
      floor = 1;
      floorField.setText(graph.getFloorString(floor, c));
      currCanvas = newCanvas;
      currCanvas.draw(floor);

      selections.clear();
    }
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
