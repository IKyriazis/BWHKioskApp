package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.InterpreterDialogController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.InterpreterRequestDialogController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Interpreter;
import edu.wpi.cs3733.d20.teamA.database.InterpreterRequest;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class InterpreterController extends AbstractController {
  @FXML private GridPane interpreterTablePane;
  @FXML private GridPane requestTablePane;
  @FXML private StackPane dialogPane;
  @FXML private Pane rootPane;

  @FXML private JFXButton registerButton;
  @FXML private JFXButton deleteButton;
  @FXML private JFXButton requestButton;
  @FXML private JFXButton completeButton;

  private SimpleTableView<Interpreter> interpreterTable;
  private SimpleTableView<InterpreterRequest> requestTable;

  @FXML
  public void initialize() {
    // Set up tables
    interpreterTable = new SimpleTableView<>(new Interpreter("", ""), 80.0);
    interpreterTablePane.getChildren().addAll(interpreterTable);

    requestTable = new SimpleTableView<>(new InterpreterRequest(0, "", "", "", ""), 60.0);
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
      interpreterTable.add(iDB.getInterpreters());

      requestTable.clear();
      requestTable.add(iDB.getRequests());
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
    Interpreter selected = interpreterTable.getSelected();
    if (selected != null) {
      boolean success = iDB.deleteInterpreter(selected.getName());
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
    InterpreterRequest selected = requestTable.getSelected();
    if (selected != null) {
      boolean success = iDB.updateRequestStatus(selected.getRequestNumber(), "Completed");
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
