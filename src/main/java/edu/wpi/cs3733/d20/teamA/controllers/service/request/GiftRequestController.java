package edu.wpi.cs3733.d20.teamA.controllers.service.request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamL.GiftServiceRequest;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class GiftRequestController extends AbstractController {
  @FXML private Label headerLabel;

  @FXML private JFXComboBox<Node> comboLocation;

  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  public GiftRequestController() {}

  public void initialize() {

    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.GIFT));
    orderButton.setGraphic(new FontIcon(FontAwesomeRegular.ARROW_ALT_CIRCLE_RIGHT));

    setupNodeBox(comboLocation, null);
  }

  @FXML
  public void placeOrder() {
    if (comboLocation.getSelectionModel().getSelectedItem() != null) {
      try {
        GiftServiceRequest g = new GiftServiceRequest();
        // Main.main(null);
        Rectangle2D primScreenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        int width = 1000;

        String s =
            g.run(
                (int) ((primScreenBounds.getWidth() - width) / 2),
                (int) (primScreenBounds.getHeight() / 3),
                width,
                750,
                App.class.getResource("stylesheet.css").toExternalForm(),
                "Node",
                "EndNode");
        if (s != null) {
          serviceDatabase.addServiceReq(
              ServiceType.GIFT,
              comboLocation.getSelectionModel().getSelectedItem().getNodeID(),
              s,
              null);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
