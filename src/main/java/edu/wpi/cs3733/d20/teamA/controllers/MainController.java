package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.graph.*;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import java.io.IOException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
  @FXML private VBox searchFields;
  @FXML private ComboBox<Node> startLocation;
  @FXML private ComboBox<Node> destination;
  @FXML private AnchorPane canvasPane;
  @FXML private Label textualDirectionsLabel;

  private ObservableList<Node> mapNodes = FXCollections.observableArrayList();

  private MapCanvas canvas;
  private Path path;

  public void initialize() {
    searchFields.setVisible(false);

    // Make canvas occupy the full width / height of its parent anchor pane. Couldn't set in FXML.
    canvas = new MapCanvas();
    canvasPane.getChildren().add(0, canvas);
    canvas.widthProperty().bind(canvasPane.widthProperty());
    canvas.heightProperty().bind(canvasPane.heightProperty());

    // Draw background asap
    Platform.runLater(() -> canvas.drawFloorBackground(1));

    path = new Path(Graph.getInstance());
    // Create the dropdown observable list
    for (Node node : Graph.getInstance().getNodes().values()) {
      mapNodes.add(node);
    }

    Node one = new Node("Test node", 1208, 600, 1, "", NodeType.DEPT, "long name", "short", "A");
    Node two = new Node("Test node 2", 1000, 600, 1, "", NodeType.DEPT, "long name", "short", "A");
    Edge oneEdge = new Edge(one, two, 1);
    mapNodes.add(one);
    mapNodes.add(two);

    startLocation.setItems(mapNodes);
    startLocation.setValue(one);
    destination.setItems(mapNodes);
  }

  @FXML
  public void toggleSearch(ActionEvent actionEvent) {
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
  public void pressedGo(ActionEvent actionEvent) {
    path.findPath(mapNodes.get(0), mapNodes.get(1));
    canvas.drawPath(path);
    textualDirectionsLabel.setText(path.textualDirections());
    textualDirectionsLabel.setText(
        "Here is where the path would go.\n if the path existed it would go here \n hi");
  }
}
