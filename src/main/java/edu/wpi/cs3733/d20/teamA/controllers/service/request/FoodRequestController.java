package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import edu.wpi.cs3733.d20.teamP.APIController;
import edu.wpi.cs3733.d20.teamP.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class FoodRequestController extends AbstractController {
  @FXML private Label headerLabel;

  @FXML private JFXComboBox<Node> comboLocation;

  @FXML private JFXButton adminButton;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  public FoodRequestController() {}

  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.HAMBURGER));
    adminButton.setGraphic(new FontIcon(FontAwesomeSolid.USER));
    orderButton.setGraphic(new FontIcon(FontAwesomeRegular.ARROW_ALT_CIRCLE_RIGHT));

    // Listen for tab switch events to update flower list
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          adminButton.setVisible(eDB.getLoggedIn() != null);
        });

    setupNodeBox(comboLocation, null);
  }

  @FXML
  public void placeOrder() {
    if (comboLocation.getSelectionModel().getSelectedItem() != null) {

      APIController.setAdmin(false); // restricts admin access
      APIController.setAutoClose(true); // auto-closes window after making a request
      // APIController.addListener(request -> uController.back());  // leaves request screen after
      // making a request, unsure how to integrate with our stuff

      // open window
      try {
        APIController.run(
            -1,
            -1,
            1280,
            720,
            null, /*Add stylesheet*/
            comboLocation.getSelectionModel().getSelectedItem().getNodeID(),
            null);
      } catch (ServiceException serviceException) {
        serviceException.printStackTrace();
      }
    } else {
      DialogUtil.simpleErrorDialog("Can't place order", "Please select delivery location");
    }
  }

  @FXML
  public void openAdmin() {
    APIController.setAdmin(true);

    DialogUtil.complexDialog(
        dialogPane, "Admin", "views/food/FoodAdmin.fxml", false, null, new FoodAdminController());
  }
  /*
  APIController.setAdmin(true);

  // open window
    try {
    APIController.run(-1, -1, 1280, 720, null, null, null);
  } catch (ServiceException serviceException) {
    serviceException.printStackTrace();
  }*/
  /*List<Employee> cafeteriaEmployees = new ArrayList<>();
  cafeteriaEmployees.add(new Employee("123", "Bob", 1));
  List<Employee> starbucksEmployees = new ArrayList<>();
  cafeteriaEmployees.add(new Employee("124", "Tom", 2));
  List<Employee> foodManagers = new ArrayList<>();
  cafeteriaEmployees.add(new Employee("125", "Joe", 3));

  // add employees that work at cafeteria
  for (Employee e : cafeteriaEmployees) {
    APIController.addEmployee(e.getId().toString(), e.getName(), 1);
  }
  // add employees that work at Starbucks
  for (Employee e : starbucksEmployees) {
    APIController.addEmployee(e.getId().toString(), e.getName(), 2);
  }
  // add employees that work at both cafeteria and Starbucks
  for (Employee e : foodManagers) {
    APIController.addEmployee(e.getId().toString(), e.getName(), 3);
  }*/
}
