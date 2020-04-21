package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
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
  private boolean hasOrder;

  @SneakyThrows
  public FlowerDialogController() throws Exception {
    super();

    modify = false;
  }

  @SneakyThrows
  public FlowerDialogController(Flower f, boolean hasOrder) throws Exception {
    super();

    this.modify = true;
    this.myFlower = f;
    this.hasOrder = hasOrder;
  }

  public void initialize() {
    txtQty.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    txtCost.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));

    if (modify) {
      txtName.setText(myFlower.getTypeFlower());
      txtColor.setText(myFlower.getColor());
      txtQty.setText(String.valueOf(myFlower.getQty()));
      txtCost.setText(String.valueOf(myFlower.getPricePer()));
    }

    if (hasOrder) {
      txtName.setEditable(false);
      txtColor.setEditable(false);
    }

    doneButton.setOnAction(this::isDone);

    // Set button icon
    doneButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));
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
      String name = txtName.getText().substring(0, Math.min(15, txtName.getText().length()));
      String color = txtColor.getText().substring(0, Math.min(15, txtColor.getText().length()));
      int qty = Integer.parseInt(txtQty.getText());
      double price = Double.parseDouble(txtCost.getText());

      if (!modify) {
        super.flDatabase.addFlower(name, color, qty, price);
      } else {
        if (hasOrder) {
          super.flDatabase.updatePrice(myFlower.getTypeFlower(), myFlower.getColor(), price);
          super.flDatabase.updateQTY(myFlower.getTypeFlower(), myFlower.getColor(), qty);
        } else {
          super.flDatabase.deleteFlower(myFlower.getTypeFlower(), myFlower.getColor());
          super.flDatabase.addFlower(name, color, qty, price);
        }
      }

      dialog.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
