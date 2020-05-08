package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import java.util.Comparator;
import javafx.collections.ObservableList;

public abstract class AbstractRequestController extends AbstractController {
  protected void setupDescriptionArea(JFXTextArea descriptionArea) {
    descriptionArea
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null && newValue.length() > 100) {
                descriptionArea.setText(newValue.substring(0, 100));
              }
            });
  }

  protected void setupEmployeeBox(JFXComboBox<Employee> employeeBox, String requestedEmployees) {
    ObservableList<Employee> allEmployeeList = eDB.getObservableList();

    if (!requestedEmployees.equals("")) {
      System.out.println(allEmployeeList.size());
      for (int i = allEmployeeList.size() - 1; i > -1; i--) {
        System.out.println(allEmployeeList.get(i).getTitle());
        System.out.println(allEmployeeList.get(i).getUsername());
        if (!allEmployeeList.get(i).getTitle().equals(requestedEmployees)) {
          allEmployeeList.remove(eDB.findFromUsername(allEmployeeList.get(i).getUsername()));
        }
      }
    }

    allEmployeeList.sort(Comparator.comparing(Employee::toString));

    employeeBox.setItems(allEmployeeList);
    employeeBox.setOnMouseClicked(
        event -> {
          allEmployeeList.clear();

          allEmployeeList.addAll(eDB.getObservableList());
          allEmployeeList.sort(Comparator.comparing(Employee::toString));

          if (!requestedEmployees.equals("")) {
            System.out.println(allEmployeeList.size());
            for (int i = allEmployeeList.size() - 1; i > -1; i--) {
              System.out.println(allEmployeeList.get(i).getTitle());
              System.out.println(allEmployeeList.get(i).getUsername());
              if (!allEmployeeList.get(i).getTitle().equals(requestedEmployees)) {
                allEmployeeList.remove(eDB.findFromUsername(allEmployeeList.get(i).getUsername()));
              }
            }
          }

          employeeBox.setItems(allEmployeeList);
        });
  }
}
