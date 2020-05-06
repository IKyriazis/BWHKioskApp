package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.DialogMaker;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.QRDialogController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ViewEmployeesController extends AbstractController {

  @FXML private GridPane empList;
  @FXML private StackPane empPane;
  @FXML private Label img;
  @FXML private JFXButton addBtn;
  //  @FXML private JFXButton editBtn;
  @FXML private JFXButton deleteBtn;
  @FXML private JFXButton showQRBtn;

  private SimpleTableView<Employee> tblEmployees;

  public void initialize() {
    img.setGraphic(new FontIcon(FontAwesomeSolid.USER));

    // Set icon
    addBtn.setGraphic(new FontIcon(FontAwesomeSolid.USER_PLUS));
    // editBtn.setGraphic(new FontIcon(FontAwesomeSolid.USER_COG));
    deleteBtn.setGraphic(new FontIcon(FontAwesomeSolid.USER_SLASH));
    showQRBtn.setGraphic(new FontIcon(FontAwesomeSolid.QRCODE));

    empPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    tblEmployees = new SimpleTableView<>(new Employee("", "", "", EmployeeTitle.DOCTOR, ""), 150.0);
    empList.getChildren().add(tblEmployees);

    // Set up table to open edit controller when double clicking row
    /*
    tblEmployees.setRowFactory(
            tv -> {
                TreeTableRow<Employee> row = new TreeTableRow<>();
                row.setOnMouseClicked(
                        event -> {
                            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                editRequest();
                            }
                        });
                return row;
            });

     */

    update();
  }

  public void update() {
    try {
      tblEmployees.clear();

      tblEmployees.add(eDB.getObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(empPane, "Error", "Failed to update employee table");
    }
  }

  @FXML
  public void addBtn(ActionEvent actionEvent) {
    DialogMaker maker = new DialogMaker();
    maker.makeEmployeeDialog(this);
  }

  //  @FXML
  //  public void editBtn(ActionEvent actionEvent) {
  //    Employee employee = tblEmployees.getSelected();
  //    DialogUtil.complexDialog(
  //        "Edit Employee",
  //        "views/EditEmployeePopup.fxml",
  //        true,
  //        event -> update(),
  //        new EditEmployeeController(employee));
  //  }

  @FXML
  public void deleteBtn(ActionEvent actionEvent) {
    eDB.deleteEmployee(tblEmployees.getSelected().getUsername());
    update();
  }

  @FXML
  public void qrCode() {
    String uname = tblEmployees.getSelected().getUsername();
    String secretKey = eDB.getSecretKey(uname);
    String companyName = "Amethyst Asgardians";
    if (secretKey != null) {
      String barCodeUrl = getGoogleAuthenticatorBarCode(secretKey, uname, companyName);
      DialogUtil.complexDialog(
          "You must scan the QR code in Google Authenticator and use it for logging in",
          "views/QRCodePopup.fxml",
          true,
          null,
          new QRDialogController(barCodeUrl));
    } else {
      DialogUtil.simpleErrorDialog(
          empPane, "Error", "There is no authenticator code associated with this user.");
    }
  }
}
