package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Patient;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class PatientInfoController extends AbstractController {
  @FXML private GridPane patientServiceTablePane;
  @FXML private JFXButton addPatientButton;
  @FXML private JFXButton editPatientButton;

  @FXML private SimpleTableView<Patient> patientTable;

  public PatientInfoController() {}

  public void initialize() {
    if (patientDatabase.getSizePatients() == -1) {
      patientDatabase.dropTables();
      patientDatabase.createTables();
      // patientDatabase.readPatientCSV();
    } else if (patientDatabase.getSizePatients() == 0) {
      patientDatabase.removeAllPatients();
      // patientDatabase.readFlowersCSV();
    }

    // Setup any icons we want to use

    // Setup Table
    patientTable = new SimpleTableView<>(new Patient("", "", "", "", 0, 0, 0.0, "", "", ""), 20.0);
    patientServiceTablePane.getChildren().add(patientTable);

    update();
  }

  public void update() {
    try {
      patientTable.clear();

      patientTable.add(patientDatabase.patientOl());
    } catch (Exception e) {
      e.printStackTrace();
      // DialogUtil.simpleErrorDialog(
      //         dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }
}
