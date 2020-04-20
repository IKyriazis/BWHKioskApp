package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.graph.NodeType;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class NodePopupController {
  @FXML private JFXTextField nodeIDField;
  @FXML private JFXComboBox<Integer> floorBox;
  @FXML private JFXComboBox<NodeType> typeBox;
  @FXML private JFXTextField longNameField;
  @FXML private JFXTextField shortNameField;
  @FXML private JFXComboBox<String> buildingBox;
  @FXML private JFXComboBox<String> teamBox;
  @FXML private JFXButton doneButton;
  @FXML private JFXTextField xField;
  @FXML private JFXTextField yField;

  private JFXDialog dialog;

  private int x, y;
  private Node oldNode;
  private Graph graph;

  public NodePopupController(JFXDialog dialog, Node node, int x, int y) {
    this.oldNode = node;
    // Use existing node coordinates if set
    if (oldNode != null) {
      this.x = node.getX();
      this.y = node.getY();
    } else {
      this.x = x;
      this.y = y;
    }
    this.dialog = dialog;

    try {
      graph = Graph.getInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void initialize() {
    // Setup floor combobox
    // ObservableList<Integer> floors = FXCollections.observableArrayList(1, 2, 3, 4, 5);
    ObservableList<Integer> floors = FXCollections.observableArrayList(1);
    floorBox.setItems(floors);
    floorBox.setValue(1);
    floorBox.setEditable(false);

    ObservableList<NodeType> types = FXCollections.observableArrayList(NodeType.values());
    typeBox.setItems(types);

    ObservableList<String> buildings = FXCollections.observableArrayList("Main");
    buildingBox.setItems(buildings);

    ObservableList<String> teams = FXCollections.observableArrayList("Team A");
    teamBox.setItems(teams);

    // Setup coordinate formatters
    xField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    yField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

    // Button hook
    doneButton.setOnAction(this::pressedDone);

    // Populate fields if modifying
    if (oldNode != null) {
      nodeIDField.setText(oldNode.getNodeID());
      floorBox.setValue(oldNode.getFloor());
      typeBox.setValue(oldNode.getType());
      longNameField.setText(oldNode.getLongName());
      shortNameField.setText(oldNode.getShortName());
      buildingBox.setValue(oldNode.getBuilding());
      teamBox.setValue(oldNode.getTeamAssigned());
      xField.setText(String.valueOf(oldNode.getX()));
      yField.setText(String.valueOf(oldNode.getY()));
    } else {
      xField.setText(String.valueOf(x));
      yField.setText(String.valueOf(y));
    }
  }

  @FXML
  public void pressedDone(ActionEvent actionEvent) {
    if (nodeIDField.getText().isEmpty()
        || floorBox.getSelectionModel().isEmpty()
        || typeBox.getSelectionModel().isEmpty()
        || longNameField.getText().isEmpty()
        || shortNameField.getText().isEmpty()
        || buildingBox.getSelectionModel().isEmpty()
        || teamBox.getSelectionModel().isEmpty()
        || xField.getText().isEmpty()
        || yField.getText().isEmpty()) {
      return;
    }

    String nodeID = nodeIDField.getText();
    int floor = ((Integer) floorBox.getValue());
    NodeType type = ((NodeType) typeBox.getValue());
    String longName = longNameField.getText();
    String shortName = shortNameField.getText();
    String building = ((String) buildingBox.getValue());
    String team = ((String) teamBox.getValue());
    int newX = Integer.parseInt(xField.getText());
    int newY = Integer.parseInt(yField.getText());

    Node newNode = new Node(nodeID, newX, newY, floor, building, type, longName, shortName, team);

    ArrayList<Node> edgesTo = new ArrayList<>();
    if (oldNode != null) {
      edgesTo.addAll(oldNode.getEdges().keySet());
      try {
        graph.deleteNode(oldNode);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      graph.addNode(newNode);

      edgesTo.forEach(
          node -> {
            try {
              graph.addEdge(newNode, node);
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    }

    dialog.close();
  }
}
