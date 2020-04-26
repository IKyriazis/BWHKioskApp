package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Patient;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.SneakyThrows;

public class PatientEditController extends AbstractController implements IDialogController {

    private final boolean modify;
    @FXML private JFXTextField txtPatientID;
    @FXML private JFXTextField txtFirstName;
    @FXML private JFXTextField txtLastName;
    @FXML private JFXTextField txtHealthInsurance;
    @FXML private JFXTextField txtDateOfBirth;
    @FXML private JFXTextField txtHeightFeet;
    @FXML private JFXTextField txtHeightInches;
    @FXML private JFXTextField txtWeight;
    @FXML private JFXTextField txtSymptoms;
    @FXML private JFXTextField txtAllergies;
    @FXML private JFXTextField txtCurrentMeds;

    @FXML private JFXButton doneButton;

    private Patient myPatient;
    private JFXDialog dialog;


    @SneakyThrows
    public PatientEditController() {
        super();
        modify = false;
    }

    @SneakyThrows
    public PatientEditController(Patient p) {
        super();

        this.modify = true;
        this.myPatient = p;
    }

    public void initialize() {
        // Set formatters to restrict input in boxes
        txtPatientID.setTextFormatter(InputFormatUtil.getIntFilter());
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

        txtHeightFeet.setTextFormatter(InputFormatUtil.getIntFilter());
        txtHeightInches.setTextFormatter(InputFormatUtil.getIntFilter());
        txtWeight.setTextFormatter(InputFormatUtil.getDoubleFilter());
        txtSymptoms
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue.length() > 15) {
                                txtSymptoms.setText(newValue.substring(0, 15));
                            }
                        });
        txtAllergies
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue.length() > 15) {
                                txtAllergies.setText(newValue.substring(0, 15));
                            }
                        });
        txtCurrentMeds
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue.length() > 15) {
                                txtCurrentMeds.setText(newValue.substring(0, 15));
                            }
                        });

        if (modify) {
            txtHealthInsurance.setText(myPatient.getHealthInsurance());
            txtHeightFeet.setText(String.valueOf(myPatient.getHeightFeet()));
            txtHeightInches.setText(String.valueOf(myPatient.getHeightInches()));
            txtWeight.setText(String.valueOf(myPatient.getWeight()));
            txtSymptoms.setText(myPatient.getSymptoms());
            txtAllergies.setText(myPatient.getAllergies());
            txtCurrentMeds.setText(myPatient.getCurrentMeds());
        }


        doneButton.setOnAction(this::isDone);

        // Set button icon
        doneButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));
    }

    @FXML
    public void isDone(ActionEvent e) {
        if (txtPatientID.getText().isEmpty()
                || txtFirstName.getText().isEmpty()
                || txtLastName.getText().isEmpty()
                || txtHealthInsurance.getText().isEmpty()
                || txtDateOfBirth.getText().isEmpty()
                || txtHeightFeet.getText().isEmpty()
                || txtHeightInches.getText().isEmpty()
                || txtWeight.getText().isEmpty()
                || txtSymptoms.getText().isEmpty()
                || txtAllergies.getText().isEmpty()
                || txtCurrentMeds.getText().isEmpty()) {
            return;
        }

        try {
            int id = Integer.parseInt(txtPatientID.getText());
            String first = txtFirstName.getText();
            String last = txtLastName.getText();
            String healthIns = txtHealthInsurance.getText();
            String birthday = txtDateOfBirth.getText();
            int heightF = Integer.parseInt(txtHeightFeet.getText());
            int heightI = Integer.parseInt(txtHeightInches.getText());
            double weigh = Double.parseDouble(txtWeight.getText());
            String symps = txtSymptoms.getText();
            String allergy = txtAllergies.getText();
            String meds = txtCurrentMeds.getText();

            if (!modify) {
                super.patientDatabase.addPatient(id, first, last, healthIns, birthday, heightF, heightI, weigh, symps, allergy, meds);
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
