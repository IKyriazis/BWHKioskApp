package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.database.flower.Flower;
import edu.wpi.cs3733.d20.teamA.graph.Campus;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class FlowerOrderController extends AbstractController implements IDialogController {
  @FXML private JFXComboBox<Node> roomList;
  @FXML private JFXButton confirmButton;

  @FXML private JFXTreeTableView<TempFlower> tblDisplay;
  @FXML private JFXTextField txtTotal;

  @FXML private JFXTextField txtMessage;

  private JFXDialog dialog;
  private List<Flower> orderContent;
  private double cost;

  public void setList(List<Flower> myList) {
    orderContent = myList;
  }

  @FXML
  public void initialize() throws Exception {
    // Setup list of destination nodes
    // TODO get nodes for all floors
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance(Campus.FAULKNER).getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));
    roomList.setItems(allNodeList);
    roomList
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(roomList, roomList, allNodeList));

    // Set up table
    TreeItem rootItem = new TreeItem<>(new TempFlower("", "", 0));

    // Setup root item in table
    tblDisplay.setRoot(rootItem);
    tblDisplay.setShowRoot(false);
    tblDisplay.setEditable(false);

    // Setup columns in the table
    setupColumns();

    // Calculate and display each flower along with the total cost
    cost = 0;
    for (Flower f : orderContent) {
      cost += f.getPricePer() * f.getQuantitySelected();
      TempFlower flow = new TempFlower(f.getTypeFlower(), f.getColor(), f.getQuantitySelected());
      tblDisplay.getRoot().getChildren().add(new TreeItem<>(flow));
    }

    txtTotal.setText("Total cost: " + String.format("$%.2f", cost));

    // Set button icons
    confirmButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK));
  }

  // Set up columns in table
  private void setupColumns() {
    JFXTreeTableColumn<TempFlower, String> columnName = new JFXTreeTableColumn<>("Type");
    columnName.setCellValueFactory(param -> param.getValue().getValue().type);
    columnName.setMinWidth(125);
    columnName.setResizable(false);

    JFXTreeTableColumn<TempFlower, String> columnColor = new JFXTreeTableColumn<>("Color");
    columnColor.setCellValueFactory(param -> param.getValue().getValue().color);
    columnColor.setMinWidth(125);
    columnColor.setResizable(false);

    JFXTreeTableColumn<TempFlower, Integer> columnNum = new JFXTreeTableColumn<>("Number in Order");
    columnNum.setCellValueFactory(param -> param.getValue().getValue().num.asObject());
    columnNum.setMinWidth(125);
    columnNum.setResizable(false);

    tblDisplay.getColumns().addAll(columnName, columnColor, columnNum);
  }

  @FXML
  public void placeOrder(ActionEvent actionEvent) {
    Node node = roomList.getSelectionModel().getSelectedItem();
    if (node != null) {
      String flowerString =
          ""; // This is a specifically formatted string that represents type and quantity of
      // flowers in the order
      int numFlowers = 0;
      for (Flower f : orderContent) {
        numFlowers += f.getQuantitySelected();
        flowerString += f.getFlowerID() + "/" + f.getQuantitySelected() + "|";
      }

      String message = txtMessage.getText();
      try {
        int i = flDatabase.addOrder(numFlowers, flowerString, node.getNodeID(), message, cost);
        dialog.close();
        // Once the order is placed, remove the flowers from the list
        for (Flower f : orderContent) {
          flDatabase.updateQTY(
              f.getTypeFlower(), f.getColor(), f.getQty() - f.getQuantitySelected());
        }
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
    dialog.close();
  }

  // Temporary flower object used to create a treetable
  private class TempFlower extends RecursiveTreeObject<TempFlower> {
    public SimpleStringProperty type;
    public SimpleStringProperty color;
    public SimpleIntegerProperty num;

    public TempFlower(String tp, String col, int num) {
      type = new SimpleStringProperty(tp);
      color = new SimpleStringProperty(col);
      this.num = new SimpleIntegerProperty(num);
    }
  }
}
