package edu.wpi.cs3733.d20.teamA.controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.QRDialogController;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.ThreadPool;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class CreateAcctController extends AbstractController {

  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField uName;
  @FXML private JFXPasswordField pass;
  @FXML private JFXPasswordField cPass;
  @FXML private StackPane dialogPane;
  @FXML private JFXCheckBox addRFID;
  @FXML private JFXComboBox title;

  @FXML private JFXButton submit;
  @FXML private JFXButton clear;

  private SerialPort comPort = null;

  public void initialize() {

    // find arduino
    if (comPort == null) {
      comPort = SerialPort.getCommPorts()[0];
    }

    ArrayList<String> titles = new ArrayList<String>(7);
    titles.add("Choose one:");
    titles.add("Admin");
    titles.add("Doctor");
    titles.add("Nurse");
    titles.add("Janitor");
    titles.add("Interpreter");
    titles.add("Receptionist");
    titles.add("Retail");
    title.setItems(FXCollections.observableList(titles));
  }

  public String scanRFID() {
    try {
      comPort.openPort();
      while (true) {
        while (comPort.bytesAvailable() != 14) Thread.sleep(20);

        byte[] readBuffer = new byte[comPort.bytesAvailable()];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
        String scannedString = new String(readBuffer, "UTF-8");
        String[] scannedArray = scannedString.split(" ");
        if (scannedArray[1].contains("p")) {
          comPort.closePort();
          return scannedArray[0];
        } else {
          comPort.closePort();
          return null;
        }
      }
    } catch (Exception e) {
      comPort.closePort();
      e.printStackTrace();
      return null;
    }
  }

  public void submitEmployee() {
    if (fName.getText().isEmpty()
        || lName.getText().isEmpty()
        || uName.getText().isEmpty()
        || pass.getText().isEmpty()
        || cPass.getText().isEmpty()) {
      // make popup that says one or more fields are empty
      DialogUtil.simpleInfoDialog(
          dialogPane,
          "Empty fields",
          "You left some fields empty. Please make sure they are all filled.");
      return;
    }
    if (eDB.uNameExists(uName.getText())) {
      // make popup that says username already exists
      DialogUtil.simpleInfoDialog(
          dialogPane,
          "Invalid Username",
          "The username you have chosen is already taken. Please choose another.");
      return;
    }
    if (pass.getText().length() < 8) {
      DialogUtil.simpleInfoDialog(
          dialogPane,
          "Invalid Password",
          "Please make sure your password is at least 8 characters long.");
      return;
    }
    if (!cPass.getText().equals(pass.getText())) {
      // make popup that says passwords are not the same
      DialogUtil.simpleInfoDialog(
          dialogPane,
          "Passwords Don't Match",
          "Please make sure that the password you entered in the confirm password field matches your intended password.");
      return;
    }
    if (!eDB.checkSecurePass(pass.getText())) {
      // make popup that says make sure password includes a number, lowercase letter, and uppercase
      // letter, and it's
      // less than 72 characters
      DialogUtil.simpleInfoDialog(
          dialogPane,
          "Invalid Password",
          "Please create a password that includes a number, lowercase letter, and uppercase letter. Shorter than 72 characters.");
      return;
    }

    ThreadPool.runBackgroundTask(
        () -> {
          String secretKey = "";
          // must add rfid card if the user checks they want to add
          // an rfid card
          if (addRFID.isSelected()) {
            // popup message saying that we are scanning for rfid card
            Platform.runLater(
                () -> {
                  // tell user we are scanning for a card
                  DialogUtil.simpleErrorDialog(
                      dialogPane, "Started Scanning", "We are looking for your card.");
                });
            String rfid = scanRFID();
            if (rfid != null) {
              secretKey =
                  eDB.addEmployeeGA(
                      fName.getText(),
                      lName.getText(),
                      uName.getText(),
                      cPass.getText(),
                      title.getValue().toString(),
                      rfid);
              Platform.runLater(
                  () -> {
                    // tell user we are scanning for a card
                    DialogUtil.simpleErrorDialog(
                        dialogPane, "Finished Scanning", "We have found your card");
                  });
            } else {
              Platform.runLater(
                  () -> {
                    DialogUtil.simpleErrorDialog(
                        dialogPane,
                        "Read Fail",
                        "There was an error reading the card please try again");
                  });
              clearFields();
            }
          } else {
            secretKey =
                eDB.addEmployeeGA(
                    fName.getText(),
                    lName.getText(),
                    uName.getText(),
                    cPass.getText(),
                    EmployeeTitle.valueOf(title.getValue().toString().toUpperCase()));
          }
          String companyName = "Amethyst Asgardians";
          String barCodeUrl =
              getGoogleAuthenticatorBarCode(secretKey, uName.getText(), companyName);
          if (secretKey != null) {
            // print that account has been created successfully
            Platform.runLater(
                () -> {
                  DialogUtil.complexDialog(
                      dialogPane,
                      "You must scan the QR code in Google Authenticator and use it for logging in",
                      "views/QRCodePopup.fxml",
                      true,
                      null,
                      new QRDialogController(barCodeUrl));
                });
          } else {
            // print that for some reason the account couldn't be added
            Platform.runLater(
                () -> {
                  DialogUtil.simpleErrorDialog(
                      dialogPane,
                      "Account creation failed",
                      "For some reason we could not create your account.");
                });
          }
        });
  }

  public String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
    try {
      return "otpauth://totp/"
          + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
          + "?secret="
          + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
          + "&issuer="
          + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void clearFields() {
    fName.clear();
    lName.clear();
    uName.clear();
    title.setValue("Choose one:");
    pass.clear();
    cPass.clear();
  }
}
