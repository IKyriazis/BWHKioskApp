package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;

public class LaundryRequestController extends AbstractController {
  @FXML private Label headerLabel;
  @FXML private JFXComboBox<Node> nodeBox;
  @FXML private JFXButton submitButton;
  @FXML private JFXTextArea descriptionArea;

  public void initialize() {
    headerLabel.setGraphic(new FontIcon(Material.LOCAL_LAUNDRY_SERVICE));

    // Limit text length in description area to 100 chars
    descriptionArea
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 100) {
                descriptionArea.setText(newValue.substring(0, 100));
              }
            });

    // Set up node box
    nodeBox.setItems(Graph.getInstance().getNodeObservableList());
    nodeBox
        .getEditor()
        .setOnKeyTyped(
            new NodeAutoCompleteHandler(
                nodeBox, submitButton, Graph.getInstance().getNodeObservableList()));
  }

  public void pressedSubmit() {
    Optional<Node> selected =
        nodeBox.getItems().stream()
            .filter(node -> node.toString().equals(nodeBox.getEditor().getText()))
            .findFirst();

    if (selected.isPresent()) {
      String loc = selected.get().getLongName();

      String l =
          serviceDatabase.addServiceReq(ServiceType.LAUNDRY, loc, descriptionArea.getText(), "");
      if (l == null) {
        DialogUtil.simpleErrorDialog("Error", "Cannot add request");
      } else {
        DialogUtil.simpleInfoDialog("Requested", "Request " + l + " Has Been Added");
        SceneSwitcherController.popScene();
      }
    } else {
      DialogUtil.simpleInfoDialog(
          "No Room Selected", "Please select a room in the dropdown and try again");
    }
  }
}
