package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import flowerapi.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class FlowerServiceController extends AbstractController {
  @FXML private Label headerLabel;

  @FXML private JFXComboBox<Node> comboLocation;

  @FXML private JFXButton adminButton;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  public FlowerServiceController() {}

  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeRegular.FILE));
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
  }

  @FXML
  public void placeOrder() {
    if (comboLocation.getSelectionModel().getSelectedItem() != null) {
      // Open new window centered on screen with baseline width and height
      try {
        flowerapi.FlowerAPI.run(
            0,
            0,
            0,
            0,
            App.class.getResource("stylesheet.css").toExternalForm(),
            comboLocation.getSelectionModel().getSelectedItem().getLongName(),
            null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      DialogUtil.simpleErrorDialog("Can't place order", "Please select delivery location");
    }
  }

  @FXML
  public void openAdmin() {
    try {
      flowerapi.FlowerAPI.runAdmin(
          0, 0, 0, 0, App.class.getResource("stylesheet.css").toExternalForm());
    } catch (ServiceException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void trackOrder() {
    try {
      flowerapi.FlowerAPI.runTracker(
          0, 0, 0, 0, App.class.getResource("stylesheet.css").toExternalForm());
    } catch (ServiceException e) {
      e.printStackTrace();
    }
  }
}
