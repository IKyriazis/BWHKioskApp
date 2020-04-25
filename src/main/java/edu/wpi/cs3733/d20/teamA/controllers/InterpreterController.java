package edu.wpi.cs3733.d20.teamA.controllers;

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
          updateTable();
        });
  }

  @FXML
  public void updateTable() {
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
}
