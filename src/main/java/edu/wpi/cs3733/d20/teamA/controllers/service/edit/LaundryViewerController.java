package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;

public class LaundryViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;

  public LaundryViewerController(ServiceRequest req) {
    super(req);
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(Material.LOCAL_LAUNDRY_SERVICE));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

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
  }

  @Override
  public void reset(ServiceRequest req) {
    super.reset(req);

    // Update standard fields
    genericController.fillFields(req);
  }
}
