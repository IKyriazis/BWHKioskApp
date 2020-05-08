package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class InterpreterRequestController extends AbstractRequestController {
  @FXML private Label headerLabel;
  @FXML private JFXComboBox<Node> locationBox;
  @FXML private JFXComboBox<Employee> interpreterBox;
  @FXML private JFXButton submitBtn;

  public void initialize() {
    // Setup icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.GLOBE));
    submitBtn.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Set up node box
    setupNodeBox(locationBox, submitBtn);

    // Set up interpreter box
    setupEmployeeBox(interpreterBox, "interpreter");
  }

  public void pressedSubmit() {
    Node location = getSelectedNode(locationBox);
    Employee interpreter = interpreterBox.getSelectionModel().getSelectedItem();

    if (location != null && interpreter != null) {
      String l =
          serviceDatabase.addServiceReq(
              ServiceType.INTERPRETER_REQ,
              location.toString(),
              interpreter.getUsername(),
              null,
              null);
      if (l == null) {
        DialogUtil.simpleErrorDialog("Database Error", "Cannot add request");
      } else {
        DialogUtil.simpleInfoDialog("Requested", "Request " + l + " Has Been Added");
        SceneSwitcherController.popScene();
      }
    } else {
      DialogUtil.simpleInfoDialog(
          "No Room Selected", "Please select a room in the dropdown and try again");
    }
    locationBox.setValue(null);
    interpreterBox.getSelectionModel().clearSelection();
  }
}
