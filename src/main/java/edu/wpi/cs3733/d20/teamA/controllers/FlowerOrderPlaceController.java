package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class FlowerOrderPlaceController extends AbstractController {
  @FXML private GridPane flowerOrderPane;
  @FXML private JFXComboBox<String> choiceFlower;
  @FXML private JFXComboBox<Node> roomList;
  @FXML private JFXTextField txtNumber;
  @FXML private JFXTextField txtTotal;
  @FXML private Label lblMax;

  private StackPane dialogPane;

  public FlowerOrderPlaceController(StackPane dialogPane) throws Exception {
    super();

    this.dialogPane = dialogPane;
  }

  public void initialize() throws SQLException, IOException, CsvException {
    if (flDatabase.getSizeFlowers() == -1 || flDatabase.getSizeFlowers() == -1) {
      flDatabase.dropTables();
      flDatabase.createTables();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    } else if (flDatabase.getSizeFlowers() == 0 || flDatabase.getSizeOrders() == 0) {
      flDatabase.removeAllOrders();
      flDatabase.removeAllFlowers();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    }
    updateList();
    // Set up autofill for nodes
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
    // Limit input to integer values
    txtNumber.setTextFormatter(InputFormatUtil.getIntFilter());

    choiceFlower.setOnMouseClicked(
        param -> {
          updateList();
        });
  }

  public void placeOrder(ActionEvent actionEvent) throws SQLException, IOException {
    if (choiceFlower.getSelectionModel().getSelectedItem() != null) {
      // Get flower type selected
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);

      // Validate number of flowers is within bounds
      int num;
      int max = super.flDatabase.getFlowerQuantity(type, color);
      try {
        num = Integer.parseInt(txtNumber.getText());
        if (num > max || num <= 0) {
          DialogUtil.simpleInfoDialog(
              dialogPane,
              "Select Number",
              "Please input a number of flowers to order between 0 and the maximum in stock");
          return;
        }
      } catch (NumberFormatException e) {
        DialogUtil.simpleInfoDialog(
            dialogPane, "Invalid Number", "Please input a numeric value for number of flowers");
        return;
      }
      Node node = roomList.getSelectionModel().getSelectedItem();
      String loc = "";
      if (node != null) {
        loc = node.getNodeID();
      } else {
        DialogUtil.simpleInfoDialog(dialogPane, "Select Room", "Please select a flower room");
        return;
      }

      int i = super.flDatabase.addOrder(num, type, color, loc);

      if (i == 0) {
        DialogUtil.simpleErrorDialog(
            dialogPane, "Order Error", "Order not placed successfully, please try again");
      } else {
        DialogUtil.simpleInfoDialog(
            dialogPane, "Order Placed", "Your order has been placed. Your order number is: " + i);
        choiceFlower.getSelectionModel().clearSelection();
        txtNumber.setText("");
        lblMax.setText("X are in stock at X each");
        roomList.getSelectionModel().clearSelection();
        txtTotal.clear();
      }
    }
  }

  public void setMaxNumber(ActionEvent actionEvent) {
    try {
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);

      int i = super.flDatabase.getFlowerQuantity(type, color);
      double d = super.flDatabase.getFlowerPricePer(type, color);
      lblMax.setText(i + " are in stock at " + String.format("$%.2f", d) + " each");

      updateCost();
    } catch (Exception e) {
      lblMax.setText("X are in stock at X each");
    }
  }

  @FXML
  public void updateCost() {
    try {
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);

      // Read from txtNumber, if an exception is thrown, invalid data entered
      double i =
          super.flDatabase.getFlowerPricePer(type, color) * Integer.parseInt(txtNumber.getText());
      txtTotal.setText(String.format("$%.2f", i));
    } catch (Exception e) {
      txtTotal.clear();
    }
  }

  public void updateList() {
    ObservableList<Flower> list = null;
    choiceFlower.getItems().clear();
    list = super.flDatabase.flowerOl();
    for (Flower f : list) {
      choiceFlower.getItems().add(f.getTypeFlower() + ", " + f.getColor());
    }
  }
}
