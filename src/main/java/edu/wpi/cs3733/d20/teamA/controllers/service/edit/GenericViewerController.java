package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.service.request.AbstractRequestController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import java.util.ArrayList;
import javafx.fxml.FXML;

public class GenericViewerController extends AbstractRequestController {
  @FXML private JFXTextField requestIDField;
  @FXML private JFXComboBox<String> nodeBox;
  @FXML private JFXTextField requestorField;
  @FXML private JFXComboBox<Employee> assigneeBox;
  @FXML private JFXTextField timeStampField;
  @FXML private JFXComboBox<String> statusBox;
  @FXML private JFXTextArea descriptionArea;

  public void fillFields(ServiceRequest request) {
    requestIDField.setText(request.getReqID());

    nodeBox.getItems().add(request.getLocation());
    nodeBox.getSelectionModel().select(request.getLocation());

    requestorField.setText(request.getMadeReqName());

    setupEmployeeBox(assigneeBox);
    // Locate employee object corresponding to assigned
    eDB.getObservableList().stream()
        .filter(employee1 -> employee1.getUsername().equals(request.getMadeReqName()))
        .findFirst()
        .ifPresent(value -> assigneeBox.getSelectionModel().select(value));

    timeStampField.setText(request.getTimestamp());

    statusBox.getItems().add(request.getStatus());
    statusBox.getSelectionModel().select(request.getStatus());

    descriptionArea.setText(request.getDescription());
  }

  protected void setStatusList(ArrayList<String> statusList) {
    statusList.forEach(
        s -> {
          if (!statusBox.getItems().contains(s)) {
            statusBox.getItems().add(s);
          }
        });
  }
}
