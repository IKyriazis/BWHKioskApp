package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.graph.Path;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
  @FXML private VBox searchFields;
  @FXML private JFXComboBox<Node> startingLocationBox;
  @FXML private JFXComboBox<Node> destinationBox;
  @FXML private AnchorPane canvasPane;
  @FXML private Label textualDirectionsLabel;
  @FXML private JFXSlider zoomSlider;
  @FXML private JFXButton goButton;

  private MapCanvas canvas;

  @SuppressWarnings("DuplicatedCode")
  public void initialize() {
    searchFields.setVisible(false);

    // Make canvas occupy the full width / height of its parent anchor pane. Couldn't set in FXML.
    canvas = new MapCanvas();
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Draw background asap
    Platform.runLater(() -> canvas.draw(1));

    // Setup zoom slider hook
    zoomSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              canvas.setZoom(1.0 + (newValue.doubleValue() / 100));
              canvas.draw(1);
            });

    ArrayList<Node> nodeList = new ArrayList<>(Graph.getInstance().getNodes().values());
    ObservableList<Node> allNodeList = FXCollections.observableArrayList(nodeList);
    startingLocationBox.setItems(allNodeList);
    startingLocationBox
        .focusedProperty()
        .addListener(observable -> startingLocationBox.setItems(allNodeList));
    startingLocationBox
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(startingLocationBox, destinationBox, nodeList));

    destinationBox.setItems(allNodeList);
    destinationBox
        .focusedProperty()
        .addListener(observable -> destinationBox.setItems(allNodeList));
    destinationBox
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(destinationBox, goButton, nodeList));
  }

  @FXML
  public void toggleSearch() {
    searchFields.setVisible(!searchFields.isVisible());
  }

  @FXML
  public void showServices() throws IOException {
    Stage stage = new Stage();
    Parent root = FXMLLoader.load(App.class.getResource("views/FlowerDeliveryServiceHome.fxml"));

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void login() throws IOException {
    Stage stage = new Stage();
    Parent root = FXMLLoader.load(App.class.getResource("views/Login.fxml"));

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void pressedGo() {
    Optional<Node> start =
        startingLocationBox.getItems().stream()
            .filter(node -> node.getShortName().contains(startingLocationBox.getEditor().getText()))
            .findFirst();
    Optional<Node> end =
        destinationBox.getItems().stream()
            .filter(node -> node.getShortName().contains(destinationBox.getEditor().getText()))
            .findFirst();
    if (start.isPresent() && end.isPresent()) {
      Path path = new Path(Graph.getInstance());
      path.findPath(start.get(), end.get());
      canvas.setPath(path);
      canvas.draw(1);
    }
  }
}
