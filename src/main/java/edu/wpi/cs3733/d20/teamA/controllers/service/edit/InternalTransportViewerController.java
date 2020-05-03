package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class InternalTransportViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;
  @FXML private JFXTextField destinationField;

  public InternalTransportViewerController(ServiceRequest req) {
    super(req);
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.WHEELCHAIR));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Fill out destination field
    destinationField.setText(req.getAdditional());

    // Fill out standard service request fields
    genericController.fillFields(req);

    // Set up statuses
    genericController.fillStandardStatusList();
  }

  public void pressedSave() {
    // Fill standard fields
    genericController.updateRequestFromFields(req);
  }

  @Override
  public void reset(ServiceRequest req) {
    super.reset(req);

    // Update destination field
    destinationField.setText(req.getAdditional());

    // Update standard fields
    genericController.fillFields(req);
  }
}
