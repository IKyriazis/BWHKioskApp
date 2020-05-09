package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamP.APIController;
import edu.wpi.cs3733.d20.teamP.ServiceException;
import javafx.fxml.FXML;
import javax.swing.*;

public class FoodAdminController extends AbstractController implements IDialogController {
  private JFXDialog dialog;

  @FXML private JFXTextField txtNum;
  @FXML private JFXTextField txtName;
  @FXML private JFXComboBox comboRole;

  public FoodAdminController() {}

  public void initialize() {
    comboRole.getItems().addAll("Cafeteria", "Starbucks", "Both");
  }

  public void addEmployee() {
    int i = comboRole.getSelectionModel().getSelectedIndex() + 1;
    APIController.addEmployee(txtNum.getText(), txtName.getText(), i);
  }

  public void runAdmin() {
    // open window
    try {
      APIController.run(-1, -1, 1280, 720, null, null, null);
    } catch (ServiceException serviceException) {
      serviceException.printStackTrace();
    }
  }

  public void purge() {
    APIController.clearDatabase();
  }

  public void close() {
    dialog.close();
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
