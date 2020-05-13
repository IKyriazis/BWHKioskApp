package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import edu.wpi.cs3733.d20.teamP.APIController;
import edu.wpi.cs3733.d20.teamP.ServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class FoodRequestController extends AbstractController {
  private static boolean hasReadFoodEmployees = false;

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

    setupNodeLocationBox(comboLocation, null);

    // Load employees if needed
    if (!hasReadFoodEmployees) {
      readFoodEmployeeCSV();
      hasReadFoodEmployees = true;
    }
  }

  // Initialize the food API, REALLY annoying to work with
  protected void readFoodEmployeeCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/FoodEmployees.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 0; i < data.size(); i++) {
        String name = data.get(i)[0];
        int role = Integer.parseInt(data.get(i)[1]);
        APIController.addEmployee("UselessField", name, role);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
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
}
