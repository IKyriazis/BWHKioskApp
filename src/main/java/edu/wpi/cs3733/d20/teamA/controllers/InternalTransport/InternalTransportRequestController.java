package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class InternalTransportRequestController extends AbstractController
    implements IDialogController {
  @FXML private JFXComboBox<Node> startList;
  @FXML private JFXComboBox<Node> destinationList;
  @FXML private JFXButton confirmButton;
  private JFXDialog dialog;

  @FXML
  public void initialize() throws Exception {
    // Setup list of destination nodes
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));
    startList.setItems(allNodeList);
    startList
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(startList, startList, allNodeList));
    destinationList.setItems(allNodeList);
    destinationList
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(destinationList, destinationList, allNodeList));
  }

  @FXML
  public void placeRequest(ActionEvent actionEvent) {
    if (startList.getSelectionModel().getSelectedItem() != null
        && destinationList.getSelectionModel().getSelectedItem() != null) {
      Optional<Node> start =
          startList.getItems().stream()
              .filter(node -> node.toString().contains(startList.getEditor().getText()))
              .findFirst();
      Optional<Node> end =
          destinationList.getItems().stream()
              .filter(node -> node.toString().contains(destinationList.getEditor().getText()))
              .findFirst();
      if (start.isPresent() && end.isPresent()) {
        Node startNode = start.get();
        Node endNode = end.get();
        try {
          int i = itDatabase.addRequest(startNode.getNodeID(), endNode.getNodeID());
          dialog.close();
          DialogUtil.simpleInfoDialog(
              dialog.getDialogContainer(),
              "Request Placed",
              "Request #" + i + " placed successfully");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
