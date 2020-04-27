package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.SneakyThrows;

public class EmployeeDialogController extends AbstractController implements IDialogController {

  @FXML private JFXTextField txtFirst;
  @FXML private JFXTextField txtLast;

  @FXML private JFXButton doneButton;
  @FXML private JFXButton cancelButton;

  private JFXDialog dialog;

  @SneakyThrows
  public EmployeeDialogController() {
    super();
  }

  public void initialize() {
    // Set formatters to restrict input in boxes
    txtFirst
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtFirst.setText(newValue.substring(0, 50));
              }
            });
    txtLast
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 15) {
                txtLast.setText(newValue.substring(0, 50));
              }
            });

    // Set button icon
    doneButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));
    cancelButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TIMES));
  }

  // Scene switch & database addNode
  @FXML
  public void addEmployee(ActionEvent e) {
    if (txtFirst.getText().isEmpty() || txtLast.getText().isEmpty()) {
      return;
    }

    try {
      String f = txtFirst.getText();
      String l = txtLast.getText();
      super.flDatabase.addEmployee(f, l);

      dialog.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }

  public void cancel() {
    dialog.close();
  }
}
