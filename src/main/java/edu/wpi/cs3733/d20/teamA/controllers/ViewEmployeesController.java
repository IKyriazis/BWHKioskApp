package edu.wpi.cs3733.d20.teamA.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ViewEmployeesController {

  @FXML private GridPane empList;
  @FXML private StackPane empPane;
  @FXML private Label img;
  // private SimpleTableView<Employee> tblEmployees;

  public void initialize() {
    img.setGraphic(new FontIcon(FontAwesomeSolid.USER));

    /*
    // Set icon
    addBtn.setGraphic(new FontIcon(FontAwesomeSolid.PLUS_SQUARE));
    editBtn.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_SQUARE));
    deleteBtn.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_SQUARE));
    infoBtn.setGraphic(new FontIcon(FontAwesomeSolid.QUESTION));



    empPane.addEventHandler(
            TabSwitchEvent.TAB_SWITCH,
            event -> {
                event.consume();
                update();
            });


    tblEmployees = new SimpleTableView<>(new Employee(0, "", "", ""), 150.0);
    empList.getChildren().add(tblEmployees);

    // Set up table to open edit controller when double clicking row
    tblEmployees.setRowFactory(
            tv -> {
                TreeTableRow<MedRequest> row = new TreeTableRow<>();
                row.setOnMouseClicked(
                        event -> {
                            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                editRequest();
                            }
                        });
                return row;
            });

    update();



     */
  }
}
