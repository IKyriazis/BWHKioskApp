package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Laundry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class LaundryEditController extends AbstractController implements IDialogController {

  @FXML private GridPane modPane;
  @FXML private JFXComboBox<String> cleanerComboBox;
  @FXML private JFXComboBox<String> progressComboBox;
  @FXML private JFXButton doneButton;

  private JFXDialog dialog;

  private Laundry laundry;

  public LaundryEditController(Laundry laundry) {
    super();

    this.laundry = laundry;
  }

  public void initialize() {
    progressComboBox.getItems().addAll("Requested", "Collected", "Washing", "Drying", "Returned");

    // Will need to connect to employee database later
    cleanerComboBox.getItems().addAll("admin", "staff");

    progressComboBox.getSelectionModel().select(laundry.getProgress());
    if (laundry.getEmployeeWash() != null) {
      cleanerComboBox.getSelectionModel().select(laundry.getEmployeeWash());
    }
    doneButton.setOnAction(this::isDone);
    // Set button icon
    doneButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));
  }

  @FXML
  public void isDone(ActionEvent e) {
    if (progressComboBox.getValue() != null) {
      serviceDatabase.setStatus(laundry.getRequestNum(), progressComboBox.getValue());
    }
    if (cleanerComboBox.getValue() != null) {
      serviceDatabase.setAssignedEmployee(laundry.getRequestNum(), cleanerComboBox.getValue());
    }
    dialog.close();
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
