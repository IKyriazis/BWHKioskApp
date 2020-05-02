package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Employee;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Comparator;
import java.util.Optional;
import javafx.collections.ObservableList;

public abstract class AbstractRequestController extends AbstractController {
  protected void setupDescriptionArea(JFXTextArea descriptionArea) {
    descriptionArea
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 100) {
                descriptionArea.setText(newValue.substring(0, 100));
              }
            });
  }

  protected void setupNodeBox(JFXComboBox<Node> box, javafx.scene.Node toFocus) {
    box.setItems(Graph.getInstance().getNodeObservableList());
    box.getEditor()
        .setOnKeyTyped(
            new NodeAutoCompleteHandler(box, toFocus, Graph.getInstance().getNodeObservableList()));
  }

  protected void setupEmployeeBox(JFXComboBox<Employee> employeeBox) {
    ObservableList<Employee> allEmployeeList = eDB.getObservableList();
    allEmployeeList.sort(Comparator.comparing(Employee::toString));

    employeeBox.setItems(allEmployeeList);
    employeeBox.setOnMouseClicked(
        event -> {
          allEmployeeList.clear();

          allEmployeeList.addAll(eDB.getObservableList());
          allEmployeeList.sort(Comparator.comparing(Employee::toString));

          employeeBox.setItems(allEmployeeList);
        });
  }

  protected Node getSelectedNode(JFXComboBox<Node> nodeBox) {
    Optional<Node> selected =
        nodeBox.getItems().stream()
            .filter(node -> node.toString().equals(nodeBox.getEditor().getText()))
            .findFirst();

    return selected.orElse(null);
  }
}
