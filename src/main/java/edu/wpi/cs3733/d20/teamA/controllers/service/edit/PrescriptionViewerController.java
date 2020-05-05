package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class PrescriptionViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;

  @FXML private JFXTextField patientNameField;
  @FXML private JFXTextField prescriptionField;
  @FXML private JFXTextField pharmacyField;
  @FXML private JFXTextField dosageField;
  @FXML private JFXTextField refillField;

  public PrescriptionViewerController(ServiceRequest req) {
    super(req);
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.PILLS));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Strip out additional info
    fillAdditionalFields();

    // Set up the default statuses (or stati, as the cool kids call it)
    genericController.fillStandardStatusList();
  }

  private void fillAdditionalFields() {
    String[] additional = req.getAdditional().split("\\|");

    patientNameField.setText(additional[0]);
    prescriptionField.setText(additional[1]);
    dosageField.setText(additional[2]);
    refillField.setText(additional[3]);
    pharmacyField.setText(additional[4]);
  }

  public void pressedSave() {
    // Fill standard fields
    genericController.updateRequestFromFields(req);

    String newAdditional =
        patientNameField.getText()
            + "|"
            + prescriptionField.getText()
            + "|"
            + dosageField.getText()
            + "|"
            + refillField.getText()
            + "|"
            + pharmacyField.getText();

    // Fill additional
    serviceDatabase.setAdditional(req.getReqID(), newAdditional);
  }

  @Override
  public void reset(ServiceRequest req) {
    super.reset(req);

    // Update additional info
    fillAdditionalFields();

    // Update standard fields
    genericController.fillFields(req);
  }
}
