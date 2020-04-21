package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class FlowerOrderPlaceController extends AbstractController {
  @FXML private GridPane flowerOrderPane;
  @FXML private JFXComboBox<String> choiceFlower;
  @FXML private JFXComboBox<Node> roomList;
  @FXML private JFXTextField txtNumber;
  @FXML private JFXTextField txtTotal;
  @FXML private Label lblMax;

  public void initialize() throws SQLException, IOException, CsvException {
    ObservableList<Flower> list = super.flDatabase.flowerOl(); // Get from FlowerDatabase @TODO
    for (Flower f : list) {
      choiceFlower.getItems().add(f.getTypeFlower() + ", " + f.getColor());
    }
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
  }

  public void placeOrder(ActionEvent actionEvent) throws SQLException, IOException {
    if (choiceFlower.getSelectionModel().getSelectedItem() != null) {
      // Get flower type selected
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);

      // Validate number of flowers is within bounds
      int num;
      int max = super.flDatabase.getFlowerNumber(type, color);
      try {
        num = Integer.parseInt(txtNumber.getText());
        if (num > max || num <= 0) {
          alertFail("Please input a number of flowers to order between 0 and the maximum in stock");
          return;
        }
      } catch (NumberFormatException e) {
        alertFail("Please input a numeric value for number of flowers");
        return;
      }
      Node node = roomList.getSelectionModel().getSelectedItem();
      String loc = "";
      if (node != null) {
        loc = node.getNodeID();
      } else {
        alertFail("Please select a room");
        return;
      }

      int i = super.flDatabase.addOrder(num, type, color, loc);

      if (i == 0) {
        alertFail("Order not placed successfully, please try again");
      } else {
        // Update number of flowers in database
        super.flDatabase.updateQTY(type, color, max - num);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order placed");
        alert.setContentText("Your order has been placed. Your order number is: " + i);
        alert.showAndWait();
        choiceFlower.getSelectionModel().clearSelection();
        txtNumber.setText("");
        lblMax.setText("X are available");
        roomList.getSelectionModel().clearSelection();
        txtTotal.clear();
      }
    }
  }

  private void alertFail(String s) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Unable to place order");
    alert.setContentText(s);
    alert.showAndWait();
  }

  public void setMaxNumber(ActionEvent actionEvent) {
    try {
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);

      int i = super.flDatabase.getFlowerNumber(type, color);
      lblMax.setText(i + " are available");

      updateCost();
    } catch (Exception e) {
      lblMax.setText("X are available");
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
}
