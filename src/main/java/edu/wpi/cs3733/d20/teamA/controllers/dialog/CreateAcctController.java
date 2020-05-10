package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class CreateAcctController extends AbstractController implements IDialogController {

  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField uName;
  @FXML private JFXPasswordField pass;
  @FXML private JFXPasswordField cPass;
  @FXML private JFXPasswordField oldPass;
  @FXML private JFXComboBox title;
  @FXML private JFXTextField EditPager;

  @FXML private JFXButton submit;
  @FXML private JFXButton clear;

  Employee employee;
  private JFXDialog dialog;

  public CreateAcctController(Employee employee) {
    super();
    this.employee = employee;
  }

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
    fName.setText(eDB.getFirstName(employee.getUsername()));
    lName.setText(eDB.getLastName(employee.getUsername()));
    uName.setText(employee.getUsername());
    title.getSelectionModel().select(eDB.getTitle(employee.getUsername()));
    EditPager.setText(eDB.getPager(employee.getUsername()));
  }

  @FXML
  public void submitEmployee() {
    if (fName.getText().isEmpty()
        || lName.getText().isEmpty()
        || uName.getText().isEmpty()
        || title.getValue().toString().equals("Choose one:")
        || EditPager.getText().isEmpty()) {
      // make popup that says one or more fields are empty
      DialogUtil.simpleInfoDialog(
          "Empty fields", "You left some fields empty. Please make sure they are all filled.");
      return;
    }
    //    if (eDB.editUsername(employee.getId(), uName.getText())) {
    //      // make popup that says username already exists
    //      DialogUtil.simpleInfoDialog(
    //          "Invalid Username",
    //          "The username you have chosen is already taken. Please choose another.");
    //      return;
    //    }
    if (pass.getText().length() < 8) {
      DialogUtil.simpleInfoDialog(
          "Invalid Password", "Please make sure your password is at least 8 characters long.");
      return;
    }
    if (!cPass.getText().equals(pass.getText())) {
      // make popup that says passwords are not the same
      DialogUtil.simpleInfoDialog(
          "Passwords Don't Match",
          "Please make sure that the password you entered in the confirm password field matches your intended password.");
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
    if (eDB.editNameFirst(employee.getUsername(), fName.getText())) {}

    if (eDB.editNameLast(employee.getUsername(), lName.getText())) {}

    if (eDB.editTitle(employee.getUsername(), title.getValue().toString().toUpperCase())) {}
  }

  public void clearFields() {
    fName.clear();
    lName.clear();
    uName.clear();
    title.setValue("Choose one:");
    pass.clear();
    oldPass.clear();
    cPass.clear();
    EditPager.clear();
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
