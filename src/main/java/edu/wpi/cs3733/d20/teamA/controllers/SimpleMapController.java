package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.graph.Path;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SimpleMapController {
  @FXML private BorderPane rootPane;
  @FXML private JFXDrawer directionsDrawer;
  @FXML private VBox directionsBox;
  @FXML private JFXComboBox<Node> startingLocationBox;
  @FXML private JFXComboBox<Node> destinationBox;
  @FXML private AnchorPane canvasPane;
  @FXML private Label textualDirectionsLabel;
  @FXML private JFXSlider zoomSlider;

  @FXML private JFXButton goButton;
  @FXML private JFXButton directionsButton;

  @FXML private JFXRadioButton drawPathButton;
  @FXML private JFXRadioButton drawAllNodesButton;

  private MapCanvas canvas;
  private Graph graph;

  private ObservableList<Node> allNodeList;

  public void initialize() {
    directionsDrawer.close();

    // Make canvas occupy the full width / height of its parent anchor pane. Couldn't set in FXML.
    canvas = new MapCanvas(true);
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Draw background asap
    Platform.runLater(() -> canvas.draw(1));

    // Setup zoom slider hook
    canvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());

    // Setup zoom slider cursor
    zoomSlider.setCursor(Cursor.H_RESIZE);

    // Setup directions drawer
    directionsDrawer.setSidePane(directionsBox);
    directionsDrawer.setOnDrawerClosed(event -> directionsDrawer.setMouseTransparent(true));
    directionsDrawer.setOnDrawerOpened(event -> directionsDrawer.setMouseTransparent(false));

    // Set button icons
    goButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCATION_ARROW));
    directionsButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MAP_SIGNS));

    // Setup radio buttons
    drawPathButton.setOnAction(
        event -> {
          drawAllNodesButton.setSelected(false);
          canvas.setDrawAllNodes(false);
          canvas.draw(1);
        });
    drawAllNodesButton.setOnAction(
        event -> {
          drawPathButton.setSelected(false);
          canvas.setDrawAllNodes(true);
          canvas.draw(1);
        });

    // Register event handler to redraw map on tab selection
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          canvas.draw(1);
        });

    try {
      // Load graph info
      graph = Graph.getInstance();

      allNodeList =
          FXCollections.observableArrayList(
              graph.getNodes().values().stream()
                  .filter(node -> node.getFloor() == 1)
                  .collect(Collectors.toList()));
      allNodeList.sort(Comparator.comparing(Node::getLongName));

      InvalidationListener focusListener =
          observable -> {
            allNodeList.clear();
            allNodeList.addAll(
                FXCollections.observableArrayList(
                    graph.getNodes().values().stream()
                        .filter(node -> node.getFloor() == 1)
                        .collect(Collectors.toList())));
            allNodeList.sort(Comparator.comparing(Node::getLongName));
            startingLocationBox.setItems(allNodeList);
            destinationBox.setItems(allNodeList);
            startingLocationBox.setVisibleRowCount(12);
            destinationBox.setVisibleRowCount(12);
          };
      startingLocationBox.setItems(allNodeList);
      startingLocationBox.focusedProperty().addListener(focusListener);
      startingLocationBox
          .getEditor()
          .setOnKeyTyped(
              new NodeAutoCompleteHandler(startingLocationBox, destinationBox, allNodeList));

      destinationBox.setItems(allNodeList);
      destinationBox.focusedProperty().addListener(focusListener);
      destinationBox
          .getEditor()
          .setOnKeyTyped(new NodeAutoCompleteHandler(destinationBox, goButton, allNodeList));
    } catch (Exception e) {
      e.printStackTrace();

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Unable to load map");
      alert.setContentText(e.toString());
      alert.show();
    }
  }

  @FXML
  public void toggleSearch() {
    directionsDrawer.toggle();
  }

  @FXML
  public void pressedGo() {
    Optional<Node> start =
        startingLocationBox.getItems().stream()
            .filter(node -> node.toString().contains(startingLocationBox.getEditor().getText()))
            .findFirst();
    Optional<Node> end =
        destinationBox.getItems().stream()
            .filter(node -> node.toString().contains(destinationBox.getEditor().getText()))
            .findFirst();
    if (start.isPresent() && end.isPresent()) {
      Path path = new Path(graph);
      path.findPath(start.get(), end.get());
      canvas.setPath(path);
      canvas.draw(1);
    }
  }
}
