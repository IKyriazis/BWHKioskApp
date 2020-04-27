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

  @FXML private JFXButton floorUpButton;
  @FXML private JFXButton floorDownButton;
  @FXML private JFXTextField floorField;

  @FXML private AnchorPane infoPane;
  @FXML private JFXDrawer infoDrawer;
  private JFXRippler infoRippler;

  private MapCanvas canvas;
  private Graph graph;
  private int floor = 1;

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
    canvas = new MapCanvas(true, true);
    canvas.setDrawAllNodes(true);
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Setup zoom slider hook
    canvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());

    // Set canvas to pan with middle mouse button, drag nodes with left mouse click
    canvas.setDragMapButton(MouseButton.MIDDLE);
    canvas.setDragNodeButton(MouseButton.PRIMARY);

    // Setup canvas click handler
    canvas.setOnMouseClicked(this::canvasClicked);

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
    Point2D mousePos = new Point2D(event.getX(), event.getY());
    Point2D mouseGraphPos = canvas.canvasToGraph(mousePos);
    Optional<Node> optionalNode = canvas.getClosestNode(floor, mousePos, 15);

    if (event.getButton() == MouseButton.SECONDARY) {
      JFXPopup popup = new JFXPopup();
      VBox buttonBox = new VBox();
      String buttonStyle = "-fx-font-size: 18pt; -fx-background-radius: 0px;";

      double popX = event.getX();
      double popY = event.getY();
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
              canvas.setSelectedNode(null);
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

        JFXButton addEdgeButton = new JFXButton("Add Edge");
        addEdgeButton.setOnAction(
            e -> {
              mode = Action.ADDING_EDGE;
              popup.hide();
            });

        JFXButton deleteEdgeButton = new JFXButton("Delete Edge");
        deleteEdgeButton.setOnAction(
            e -> {
              mode = Action.DELETING_EDGE;
              popup.hide();
            });

        infoButton.setStyle(buttonStyle);
        editButton.setStyle(buttonStyle);
        deleteButton.setStyle(buttonStyle);
        addEdgeButton.setStyle(buttonStyle);
        deleteEdgeButton.setStyle(buttonStyle);

        buttonBox
            .getChildren()
            .addAll(infoButton, editButton, deleteButton, addEdgeButton, deleteEdgeButton);
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
      popup.setHideOnEscape(true);
      popup.setOnHidden(
          e -> {
            if (mode == Action.NONE) {
              canvas.setSelectedNode(null);
              canvas.draw(floor);
              canvas.setDragNodeEnabled(true);
            }
          });
      popup.show(dialogPane, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, popX, popY);

      canvas.setDragNodeEnabled(false);
    } else if (event.getButton() == MouseButton.PRIMARY) {
      if (optionalNode.isPresent()) {
        Node node = optionalNode.get();
        if (mode == Action.ADDING_EDGE) {
          graph.addEdge(canvas.getSelectedNode(), node);
          canvas.setDragNodeEnabled(true);
        } else if (mode == Action.DELETING_EDGE) {
          graph.deleteEdge(canvas.getSelectedNode(), node);
          canvas.setDragNodeEnabled(true);
        }

        mode = Action.NONE;
      }

      canvas.setSelectedNode(null);
    } else {
      canvas.setSelectedNode(null);
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
