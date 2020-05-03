package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.sql.Timestamp;
import java.time.LocalTime;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MedicineRequestController extends AbstractRequestController {
  @FXML private Label headerLabel;
  @FXML private JFXTextField patientName;
  @FXML private JFXComboBox<Employee> doctorBox;
  @FXML private JFXTextField medicineField;
  @FXML private JFXComboBox<Node> locationBox;
  @FXML private JFXTimePicker administerTime;
  @FXML private JFXTextArea descriptionArea;
  @FXML private JFXButton submitButton;

  public void initialize() {
    // Setup icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.MEDKIT));
    submitButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Limit text length in description area to 100 chars
    setupDescriptionArea(descriptionArea);

    // Set up employee box
    setupEmployeeBox(doctorBox);

    // Set up node box
    setupNodeBox(locationBox, submitButton);
  }

  @FXML
  public void pressedSubmit() {
    Employee doctor = doctorBox.getSelectionModel().getSelectedItem();
    Node location = getSelectedNode(locationBox);
    if (patientName.getText().isEmpty()
        || doctor == null
        || medicineField.getText().isEmpty()
        || location == null
        || administerTime.getEditor().getText().isEmpty()) {
      DialogUtil.simpleInfoDialog(
          "Empty Fields", "Please fully fill out the service request form and try again.");
      return;
    }

    String patientNameText = patientName.getText();
    String medicine = medicineField.getText();

    Timestamp timestamp = new Timestamp(0);

    try {
      LocalTime time = administerTime.getValue();
      int hour = time.getHour();
      int minute = time.getMinute();

      timestamp.setHours(hour);
      timestamp.setMinutes(minute);
    } catch (Exception e) {
      DialogUtil.simpleInfoDialog("Invalid Time", "Please select a valid time and try again");
      return;
    }

    String additional = patientNameText + "|" + doctor.getUsername() + "|" + medicine;

    String l =
        serviceDatabase.addServiceReq(
            ServiceType.MEDICINE,
            timestamp,
            location.getLongName(),
            descriptionArea.getText(),
            additional);
    if (l == null) {
      DialogUtil.simpleErrorDialog("Database Error", "Cannot add request");
    } else {
      DialogUtil.simpleInfoDialog("Requested", "Request " + l + " Has Been Added");
      SceneSwitcherController.popScene();
    }

    patientName.clear();
    doctorBox.getSelectionModel().clearSelection();
    medicineField.clear();
    locationBox.getSelectionModel().clearSelection();
    administerTime.getEditor().clear();
    descriptionArea.clear();
  }
}
