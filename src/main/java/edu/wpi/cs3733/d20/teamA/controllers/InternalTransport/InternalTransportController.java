package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class InternalTransportController extends AbstractController {
  @FXML private Label headerLabel;

  @FXML private JFXButton trackButton;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  InternalTransportRequestController itrc = new InternalTransportRequestController();

  public void initialize() {
    itDatabase.dropTables();
    itDatabase.createTables();
  }

  @FXML
  public void placeOrder() {
    DialogUtil.complexDialog(
        dialogPane,
        "Request Internal Transportation",
        "views/InternalTransportRequestDialog.fxml",
        false,
        null,
        new InternalTransportRequestController());
  }

  @FXML
  public void trackOrder() {
    DialogUtil.complexDialog(
        dialogPane,
        "Track Internal Transportation Request",
        "views/InternalTransportTrackerDialog.fxml",
        false,
        null,
        new InternalTransportTrackerController());
  }
}
