package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.database.flower.Flower;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;

public class FlowerEditController extends AbstractController implements IDialogController {
  private final boolean modify;

  @FXML private GridPane modPane;
  @FXML private JFXTextField txtName;
  @FXML private JFXTextField txtColor;
  @FXML private JFXTextField txtQty;
  @FXML private JFXTextField txtCost;

  @FXML private JFXButton doneButton;

  private Flower myFlower;
  private JFXDialog dialog;
  private boolean hasOrder;

  // Set up window to add flower
  public FlowerEditController() {
    super();
    modify = false;
  }

  // Constructor that sets up window to modify flower
  public FlowerEditController(Flower f, boolean hasOrder) {
    super();

    this.modify = true;
    this.myFlower = f;
    this.hasOrder = hasOrder;
  }

  public void initialize() {
    // Set formatters to restrict input in boxes
    txtName
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtName.setText(newValue.substring(0, 15));
              }
            });
    txtColor
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtColor.setText(newValue.substring(0, 15));
              }
            });
    // Prevent input of letters in number fields
    txtQty.setTextFormatter(InputFormatUtil.getIntFilter());
    txtCost.setTextFormatter(InputFormatUtil.getDoubleFilter());

    if (modify) {
      txtName.setText(myFlower.getTypeFlower());
      txtColor.setText(myFlower.getColor());
      txtQty.setText(String.valueOf(myFlower.getQty()));
      txtCost.setText(String.valueOf(myFlower.getPricePer()));
      txtName.setEditable(false);
      txtColor.setEditable(false);
    }

    doneButton.setOnAction(this::isDone);

    // Set button icon
    doneButton.setGraphic(new FontIcon(FontAwesomeRegular.CHECK_CIRCLE));
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
      /*String name = txtName.getText();
      String color = txtColor.getText();
      int qty = Integer.parseInt(txtQty.getText());
      String price = txtCost.getText();

      if (!modify) {
        super.flDatabase.addFlower(name, color, qty, Double.parseDouble(price));
      } else {
        System.out.println(
            super.flDatabase.updatePrice(myFlower.getTypeFlower(), myFlower.getColor(), price));
        super.flDatabase.updateQTY(myFlower.getTypeFlower(), myFlower.getColor(), qty);
      }*/

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
