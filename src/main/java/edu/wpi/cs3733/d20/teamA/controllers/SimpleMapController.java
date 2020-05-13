package edu.wpi.cs3733.d20.teamA.controllers;

import animatefx.animation.FadeInLeft;
import animatefx.animation.FadeOutLeft;
import com.google.common.io.Resources;
import com.jfoenix.controls.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.graph.*;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.icomoon.Icomoon;
import org.kordamp.ikonli.javafx.FontIcon;

@SuppressWarnings("DuplicatedCode")
public class SimpleMapController extends AbstractController {
  @FXML private BorderPane rootPane;
  @FXML private VBox directionsBox;
  @FXML private JFXComboBox<Node> startingLocationBox;
  @FXML private JFXComboBox<Node> destinationBox;
  @FXML private AnchorPane canvasPane;
  @FXML private JFXSlider zoomSlider;
  @FXML private JFXListView<Label> directionsList;
  @FXML private GridPane directionsPane;
  @FXML private StackPane dialogPane;
  @FXML private Pane gluonMapPane;
  @FXML private HBox buttonBox;
  @FXML private VBox floorBox;

  @FXML private JFXButton goButton;
  @FXML private JFXButton swapBtn;
  @FXML private JFXButton directionsButton;
  @FXML private JFXButton dirBackButton;
  @FXML private JFXButton dirNextButton;
  @FXML private JFXButton directionListButton;

  @FXML private JFXButton floorUpButton;
  @FXML private JFXButton floorDownButton;
  @FXML private JFXTextField floorField;

  @FXML private JFXTextField phoneNumberField;

  @FXML private JFXRadioButton faulknerRadioButton;
  @FXML private JFXRadioButton mainRadioButton;

  private MapCanvas faulknerCanvas;
  private MapCanvas mainCanvas;
  private MapCanvas currCanvas;
  private WebView gMapView;

  private Graph graph;
  private String lastDirs;
  private int floor = 1;

  private ArrayList<PathSegment> pathSegments;
  private int currPathSegment = 0;
  private boolean displayingPathOverview;

  private static final String FAULKNER_EXIT_NODE = "MHALL00342";
  private static final String MAIN_EXIT_NODE = "AEXIT0010G";
  private static final String ACCOUNT_SID = "AC809fda5395cc3a459bc3555e6477ba72";
  private static final String AUTH_TOKEN = "215f4ff302e84eccd3e7838c51d86506";

  private final ArrayList<Node> highlights;

  private ChangeListener<Label> listChangeListener =
      (observable, oldVal, newVal) -> {
        selectedDirection();
      };

  public SimpleMapController() {
    highlights = new ArrayList<>();
    highlights.add(null);
    highlights.add(null);
  }

  public void initialize() {
    try {
      gMapView = new WebView();
      gMapView
          .getEngine()
          .loadContent(
              Resources.toString(
                  App.class.getResource("html/tomain.html"), StandardCharsets.UTF_8));
      gMapView.setVisible(false);
      gluonMapPane.getChildren().add(gMapView);
    } catch (Exception e) {
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Map Load Error",
          "Failed to load bing map. Directions between hospitals will be unavailable.");
      e.printStackTrace();
    }

    // Make canvas occupy the full width / height of its parent anchor pane. Couldn't set in FXML.
    currCanvas = faulknerCanvas = new MapCanvas(true, Campus.FAULKNER);
    faulknerCanvas.setHighlights(highlights);

    mainCanvas = new MapCanvas(true, Campus.MAIN);
    mainCanvas.setHighlights(highlights);
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

    // Set button icons
    goButton.setGraphic(new FontIcon(FontAwesomeSolid.LOCATION_ARROW));
    swapBtn.setGraphic(new FontIcon(FontAwesomeSolid.RETWEET));
    directionsButton.setGraphic(new FontIcon(FontAwesomeSolid.MAP_SIGNS));
    directionListButton.setGraphic(new FontIcon(FontAwesomeSolid.LIST));
    dirBackButton.setGraphic(new FontIcon(FontAwesomeSolid.LONG_ARROW_ALT_LEFT));
    dirNextButton.setGraphic(new FontIcon(FontAwesomeSolid.LONG_ARROW_ALT_RIGHT));

    floorUpButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_UP));
    floorDownButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_DOWN));

    // Set phonenumber field to float
    phoneNumberField.setLabelFloat(true);

    // Hide directions display
    directionsBox.setVisible(false);
    directionsPane.setVisible(false);

    // Setup direction selection callback
    directionsList.setOnMouseReleased(
        event -> {
          selectedDirection();
        });

    // Register canvas click callback
    mainCanvas.setOnMouseClicked(
        event -> {
          if (currCanvas != mainCanvas || event.getButton() != MouseButton.PRIMARY) {
            return;
          }

          Point2D clickPos = new Point2D(event.getX(), event.getY());
          Optional<Node> clickNode = mainCanvas.getClosestNode(floor, clickPos, 100);

          if (clickNode.isPresent()) {
            Node node = clickNode.get();

            if (node.getType() == NodeType.HALL || node.getType() == NodeType.ELEV) {
              return;
            }
            if (startingLocationBox.isFocused()) {
              startingLocationBox.getEditor().setText(node.toString());
            } else if (destinationBox.isFocused()) {
              destinationBox.getEditor().setText(node.toString());
            }
          }
        });

    faulknerCanvas.setOnMouseClicked(
        event -> {
          if (currCanvas != faulknerCanvas || event.getButton() != MouseButton.PRIMARY) {
            return;
          }

          Point2D clickPos = new Point2D(event.getX(), event.getY());
          Optional<Node> clickNode = faulknerCanvas.getClosestNode(floor, clickPos, 100);

          if (clickNode.isPresent()) {
            Node node = clickNode.get();

            if (node.getType() == NodeType.HALL || node.getType() == NodeType.ELEV) {
              return;
            }
            if (startingLocationBox.isFocused()) {
              startingLocationBox.getEditor().setText(node.toString());
            } else if (destinationBox.isFocused()) {
              destinationBox.getEditor().setText(node.toString());
            }
          }
        });

    // Register handler to show selected nodes
    startingLocationBox
        .getEditor()
        .textProperty()
        .addListener(
            observable -> {
              Node node = getSelectedNode(startingLocationBox);
              if (node != null) {
                highlights.set(0, node);
                currCanvas.draw(floor);
              }
            });

    startingLocationBox
        .getEditor()
        .textProperty()
        .addListener(
            observable -> {
              Node node = getSelectedNode(startingLocationBox);
              if (node != null) {
                highlights.set(1, node);
                currCanvas.draw(floor);
              }
            });

    // Register event handler to redraw map on tab selection
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();

          clearPath();

          // Redraw map
          mainCanvas.draw(floor);
          faulknerCanvas.draw(floor);

          // Hide drawers
          hideDirectionsBox();
          hideSearchBox();
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
    if (directionsBox.isVisible()) {
      hideSearchBox();
    } else {
      clearPath();
      showSearchBox();
      if (directionsPane.isVisible()) {
        hideDirectionsBox();
      }
    }
  }

  @FXML
  public void pressedSwap() {
    clearPath();
    if (directionsPane.isVisible()) {
      FadeOutLeft textOut = new FadeOutLeft(directionsPane);
      textOut.setSpeed(2.0);
      textOut.setOnFinished(
          event -> {
            directionsPane.setVisible(false);
          });
      textOut.play();
    }
    Node start = getSelectedNode(startingLocationBox);
    Node end = getSelectedNode(destinationBox);
    startingLocationBox.setItems(Graph.getAllValidDestinationList());
    destinationBox.setItems(Graph.getAllValidDestinationList());

    startingLocationBox.getEditor().setText(end.toString());
    destinationBox.getEditor().setText(start.toString());
  }

  @FXML
  public void pressedGo() {
    Node start = getSelectedNode(startingLocationBox);
    Node end = getSelectedNode(destinationBox);
    String phoneNumber = phoneNumberField.getText();

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

        // Remove dest on this graph
        pathSegments.get(pathSegments.size() - 1).removeLast();

        // Insert inter segment
        pathSegments.add(PathSegment.calcInterSegment(Campus.MAIN));

        // Path from main exit node to main dest
        path.setGraph(Graph.getInstance(Campus.MAIN));
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
        mainCanvas.setPath(path);

        // Remove dest on this graph
        pathSegments.get(pathSegments.size() - 1).removeLast();

        // Insert inter segment
        pathSegments.add(PathSegment.calcInterSegment(Campus.FAULKNER));

        // Path from main exit node to main dest
        path.setGraph(Graph.getInstance(Campus.FAULKNER));
        path.findPath(Graph.getInstance(Campus.FAULKNER).getNodeByID(FAULKNER_EXIT_NODE), end);
        pathSegments.addAll(
            PathSegment.calcPathSegments(
                texDirectionsWithLabels(path.getPathFindingAlgo().textualDirections())));
        faulknerCanvas.setPath(path);
      }

      if (path.getPathNodes().size() != 0) {
        updateDisplayedPath();
        showDirectionsBox();
        hideSearchBox();
      } else {
        DialogUtil.simpleInfoDialog(
            dialogPane,
            "No Path Found",
            "No path between the selected locations could be found. Try choosing different locations.");
        hideDirectionsBox();
        showSearchBox();
      }

      if (phoneNumber != null && phoneNumber.length() >= 10) {
        sendTextMessage(phoneNumber, path);
      }
      phoneNumberField.clear();
    }
  }

  public void sendTextMessage(String phoneNumber, ContextPath path) {
    String directions = "";
    for (Pair<Node, String> pair : path.getPathFindingAlgo().textualDirections()) {
      directions += (pair.getValue() + "\n");
    }
    if (phoneNumber.contains(" ")
        || phoneNumber.contains("(")
        || phoneNumber.contains(")")
        || phoneNumber.contains("-")) {
      phoneNumber.replace(" ", "");
      phoneNumber.replace("(", "");
      phoneNumber.replace(")", "");
      phoneNumber.replace("-", "");
    }
    if (!phoneNumber.substring(0, 2).equals("+1")) {
      if (phoneNumber.length() == 10) {
        phoneNumber = "+1" + phoneNumber;
      }
    }

    // Use the proper AWS code if you want to implement AWS here
    /*
    AmazonSNSClient snsClient = new AmazonSNSClient();
      String message = "My SMS message";
      Map<String, MessageAttributeValue> smsAttributes =
              new HashMap<String, MessageAttributeValue>();
      //<set SMS attributes>
      sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);*/
    // Below is the Twilio free trial code to send text messages to verified numbers
    // Comment out or delete this code for
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    Message message =
        Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber("+12029310539"),
                directions)
            .create();
    System.out.println(message.getSid());
  }

  /*
  // Use this method in conjunction with the one above for sending SMS messages with AWS
  public static void sendSMSMessage(AmazonSNSClient snsClient, String message,
                                    String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
    PublishResult result = snsClient.publish(new PublishRequest()
            .withMessage(message)
            .withPhoneNumber(phoneNumber)
            .withMessageAttributes(smsAttributes));
    System.out.println(result); // Prints the message ID.
  }*/

  @FXML
  public void toggleDisplayedMap() {
    currCanvas.setVisible(false);
    currCanvas.disablePathAnimation();

    if (mainRadioButton.isSelected()) {
      currCanvas = mainCanvas;
      floorField.setText(graph.getFloorString(floor, "MAIN"));
    } else {
      currCanvas = faulknerCanvas;
      floorField.setText(graph.getFloorString(floor, "Faulkner"));
      if (floor == 6) {
        floor = 5;
        floorField.setText(graph.getFloorString(floor, "Faulkner"));
      }
    }

    currCanvas.setVisible(true);
    currCanvas.enablePathAnimation();
    currCanvas.animatePath(floor);
  }

  public Graph getGraph() {
    return graph;
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
    currCanvas.draw(floor);
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

  @FXML
  public void pressedDirectionList() {
    displayingPathOverview = true;
    updateDisplayedPath();
  }

  public void clearPath() {
    if (currCanvas == mainCanvas) {
      faulknerCanvas.setVisible(false);
    } else {
      mainCanvas.setVisible(false);
    }
    mainCanvas.disablePathAnimation();
    mainCanvas.clearPath();

    faulknerCanvas.disablePathAnimation();
    faulknerCanvas.clearPath();

    displayingPathOverview = true;

    if (gMapView.isVisible()) {
      gMapView.setVisible(false);
      currCanvas.setVisible(true);
    }

    currCanvas.draw(floor);
  }

  private void updateDisplayedPath() {
    PathSegment currSegment = pathSegments.get(currPathSegment);
    // Canvas updates if switching to a canvas-based segment
    if (currSegment.getCampus() != Campus.INTER) {
      // Resize directions thingy
      directionsPane.setMinHeight(550);
      directionsPane.setPrefHeight(550);
      directionsList.setMouseTransparent(false);
      directionsList.setPrefHeight(498);

      // Show floor selection
      floorBox.setVisible(true);

      MapCanvas newCanvas = (currSegment.getCampus() == Campus.MAIN) ? mainCanvas : faulknerCanvas;
      if (newCanvas != currCanvas) {
        currCanvas.disablePathAnimation();
        currCanvas.setVisible(false);
        currCanvas = newCanvas;
      }
      currCanvas.setVisible(true);

      // Hide bing map, bring slider back
      gMapView.setVisible(false);
      zoomSlider.setVisible(true);

      // Center path
      currCanvas.centerPathOnFloor(currSegment.getFloor());

      // Animate path
      currCanvas.enablePathAnimation();
      currCanvas.animatePath(currSegment.getFloor());
    }
    // Set canvas
    if (currSegment.getCampus() == Campus.FAULKNER) {
      faulknerRadioButton.setSelected(true);
    } else if (currSegment.getCampus() == Campus.MAIN) {
      mainRadioButton.setSelected(true);
    } else if (currSegment.getCampus() == Campus.INTER) {
      // Resize directions thingy
      directionsPane.setMinHeight(52);
      directionsPane.setPrefHeight(52);
      directionsList.setPrefHeight(0);
      directionsList.setMouseTransparent(true);

      // Hide floor selection
      floorBox.setVisible(false);

      currCanvas.setVisible(false);
      currCanvas.disablePathAnimation();

      gMapView.setVisible(true);
      if (pathSegments.get(currPathSegment - 1).getCampus() == Campus.MAIN) {
        gMapView.getEngine().executeScript("pathToFaulkner(); map.setView({zoom: 14});");
      } else {
        gMapView.getEngine().executeScript("pathToMain(); map.setView({zoom: 14});");
      }
      zoomSlider.setVisible(false);
    }

    // Set floor
    floor = currSegment.getFloor();
    String c;
    if (currCanvas == faulknerCanvas) {
      c = "Faulkner";
    } else {
      c = "MAIN";
    }
    floorField.setText(graph.getFloorString(floor, c));

    // Show directions in list
    if (displayingPathOverview) {
      showPathOverview();
    } else {
      showCurrSegment();
    }

    currCanvas.draw(floor);
  }

  private void selectedDirection() {
    Label selected = directionsList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    if (displayingPathOverview) {
      pathSegments.stream()
          .filter(pathSegment -> pathSegment.toString().equals(selected.getText()))
          .findFirst()
          .ifPresent(
              pathSegment -> {
                currPathSegment = pathSegments.indexOf(pathSegment);
                displayingPathOverview = false;
                updateDisplayedPath();
              });
    } else {

    }
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
            new Pair<>(
                textualPath.get(j).getKey(),
                new Label(textualPath.get(j).getValue(), new FontIcon(Icomoon.ICM_ARROW_UP_LEFT))));
      } else if (textualPath.get(j).getValue().contains("slight right")) {
        textPath.add(
            new Pair<>(
                textualPath.get(j).getKey(),
                new Label(
                    textualPath.get(j).getValue(), new FontIcon(Icomoon.ICM_ARROW_UP_RIGHT))));
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

  private void hideSearchBox() {
    FadeOutLeft fadeOut = new FadeOutLeft(directionsBox);
    fadeOut.setSpeed(2.0);
    fadeOut.setOnFinished(
        event -> {
          directionsBox.setVisible(false);
        });
    fadeOut.play();
  }

  private void showSearchBox() {
    FadeInLeft fadeIn = new FadeInLeft(directionsBox);
    fadeIn.setSpeed(2.0);
    directionsBox.setVisible(true);
    fadeIn.play();
  }

  private void hideDirectionsBox() {
    if (directionsPane.isVisible()) {
      FadeOutLeft fadeOut = new FadeOutLeft(directionsPane);
      fadeOut.setSpeed(2.0);
      fadeOut.setOnFinished(
          event -> {
            directionsPane.setVisible(false);
          });
      fadeOut.play();
    }
  }

  private void showDirectionsBox() {
    if (!directionsPane.isVisible()) {
      directionsPane.setVisible(true);
      FadeInLeft fadeIn = new FadeInLeft(directionsPane);
      fadeIn.setSpeed(2.0);
      fadeIn.play();
    }
  }

  private void showPathOverview() {
    directionsPane.setMinHeight(550);
    directionsPane.setPrefHeight(550);
    directionsList.setMouseTransparent(false);
    directionsList.setPrefHeight(498);
    directionsList.getItems().clear();

    ArrayList<Label> segmentLabels = new ArrayList<>();
    pathSegments.forEach(
        pathSegment -> {
          segmentLabels.add(new Label(pathSegment.toString()));
        });

    segmentLabels.get(0).setGraphic(new FontIcon(FontAwesomeSolid.DOT_CIRCLE));
    segmentLabels.get(segmentLabels.size() - 1).setGraphic(new FontIcon(FontAwesomeSolid.TIMES));

    for (int i = 1; i < segmentLabels.size() - 1; i++) {
      segmentLabels.get(i).setGraphic(new FontIcon(FontAwesomeSolid.GRIP_LINES_VERTICAL));
    }

    Platform.runLater(
        () -> {
          // Get widest icon
          double widestIconWidth = 0.0;
          for (int i = 0; i < segmentLabels.size(); i++) {
            widestIconWidth =
                Math.max(
                    widestIconWidth,
                    segmentLabels.get(i).getGraphic().prefWidth(segmentLabels.get(i).getHeight()));
          }
          widestIconWidth += 4.0;

          // Align based on widest icon
          for (int i = 0; i < segmentLabels.size(); i++) {
            double currGraphicWidth =
                segmentLabels.get(i).getGraphic().prefWidth(segmentLabels.get(i).getHeight());
            double widthDiff = widestIconWidth - currGraphicWidth;

            segmentLabels.get(i).setPadding(new Insets(0, 0, 0, widthDiff / 2.0));

            segmentLabels.get(i).setGraphicTextGap(widthDiff / 2.0);
          }
        });

    directionsList.getItems().addAll(segmentLabels);
    directionsList.getSelectionModel().select(segmentLabels.get(currPathSegment));
  }

  private void showCurrSegment() {
    // Setup labels
    directionsList.getItems().clear();
    directionsList.getItems().addAll(pathSegments.get(currPathSegment).getDirections());
  }
}
