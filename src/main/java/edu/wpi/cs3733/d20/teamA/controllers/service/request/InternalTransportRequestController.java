package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class InternalTransportRequestController extends AbstractRequestController {
  @FXML private Label headerLabel;
  @FXML private JFXComboBox<Node> pickupLocationBox;
  @FXML private JFXComboBox<Node> destinationLocationBox;
  @FXML private JFXButton submitBtn;
  @FXML private JFXTextField trackingCodeField;
  @FXML private JFXButton trackBtn;
  @FXML private Label statusLabel;
  @FXML private JFXProgressBar progressBar;

  public void initialize() {
    // Setup icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.WHEELCHAIR));
    submitBtn.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));
    trackBtn.setGraphic(new FontIcon(FontAwesomeSolid.WHEELCHAIR));

    // Set up pickup box
    setupNodeBox(pickupLocationBox, submitBtn);

    // Set up track box
    setupNodeBox(destinationLocationBox, submitBtn);

    // Set the progress to 0
    progressBar.setProgress(0);
  }

  public void pressedSubmitBtn() {
    Node selectedPickupLocation = getSelectedNode(pickupLocationBox);
    Node selectedDestinationLocation = getSelectedNode(destinationLocationBox);

    if (selectedPickupLocation == null || selectedDestinationLocation == null) {
      DialogUtil.simpleInfoDialog(
          "Empty Fields", "Please fully fill out the service request form and try again.");
      return;
    }

    String l =
        serviceDatabase.addServiceReq(
            ServiceType.INTERNAL_TRANSPORT,
            selectedPickupLocation.toString(),
            null,
            selectedDestinationLocation.toString());
    if (l == null) {
      DialogUtil.simpleErrorDialog("Database Error", "Cannot add request");
    } else {
      DialogUtil.simpleInfoDialog("Requested", "Request " + l + " Has Been Added");
      SceneSwitcherController.popScene();
    }

    pickupLocationBox.setValue(null);
    destinationLocationBox.setValue(null);
  }

  public void pressedTrackBtn(ActionEvent actionEvent) {
    String trackingCode = trackingCodeField.getText();

    if (trackingCodeField.getText().isEmpty()) {
      DialogUtil.simpleInfoDialog(
          "Empty Fields", "Please fully fill out the service request form and try again.");
      return;
    }
    try {
      String s = serviceDatabase.getStatus(trackingCode);
      String name = serviceDatabase.getDidReqName(trackingCode);
      if (s == null) {
        progressBar.setProgress(0);
        statusLabel.setText("Status");
        return;
      }
      if (s.equals("Request Made")) {
        progressBar.setProgress(.1);
        statusLabel.setText("No one has been assigned to your request");
      } else if (s.equals("In Progress")) {
        progressBar.setProgress(.5);
        if (name != null)
          statusLabel.setText("Your request has been assigned! " + name + " is on the way!");
        else statusLabel.setText("Your request has been assigned!");
      } else if (s.equals("Completed")) {
        progressBar.setProgress(1);
        statusLabel.setText(name + " brought you to your destination.");
      } else {
        progressBar.setProgress(0);
        statusLabel.setText("Status");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
