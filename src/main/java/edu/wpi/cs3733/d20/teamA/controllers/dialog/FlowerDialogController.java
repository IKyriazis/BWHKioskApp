package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import lombok.SneakyThrows;

public class FlowerDialogController extends AbstractController implements IDialogController {
  private boolean modify;

  @FXML private GridPane modPane;
  @FXML private JFXTextField txtName;
  @FXML private JFXTextField txtColor;
  @FXML private JFXTextField txtQty;
  @FXML private JFXTextField txtCost;

  @FXML private JFXButton doneButton;

  private Flower myFlower;
  private JFXDialog dialog;

  @SneakyThrows
  public FlowerDialogController() {
    super();
    modify = false;
  }

  @SneakyThrows
  public FlowerDialogController(Flower f) {
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

    doneButton.setOnAction(this::isDone);
  }

  // Scene switch & database addNode
  @FXML
  public void isDone(ActionEvent e) {
    if (txtName.getText().isEmpty()
        || txtColor.getText().isEmpty()
        || txtQty.getText().isEmpty()
        || txtCost.getText().isEmpty()) {
      return;
    }

    try {
      String name = txtName.getText();
      String color = txtColor.getText();
      int qty = Integer.parseInt(txtQty.getText());
      double price = Double.parseDouble(txtCost.getText());

      if (!modify) super.flDatabase.addFlower(name, color, qty, price);
      else {
        super.flDatabase.updatePrice(name, color, price);
        super.flDatabase.updateQTY(name, color, qty);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    dialog.close();
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
