package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import lombok.SneakyThrows;

public class FlowerModController extends AbstractController {
  private boolean modify;

  @FXML private GridPane modPane;
  @FXML private JFXTextField txtName;
  @FXML private JFXTextField txtColor;
  @FXML private JFXTextField txtQty;
  @FXML private JFXTextField txtCost;

  private Flower myFlower;
  private JFXDialog thisDialog;

  @SneakyThrows
  public FlowerModController(JFXDialog d) {
    super();
    modify = false;
    thisDialog = d;
  }

  @SneakyThrows
  public FlowerModController(JFXDialog d, Flower f) {
    super();
    modify = true;
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
  public void isDone(ActionEvent e) throws SQLException, IOException {
    String name = txtName.getText();
    String color = txtColor.getText();
    int qty = Integer.parseInt(txtQty.getText());
    double price = Double.parseDouble(txtCost.getText());

    if (!modify) super.flDatabase.addFlower(name, color, qty, price);
    else {
      super.flDatabase.updatePrice(name, color, price);
      super.flDatabase.updateQTY(name, color, qty);
    }
    thisDialog.close();
  }
}
