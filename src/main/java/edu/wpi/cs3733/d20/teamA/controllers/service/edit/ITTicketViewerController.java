package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ITTicketViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;
  @FXML private JFXButton deleteButton;
  @FXML private JFXTextField categoryField;

  public ITTicketViewerController(ServiceRequest req) {
    super(req);
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.LAPTOP));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Fill out category field
    categoryField.setText(req.getAdditional());

    // Fill out standard service request fields
    genericController.fillFields(req);

    // Set up statuses
    genericController.fillStandardStatusList();
  }

  public void pressedSave() {
    // Fill standard fields
    genericController.updateRequestFromFields(req);

    // Fill additional
    serviceDatabase.setAdditional(req.getReqID(), categoryField.getText());

    // Fire tab switch event forcing table to update
    headerLabel.fireEvent(new TabSwitchEvent());
  }

  public void pressedDelete() {
    serviceDatabase.deleteServReq(req.getReqID());
    headerLabel.fireEvent(new TabSwitchEvent());
  }

  @Override
  public void reset(ServiceRequest req) {
    super.reset(req);

    // Update category field
    categoryField.setText(req.getAdditional());

    // Update standard fields
    genericController.fillFields(req);
  }
}
