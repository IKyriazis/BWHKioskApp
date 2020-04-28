package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FlowerTrackerController extends AbstractController implements IDialogController {
  @FXML private JFXTextField txtNumber;
  @FXML private JFXProgressBar progressBar;
  @FXML private Label orderStatusLabel;

  private JFXDialog dialog;

  @FXML
  public void initialize() {
    txtNumber.setTextFormatter(InputFormatUtil.getIntFilter());
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }

  @FXML
  public void updateProgress() {
    try {
      String s = flDatabase.getOrderStatus(Integer.parseInt(txtNumber.getText()));
      if (s == null) {
        progressBar.setProgress(0);
        orderStatusLabel.setText("Input an order number");
        return;
      }

      if (s.equals("Order Sent")) {
        progressBar.setProgress(.1);
        orderStatusLabel.setText("Order Sent");
      } else if (s.equals("Order Received")) {
        progressBar.setProgress(.35);
        orderStatusLabel.setText("Order Received");
      } else if (s.equals("Flower Sent")) {
        progressBar.setProgress(.7);
        orderStatusLabel.setText("Flower Sent");
      } else if (s.equals("Flower Delivered")) {
        progressBar.setProgress(1);
        orderStatusLabel.setText("Flower Delivered");
      } else {
        progressBar.setProgress(0);
        orderStatusLabel.setText("Input an order number");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
