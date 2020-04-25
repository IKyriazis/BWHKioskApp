package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamA.database.Order;
import javafx.fxml.FXML;

public class ShowOrderController implements IDialogController {

  @FXML private JFXTextArea txtContent;
  private Order myOrder;

  private JFXDialog dialog;

  public void initialize() {
    String s = myOrder.getFlowerString();
    while (s.indexOf('|') != -1) {
      int flNam = Integer.parseInt(s.substring(0, s.indexOf(",")));
      int num = Integer.parseInt(s.substring(s.indexOf(",") + 1, s.indexOf("|")));
      txtContent.appendText(flNam + " " + num + "\n");
      s = s.substring(s.indexOf("|") + 1);
    }
  }

  public void setOrder(Order value) {
    myOrder = value;
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
