package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class FlowerOrderController extends AbstractController implements IDialogController {
  @FXML private JFXComboBox<String> choiceFlower;
  @FXML private JFXComboBox<Node> roomList;
  @FXML private JFXTextField txtNumber;
  @FXML private JFXTextField txtTotal;
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
    roomList.setItems(allNodeList);
    roomList
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(roomList, roomList, allNodeList));

    // Setup input formatter for flower number
    txtNumber.setTextFormatter(InputFormatUtil.getIntFilter());
  }

  @FXML
  public void updateCost() {
    String s = choiceFlower.getSelectionModel().getSelectedItem();
    String type = s.substring(0, s.indexOf(','));
    String color = s.substring(s.indexOf(' ') + 1);

    try {
      double i = flDatabase.getFlowerPricePer(type, color) * Integer.parseInt(txtNumber.getText());
      txtTotal.setText(String.format("$%.2f", i));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void placeOrder(ActionEvent actionEvent) {
    if (choiceFlower.getSelectionModel().getSelectedItem() != null) {
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);
      int count = Integer.parseInt(txtNumber.getText());

      try {
        if ((count <= 0) || (count > flDatabase.getFlowerQuantity(type, color))) {
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }

      Optional<Node> found =
          roomList.getItems().stream()
              .filter(node -> node.toString().contains(roomList.getEditor().getText()))
              .findFirst();
      if (found.isPresent()) {
        Node node = found.get();
        try {
          int i = flDatabase.addOrder(count, type, color, node.getNodeID());
          dialog.close();
          DialogUtil.simpleInfoDialog(
              dialog.getDialogContainer(), "Order Placed", "Order #" + i + " placed successfully");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  @FXML
  public void updateFlowerList() {
    try {
      ObservableList<Flower> list = flDatabase.flowerOl();
      list.forEach(
          flower -> choiceFlower.getItems().add(flower.getTypeFlower() + ", " + flower.getColor()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
