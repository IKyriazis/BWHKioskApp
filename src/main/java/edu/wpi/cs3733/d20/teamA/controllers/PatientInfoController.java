package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.PatientEditController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Patient;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.awt.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class PatientInfoController extends AbstractController {
  @FXML private Label lblTitle;
  @FXML private JFXButton addPatientButton;
  @FXML private JFXButton editPatientButton;
  @FXML private JFXButton deletePatientButton;

  @FXML private SimpleTableView<Patient> patientTable;

  @FXML private AnchorPane patientPane;
  @FXML private StackPane dialogStackPane;
  @FXML private GridPane patientTablePane;

  public PatientInfoController() {}

  public void initialize() {
    if (patientDatabase.getSize() == -1) {
      patientDatabase.dropTables();
      patientDatabase.createTables();
      // patientDatabase.readPatientCSV();
    } else if (patientDatabase.getSize() == 0) {
      patientDatabase.removeAll();
      // patientDatabase.readFlowersCSV();
    }

    // Setup icons
    lblTitle.setGraphic(new FontIcon(FontAwesomeSolid.USER_PLUS));

    addPatientButton.setGraphic(new FontIcon(FontAwesomeSolid.PLUS_SQUARE));
    editPatientButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_SQUARE));
    deletePatientButton.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_SQUARE));

    // Setup Table
    patientTable = new SimpleTableView<>(new Patient("", "", "", "", ""), 80.0);
    patientTablePane.getChildren().add(patientTable);
    // Double click a row in the order table to bring up the dialog for that order
    patientTable.setRowFactory(
        tv -> {
          TreeTableRow<Patient> row = new TreeTableRow<>();
          row.setOnMouseClicked(
              event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                  editPatient();
                }
              });
          return row;
        });

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

      patientTable.add(patientDatabase.getObservableList());
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

  public void deletePatient() {
    Patient patient = patientTable.getSelected();
    if (patient != null) {
      String id = patient.getPatientID();

      try {
        super.patientDatabase.deletePatient(id);
      } catch (Exception e) {
        e.printStackTrace();
        DialogUtil.simpleErrorDialog(
            dialogStackPane, "Error Deleting Patient", "Could not delete patient: " + patient);
      }

      update();
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Patient Selected",
          "Please select a patient by clicking a row in the table");
    }
  }
}
