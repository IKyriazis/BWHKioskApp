package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InternalTransportTrackerController extends AbstractController
    implements IDialogController {
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
      String s = itDatabase.getRequestStatus(Integer.parseInt(txtNumber.getText()));
      String name = itDatabase.getName(Integer.parseInt(txtNumber.getText()));
      if (s == null) {
        progressBar.setProgress(0);
        orderStatusLabel.setText("Input an order number");
        return;
      }

      if (s.equals("Reported")) {
        progressBar.setProgress(.1);
        orderStatusLabel.setText("No one has been assigned to your request");
      } else if (s.equals("Dispatched")) {
        progressBar.setProgress(.5);
        orderStatusLabel.setText("Your request has been assigned! " + name + " is on the way.");
      } else if (s.equals("Done")) {
        progressBar.setProgress(1);
        orderStatusLabel.setText(name + " has brought you to your destination.");
      } else {
        progressBar.setProgress(0);
        orderStatusLabel.setText("Input an order number");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
