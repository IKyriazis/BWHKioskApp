package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.ThreadPool;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javax.imageio.ImageIO;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class CreateAcctController extends AbstractController implements IDialogController {

  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField uName;
  @FXML private JFXPasswordField pass;
  @FXML private JFXPasswordField cPass;
  @FXML private JFXCheckBox addRFID;
  @FXML private JFXCheckBox addPic;
  @FXML private JFXComboBox title;
  @FXML private JFXTextField IPager;

  @FXML private JFXButton submit;
  @FXML private JFXButton clear;

  private JFXDialog dialog;

  public void initialize() {

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
    title.getSelectionModel().selectFirst();
    submit.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));
    clear.setGraphic(new FontIcon(FontAwesomeSolid.TIMES_CIRCLE));
  }

  @FXML
  public void submitEmployee() {
    if (fName.getText().isEmpty()
        || lName.getText().isEmpty()
        || uName.getText().isEmpty()
        || pass.getText().isEmpty()
        || title.getValue().toString().equals("Choose one:")
        || cPass.getText().isEmpty()) {
      // make popup that says one or more fields are empty
      DialogUtil.simpleInfoDialog(
          "Empty fields", "You left some fields empty. Please make sure they are all filled.");
      return;
    }
    if (eDB.uNameExists(uName.getText())) {
      // make popup that says username already exists
      DialogUtil.simpleInfoDialog(
          "Invalid Username",
          "The username you have chosen is already taken. Please choose another.");
      return;
    }
    if (!cPass.getText().equals(pass.getText())) {
      // make popup that says passwords are not the same
      DialogUtil.simpleInfoDialog(
          "Passwords Don't Match",
          "Please make sure that the password you entered in the confirm password field matches your intended password.");
      return;
    }
    if (pass.getText().length() < 8) {
      DialogUtil.simpleInfoDialog(
          "Invalid Password", "Please make sure your password is at least 8 characters long.");
      return;
    }
    if (!eDB.checkSecurePass(pass.getText())) {
      // make popup that says make sure password includes a number, lowercase letter, and uppercase
      // letter, and it's
      // less than 72 characters
      DialogUtil.simpleInfoDialog(
          "Invalid Password",
          "Please create a password that includes a number, lowercase letter, and uppercase letter. Shorter than 72 characters.");
      return;
    }

    if (!IPager.getText().isEmpty()) {
      if (IPager.getText().length() != 10) {
        DialogUtil.simpleInfoDialog(
            "Invalid Length", "Please enter a pager number consisting of 10 digits.");
        return;
      }
      if (!IPager.getText().matches("[0-9]+")) {
        DialogUtil.simpleInfoDialog(
            "Invalid Pager Number", "Please enter a pager number consisting of only digits.");
        return;
      }
    }

    ThreadPool.runBackgroundTask(
        () -> {
          String secretKey =
              eDB.addEmployeeGA(
                  fName.getText(),
                  lName.getText(),
                  uName.getText(),
                  cPass.getText(),
                  EmployeeTitle.valueOf(title.getValue().toString().toUpperCase()));
          if (!IPager.getText().isEmpty()) {
            eDB.addPagerNum(uName.getText(), IPager.getText());
          }
          if (addPic.isSelected()) {
            Webcam webcam = Webcam.getDefault();
            webcam.open();
            try {
              ImageIO.write(webcam.getImage(), "PNG", new File("temp.png"));
              webcam.close();
              eDB.addImage("temp.png", uName.getText());
            } catch (IOException e) {
              e.printStackTrace();
              clearFields();
              webcam.close();
              eDB.deleteEmployee(uName.getText());
            }
          }
          if (addRFID.isSelected()) {
            // popup message saying that we are scanning for rfid card
            Platform.runLater(
                () -> {
                  // tell user we are scanning for a card
                  DialogUtil.simpleErrorDialog("Started Scanning", "We are looking for your card.");
                });
            String rfid = scanRFID();
            if (rfid != null) {
              // if the rfid card is associated with a user it will return the username which is not
              // null
              // if there is a username associated with this card then we pop up a dialog
              if (!eDB.getUsername(rfid).isEmpty()) {
                Platform.runLater(
                    () -> {
                      DialogUtil.simpleErrorDialog(
                          "Duplicate Card", "There is another account associated with this card.");
                      clearFields();
                    });
                // delete the employee we just made as we want to start from scratch if the rfid
                // messes up
                eDB.deleteEmployee(uName.getText());
                return;
              } else {
                eDB.addRFID(uName.getText(), rfid);
              }
            } else {
              Platform.runLater(
                  () -> {
                    DialogUtil.simpleErrorDialog(
                        "Read Fail", "There was an error reading the card please try again");
                    clearFields();
                  });
              // delete the employee we just made as we want to start from scratch if the rfid
              // messes up
              eDB.deleteEmployee(uName.getText());
              return;
            }
          }

          String barCodeUrl =
              getGoogleAuthenticatorBarCode(secretKey, uName.getText(), companyName);
          if (!secretKey.isEmpty()) {
            // print that account has been created successfully
            Platform.runLater(
                () -> {
                  DialogUtil.complexDialog(
                      "You must scan the QR code in Google Authenticator and use it for logging in",
                      "views/QRCodePopup.fxml",
                      true,
                      null,
                      new QRDialogController(barCodeUrl));
                  clearFields();
                });
          }
        });
  }

  public void clearFields() {
    fName.clear();
    lName.clear();
    uName.clear();
    title.setValue("Choose one:");
    pass.clear();
    cPass.clear();
    IPager.clear();
    addRFID.setSelected(false);
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
