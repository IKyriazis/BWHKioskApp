package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class CreateAcctController extends AbstractController {

  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField uName;
  @FXML private JFXTextField title;
  @FXML private JFXPasswordField pass;
  @FXML private JFXPasswordField cPass;
  @FXML private JFXButton submit;
  @FXML private JFXButton clear;

  public void submitEmployee() {
    if (fName.getText().isEmpty()
        || lName.getText().isEmpty()
        || uName.getText().isEmpty()
        || title.getText().isEmpty()
        || pass.getText().isEmpty()
        || cPass.getText().isEmpty()) {
      // make popup that says one or more fields are empty
      return;
    }
    if (eDB.uNameExists(uName.getText())) {
      // make popup that says username already exists
      return;
    }
    if (cPass.getText() != pass.getText()) {
      // make popup that says passwords are not the same
      return;
    }
    if (!eDB.checkSecurePass(pass.getText())) {
      // make popup that says make sure password includes a number, lowercase letter, and uppercase
      // letter, and it's
      // less than 72 characters
      return;
    }
    if (eDB.addEmployee(
        fName.getText(), lName.getText(), uName.getText(), cPass.getText(), title.getText())) {
      // print that account has been created successfully
    } else {
      // print that for some reason the account couldn't be added
    }
  }

  public void clearFields() {
    fName.clear();
    lName.clear();
    uName.clear();
    title.clear();
    pass.clear();
    cPass.clear();
  }
}
