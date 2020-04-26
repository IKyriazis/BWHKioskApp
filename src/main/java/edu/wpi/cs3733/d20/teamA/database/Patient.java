package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Patient implements ITableable<Patient> {
  @Setter private SimpleIntegerProperty patientID;
  @Setter private SimpleStringProperty firstName;
  @Setter private SimpleStringProperty lastName;
  @Setter private SimpleStringProperty healthInsurance;
  @Setter private SimpleStringProperty dateOfBirth;
  @Setter private SimpleIntegerProperty heightFeet;
  @Setter private SimpleIntegerProperty heightInches;
  @Setter private SimpleDoubleProperty weight;
  @Setter private SimpleStringProperty symptoms;
  @Setter private SimpleStringProperty allergies;
  @Setter private SimpleStringProperty currentMeds;

  public Patient(
      int patientID,
      String firstName,
      String lastName,
      String healthInsurance,
      String dateOfBirth,
      int heightFeet,
      int heightInches,
      double weight,
      String symptoms,
      String allergies,
      String currentMeds) {
    this.patientID = new SimpleIntegerProperty(patientID);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.healthInsurance = new SimpleStringProperty(healthInsurance);
    this.dateOfBirth = new SimpleStringProperty(dateOfBirth);
    this.heightFeet = new SimpleIntegerProperty(heightFeet);
    this.heightInches = new SimpleIntegerProperty(heightInches);
    this.weight = new SimpleDoubleProperty(weight);
    this.symptoms = new SimpleStringProperty(symptoms);
    this.allergies = new SimpleStringProperty(allergies);
    this.currentMeds = new SimpleStringProperty(currentMeds);
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

  public int getHeightFeet() {
    return heightFeet.get();
  }

  public SimpleIntegerProperty heightFeetProperty() {
    return heightFeet;
  }

  public int getHeightInches() {
    return heightInches.get();
  }

  public SimpleIntegerProperty heightInchesProperty() {
    return heightInches;
  }

  public double getWeight() {
    return weight.get();
  }

  public SimpleDoubleProperty weightProperty() {
    return weight;
  }

  public String getSymptoms() {
    return symptoms.get();
  }

  public SimpleStringProperty symptomsProperty() {
    return symptoms;
  }

  public String getAllergies() {
    return allergies.get();
  }

  public SimpleStringProperty allergiesProperty() {
    return allergies;
  }

  public String getCurrentMeds() {
    return currentMeds.get();
  }

  public SimpleStringProperty currentMedsProperty() {
    return currentMeds;
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

    JFXTreeTableColumn<Patient, Integer> column5 = new JFXTreeTableColumn<>("Height in Feet");
    column5.setCellValueFactory(
        param -> param.getValue().getValue().heightFeetProperty().asObject());

    JFXTreeTableColumn<Patient, Integer> column6 = new JFXTreeTableColumn<>("Height in Inches");
    column6.setCellValueFactory(
        param -> param.getValue().getValue().heightInchesProperty().asObject());

    JFXTreeTableColumn<Patient, Double> column7 = new JFXTreeTableColumn<>("Weight in Pounds");
    column7.setCellValueFactory(param -> param.getValue().getValue().weightProperty().asObject());

    JFXTreeTableColumn<Patient, String> column8 = new JFXTreeTableColumn<>("Symptoms");
    column8.setCellValueFactory(param -> param.getValue().getValue().symptomsProperty());

    JFXTreeTableColumn<Patient, String> column9 = new JFXTreeTableColumn<>("Allergies");
    column9.setCellValueFactory(param -> param.getValue().getValue().allergiesProperty());

    JFXTreeTableColumn<Patient, String> column10 = new JFXTreeTableColumn<>("Current Medications");
    column10.setCellValueFactory(param -> param.getValue().getValue().currentMedsProperty());

    return new ArrayList<>(
        List.of(
            column0, column1, column2, column3, column4, column5, column6, column7, column8,
            column9, column10));
  }
}
