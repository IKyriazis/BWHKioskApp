package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamL.GiftServiceRequest;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class GiftRequestController extends AbstractController {
  @FXML private Label headerLabel;

  @FXML private JFXComboBox<Node> comboLocation;

  @FXML private JFXButton adminButton;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  public GiftRequestController() {}

  public void initialize() {
    try {
      GiftServiceRequest g = new GiftServiceRequest();
      // Main.main(null);
      Rectangle2D primScreenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
      int width = 1000;

      System.out.println(
          g.run(
              (int) ((primScreenBounds.getWidth() - width) / 2),
              (int) (primScreenBounds.getHeight() / 3),
              width,
              750,
              App.class.getResource("stylesheet.css").toExternalForm(),
              "Node",
              null));
    } catch (Exception e) {
      e.printStackTrace();
    }
    /*// Set up icons
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

    setupNodeBox(comboLocation, null);*/
  }

  @FXML
  public void placeOrder() {
    /* if (comboLocation.getSelectionModel().getSelectedItem() != null) {

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
                    null, /*Add stylesheet
                    comboLocation.getSelectionModel().getSelectedItem().getNodeID(),
                    null);
        } catch (ServiceException serviceException) {
            serviceException.printStackTrace();
        }
    } else {
        DialogUtil.simpleErrorDialog("Can't place order", "Please select delivery location");
    }*/
  }

  @FXML
  public void openAdmin() {
    /*
    APIController.setAdmin(true);

    DialogUtil.complexDialog(
            dialogPane, "Admin", "views/food/FoodAdmin.fxml", false, null, new FoodAdminController());*/
  }
}
