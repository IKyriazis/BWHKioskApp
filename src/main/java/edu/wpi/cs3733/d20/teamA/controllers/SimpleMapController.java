package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.QRDialogController;
import edu.wpi.cs3733.d20.teamA.graph.*;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.ArrayList;
import java.util.Optional;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SimpleMapController extends AbstractController {
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
    canvas = new MapCanvas(true, Campus.FAULKNER);
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
    goButton.setGraphic(new FontIcon(FontAwesomeSolid.LOCATION_ARROW));
    swapBtn.setGraphic(new FontIcon((FontAwesomeSolid.EXCHANGE_ALT)));
    directionsButton.setGraphic(new FontIcon(FontAwesomeSolid.MAP_SIGNS));
    qrCodeButton.setGraphic(new FontIcon(FontAwesomeSolid.QRCODE));

    floorUpButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_UP));
    floorDownButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_DOWN));

    // Register event handler to redraw map on tab selection
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();

          if (canvas.getPath() != null) {
            // Try to update path if possible
            canvas.getPath().update();
            if (canvas.getPath().getPathNodes().isEmpty()) {
              pressedGo();
            }

            // Redraw map
            canvas.draw(floor);
          }
        });

    try {
      // Load graph info
      graph = Graph.getInstance(Campus.FAULKNER);

      setupNodeBox(startingLocationBox, destinationBox);
      setupNodeBox(destinationBox, goButton);
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

      if (!canvas.getGroup().getChildren().isEmpty()) {
        canvas.getGroup().getChildren().clear();
        canvas.setGroup(new Group());
        canvas.setTransition(new PathTransition());
      }
      canvas.draw(floor);

      directionsList.getItems().clear();
      if (path.getPathNodes().size() != 0) {
        ArrayList<Label> directions =
            texDirectionsWithLabels(path.getPathFindingAlgo().textualDirections());
        directions.forEach(
            l -> {
              directionsList.getItems().add(l);
            });

        canvasPane.getChildren().add(canvas.getGroup());

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
    if (!canvas.getGroup().getChildren().isEmpty()) {
      canvas.getGroup().getChildren().clear();
      canvas.setGroup(new Group());
      canvas.setTransition(new PathTransition());
    }
    canvas.draw(floor);
    canvasPane.getChildren().add(canvas.getGroup());
  }

  @FXML
  public void floorDown() {
    floor = Math.max(1, floor - 1);
    canvas.draw(floor);
    floorField.setText(String.valueOf(floor));
    if (!canvas.getGroup().getChildren().isEmpty()) {
      canvas.getGroup().getChildren().clear();
      canvas.setGroup(new Group());
      canvas.setTransition(new PathTransition());
    }
    canvas.draw(floor);
    canvasPane.getChildren().add(canvas.getGroup());
  }

  public ArrayList<Label> texDirectionsWithLabels(ArrayList<String> textualPath) {
    ArrayList<Label> textPath = new ArrayList<>();
    for (int j = 0; j < textualPath.size() - 1; j++) {
      if (textualPath.get(j).contains("Turn right")) {
        textPath.add(
            new Label(textualPath.get(j), new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT)));
      } else if (textualPath.get(j).contains("Turn left")) {
        textPath.add(
            new Label(textualPath.get(j), new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_LEFT)));
      } else if (textualPath.get(j).contains("slight left")) {
        textPath.add(new Label(textualPath.get(j)));
      } else if (textualPath.get(j).contains("slight right")) {
        textPath.add(new Label(textualPath.get(j)));
      } else if (textualPath.get(j).contains("up")) {
        textPath.add(new Label(textualPath.get(j), new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP)));
      } else if (textualPath.get(j).contains("down")) {
        textPath.add(
            new Label(textualPath.get(j), new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_DOWN)));

      } else {
        textPath.add(
            new Label(textualPath.get(j), new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_UP)));
      }
    }

    textPath.add(
        new Label(
            textualPath.get(textualPath.size() - 1), new FontIcon(FontAwesomeSolid.DOT_CIRCLE)));

    return textPath;
  }
}
