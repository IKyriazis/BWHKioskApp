package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.database.service.prescription.Prescription;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PrescriptionDialogController extends AbstractController implements IDialogController {

  @FXML private JFXTextField txtPatientName;
  @FXML private JFXTextField txtDoctorName;
  @FXML private JFXTextField txtPrescription;
  @FXML private JFXTextField txtPharmacy;
  @FXML private JFXTextField txtDosage;
  @FXML private JFXTextField txtNumberOfRefills;
  @FXML private JFXTextArea txtNotes;
  @FXML private JFXButton btnDone;
  private JFXDialog dialog;
  boolean modify = false;
  boolean info = false;
  Prescription prescription;

  public PrescriptionDialogController() {
    super();
  }

  public PrescriptionDialogController(Prescription prescription, boolean modify, boolean info) {
    super();
    this.prescription = prescription;
    if (modify) {
      this.modify = true;
    }
    if (info) {
      this.info = true;
    }
  }

  public void initialize() {
    // Set formatters to restrict input in boxes
    txtPatientName
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 50) {
                txtPatientName.setText(newValue.substring(0, 50));
              }
            });
    txtDoctorName
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 50) {
                txtDoctorName.setText(newValue.substring(0, 50));
              }
            });

    txtPrescription
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 50) {
                txtPrescription.setText(newValue.substring(0, 50));
              }
            });

    txtPharmacy
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 50) {
                txtPharmacy.setText(newValue.substring(0, 50));
              }
            });

    txtDosage
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 50) {
                txtDosage.setText(newValue.substring(0, 50));
              }
            });

    txtNumberOfRefills.setTextFormatter(InputFormatUtil.getIntFilter());

    txtNotes
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 100) {
                txtNotes.setText(newValue.substring(0, 100));
              }
            });

    if (modify) {
      txtPatientName.setText(prescription.getPatientName());
      txtPrescription.setText(prescription.getPrescription());
      txtPharmacy.setText(prescription.getPharmacy());
      txtDosage.setText(prescription.getDosage());
      txtNumberOfRefills.setText(String.valueOf(prescription.getNumberOfRefills()));
      txtNotes.setText(prescription.getNotes());
    }

    if (info) {
      txtPatientName.editableProperty().setValue(false);
      txtPrescription.editableProperty().setValue(false);
      txtPharmacy.editableProperty().setValue(false);
      txtDosage.editableProperty().setValue(false);
      txtNumberOfRefills.editableProperty().setValue(false);
      txtNotes.editableProperty().setValue(false);
      btnDone.setDisable(false);
    }
  }

  // Scene switch & database addNode
  @FXML
  public void pressDone(ActionEvent e) {

    String patientName = txtPatientName.getText();
    String prescription = txtPrescription.getText();
    String pharmacy = txtPharmacy.getText();
    String dosage = txtDosage.getText();
    int numberOfRefills = Integer.parseInt(txtNumberOfRefills.getText());
    String notes = txtNotes.getText();
    String additional =
        patientName
            + "|"
            + prescription
            + '|'
            + pharmacy
            + '|'
            + dosage
            + '|'
            + numberOfRefills
            + '|'
            + notes;
    if (!modify) {
      serviceDatabase.addServiceReq(ServiceType.PRESCRIPTION, null, "", additional);
    } else { // Modify each field
      String id = this.prescription.getPrescriptionID();
      serviceDatabase.setAdditional(id, additional);
    }
    dialog.close();
  }

  public void disableDoneText() {
    if (!(txtPatientName.getText().isEmpty()
        || txtPrescription.getText().isEmpty()
        || txtPharmacy.getText().isEmpty()
        || txtDosage.getText().isEmpty()
        || txtNumberOfRefills.getText().isEmpty())) {
      btnDone.setDisable(false);
    } else {
      btnDone.setDisable(true);
    }

    if (info) {
      btnDone.setDisable(false);
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
