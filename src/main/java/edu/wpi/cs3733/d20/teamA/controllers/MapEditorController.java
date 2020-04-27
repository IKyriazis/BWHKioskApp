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
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

  @FXML private JFXButton panMapButton;
  @FXML private JFXButton editNodeButton;
  @FXML private JFXButton editEdgeButton;
  @FXML private JFXButton floorUpButton;
  @FXML private JFXButton floorDownButton;
  @FXML private JFXTextField floorField;

  @FXML private AnchorPane infoPane;
  @FXML private JFXDrawer infoDrawer;
  private JFXRippler infoRippler;

  private MapCanvas canvas;
  private Graph graph;
  private int floor = 1;

  enum Mode {
    NONE,
    NODE,
    EDGE
  }

  EventHandler<MouseEvent> nodeClickHandler =
      event -> {
        Point2D mousePos = new Point2D(event.getX(), event.getY());
        Point2D mouseGraphPos = canvas.canvasToGraph(mousePos);

        double popX = event.getX();
        double popY = event.getY();
        if (event.getButton() == MouseButton.SECONDARY) {
          JFXPopup popup = new JFXPopup();
          VBox buttonBox = new VBox();
          String buttonStyle = "-fx-font-size: 18pt; -fx-background-radius: 0px;";

          Optional<Node> optionalNode = canvas.getClosestNode(floor, mousePos, 15);
          if (optionalNode.isPresent()) {
            Node node = optionalNode.get();
            Point2D nodeCanvasPos = canvas.graphToCanvas(new Point2D(node.getX(), node.getY()));
            popX = nodeCanvasPos.getX();
            popY = nodeCanvasPos.getY();

            canvas.setSelectedNode(node);

            JFXButton infoButton = new JFXButton("Node Info");
            infoButton.setOnAction(
                e -> {
                  buttonBox.getChildren().clear();

                  Label infoLabel = new Label(getNodeInfo(node));
                  infoLabel.setStyle("-fx-font-size: 18pt");
                  buttonBox.getChildren().add(infoLabel);
                });

            JFXButton editButton = new JFXButton("Edit Node");
            editButton.setOnAction(
                e -> {
                  popup.hide();
                  openNodeModifyDialog(
                      node, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor);
                });

            JFXButton deleteButton = new JFXButton("Delete Node");
            deleteButton.setOnAction(
                e -> {
                  graph.deleteNode(node);
                  canvas.draw(floor);
                  popup.hide();
                });

            infoButton.setStyle(buttonStyle);
            editButton.setStyle(buttonStyle);
            deleteButton.setStyle(buttonStyle);

            buttonBox.getChildren().addAll(infoButton, editButton, deleteButton);
          } else {
            JFXButton newButton = new JFXButton("New Node");
            newButton.setStyle(buttonStyle);
            newButton.setOnAction(
                e -> {
                  popup.hide();
                  openNodeModifyDialog(
                      null, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor);
                });

            buttonBox.getChildren().add(newButton);
            canvas.setSelectedNode(null);
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
          popup.show(
              dialogPane, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, popX, popY);
        } else {
          canvas.setSelectedNode(null);
        }

        canvas.draw(floor);
      };

  // Setup the Mode
  private Mode mode = Mode.NONE;

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
          if (mode == Mode.NONE) {
            infoDrawer.setMouseTransparent(true);
          } else {
            // Bring the drawer back when people drag it closed when they shouldn't
            infoDrawer.open();
          }
        });

    // Setup button icons
    panMapButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROWS_ALT));
    editNodeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
    editEdgeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROWS_H));
    floorUpButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
    floorDownButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));

    // Setup tool button press handler
    panMapButton.setOnAction(this::toolPressed);
    editNodeButton.setOnAction(this::toolPressed);
    editEdgeButton.setOnAction(this::toolPressed);

    // Try to get graph
    try {
      graph = Graph.getInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Platform.runLater(() -> canvas.draw(floor));
  }

  public void toolPressed(ActionEvent event) {
    Mode startMode = mode;

    if (event.getTarget().equals(panMapButton)) {
      mode = Mode.NONE;
      canvas.setDragEnabled(true);
      infoDrawer.close();
      canvas.setOnMouseClicked(null);
    } else if (event.getTarget().equals(editNodeButton)) {
      mode = Mode.NODE;
      canvas.setOnMouseClicked(nodeClickHandler);
    } else if (event.getTarget().equals(editEdgeButton)) {
      mode = Mode.EDGE;
      canvas.setOnMouseClicked(null);
    }

    if (!event.getTarget().equals(panMapButton)) {
      canvas.setDragEnabled(false);
    }

    if (!event.getTarget().equals(panMapButton)) {
      infoDrawer.open();
    }

    if (mode != startMode) {
      canvas.setSelectedNode(null);
      canvas.draw(floor);
    }
  }

  public void canvasClicked(MouseEvent mouse) {
    /*Point2D mousePos = new Point2D(mouse.getX(), mouse.getY());
    Point2D mouseGraphPos = canvas.canvasToGraph(mousePos);
    Optional<Node> optionalNode = canvas.getClosestNode(floor, mousePos, 15);
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
        openNodeModifyDialog(null, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor);
        break;
      case EDIT_NODE:
        optionalNode.ifPresent(
            node ->
                openNodeModifyDialog(
                    node, (int) mouseGraphPos.getX(), (int) mouseGraphPos.getY(), floor));
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
          editorTipLabel.setText("Select Second Node");
        } else {
          optionalNode.ifPresent(
              node -> {
                try {
                  graph.addEdge(lastSelected, node);
                } catch (Exception e) {
                  e.printStackTrace();
                }
                canvas.setSelectedNode(null);
                editorTipLabel.setText("Select First Node");
              });
        }
        break;
      case DELETE_EDGE:
        if (lastSelected == null) {
          optionalNode.ifPresent(canvas::setSelectedNode);
          editorTipLabel.setText("Select Second Node");
        } else {
          optionalNode.ifPresent(
              node -> {
                try {
                  graph.deleteEdge(lastSelected, node);
                } catch (Exception e) {
                  e.printStackTrace();
                }
                canvas.setSelectedNode(null);
                editorTipLabel.setText("Select First Node");
              });
        }
        break;
      default:
        break;
    }*/

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
