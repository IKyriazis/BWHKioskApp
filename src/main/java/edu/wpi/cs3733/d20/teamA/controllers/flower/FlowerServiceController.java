package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class FlowerServiceController extends AbstractController {
  @FXML private Label headerLabel;

  @FXML private JFXComboBox<Node> cmbSelect;

  @FXML private JFXButton btnAdmin;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  public FlowerServiceController() {}

  public void initialize() {
    // Set up icons
    headerLabel.setGraphic(new FontIcon(FontAwesomeRegular.FILE));
    btnAdmin.setGraphic(new FontIcon(FontAwesomeSolid.USER));
    orderButton.setGraphic(new FontIcon(FontAwesomeRegular.ARROW_ALT_CIRCLE_RIGHT));

    // Set up list of node IDs
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));
    cmbSelect.setItems(allNodeList);
    cmbSelect
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(cmbSelect, orderButton, allNodeList));

    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          btnAdmin.setVisible(eDB.getLoggedIn() != null);
        });
  }

  @FXML
  public void placeOrder() throws IOException {
    if (cmbSelect.getSelectionModel().getSelectedItem() != null) {
      DialogUtil.simpleErrorDialog(
          dialogPane, "API needed", "The API isnt currently set up, sorry");
      /*myapi.App.run(
      0,
      0,
      0,
      0,
      App.class.getResource("stylesheet.css").toExternalForm(),
      cmbSelect.getSelectionModel().getSelectedItem().getNodeID(),
      null);*/
    } else {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Unable to place order", "Please select a location to deliver the order to");
    }
  }

  @FXML
  public void openAdmin() throws IOException {
    DialogUtil.simpleErrorDialog(dialogPane, "API needed", "The API isnt currently set up, sorry");
    /*myapi.App.runAdmin(
    0, 0, 0, 0, App.class.getResource("stylesheet.css").toExternalForm(), null, null);*/
  }
}
