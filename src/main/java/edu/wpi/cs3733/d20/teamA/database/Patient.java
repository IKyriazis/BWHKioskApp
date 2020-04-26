package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Patient implements ITableable<Patient> {
  @Setter private SimpleIntegerProperty patientID;
  @Setter private SimpleStringProperty firstName;
  @Setter private SimpleStringProperty lastName;
  @Setter private SimpleStringProperty healthInsurance;
  @Setter private SimpleStringProperty dateOfBirth;

  public Patient(
      int patientID,
      String firstName,
      String lastName,
      String healthInsurance,
      String dateOfBirth) {
    this.patientID = new SimpleIntegerProperty(patientID);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.healthInsurance = new SimpleStringProperty(healthInsurance);
    this.dateOfBirth = new SimpleStringProperty(dateOfBirth);
  }

  public String getFirstName() {
    return firstName.get();
  }

  public SimpleStringProperty firstNameProperty() {
    return firstName;
  }

  public String getLastName() {
    return lastName.get();
  }

  public SimpleStringProperty lastNameProperty() {
    return lastName;
  }

  public String getHealthInsurance() {
    return healthInsurance.get();
  }

  public SimpleStringProperty healthInsuranceProperty() {
    return healthInsurance;
  }

  public String getDateOfBirth() {
    return dateOfBirth.get();
  }

  public SimpleStringProperty dateOfBirthProperty() {
    return dateOfBirth;
  }

  public int getPatientID() {
    return patientID.get();
  }

  public SimpleIntegerProperty patientIDProperty() {
    return patientID;
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Patient, ?>> getColumns() {
    JFXTreeTableColumn<Patient, Integer> column0 = new JFXTreeTableColumn<>("Patient ID");
    column0.setCellValueFactory(
        param -> param.getValue().getValue().patientIDProperty().asObject());

    JFXTreeTableColumn<Patient, String> column1 = new JFXTreeTableColumn<>("First Name");
    column1.setCellValueFactory(param -> param.getValue().getValue().firstNameProperty());

    JFXTreeTableColumn<Patient, String> column2 = new JFXTreeTableColumn<>("Last Name");
    column2.setCellValueFactory(param -> param.getValue().getValue().lastNameProperty());

    JFXTreeTableColumn<Patient, String> column3 = new JFXTreeTableColumn<>("Health Insurance");
    column3.setCellValueFactory(param -> param.getValue().getValue().healthInsuranceProperty());

    JFXTreeTableColumn<Patient, String> column4 = new JFXTreeTableColumn<>("Date Of Birth");
    column4.setCellValueFactory(param -> param.getValue().getValue().dateOfBirthProperty());

    return new ArrayList<>(List.of(column0, column1, column2, column3, column4));
  }
}
