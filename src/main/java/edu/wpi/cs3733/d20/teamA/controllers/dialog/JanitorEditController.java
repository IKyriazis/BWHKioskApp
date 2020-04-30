package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.Employee;
import edu.wpi.cs3733.d20.teamA.database.JanitorService;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class JanitorEditController extends AbstractController implements IDialogController {
  private final boolean modify;

  @FXML private GridPane modPane;
  @FXML private JFXComboBox comboboxLocation;
  @FXML private JFXComboBox comboboxPriority;

  @FXML private JFXButton doneButton;

  @FXML private JFXComboBox comboboxJanitor;

  private JanitorService janitorService;
  private JFXDialog dialog;

  private Hashtable<String, String> nodeIDHash;

  ObservableList activeItems = FXCollections.observableArrayList();

  public JanitorEditController() {
    super();

    modify = false;
  }

  public JanitorEditController(JanitorService s) {
    super();

    this.modify = true;
    this.janitorService = s;
  }

  public void initialize() {

    if (modify) {
      comboboxLocation.setPromptText(janitorService.getLocation());
      comboboxPriority.setPromptText(janitorService.getPriority());
    }
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));
    comboboxLocation.setItems(allNodeList);
    comboboxLocation
        .getEditor()
        .setOnKeyTyped(
            new NodeAutoCompleteHandler(comboboxLocation, comboboxLocation, allNodeList));

    ObservableList<String> priorityItems = FXCollections.observableArrayList();
    String a = "High";
    String b = "Medium";
    String c = "Low";
    priorityItems.addAll(a, b, c);
    comboboxPriority.getItems().addAll(priorityItems);

    ObservableList<Employee> allEmployeeList = eDB.employeeOl();
    allEmployeeList.sort(Comparator.comparing(Employee::toString));

    comboboxJanitor.getItems().addAll(allEmployeeList);

    activeItems.addAll("Unassigned");

    comboboxJanitor.getItems().addAll(activeItems);
    comboboxJanitor.setOnMouseClicked(
        event -> {
          allEmployeeList.clear();

          allEmployeeList.addAll(eDB.employeeOl());
          allEmployeeList.sort(Comparator.comparing(Employee::toString));

          comboboxJanitor.setItems(allEmployeeList);
          comboboxJanitor.getItems().addAll(activeItems);
        });
    doneButton.setOnAction(this::isDone);

    // Set button icon
    doneButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));
  }

  // Scene switch & database addNode
  @FXML
  public void isDone(ActionEvent e) {

    if (comboboxLocation.getSelectionModel().isEmpty()
        || comboboxPriority.getSelectionModel().isEmpty()
        || comboboxJanitor.getSelectionModel().isEmpty()) {
      return;
    }

    try {
      Optional<Node> start =
          comboboxLocation.getItems().stream()
              .filter(node -> node.toString().contains(comboboxLocation.getEditor().getText()))
              .findFirst();

      String location = start.get().getLongName();
      String priority = comboboxPriority.getValue().toString();
      String longName = comboboxLocation.getValue().toString();
      String employee = "";

      if (!comboboxJanitor.getSelectionModel().getSelectedItem().toString().equals("Unassigned"))
        employee = comboboxJanitor.getSelectionModel().getSelectedItem().toString();

      if (modify) {
        primaryDB.deleteServReq(janitorService.getIndex());
      }
      primaryDB.addServiceReq(ServiceType.JANITOR, location, employee, priority, "");

      dialog.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
