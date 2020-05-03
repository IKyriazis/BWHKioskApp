package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

@SuppressWarnings("DuplicatedCode")
public class InternalTransportRequestController extends AbstractRequestController {
  @FXML private Label headerLabel;
  @FXML private JFXComboBox<Node> nodeBox;
  @FXML private JFXComboBox<Employee> employeeBox;
  @FXML private JFXComboBox<String> categoryBox;
  @FXML private JFXButton submitButton;
  @FXML private JFXTextArea descriptionArea;

  public void initialize() {
    // Setup icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.LAPTOP));
    submitButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Limit text length in description area to 100 chars
    setupDescriptionArea(descriptionArea);

    // Set up node box
    setupNodeBox(nodeBox, submitButton);

    // Set up employee box
    setupEmployeeBox(employeeBox);

    // Set up category box
    categoryBox.getItems().addAll("Wifi", "Email", "Login", "Kiosk", "Pager", "Other");
  }

  public void pressedSubmit() {
    Employee selectedEmployee = employeeBox.getSelectionModel().getSelectedItem();
    Node selectedNode = getSelectedNode(nodeBox);
    String selectedCategory = categoryBox.getSelectionModel().getSelectedItem();

    if (selectedEmployee == null
        || selectedNode == null
        || selectedCategory == null
        || selectedCategory.isEmpty()) {
      DialogUtil.simpleInfoDialog(
          "Empty Fields", "Please fully fill out the service request form and try again.");
      return;
    }

    String l =
        serviceDatabase.addServiceReq(
            ServiceType.IT_TICKET,
            selectedNode.getLongName(),
            descriptionArea.getText(),
            selectedCategory);
    if (l == null) {
      DialogUtil.simpleErrorDialog("Database Error", "Cannot add request");
    } else {
      DialogUtil.simpleInfoDialog("Requested", "Request " + l + " Has Been Added");
      SceneSwitcherController.popScene();
    }

    nodeBox.getSelectionModel().clearSelection();
    descriptionArea.clear();
    employeeBox.getSelectionModel().clearSelection();
    categoryBox.getSelectionModel().clearSelection();
  }
}
