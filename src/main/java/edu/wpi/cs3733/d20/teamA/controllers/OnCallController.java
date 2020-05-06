package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.PublicEmployee;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.awt.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class OnCallController extends AbstractController {

  @FXML private GridPane empList;
  @FXML private StackPane empPane;
  @FXML private Label img;
  @FXML private Button signinbttn;
  @FXML private Button signoutbttn;
  @FXML private Button changebttn;
  private SimpleTableView<PublicEmployee> tblEmployees;

  public void initialize() {
    img.setGraphic(new FontIcon(FontAwesomeSolid.USER));

    empPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    tblEmployees =
        new SimpleTableView<>(new PublicEmployee("", "", "", EmployeeTitle.DOCTOR, 0l, ""), 150.0);
    empList.getChildren().add(tblEmployees);

    update();
  }

  public void update() {
    try {
      tblEmployees.clear();

      tblEmployees.add(ocDB.getObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(empPane, "Error", "Failed to update employee table");
    }
  }

  public void onSignedin(ActionEvent actionEvent) {
    Employee user = ocDB.getLoggedIn();
    String name = user.getUsername();
    ocDB.signOntoShift(name);
    update();
  }

  public void onSignedout(ActionEvent actionEvent) {
    Employee user = ocDB.getLoggedIn();
    String name = user.getUsername();
    ocDB.signOffShift(name);
    update();
  }

  public void onchange(ActionEvent actionEvent) {
    Employee user = ocDB.getLoggedIn();
    String name = user.getUsername();
    ocDB.updateStatus(name);
    update();
  }
}
