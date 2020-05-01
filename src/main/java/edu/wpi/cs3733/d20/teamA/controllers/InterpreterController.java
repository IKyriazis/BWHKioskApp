package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.InterpreterDialogController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.InterpreterRequestDialogController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Interpreter;
import edu.wpi.cs3733.d20.teamA.database.InterpreterRequest;
import edu.wpi.cs3733.d20.teamA.database.ItemType;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class InterpreterController extends AbstractController {
  @FXML private GridPane interpreterTablePane;
  @FXML private GridPane requestTablePane;
  @FXML private StackPane dialogPane;
  @FXML private Pane rootPane;

  @FXML private JFXButton registerButton;
  @FXML private JFXButton deleteButton;
  @FXML private JFXButton requestButton;
  @FXML private JFXButton completeButton;

  @FXML private Label interpreterLabel;
  @FXML private Label requestLabel;

  private SimpleTableView interpreterTable;
  private SimpleTableView requestTable;

  @FXML
  public void initialize() {
    // Set up DB if not yet populated.

    // Set up icons
    interpreterLabel.setGraphic(new FontIcon(FontAwesomeSolid.USERS));
    requestLabel.setGraphic(new FontIcon(FontAwesomeRegular.PAPER_PLANE));
    registerButton.setGraphic(new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
    deleteButton.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_CIRCLE));
    requestButton.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT));
    completeButton.setGraphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE));

    // Set up tables
    interpreterTable = new SimpleTableView<>(new Interpreter("", "", ""), 80.0);
    interpreterTablePane.getChildren().addAll(interpreterTable);

    requestTable = new SimpleTableView<>(new InterpreterRequest("", "", "", "", ""), 60.0);
    requestTablePane.getChildren().addAll(requestTable);

    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          updateTables();
        });
  }

  @FXML
  public void updateTables() {
    try {
      interpreterTable.clear();
      interpreterTable.add(inventoryDatabase.getObservableListItem(ItemType.INTERPRETER));

      requestTable.clear();
      requestTable.add(serviceDatabase.getObservableListService(ServiceType.INTERPRETER_REQ));
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogPane,
          "Database Error",
          "Failed to pull interpreter list from database. Please try again later.");
    }
  }

  @FXML
  public void registerClicked() {
    DialogUtil.complexDialog(
        dialogPane,
        "Register Interpreter",
        "views/InterpreterDialog.fxml",
        false,
        event -> {
          updateTables();
        },
        new InterpreterDialogController());
  }

  @FXML
  public void deleteClicked() {
    Interpreter selected = (Interpreter) interpreterTable.getSelected();
    if (selected != null) {
      boolean success = inventoryDatabase.removeItem(selected.getId());
      if (success) {
        updateTables();
      } else {
        DialogUtil.simpleErrorDialog(
            dialogPane,
            "Database Error",
            "Failed to delete interpreter from database. Please try again later.");
      }
    } else {
      DialogUtil.simpleInfoDialog(
          dialogPane, "No Selection", "Please select an interpreter from the list and try again,");
    }
  }

  @FXML
  public void requestClicked() {
    DialogUtil.complexDialog(
        dialogPane,
        "New Interpreter Request",
        "views/InterpreterRequestDialog.fxml",
        false,
        event -> {
          updateTables();
        },
        new InterpreterRequestDialogController());
  }

  @FXML
  public void completeClicked() {
    InterpreterRequest selected = (InterpreterRequest) requestTable.getSelected();
    if (selected != null) {
      boolean success = serviceDatabase.setStatus(selected.getRequestID(), "Completed");
      if (success) {
        updateTables();
      } else {
        DialogUtil.simpleErrorDialog(
            dialogPane,
            "Database Error",
            "Failed to update status in database. Please try again later.");
      }
    }
  }
}
