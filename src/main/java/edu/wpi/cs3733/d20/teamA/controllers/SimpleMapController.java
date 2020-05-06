package edu.wpi.cs3733.d20.teamA.controllers;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controls.PathLayer;
import edu.wpi.cs3733.d20.teamA.graph.*;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Pair;
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
  @FXML private Pane gluonMapPane;

  @FXML private JFXButton goButton;
  @FXML private JFXButton swapBtn;
  @FXML private JFXButton directionsButton;
  @FXML private JFXButton dirBackButton;
  @FXML private JFXButton dirNextButton;

  @FXML private JFXButton floorUpButton;
  @FXML private JFXButton floorDownButton;
  @FXML private JFXTextField floorField;

  @FXML private JFXRadioButton mainRadioButton;
  @FXML private JFXRadioButton faulknerRadioButton;

  private MapCanvas faulknerCanvas;
  private MapCanvas mainCanvas;
  private MapCanvas currCanvas;
  private MapView gluonMap;

  private Graph graph;
  private String lastDirs;
  private int floor = 1;

  private ArrayList<PathSegment> pathSegments;
  private int currPathSegment = 0;

  private static final String FAULKNER_EXIT_NODE = "ARETL00101";
  private static final String MAIN_EXIT_NODE = "ACONF0010G";

  private static final MapPoint FAULKNER_COORDS = new MapPoint(42.301572, -71.128472);
  private static final MapPoint MAIN_COORDS = new MapPoint(42.335679, -71.106042);

  public void initialize() {
    // Setup gluon map
    PathLayer pathLayer = new PathLayer();
    pathLayer.importPointsFromCSV();

    gluonMap = new MapView();
    gluonMap.addLayer(pathLayer);
    gluonMap.setCenter(new MapPoint(42.3016445, -71.1281649));
    gluonMap.setZoom(16);
    gluonMap.setVisible(false);
    gluonMapPane.getChildren().add(gluonMap);

    directionsDrawer.close();
    textDirectionsDrawer.close();

    // Make canvas occupy the full width / height of its parent anchor pane. Couldn't set in FXML.
    currCanvas = faulknerCanvas = new MapCanvas(true, Campus.FAULKNER);
    mainCanvas = new MapCanvas(true, Campus.MAIN);
    mainCanvas.setVisible(false);

    canvasPane.getChildren().add(0, faulknerCanvas);
    canvasPane.getChildren().add(0, mainCanvas);

    faulknerCanvas.widthProperty().bind(canvasPane.widthProperty());
    faulknerCanvas.heightProperty().bind(canvasPane.heightProperty());

    mainCanvas.widthProperty().bind(canvasPane.widthProperty());
    mainCanvas.heightProperty().bind(canvasPane.heightProperty());

    // Draw background asap
    Platform.runLater(() -> faulknerCanvas.draw(1));

    // Setup zoom slider hook
    faulknerCanvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());
    mainCanvas.getZoomProperty().bindBidirectional(zoomSlider.valueProperty());

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
    // qrCodeButton.setGraphic(new FontIcon(FontAwesomeSolid.QRCODE));
    dirBackButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_LEFT));
    dirNextButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_RIGHT));

    floorUpButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_UP));
    floorDownButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_DOWN));

    // Register event handler to redraw map on tab selection
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();

          currCanvas.clearPath();

          // Redraw map
          currCanvas.draw(floor);
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
    Platform.runLater(() -> currCanvas.draw(floor));
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
    Node start = getSelectedNode(startingLocationBox);
    Node end = getSelectedNode(destinationBox);
    if ((start != null) && (end != null)) {
      ContextPath path = MapSettings.getPath();

      if (start.getCampus() == Campus.MAIN) {
        path.setGraph(Graph.getInstance(Campus.MAIN));
      } else {
        path.setGraph(Graph.getInstance(Campus.FAULKNER));
      }

      pathSegments = new ArrayList<>();
      currPathSegment = 0;

      if (start.getCampus() == end.getCampus()) {
        currCanvas.setVisible(false);

        currCanvas = (start.getCampus() == Campus.MAIN) ? mainCanvas : faulknerCanvas;
        currCanvas.setVisible(true);
        // Path within canvas
        path.findPath(start, end);
        pathSegments.addAll(
            PathSegment.calcPathSegments(
                texDirectionsWithLabels(path.getPathFindingAlgo().textualDirections())));
        currCanvas.setPath(path);
      } else if (start.getCampus() == Campus.FAULKNER && end.getCampus() == Campus.MAIN) {
        // Path to faulkner exit node
        path.findPath(start, Graph.getInstance(Campus.FAULKNER).getNodeByID(FAULKNER_EXIT_NODE));
        pathSegments.addAll(
            PathSegment.calcPathSegments(
                texDirectionsWithLabels(path.getPathFindingAlgo().textualDirections())));
        faulknerCanvas.setPath(path);

        // Insert inter segment
        pathSegments.add(PathSegment.calcInterSegment(Campus.MAIN));

        // Path from main exit node to main dest
        path.findPath(Graph.getInstance(Campus.MAIN).getNodeByID(MAIN_EXIT_NODE), end);
        pathSegments.addAll(
            PathSegment.calcPathSegments(
                texDirectionsWithLabels(path.getPathFindingAlgo().textualDirections())));
        mainCanvas.setPath(path);
      } else if (start.getCampus() == Campus.MAIN && end.getCampus() == Campus.FAULKNER) {
        // Path to faulkner exit node
        path.findPath(start, Graph.getInstance(Campus.MAIN).getNodeByID(MAIN_EXIT_NODE));
        pathSegments.addAll(
            PathSegment.calcPathSegments(
                texDirectionsWithLabels(path.getPathFindingAlgo().textualDirections())));
        faulknerCanvas.setPath(path);

        // Insert inter segment
        pathSegments.add(PathSegment.calcInterSegment(Campus.FAULKNER));

        // Path from main exit node to main dest
        path.findPath(Graph.getInstance(Campus.FAULKNER).getNodeByID(FAULKNER_EXIT_NODE), end);
        pathSegments.addAll(
            PathSegment.calcPathSegments(
                texDirectionsWithLabels(path.getPathFindingAlgo().textualDirections())));
        mainCanvas.setPath(path);
      }

      directionsList.getItems().clear();
      if (path.getPathNodes().size() != 0) {
        updateDisplayedPath();

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
    Node start = getSelectedNode(startingLocationBox);
    Node end = getSelectedNode(destinationBox);

    if ((start != null) && (end != null)) {
      startingLocationBox.setValue(end);
      destinationBox.setValue(start);
    }
  }

  @FXML
  public void toggleDisplayedMap() {
    currCanvas.setVisible(false);
    currCanvas.disablePathAnimation();

    gluonMap.setVisible(false);
    if (mainRadioButton.isSelected()) {
      currCanvas = mainCanvas;
    } else {
      currCanvas = faulknerCanvas;
      if (floor == 6) {
        floor = 5;
        floorField.setText("5");
      }
    }

    currCanvas.setVisible(true);
    currCanvas.enablePathAnimation();
    currCanvas.animatePath(floor);
  }

  public Graph getGraph() {
    return graph;
  }

  public void pressedQRButton() {
    assert false;
    /*if (!lastDirs.isEmpty()) {
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
    }*/
  }

  @FXML
  public void floorUp() {
    floor = Math.min(currCanvas == mainCanvas ? 6 : 5, floor + 1);
    currCanvas.draw(floor);
    floorField.setText(String.valueOf(floor));
    currCanvas.draw(floor);
  }

  @FXML
  public void floorDown() {
    floor = Math.max(1, floor - 1);
    currCanvas.draw(floor);
    floorField.setText(String.valueOf(floor));
    currCanvas.draw(floor);
  }

  @FXML
  public void pressedDirBack() {
    if (currPathSegment > 0) {
      currPathSegment--;
    }

    updateDisplayedPath();
  }

  @FXML
  public void pressedDirNext() {
    currPathSegment = Math.min(currPathSegment + 1, pathSegments.size() - 1);
    updateDisplayedPath();
  }

  public void updateDisplayedPath() {
    PathSegment currSegment = pathSegments.get(currPathSegment);
    // Set canvas
    if (currSegment.getCampus() == Campus.FAULKNER) {
      gluonMap.setVisible(false);

      currCanvas.disablePathAnimation();
      currCanvas.setVisible(false);

      currCanvas = faulknerCanvas;
      currCanvas.setVisible(true);
      currCanvas.enablePathAnimation();
      currCanvas.animatePath(currSegment.getFloor());
    } else if (currSegment.getCampus() == Campus.MAIN) {
      gluonMap.setVisible(false);

      currCanvas.disablePathAnimation();
      currCanvas.setVisible(false);

      currCanvas = mainCanvas;
      currCanvas.setVisible(true);
      currCanvas.enablePathAnimation();
      currCanvas.animatePath(currSegment.getFloor());
    } else if (currSegment.getCampus() == Campus.INTER) {
      currCanvas.setVisible(false);
      currCanvas.disablePathAnimation();

      gluonMap.setVisible(true);
      gluonMap.setZoom(16);
      if (pathSegments.get(currPathSegment - 1).getCampus() == Campus.MAIN) {
        gluonMap.setCenter(MAIN_COORDS);
        PathLayer.setToFaulkner(true);
      } else {
        gluonMap.setCenter(FAULKNER_COORDS);
        PathLayer.setToFaulkner(false);
      }
    }

    // Set floor
    floor = currSegment.getFloor();
    floorField.setText(String.valueOf(floor));

    // Setup labels
    directionsList.getItems().clear();
    directionsList.getItems().addAll(currSegment.getDirections());

    currCanvas.draw(floor);
  }

  public ArrayList<Pair<Node, Label>> texDirectionsWithLabels(
      ArrayList<Pair<Node, String>> textualPath) {
    ArrayList<Pair<Node, Label>> textPath = new ArrayList<>();
    for (int j = 0; j < textualPath.size() - 1; j++) {
      if (textualPath.get(j).getValue().contains("Turn right")) {
        textPath.add(
            new Pair<>(
                textualPath.get(j).getKey(),
                new Label(
                    textualPath.get(j).getValue(),
                    new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT))));
      } else if (textualPath.get(j).getValue().contains("Turn left")) {
        textPath.add(
            new Pair<>(
                textualPath.get(j).getKey(),
                new Label(
                    textualPath.get(j).getValue(),
                    new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_LEFT))));
      } else if (textualPath.get(j).getValue().contains("slight left")) {
        textPath.add(
            new Pair<>(textualPath.get(j).getKey(), new Label(textualPath.get(j).getValue())));
      } else if (textualPath.get(j).getValue().contains("slight right")) {
        textPath.add(
            new Pair<>(textualPath.get(j).getKey(), new Label(textualPath.get(j).getValue())));
      } else if (textualPath.get(j).getValue().contains("up")) {
        textPath.add(
            new Pair<>(
                textualPath.get(j).getKey(),
                new Label(
                    textualPath.get(j).getValue(),
                    new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP))));
      } else if (textualPath.get(j).getValue().contains("down")) {
        textPath.add(
            new Pair<>(
                textualPath.get(j).getKey(),
                new Label(
                    textualPath.get(j).getValue(),
                    new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_DOWN))));

      } else {
        textPath.add(
            new Pair<>(
                textualPath.get(j).getKey(),
                new Label(
                    textualPath.get(j).getValue(),
                    new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_UP))));
      }
    }

    textPath.add(
        new Pair<>(
            textualPath.get(textualPath.size() - 1).getKey(),
            new Label(
                textualPath.get(textualPath.size() - 1).getValue(),
                new FontIcon(FontAwesomeSolid.DOT_CIRCLE))));

    return textPath;
  }
}
