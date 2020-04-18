package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class FlowerModController {
  private FlowerDatabase d;

  private boolean modify;

  @FXML private JFXTextField txtName;
  @FXML private JFXTextField txtColor;
  @FXML private JFXTextField txtQty;
  @FXML private JFXTextField txtCost;

  private Flower myFlower;
  private FlowerAdminController lastController;

  public FlowerModController(FlowerDatabase database, FlowerAdminController flowerAdminController) {
    d = database;
    modify = false;
    lastController = flowerAdminController;
  }

  public FlowerModController(
      FlowerDatabase database, FlowerAdminController flowerAdminController, Flower f) {
    d = database;
    modify = true;
    lastController = flowerAdminController;
    myFlower = f;
  }

  public void initialize() {
    if (modify) {
      txtName.setEditable(false);
      txtColor.setEditable(false);
      txtName.setText(myFlower.getTypeFlower());
      txtColor.setText(myFlower.getColor());
      txtQty.setText("" + myFlower.getQty());
      txtCost.setText("" + myFlower.getPricePer());
    } else {
      txtName.setEditable(true);
      txtColor.setEditable(true);
    }
  }

  // Scene switch & database addNode
  @FXML
  private void isDone(ActionEvent e) throws SQLException {
    String name = txtName.getText();
    String color = txtColor.getText();
    int qty = Integer.parseInt(txtQty.getText());
    double price = Double.parseDouble(txtCost.getText());

    if (!modify) d.addFlower(name, color, qty, price);
    else {
      d.updatePrice(name, color, price);
      d.updateQTY(name, color, qty);
    }

    Stage stage;
    stage =
        (Stage) this.txtColor.getScene().getWindow(); // Get the stage from an arbitrary component
    lastController.update();
    stage.close();
  }
}
