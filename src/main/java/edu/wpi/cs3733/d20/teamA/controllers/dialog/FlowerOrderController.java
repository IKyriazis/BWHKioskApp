package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class FlowerOrderController extends AbstractController implements IDialogController {
  @FXML private JFXComboBox<Node> roomList;
  @FXML private JFXButton confirmButton;

  @FXML private JFXTextArea showOrderFlower;
  @FXML private JFXTextArea showOrderNumber;
  @FXML private JFXTextField txtTotal;

  @FXML private JFXTextField txtMessage;

  private JFXDialog dialog;

  private List<Flower> orderContent;

  public void setList(List<Flower> myList) {
    orderContent = myList;
  }

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

    double cost = 0;
    for (Flower f : orderContent) {
      cost += f.getPricePer() * f.getQuantitySelected();
      showOrderFlower.appendText(f.getColor() + " " + f.getTypeFlower() + "s\n");
      showOrderNumber.appendText(f.getQuantitySelected() + "\n");
    }
    txtTotal.setText("Total cost: " + String.format("$%.2f", cost));
  }

  @FXML
  public void placeOrder(ActionEvent actionEvent) {
    System.out.println(roomList.getSelectionModel().getSelectedItem().getLongName());
    Node node = roomList.getSelectionModel().getSelectedItem();
    if (node != null) {
      System.out.println(node.getLongName());
      String flowerString = "";
      int numFlowers = 0;
      for (Flower f : orderContent) {
        numFlowers += f.getQuantitySelected();
        flowerString += f.getFlowerID() + "/" + f.getQuantitySelected() + "|";
      }

      String message = txtMessage.getText();
      try {
        int i = flDatabase.addOrder(numFlowers, flowerString, node.getNodeID(), message);
        dialog.close();
        DialogUtil.simpleInfoDialog(
            dialog.getDialogContainer(), "Order Placed", "Order #" + i + " placed successfully");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }

  @FXML
  public void cancel() {
    // COMPLETE
  }
}
