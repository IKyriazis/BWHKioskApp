package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.graph.Campus;
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
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;

public class NodeDialogController implements IDialogController {
  @FXML private JFXTextField nodeIDField;
  @FXML private JFXComboBox<String> floorBox;
  @FXML private JFXComboBox<NodeType> typeBox;
  @FXML private JFXTextField longNameField;
  @FXML private JFXTextField shortNameField;
  @FXML private JFXComboBox<String> buildingBox;
  @FXML private JFXComboBox<String> teamBox;
  @FXML private JFXButton doneButton;
  @FXML private JFXTextField xField;
  @FXML private JFXTextField yField;

  private int x, y, floor;
  private Node oldNode;
  private Graph graph;
  private JFXDialog dialog;
  private Campus campus;

  public NodeDialogController(Campus campus, Node node, int x, int y, int floor) {
    this.oldNode = node;
    // Use existing node coordinates if set
    if (oldNode != null) {
      this.x = node.getX();
      this.y = node.getY();
      this.floor = node.getFloor();
    } else {
      this.x = x;
      this.y = y;
      this.floor = floor;
    }

    this.campus = campus;

    try {
      graph = Graph.getInstance(campus);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void initialize() {
    shortNameField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 25) {
                shortNameField.setText(newValue.substring(0, 25));
              }
            });
    longNameField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 50) {
                longNameField.setText(newValue.substring(0, 50));
              }
            });
    // Setup floor combobox
    ObservableList<String> floors = FXCollections.observableArrayList();
    if (campus == Campus.MAIN) {
      floors = FXCollections.observableArrayList("L2", "L1", "G", "1", "2", "3");
    } else if (campus == Campus.FAULKNER) {
      floors = FXCollections.observableArrayList("1", "2", "3", "4", "5");
    }

    floorBox.setItems(floors);
    floorBox.setValue(graph.getFloorString(floor));
    floorBox.setEditable(false);

    ObservableList<NodeType> types = FXCollections.observableArrayList(NodeType.values());
    typeBox.setItems(types);

    ObservableList<String> buildings =
        FXCollections.observableArrayList(
            "Faulkner", "Main", "BTM", "15 Francis", "45 Francis", "Tower", "Shapiro");
    buildingBox.setItems(buildings);

    ObservableList<String> teams =
        FXCollections.observableArrayList(
            "Team A", "Team B", "Team C", "Team D", "Team E", "Team F", "Team K", "Team L",
            "Team M", "Team N", "Team O", "Team P");
    teamBox.setItems(teams);

    // Setup coordinate formatters
    xField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    yField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

    // Button hook
    doneButton.setOnAction(this::pressedDone);

    // Set button icon
    doneButton.setGraphic(new FontIcon(FontAwesomeRegular.CHECK_CIRCLE));

    // Populate fields if modifying
    if (oldNode != null) {
      floorBox.setValue(graph.getFloorString(oldNode.getFloor()));
      typeBox.setValue(oldNode.getType());
      longNameField.setText(oldNode.getLongName());
      shortNameField.setText(oldNode.getShortName());

      // Fix b/c other teams maps are straight ass
      if (oldNode.getBuilding().isEmpty()) {
        buildingBox.setValue("Main");
      } else {
        buildingBox.setValue(oldNode.getBuilding());
      }

      if (oldNode.getTeamAssigned().isEmpty()) {
        teamBox.setValue("Team A");
      } else {
        teamBox.setValue(oldNode.getTeamAssigned());
      }

      xField.setText(String.valueOf(oldNode.getX()));
      yField.setText(String.valueOf(oldNode.getY()));
    } else {
      xField.setText(String.valueOf(x));
      yField.setText(String.valueOf(y));
    }
  }

  @FXML
  public void pressedDone(ActionEvent actionEvent) {
    if (floorBox.getSelectionModel().isEmpty()
        || typeBox.getSelectionModel().isEmpty()
        || longNameField.getText().isEmpty()
        || shortNameField.getText().isEmpty()
        || buildingBox.getSelectionModel().isEmpty()
        || teamBox.getSelectionModel().isEmpty()
        || xField.getText().isEmpty()
        || yField.getText().isEmpty()) {
      return;
    }

    int floor = graph.getFloorInt(floorBox.getValue());
    ;
    NodeType nodeType = typeBox.getValue();
    String longName = longNameField.getText();
    String shortName = shortNameField.getText();
    String building = ((String) buildingBox.getValue());
    String team = ((String) teamBox.getValue());
    int newX = Integer.parseInt(xField.getText());
    int newY = Integer.parseInt(yField.getText());
    String nodeID = createNodeID(team, nodeType, floor);

    Node newNode =
        new Node(nodeID, newX, newY, floor, building, nodeType, longName, shortName, team);

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

  private String createNodeID(String teamName, NodeType nodeType, int floor) {
    String nodeID = "";
    // Get the Team Assigned
    String teamChar = String.valueOf(teamName.charAt(teamName.length() - 1));
    // Gets the String of the nodeType
    String nodeTypeS = nodeType.toString();
    // Gets the count of the number of nodes of a certain type
    ObservableList<Node> graphList = graph.getNodeObservableList();
    int count =
        (int)
            graphList.stream()
                .filter(
                    node -> node.getType().toString().equals(nodeTypeS) && node.getFloor() == floor)
                .count();
    count++;
    String countString = String.format("%03d", count);
    // Gets the floor number
    String floorString = String.format("%02d", floor);
    // Team Assigned, Node Type, number of that type: 3 integers, Floor padded with 0's
    nodeID = teamChar + nodeTypeS + countString + floorString;
    return nodeID;
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
