package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.controllers.dialog.EmployeeEditController;
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
  private SimpleTableView<Employee> tblEmployees;

  public void initialize() {
    img.setGraphic(new FontIcon(FontAwesomeSolid.USER));

    /*
        // Set icon
        addBtn.setGraphic(new FontIcon(FontAwesomeSolid.PLUS_SQUARE));
        editBtn.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_SQUARE));
        deleteBtn.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_SQUARE));
        infoBtn.setGraphic(new FontIcon(FontAwesomeSolid.QUESTION));


    */
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
    DialogUtil.complexDialog(
        "Add Employee",
        "views/AddEmployeePopup.fxml",
        false,
        event -> update(),
        new EmployeeEditController());
  }

  public void editBtn(ActionEvent actionEvent) {}

  public void deleteBtn(ActionEvent actionEvent) {}
}
