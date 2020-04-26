package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PatientEditController extends AbstractController implements IDialogController {

  private final boolean modify;
  @FXML private JFXTextField txtFirstName;
  @FXML private JFXTextField txtLastName;
  @FXML private JFXTextField txtHealthInsurance;
  @FXML private JFXTextField txtDateOfBirth;

  @FXML private JFXButton doneButton;

  private Patient myPatient;
  private JFXDialog dialog;

  public PatientEditController() {
    super();
    modify = false;
  }

  public PatientEditController(Patient p) {
    super();

    this.modify = true;
    this.myPatient = p;
  }

  public void initialize() {
    // Set formatters to restrict input in boxes
    txtFirstName
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtFirstName.setText(newValue.substring(0, 15));
              }
            });

    txtLastName
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtLastName.setText(newValue.substring(0, 15));
              }
            });

    txtHealthInsurance
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtHealthInsurance.setText(newValue.substring(0, 15));
              }
            });

    txtDateOfBirth
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtDateOfBirth.setText(newValue.substring(0, 15));
              }
            });

    if (modify) {
      txtFirstName.setText(myPatient.getFirstName());
      txtLastName.setText(myPatient.getLastName());
      txtHealthInsurance.setText(myPatient.getHealthInsurance());
      txtDateOfBirth.setText(myPatient.getDateOfBirth());
    }

    doneButton.setOnAction(this::isDone);

    // Set button icon
    doneButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));

    txtFirstName.setLabelFloat(true);
    txtLastName.setLabelFloat(true);
    txtDateOfBirth.setLabelFloat(true);
    txtHealthInsurance.setLabelFloat(true);
  }

  @FXML
  public void isDone(ActionEvent e) {
    if (txtFirstName.getText().isEmpty()
        || txtLastName.getText().isEmpty()
        || txtHealthInsurance.getText().isEmpty()
        || txtDateOfBirth.getText().isEmpty()) {
      return;
    }

    if (myPatient == null) {
      myPatient =
          new Patient(
              patientDatabase.getSizePatients() + 1,
              txtFirstName.getText(),
              txtLastName.getText(),
              txtHealthInsurance.getText(),
              txtDateOfBirth.getText());
    }

    try {
      int id = myPatient.getPatientID();
      String first = txtFirstName.getText();
      String last = txtLastName.getText();
      String healthIns = txtHealthInsurance.getText();
      String birthday = txtDateOfBirth.getText();

      if (!modify) {
        super.patientDatabase.addPatient(id, first, last, healthIns, birthday);
      } else {
        super.patientDatabase.updatePatient(
            myPatient.getPatientID(), myPatient.getFirstName(), myPatient.getLastName(), healthIns);
      }

      dialog.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
