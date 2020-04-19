package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class FlowerModController extends AbstractController {
  private boolean modify;

  @FXML private JFXTextField txtName;
  @FXML private JFXTextField txtColor;
  @FXML private JFXTextField txtQty;
  @FXML private JFXTextField txtCost;

  @FXML private GridPane modPane;

  private Flower myFlower;
  private FlowerAdminController lastController;

  private GaussianBlur myBlur;

  @SneakyThrows
  public FlowerModController(FlowerAdminController flowerAdminController) {
    super();
    modify = false;
    lastController = flowerAdminController;
  }

  @SneakyThrows
  public FlowerModController(FlowerAdminController flowerAdminController, Flower f) {
    super();
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

    // Blur everything in background
    myBlur = new GaussianBlur();
    myBlur.setRadius(7.5);

    lastController.getPane().setEffect(myBlur);
  }

  // Scene switch & database addNode
  @FXML
  private void isDone(ActionEvent e) throws SQLException {
    String name = txtName.getText();
    String color = txtColor.getText();
    int qty = Integer.parseInt(txtQty.getText());
    double price = Double.parseDouble(txtCost.getText());

    if (!modify) super.flDatabase.addFlower(name, color, qty, price);
    else {
      super.flDatabase.updatePrice(name, color, price);
      super.flDatabase.updateQTY(name, color, qty);
    }

    Stage stage;
    stage =
        (Stage) this.txtColor.getScene().getWindow(); // Get the stage from an arbitrary component
    lastController.update();
    lastController.getPane().setEffect(null);
    stage.close();
  }
}
