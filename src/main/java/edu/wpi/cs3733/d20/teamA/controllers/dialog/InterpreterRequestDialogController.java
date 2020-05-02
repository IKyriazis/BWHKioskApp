package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.inventory.ItemType;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.database.service.interpreter.Interpreter;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class InterpreterRequestDialogController extends AbstractController
    implements IDialogController {
  @FXML private JFXComboBox<Interpreter> interpreterNameBox;
  @FXML private JFXComboBox<Node> locationBox;

  @FXML private JFXTextField languageField;
  @FXML private JFXButton doneButton;

  private JFXDialog dialog;

  @FXML
  public void initialize() {
    // Setup list of interpreter names
    ObservableList interpreters = FXCollections.observableArrayList();
    interpreters.addAll(inventoryDatabase.getObservableListItem(ItemType.INTERPRETER));
    interpreterNameBox.setItems(interpreters);

    // Set up list of node IDs
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));
    locationBox.setItems(allNodeList);
    locationBox
        .getEditor()
        .setOnKeyTyped(new NodeAutoCompleteHandler(locationBox, doneButton, allNodeList));
  }

  @FXML
  public void updateLanguage() {
    Interpreter selection = interpreterNameBox.getSelectionModel().getSelectedItem();
    if (selection != null) {
      languageField.setText(selection.getSecondLanguage());
    } else {
      languageField.setText("");
    }
  }

  @FXML
  public void clickedDone() {
    if (interpreterNameBox.getSelectionModel().getSelectedItem() == null
        || locationBox.getSelectionModel().getSelectedItem() == null) {
      return;
    }

    Optional<Node> dest =
        locationBox.getItems().stream()
            .filter(node -> node.toString().contains(locationBox.getEditor().getText()))
            .findFirst();

    if (dest.isEmpty()) {
      return;
    }

    Interpreter interpreter = interpreterNameBox.getSelectionModel().getSelectedItem();
    String id =
        serviceDatabase.addServiceReq(
            ServiceType.INTERPRETER_REQ,
            dest.get().getLongName(),
            interpreter.getName(),
            interpreter.getSecondLanguage());
    if (id == null) {
      DialogUtil.simpleErrorDialog(
          dialog.getDialogContainer(),
          "Database Error",
          "Failed to submit request to database. Please try again later.");
    } else {
      DialogUtil.simpleInfoDialog(
          dialog.getDialogContainer(), "Request Submitted", "Submitted translation request #" + id);
    }
    dialog.close();
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
