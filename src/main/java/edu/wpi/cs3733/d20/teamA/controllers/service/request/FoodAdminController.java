package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamP.APIController;
import edu.wpi.cs3733.d20.teamP.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javax.swing.*;

public class FoodAdminController extends AbstractController implements IDialogController {
  private JFXDialog dialog;

  @FXML private JFXTextField txtName;
  @FXML private JFXComboBox comboRole;
  @FXML private GridPane employeePane;

  private SimpleTableView employeeTable;

  public FoodAdminController() {}

  public void initialize() {
    comboRole.getItems().addAll("Cafeteria", "Starbucks", "Both");
  }

  public void addEmployee() {
    if (comboRole.getSelectionModel().getSelectedItem() != null && !txtName.getText().equals("")) {
      int i = comboRole.getSelectionModel().getSelectedIndex() + 1;
      // Write to file
      // Do if feel like

      // Add to API
      APIController.addEmployee("FUCK", txtName.getText(), i);

      txtName.setText("");
    }
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
