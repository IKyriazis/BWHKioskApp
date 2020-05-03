package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.service.request.AbstractRequestController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;

public class GenericViewerController extends AbstractRequestController {
  @FXML private JFXTextField requestIDField;
  @FXML private JFXTextField locationField;
  @FXML private JFXTextField requestorField;
  @FXML private JFXComboBox<Employee> assigneeBox;
  @FXML private JFXTextField timeStampField;
  @FXML private JFXComboBox<String> statusBox;
  @FXML private JFXTextArea descriptionArea;

  public void fillFields(ServiceRequest request) {
    requestIDField.setText(request.getReqID());
    locationField.setText(request.getLocation());
    requestorField.setText(request.getMadeReqName());

    setupEmployeeBox(assigneeBox);
    // Locate employee object corresponding to assigned
    eDB.getObservableList().stream()
        .filter(employee1 -> employee1.getUsername().equals(request.getDidReqName()))
        .findFirst()
        .ifPresent(value -> assigneeBox.getSelectionModel().select(value));

    timeStampField.setText(request.getTimestamp());

    if (!statusBox.getItems().contains(request.getStatus())) {
      statusBox.getItems().add(request.getStatus());
    }
    statusBox.getSelectionModel().select(request.getStatus());

    setupDescriptionArea(descriptionArea);
    descriptionArea.setText(request.getDescription());
  }

  protected void fillStandardStatusList() {
    ArrayList<String> statuses =
        new ArrayList<>(List.of("Request Made", "In Progress", "Completed"));
    setStatusList(statuses);
  }

  protected void setStatusList(ArrayList<String> statusList) {
    statusList.forEach(
        s -> {
          if (!statusBox.getItems().contains(s)) {
            statusBox.getItems().add(s);
          }
        });
  }

  protected boolean updateRequestFromFields(ServiceRequest req) {
    boolean success = true;
    if (assigneeBox.getSelectionModel().getSelectedItem() != null
        && !assigneeBox
            .getSelectionModel()
            .getSelectedItem()
            .getUsername()
            .equals(req.getDidReqName())) {
      success =
          success
              && serviceDatabase.setAssignedEmployee(
                  req.getReqID(), assigneeBox.getSelectionModel().getSelectedItem().getUsername());
    }

    if (!statusBox.getSelectionModel().getSelectedItem().equals(req.getStatus())) {
      success =
          success
              && serviceDatabase.setStatus(
                  req.getReqID(), statusBox.getSelectionModel().getSelectedItem());
    }

    if (descriptionArea.getText() != null
        && !descriptionArea.getText().equals(req.getDescription())) {
      success =
          success && serviceDatabase.setDescription(req.getReqID(), descriptionArea.getText());
    }

    return success;
  }
}
