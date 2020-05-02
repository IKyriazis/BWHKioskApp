package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.inventory.ItemType;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.fxml.FXML;

public class InterpreterDialogController extends AbstractController implements IDialogController {
  @FXML private JFXTextField nameField;
  @FXML private JFXTextField languageField;
  @FXML private JFXButton doneButton;

  private JFXDialog dialog;

  @FXML
  public void initialize() {
    nameField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 64) {
                nameField.setText(newValue.substring(0, 64));
              }
            });

    languageField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 64) {
                languageField.setText(newValue.substring(0, 64));
              }
            });
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }

  @FXML
  public void clickedDone() {
    if (nameField.getText().isEmpty() || languageField.getText().isEmpty()) {
      return;
    }

    String success =
        inventoryDatabase.addItem(
            ItemType.INTERPRETER, nameField.getText(), 0, 0.0, null, languageField.getText());

    if (success == null) // Set up actual error checking
    {
      DialogUtil.simpleErrorDialog(
          dialog.getDialogContainer(),
          "Database Error",
          "Failed to add interpreter to database. Please try again later.");
    }
    dialog.close();
  }
}
