package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class CreateAcctController extends AbstractController {

  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField uName;
  @FXML private JFXTextField title;
  @FXML private JFXPasswordField pass;
  @FXML private JFXPasswordField cPass;
  @FXML private StackPane dialogPane;
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
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Empty fields",
          "You left some fields empty. Please make sure they are all filled.");
      return;
    }
    if (eDB.uNameExists(uName.getText())) {
      // make popup that says username already exists
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Invalid Username",
          "The username you have chosen is already taken. Please choose another.");
      return;
    }
    if (pass.getText().length() < 8) {
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Invalid Password",
          "Please make sure your password is at least 8 characters long.");
      return;
    }
    if (!cPass.getText().equals(pass.getText())) {
      // make popup that says passwords are not the same
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Passwords Don't Match",
          "Please make sure that the password you entered in the confirm password field matches your intended password.");
      return;
    }
    if (!eDB.checkSecurePass(pass.getText())) {
      // make popup that says make sure password includes a number, lowercase letter, and uppercase
      // letter, and it's
      // less than 72 characters
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Invalid Password",
          "Please create a password that includes a number, lowercase letter, and uppercase letter. Shorter than 72 characters.");
      return;
    }
    if (eDB.addEmployee(
            fName.getText(), lName.getText(), uName.getText(), cPass.getText(), title.getText())
        != "") {
      // print that account has been created successfully
      DialogUtil.simpleErrorDialog(
          dialogPane, "Account Created", "You have successfully created an account.");
    } else {
      // print that for some reason the account couldn't be added
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Account creation failed",
          "For some reason we could not create your account.");
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
