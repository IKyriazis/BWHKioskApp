package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.PatientEditController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Patient;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class PatientInfoController extends AbstractController {
  @FXML private JFXButton addPatientButton;
  @FXML private JFXButton editPatientButton;

  @FXML private SimpleTableView<Patient> patientTable;

  @FXML private AnchorPane patientPane;
  @FXML private StackPane dialogStackPane;
  @FXML private GridPane patientTablePane;

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

    // Setup button icons
    addPatientButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE));
    editPatientButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE));

    // Setup Table
    patientTable = new SimpleTableView<>(new Patient(0, "", "", "", ""), 80.0);
    patientTablePane.getChildren().add(patientTable);

    // Add tab switch update listener
    patientPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    update();
  }

  public void update() {
    try {
      patientTable.clear();

      patientTable.add(patientDatabase.patientOl());
    } catch (Exception e) {
      e.printStackTrace();
      // DialogUtil.simpleErrorDialog(
      //       dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }

  public void addPatient() {
    DialogUtil.complexDialog(
        dialogStackPane,
        "Add Patient",
        "views/AddPatientPopup.fxml",
        false,
        event -> update(),
        new PatientEditController());
  }

  public void editPatient() {
    Patient patient = patientTable.getSelected();
    if (patient != null) {
      // Figure out whether any outstanding orders depend on this flower type, in which case we
      // can't change the name / type

      PatientEditController controller = new PatientEditController(patient);
      DialogUtil.complexDialog(
          dialogStackPane,
          "Edit Patient",
          "views/AddPatientPopup.fxml",
          false,
          event -> update(),
          controller);
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Patient Selected",
          "Please select a patient by clicking a row in the table");
    }
  }
}
