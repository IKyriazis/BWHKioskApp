package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.sql.Timestamp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class PrescriptionRequestController extends AbstractRequestController {
  @FXML private Label headerLabel;
  @FXML private JFXTextField txtPatientName;
  @FXML private JFXComboBox<Employee> boxDoctorName;
  @FXML private JFXTextField txtPrescription;
  @FXML private JFXTextField txtPharmacy;
  @FXML private JFXTextField txtDosage;
  @FXML private JFXComboBox<String> numberOfRefillsBox;
  @FXML private JFXTextArea txtNotes;
  @FXML private JFXButton submitButton;

  public void initialize() {
    // Setup icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.PILLS));
    submitButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Limit text length in description area to 100 chars
    setupDescriptionArea(txtNotes);

    // Set up employee box
    setupEmployeeBox(boxDoctorName, "doctor");

    // Set up Number of Refills
    numberOfRefillsBox
        .getItems()
        .addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
  }

  @FXML
  public void pressedSubmit() {
    Employee doctor = boxDoctorName.getSelectionModel().getSelectedItem();

    if (txtPatientName.getText().isEmpty()
        || doctor == null
        || txtPrescription.getText().isEmpty()
        || txtPharmacy.getText().isEmpty()
        || txtDosage.getText().isEmpty()
        || numberOfRefillsBox.getSelectionModel().isEmpty()
        || txtNotes.getText().isEmpty()) {
      DialogUtil.simpleInfoDialog(
          "Empty Fields", "Please fully fill out the service request form and try again.");
      return;
    }

    String patientName = txtPatientName.getText();
    String prescription = txtPrescription.getText();
    String dosage = txtDosage.getText();
    String numberOfRefills = numberOfRefillsBox.getSelectionModel().getSelectedItem();
    String pharmacy = txtPharmacy.getText();

    Timestamp timestamp = new Timestamp(0);

    String additional =
        patientName + "|" + prescription + "|" + dosage + "|" + numberOfRefills + "|" + pharmacy;

    String l =
        serviceDatabase.addServiceReq(
            ServiceType.PRESCRIPTION, "", doctor.getUsername(), txtNotes.getText(), additional);
    if (l == null) {
      DialogUtil.simpleErrorDialog("Database Error", "Cannot add request");
    } else {
      // DialogUtil.simpleInfoDialog("Requested", "Request " + l + " Has Been Added");
      DialogUtil.textingDialog(l);
      SceneSwitcherController.popScene();
    }

    txtPatientName.clear();
    txtPharmacy.clear();
    boxDoctorName.getSelectionModel().clearSelection();
    txtPrescription.clear();
    numberOfRefillsBox.getSelectionModel().clearSelection();
    txtDosage.clear();
    txtNotes.clear();
  }
}
