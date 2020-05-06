package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.DialogMaker;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.patient.Patient;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

  @FXML private GridPane patientPane;
  @FXML private StackPane dialogStackPane;
  @FXML private GridPane patientTablePane;

  public PatientInfoController() {}

  public void initialize() {

    // Setup icons
    lblTitle.setGraphic(new FontIcon(FontAwesomeSolid.USER));

    addPatientButton.setGraphic(new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
    editPatientButton.setGraphic(new FontIcon(FontAwesomeSolid.EDIT));
    deletePatientButton.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_CIRCLE));

    // Setup Table
    patientTable = new SimpleTableView<>(new Patient("", "", "", "", ""), 80.0);
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

      patientTable.add(patientDatabase.getObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Failed to update patient");
    }
  }

  public void addPatient() {
    DialogMaker maker = new DialogMaker();
    maker.makePatientDialog(this);
    /*DialogUtil.complexDialog(
       "Add Patient",
       "views/AddPatientPopup.fxml",
       true,
       event -> update(),
       new PatientEditController());

    */
  }

  public void editPatient() {
    Patient patient = patientTable.getSelected();
    if (patient != null) {
      DialogMaker maker = new DialogMaker();
      maker.makePatientDialog(this);
      /*
      PatientEditController controller = new PatientEditController(patient);
      DialogUtil.complexDialog(
          "Edit Patient", "views/AddPatientPopup.fxml", true, event -> update(), controller);

       */
    } else {
      DialogUtil.simpleInfoDialog(
          "No Patient Selected", "Please select a patient by clicking a row in the table");
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
            "Error Deleting Patient", "Could not delete patient: " + patient);
      }

      update();
    } else {
      DialogUtil.simpleInfoDialog(
          "No Patient Selected", "Please select a patient by clicking a row in the table");
    }
  }
}
