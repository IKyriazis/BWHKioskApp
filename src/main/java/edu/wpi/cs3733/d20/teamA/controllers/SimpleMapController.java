package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.QRDialogController;
import edu.wpi.cs3733.d20.teamA.graph.*;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.ArrayList;
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
import javafx.scene.layout.*;

public class SimpleMapController {
  @FXML private BorderPane rootPane;
  @FXML private JFXDrawer directionsDrawer;
  @FXML private JFXDrawer textDirectionsDrawer;
  @FXML private VBox directionsBox;
  @FXML private JFXComboBox<Node> startingLocationBox;
  @FXML private JFXComboBox<Node> destinationBox;
  @FXML private AnchorPane canvasPane;
  @FXML private JFXSlider zoomSlider;
  @FXML private JFXListView<Label> directionsList;
  @FXML private GridPane directionsPane;
  @FXML private StackPane dialogPane;

  @FXML private JFXButton goButton;
  @FXML private JFXButton swapBtn;
  @FXML private JFXButton directionsButton;
  @FXML private JFXButton qrCodeButton;

  @FXML private JFXButton floorUpButton;
  @FXML private JFXButton floorDownButton;
  @FXML private JFXTextField floorField;

  private MapCanvas canvas;
  private Graph graph;
  private String lastDirs;
  private int floor = 1;

  private ObservableList<Node> allNodeList;

  public void initialize() {
    directionsDrawer.close();
    textDirectionsDrawer.close();

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

    // Setup text directions drawer
    textDirectionsDrawer.setSidePane(directionsPane);
    textDirectionsDrawer.setOnDrawerClosed(event -> textDirectionsDrawer.setMouseTransparent(true));
    textDirectionsDrawer.setOnDrawerOpened(
        event -> textDirectionsDrawer.setMouseTransparent(false));

    // Set button icons
    goButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCATION_ARROW));
    swapBtn.setGraphic(new FontAwesomeIconView((FontAwesomeIcon.EXCHANGE)));
    directionsButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MAP_SIGNS));
    qrCodeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QRCODE));

    floorUpButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
    floorDownButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));

    // Register event handler to redraw map on tab selection
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();

          // Try to update path if possible
          canvas.getPath().update();
          if (canvas.getPath().getPathNodes().isEmpty()) {
            pressedGo();
          }

          // Redraw map
          canvas.draw(floor);
        });

    try {
      // Load graph info
      graph = Graph.getInstance();

      allNodeList =
          FXCollections.observableArrayList(
              graph.getNodes().values().stream()
                  .filter(node -> node.getType() != NodeType.HALL)
                  .collect(Collectors.toList()));
      allNodeList.sort(Comparator.comparing(o -> o.getLongName().toLowerCase()));

      InvalidationListener focusListener =
          observable -> {
            allNodeList.clear();
            allNodeList.addAll(
                FXCollections.observableArrayList(
                    graph.getNodes().values().stream()
                        .filter(node -> node.getType() != NodeType.HALL)
                        .collect(Collectors.toList())));
            allNodeList.sort(Comparator.comparing(o -> o.getLongName().toLowerCase()));
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
    if (!MapSettings.isSetup()) {
      MapSettings.setup();
    }
    Platform.runLater(() -> canvas.draw(floor));
  }

  @FXML
  public void toggleSearch() {
    directionsDrawer.toggle();
    if (textDirectionsDrawer.isOpened()) {
      textDirectionsDrawer.toggle();
    }
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
      ContextPath path = MapSettings.getPath();
      path.findPath(start.get(), end.get());
      canvas.setPath(path);

      if (start.get().getFloor() != floor) {
        floor = Math.min(5, start.get().getFloor());
        floorField.setText(String.valueOf(floor));
      }

      canvas.draw(floor);

      directionsList.getItems().clear();
      if (path.getPathNodes().size() != 0) {
        ArrayList<Label> directions = path.getPathFindingAlgo().textualDirections();
        directions.forEach(
            l -> {
              directionsList.getItems().add(l);
            });

        // Generate QR code
        StringBuilder dirs = new StringBuilder();
        directions.forEach(l -> dirs.append(l.getText()).append('\n'));
        lastDirs = dirs.toString();

        if (textDirectionsDrawer.isClosed()) {
          textDirectionsDrawer.open();
        }
      } else {
        DialogUtil.simpleInfoDialog(
            dialogPane,
            "No Path Found",
            "No path between the selected locations could be found. Try choosing different locations.");

        if (textDirectionsDrawer.isOpened()) {
          textDirectionsDrawer.close();
        }
      }
    }
  }

  @FXML
  public void pressedSwap() {
    Optional<Node> start =
        startingLocationBox.getItems().stream()
            .filter(node -> node.toString().contains(startingLocationBox.getEditor().getText()))
            .findFirst();
    Optional<Node> end =
        destinationBox.getItems().stream()
            .filter(node -> node.toString().contains(destinationBox.getEditor().getText()))
            .findFirst();

    if (start.isPresent() && end.isPresent()) {
      startingLocationBox.setValue(end.get());
      destinationBox.setValue(start.get());
    }
  }

  public MapCanvas getCanvas() {
    return canvas;
  }

  public Graph getGraph() {
    return graph;
  }

  public void pressedQRButton() {
    if (!lastDirs.isEmpty()) {
      DialogUtil.complexDialog(
          dialogPane,
          "Direction QR Code",
          "views/QRCodePopup.fxml",
          true,
          null,
          new QRDialogController(lastDirs));
    } else {
      DialogUtil.simpleInfoDialog(
          dialogPane, "No Directions", "Cannot generate a QR code from empty directions");
    }
  }

  @FXML
  public void floorUp() {
    floor = Math.min(5, floor + 1);
    canvas.draw(floor);
    floorField.setText(String.valueOf(floor));
    canvas.draw(floor);
  }

  @FXML
  public void floorDown() {
    floor = Math.max(1, floor - 1);
    canvas.draw(floor);
    floorField.setText(String.valueOf(floor));
    canvas.draw(floor);
  }
}
