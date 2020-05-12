package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class PrescriptionViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;
  @FXML private JFXButton deleteButton;
  @FXML private JFXTextField patientNameField;
  @FXML private JFXTextField prescriptionField;
  @FXML private JFXTextField pharmacyField;
  @FXML private JFXTextField dosageField;
  @FXML private JFXComboBox<String> refillField;

  public PrescriptionViewerController(ServiceRequest req) {
    super(req);
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.PILLS));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    refillField.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
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
    refillField.getSelectionModel().select(additional[3]);
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
            + refillField.getSelectionModel().getSelectedItem()
            + "|"
            + pharmacyField.getText();

    // Fill additional
    serviceDatabase.setAdditional(req.getReqID(), newAdditional);

    // Fire tab switch event forcing table to update
    headerLabel.fireEvent(new TabSwitchEvent());
  }

  public void pressedDelete() {
    serviceDatabase.deleteServReq(req.getReqID());
    headerLabel.fireEvent(new TabSwitchEvent());
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
