package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Prescription implements ITableable<Prescription> {
  @Setter private SimpleStringProperty prescriptionID = null;
  @Setter private SimpleStringProperty patientName = null;
  @Setter private SimpleStringProperty prescription = null;
  @Setter private SimpleStringProperty pharmacy = null;
  @Setter private SimpleStringProperty dosage = null;
  @Setter private SimpleIntegerProperty numberOfRefills = null;
  @Setter private SimpleStringProperty doctorName = null;
  @Setter private SimpleStringProperty notes = null;

  public Prescription(String prescriptionID, String description, String additional)
        /*String patientName,
        String prescription,
        String pharmacy,
        String dosage,
        int numberOfRefills,
        String doctorName,
        String notes*/ {
    // patientName+"|"+prescription+'|'+pharmacy+'|'+dosage+'|'+numberOfRefills;
    if (additional != null) {
      String[] additionalArray = additional.split("|");
      if(additionalArray.length == 6) {
        String patientName = additionalArray[0];
        String prescription = additionalArray[1];
        String pharmacy = additionalArray[2];
        String dosage = additionalArray[3];
        int numberOfRefills = Integer.parseInt(additionalArray[4]);
        String notes = additionalArray[5];
        this.prescriptionID = new SimpleStringProperty(prescriptionID);
        this.patientName = new SimpleStringProperty(patientName);
        this.prescription = new SimpleStringProperty(prescription);
        this.pharmacy = new SimpleStringProperty(pharmacy);
        this.dosage = new SimpleStringProperty(dosage);
        this.numberOfRefills = new SimpleIntegerProperty(numberOfRefills);
        this.doctorName = new SimpleStringProperty(description);
        this.notes = new SimpleStringProperty(notes);
      }
    }
  }

  // Getters for Simple Properties
  public SimpleStringProperty getPrescriptionIDProperty() {
    return prescriptionID;
  }

  public SimpleStringProperty getPatientNameProperty() {
    return this.patientName;
  }

  public SimpleStringProperty getPrescriptionProperty() {
    return this.prescription;
  }

  public SimpleStringProperty getPharmacyProperty() {
    return this.pharmacy;
  }

  public SimpleStringProperty getDosageProperty() {
    return this.dosage;
  }

  public SimpleIntegerProperty getNumberOfRefillsProperty() {
    return this.numberOfRefills;
  }

  public SimpleStringProperty getDoctorNameProperty() {
    return this.doctorName;
  }

  public SimpleStringProperty getNotesProperty() {
    return this.notes;
  }

  // Getters for Primitive Type
  public String getPrescriptionID() {
    return prescriptionID.get();
  }

  public String getPatientName() {
    return patientName.get();
  }

  public String getPrescription() {
    return prescription.get();
  }

  public String getPharmacy() {
    return pharmacy.get();
  }

  public String getDosage() {
    return dosage.get();
  }

  public int getNumberOfRefills() {
    return numberOfRefills.get();
  }

  public String getDoctorName() {
    return doctorName.get();
  }

  public String getNotes() {
    return notes.get();
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Prescription, ?>> getColumns() {
    JFXTreeTableColumn<Prescription, String> column1 = new JFXTreeTableColumn<>("Prescription ID");
    column1.setCellValueFactory(param -> param.getValue().getValue().getPrescriptionIDProperty());

    JFXTreeTableColumn<Prescription, String> column2 = new JFXTreeTableColumn<>("Patient Name");
    column2.setCellValueFactory(param -> param.getValue().getValue().getPatientNameProperty());

    JFXTreeTableColumn<Prescription, String> column3 = new JFXTreeTableColumn<>("Prescription");
    column3.setCellValueFactory(param -> param.getValue().getValue().getPrescriptionProperty());

    JFXTreeTableColumn<Prescription, String> column4 = new JFXTreeTableColumn<>("Pharmacy");
    column4.setCellValueFactory(param -> param.getValue().getValue().getPharmacyProperty());

    JFXTreeTableColumn<Prescription, String> column8 = new JFXTreeTableColumn<>("Doctor");
    column8.setCellValueFactory(param -> param.getValue().getValue().getDoctorNameProperty());

    return new ArrayList<>(List.of(column1, column2, column3, column4, column8));
  }
}
