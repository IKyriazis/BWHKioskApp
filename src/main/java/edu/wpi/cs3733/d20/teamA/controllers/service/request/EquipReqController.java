package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

@SuppressWarnings("DuplicatedCode")
public class EquipReqController extends AbstractRequestController {
  @FXML private Label headerLabel;
  @FXML private JFXComboBox<Node> nodeBox;
  @FXML private JFXComboBox<String> priCombo;
  @FXML private JFXTextField itemField;
  @FXML private JFXTextField qtyField;
  @FXML private JFXButton submitButton;

  public void initialize() {
    // Setup icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.STETHOSCOPE));
    submitButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    qtyField.setTextFormatter(InputFormatUtil.getIntFilter());

    // Set up node box
    setupNodeLocationBox(nodeBox, submitButton);

    // Set up priority box
    priCombo.getItems().addAll("High", "Medium", "Low");
  }

  public void pressedSubmit() {
    Node selectedNode = getSelectedNode(nodeBox);
    String selectedCategory = priCombo.getSelectionModel().getSelectedItem();

    if (selectedNode == null
        || selectedCategory == null
        || selectedCategory.isEmpty()
        || itemField.getText().isEmpty()
        || qtyField.getText().isEmpty()) {
      DialogUtil.simpleInfoDialog(
          "Empty Fields", "Please fully fill out the service request form and try again.");
      return;
    }

    String l =
        serviceDatabase.addServiceReq(
            ServiceType.EQUIPMENT,
            selectedNode.toString(),
            null,
            itemField.getText()
                + "|"
                + qtyField.getText()
                + "|"
                + priCombo.getSelectionModel().getSelectedItem());
    if (l == null) {
      DialogUtil.simpleErrorDialog("Database Error", "Cannot add request");
    } else {
      DialogUtil.simpleInfoDialog("Requested", "Request " + l + " Has Been Added");
      SceneSwitcherController.popScene();
    }

    nodeBox.setValue(null);
    itemField.clear();
    qtyField.clear();
    priCombo.getSelectionModel().clearSelection();
  }
}
