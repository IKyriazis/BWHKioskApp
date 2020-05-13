package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.time.LocalTime;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MedicineViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;
  @FXML private JFXButton deleteButton;
  @FXML private JFXTextField patientNameField;
  @FXML private JFXTextField doctorNameField;
  @FXML private JFXTextField medicineField;
  @FXML private JFXTimePicker administerTimePicker;

  public MedicineViewerController(ServiceRequest req) {
    super(req);
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.MEDKIT));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Strip out additional info
    fillAdditionalFields();
  }

  private void fillAdditionalFields() {
    String[] additional = req.getAdditional().split("\\|");

    patientNameField.setText(additional[0]);
    doctorNameField.setText(additional[1]);
    medicineField.setText(additional[2]);
    administerTimePicker.setValue(LocalTime.parse(additional[3]));
  }

  public void pressedSave() {
    // Fill standard fields
    genericController.updateRequestFromFields(req);

    String newAdditional =
        patientNameField.getText()
            + "|"
            + patientNameField.getText()
            + "|"
            + doctorNameField.getText()
            + "|"
            + medicineField.getText()
            + "|"
            + administerTimePicker.getValue().toString();

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
