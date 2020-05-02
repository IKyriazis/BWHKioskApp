package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.InternalTransportRequest;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.sql.Timestamp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class InternalTransportAdminController extends AbstractController {

  @FXML private GridPane requestTablePane;

  @FXML private JFXTextField txtName;
  @FXML private JFXTextField txtPrev;
  @FXML private JFXComboBox<String> txtNext;

  @FXML private StackPane dialogStackPane;

  @FXML private Label requestTblLbl;
  @FXML private JFXButton changeProgressButton;

  @FXML private AnchorPane internalTransportPane;

  private InternalTransportRequest lastOrder;

  private SimpleTableView tblOrderView;

  public void initialize() {
    // Setup label icons
    requestTblLbl.setGraphic(new FontIcon(FontAwesomeSolid.WHEELCHAIR));

    // Setup button icons
    changeProgressButton.setGraphic(new FontIcon(FontAwesomeSolid.EXCHANGE_ALT));

    // Add tab switch update listener
    internalTransportPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    // Set up tables
    tblOrderView =
        new SimpleTableView<>(
            new InternalTransportRequest("", "", "", new Timestamp(0), "", ""), 40.0);
    requestTablePane.getChildren().addAll(tblOrderView);

    // Populate tables
    update();

    // Hook up txtPrev to show status of selected order
    tblOrderView.setOnMouseClicked(this::updateStatus);

    // Setup status change stuff
    txtNext.getItems().addAll("Request Made", "In Progress", "Completed");
    txtNext.getSelectionModel().select(0);
  }

  public void update() {
    try {
      tblOrderView.clear();
      tblOrderView.add(serviceDatabase.observableList(ServiceType.INTERNAL_TRANSPORT));
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update flower and/or order tables");
    }
  }

  public void updateStatus(MouseEvent mouseEvent) {
    InternalTransportRequest selected = (InternalTransportRequest) tblOrderView.getSelected();
    if (selected != null) {
      // track the last selected order
      lastOrder = selected;

      // Update status text display
      txtPrev.setText(lastOrder.getProgress());

      // Update combobox selection (automagically select next status)
      int i = statusStringToValue(lastOrder.getProgress()) + 1; // next status
      if (i <= 2) txtNext.getSelectionModel().select(i);
    } else {
      txtPrev.setText("");
      txtNext.getSelectionModel().select(0);
    }
  }

  private int statusStringToValue(String status) {
    switch (status) {
      case "Request Made":
        return 0;
      case "In Progress":
        return 1;
      case "Completed":
        return 2;
      default:
        return 999;
    }
  }

  public void changeProgress() {
    if (lastOrder != null) {
      String s = txtNext.getSelectionModel().getSelectedItem();
      String name = txtName.getText();
      if (!name.isEmpty()) {
        serviceDatabase.setStatus(lastOrder.getRequestNumber(), s);
        serviceDatabase.setAdditional(lastOrder.getRequestNumber(), txtName.getText());
      } else {
        serviceDatabase.setStatus(lastOrder.getRequestNumber(), s);
      }
      lastOrder = null;
      update();
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Request Selected",
          "Please select a request by clicking a row in the table");
    }
  }
}
