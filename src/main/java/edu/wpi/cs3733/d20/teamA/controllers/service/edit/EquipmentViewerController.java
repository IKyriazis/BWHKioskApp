package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import edu.wpi.cs3733.d20.teamA.util.InputFormatUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class EquipmentViewerController extends AbstractViewerController {
  @FXML private GenericViewerController genericController;
  @FXML private Label headerLabel;
  @FXML private JFXButton saveButton;
  @FXML private JFXButton deleteButton;
  @FXML private JFXTextField itemField;
  @FXML private JFXComboBox<String> priorityBox;
  @FXML private JFXTextField quantityField;

  public EquipmentViewerController(ServiceRequest req) {
    super(req);
  }

  @Override
  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.STETHOSCOPE));
    saveButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Strip out additional info
    String[] additional = req.getAdditional().split("\\|");

    // Fill out priority box
    priorityBox.getItems().addAll("High", "Medium", "Low");
    priorityBox.getSelectionModel().select(additional[2]);

    // Fill out item field
    itemField.setText(additional[0]);

    // Fill out quantity field
    quantityField.setText(additional[1]);
    quantityField.setTextFormatter(InputFormatUtil.getIntFilter());

    // Fill out standard service request fields
    genericController.fillFields(req);

    // Set up statuses
    genericController.fillStandardStatusList();
  }

  public void pressedSave() {
    // Fill standard fields
    genericController.updateRequestFromFields(req);

    String newAdditional =
        itemField.getText()
            + "|"
            + quantityField.getText()
            + "|"
            + priorityBox.getSelectionModel().getSelectedItem();

    // Fill additional
    serviceDatabase.setAdditional(req.getReqID(), newAdditional);
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

    String[] additional = req.getAdditional().split("\\|");

    // Update priority box
    priorityBox.getSelectionModel().select(additional[2]);

    // Update item field
    itemField.setText(additional[0]);

    // Update quantity field
    quantityField.setText(additional[1]);

    // Update standard fields
    genericController.fillFields(req);
  }
}
