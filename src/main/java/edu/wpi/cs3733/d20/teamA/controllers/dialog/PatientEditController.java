package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.patient.Patient;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class PatientEditController extends AbstractController implements IDialogController {

  private final boolean modify;
  @FXML private JFXTextField txtFirstName;
  @FXML private JFXTextField txtLastName;
  @FXML private JFXTextField txtHealthInsurance;
  @FXML private JFXTextField txtDateOfBirth;
  @FXML private StackPane dialogDialogStackPane;
  @FXML private Label errorLabel;

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
    doneButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

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

    String first = txtFirstName.getText();
    String last = txtLastName.getText();
    String healthIns = txtHealthInsurance.getText();
    String birthday = txtDateOfBirth.getText();

    try {

      if (checkDOBFormat(birthday)) {

        if (!modify) {
          super.patientDatabase.addPatient(first, last, healthIns, birthday);
        } else {
          super.patientDatabase.updatePatient(
              myPatient.getPatientID(), first, last, healthIns, birthday);
        }
        dialog.close();

      } else {
        DialogUtil.simpleInfoDialog(
            "Incorrect Date of Birth Format.", "Use MM/DD/YYYY format in birth date field");
        //        JFXDialog errorDialog =
        //            new JFXDialog(dialogDialogStackPane, errorLabel,
        // JFXDialog.DialogTransition.TOP);
        //        errorDialog.show();
      }

    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public boolean checkDOBFormat(String dOB) {
    boolean slashCheck = false;
    boolean monthCheck = false;
    boolean dayCheck = false;
    boolean yearCheck = false;

    if (dOB.length() != 10) return false;
    else {

      try {
        int month = Integer.parseInt(dOB.substring(0, 2));
        int day = Integer.parseInt(dOB.substring(3, 5));
        int year = Integer.parseInt(dOB.substring(6, 10));
        slashCheck = dOB.substring(2, 3).equals("/") && dOB.substring(5, 6).equals("/");
        monthCheck = month > 0 && month < 13;
        dayCheck = day > 0 && day < 31;
        yearCheck = year > 0 && year < 2021;

      } catch (NumberFormatException e) {
        e.printStackTrace();
        return false;
      }
    }

    return slashCheck && monthCheck && dayCheck && yearCheck;
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
