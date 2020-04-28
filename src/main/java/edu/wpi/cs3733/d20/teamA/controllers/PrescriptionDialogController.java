package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.database.Prescription;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
  @FXML private JFXComboBox cBoxRefillPer;
  @FXML private JFXButton btnDone;
  private JFXDialog dialog;
  boolean modify = false;
  Prescription prescription;

  public PrescriptionDialogController() {
    super();
  }

  public PrescriptionDialogController(Prescription prescription, boolean modify) {
    super();
    this.prescription = prescription;
    if (modify == true) {
      this.modify = true;
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

    ObservableList<String> per = FXCollections.observableArrayList();
    per.addAll("DAY", "WEEK", "MONTH","YEAR","");
    cBoxRefillPer.setItems(per);

    if (modify) {
      txtPatientName.setText(prescription.getPrescription());
      txtDoctorName.setText(prescription.getDoctorName());
      txtPrescription.setText(prescription.getPrescription());
      txtPharmacy.setText(prescription.getPharmacy());
      txtDosage.setText(prescription.getDosage());
      txtNumberOfRefills.setText(String.valueOf(prescription.getNumberOfRefills()));
      txtNotes.setText(prescription.getNotes());
      cBoxRefillPer.getSelectionModel().select(1);
    }

    btnDone.setOnAction(this::isDone);

    // Set button icon
    // btnDone.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));
  }

  // Scene switch & database addNode
  @FXML
  public void isDone(ActionEvent e) {
    if (txtPatientName.getText().isEmpty()
        || txtDoctorName.getText().isEmpty()
        || txtPrescription.getText().isEmpty()
        || txtPharmacy.getText().isEmpty()
        || txtDosage.getText().isEmpty()
        || txtNumberOfRefills.getText().isEmpty()
        || cBoxRefillPer.getSelectionModel().getSelectedItem() != null) {
      return;
    }

    try {
      String patientName = txtPatientName.getText();
      String doctorName = txtDoctorName.getText();
      String prescription = txtPrescription.getText();
      String pharmacy = txtPharmacy.getText();
      String dosage = txtDosage.getText();
      int numberOfRefills = Integer.parseInt(txtNumberOfRefills.getText());
      String refillPer = cBoxRefillPer.getSelectionModel().getSelectedItem().toString();
      String notes = txtNotes.getText();

      if (!modify) {
        prescriptionDatabase.addPrescription(
            patientName,
            prescription,
            pharmacy,
            dosage,
            numberOfRefills,
            refillPer,
            doctorName,
            notes);
      }

      dialog.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void disableDoneText() {
    btnDone.setDisable(true);
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
