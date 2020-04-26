package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Order;
import javafx.fxml.FXML;

public class ShowOrderController extends AbstractController implements IDialogController {

  @FXML private JFXTextArea txtContentNumber;
  @FXML private JFXTextArea txtContentFlower;
  @FXML private JFXTextField txtLocation;
  @FXML private JFXTextField txtMessage;
  @FXML private JFXTextField txtTotalCost;

  @FXML private JFXTextField txtPrevStat;
  @FXML private JFXComboBox<String> txtNextStat;

  @FXML private JFXButton changeProgressButton;
  @FXML private JFXButton changeEmployeeButton;

  private Order myOrder;

  private JFXDialog dialog;

  public void initialize() {
    String s = myOrder.getFlowerString();
    while (s.indexOf('|') != -1) {
      int flNum = Integer.parseInt(s.substring(0, s.indexOf(",")));
      int num = Integer.parseInt(s.substring(s.indexOf(",") + 1, s.indexOf("|")));

      String flType = flDatabase.getFlowerTypeID(flNum);
      String flColor = flDatabase.getFlowerColorID(flNum);
      txtContentNumber.appendText(flNum + "\n");
      txtContentFlower.appendText(flColor + "   " + flType + "\n");
      s = s.substring(s.indexOf("|") + 1);
    }

    txtTotalCost.setText(String.format("$%.2f", myOrder.getPrice()));
    txtMessage.setText(myOrder.getMessage());
    txtLocation.setText(myOrder.getLocation());

    changeProgressButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCHANGE));
    changeEmployeeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ID_CARD));

    txtPrevStat.setText(myOrder.getStatus());
    // Setup status change stuff
    txtNextStat
        .getItems()
        .addAll("Order Sent", "Order Received", "Flowers Sent", "Flowers Delivered");
    txtNextStat
        .getSelectionModel()
        .select(Math.min(statusStringToValue(myOrder.getStatus()) + 1, 3));
  }

  public void setOrder(Order value) {
    myOrder = value;
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }

  public void changeProgress() {
    String s = txtNextStat.getSelectionModel().getSelectedItem();
    super.flDatabase.changeOrderStatus(myOrder.getOrderNumber(), s);
    // Set fields to reflect this
    txtPrevStat.setText(s);
    txtNextStat
        .getSelectionModel()
        .select(Math.min(statusStringToValue(myOrder.getStatus()) + 1, 3));
  }

  public void changeEmployee() {
    // DO STUFF
  }

  private int statusStringToValue(String status) {
    switch (status) {
      case "Order Sent":
        return 0;
      case "Order Received":
        return 1;
      case "Flowers Sent":
        return 2;
      case "Flowers Delivered":
        return 3;
      default:
        return 999;
    }
  }
}
