package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class JanitorViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;
  @FXML private JFXComboBox<String> priorityBox;

  private static JanitorViewerController instance;

  public JanitorViewerController(ServiceRequest req) {
    super(req);

    instance = this;
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.BROOM));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Fill out priority box
    priorityBox.getItems().addAll("High", "Medium", "Low");
    priorityBox.getSelectionModel().select(req.getAdditional());

    // Fill out standard service request fields
    genericController.fillFields(req);

    // Set up statuses
    ArrayList<String> statuses =
        new ArrayList<>(List.of("Request Made", "In Progress", "Completed"));
    genericController.setStatusList(statuses);
  }

  public void pressedSave() {
    // Fill standard fields
    genericController.updateRequestFromFields(req);

    // Fill additional
    serviceDatabase.setAdditional(
        req.getReqID(), priorityBox.getSelectionModel().getSelectedItem());
  }

  public static void setReq(ServiceRequest req) {
    if (instance != null) {
      instance.reset(req);
    }
  }
}
