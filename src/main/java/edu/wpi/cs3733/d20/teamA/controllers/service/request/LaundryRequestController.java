package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;

public class LaundryRequestController extends AbstractRequestController {
  @FXML private GridPane rootPane;
  @FXML private Label headerLabel;
  @FXML private JFXComboBox<Node> nodeBox;
  @FXML private JFXButton submitButton;
  @FXML private JFXTextArea descriptionArea;

  public void initialize() {
    // Setup icons
    headerLabel.setGraphic(new FontIcon(Material.LOCAL_LAUNDRY_SERVICE));
    submitButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Limit text length in description area to 100 chars
    setupDescriptionArea(descriptionArea);

    // Set up node box
    setupNodeLocationBox(nodeBox, submitButton);

    rootPane.addEventHandler(
            TabSwitchEvent.TAB_SWITCH,
            event -> {
              event.consume();
              nodeBox.setValue(null);
              descriptionArea.clear();
            });
  }

  public void pressedSubmit() {
    Node node = getSelectedNode(nodeBox);

    if (node != null) {
      String loc = node.toString();

      String l =
          serviceDatabase.addServiceReq(ServiceType.LAUNDRY, loc, descriptionArea.getText(), "");
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
    nodeBox.setValue(null);
    descriptionArea.clear();
  }
}
