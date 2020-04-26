package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.ITTicket;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class ITServicesController extends AbstractController {

  @FXML private JFXTextField ITTicketName;
  @FXML private JFXComboBox<Node> ITTicketLocation;
  @FXML private JFXComboBox<String> ITTicketCategory;
  @FXML private JFXTextArea ITTicketDescription;
  @FXML private GridPane ticketTablePane;
  @FXML private HBox changeStatusOptions;
  @FXML private JFXComboBox<String> statusChangeStatus;
  @FXML private JFXTextField statusChangeName;
  @FXML private StackPane dialogStackPane;

  private SimpleTableView<ITTicket> tblViewITTicket;

  public void initialize() {
    if (itTicketDatabase.getSizeITTickets() == -1) {
      itTicketDatabase.dropTables();
      itTicketDatabase.createTables();
      itTicketDatabase.readITTicketsCSV();
    } else if (itTicketDatabase.getSizeITTickets() == 0) {
      // itTicketDatabase.removeAllOrders();
      itTicketDatabase.readITTicketsCSV();
    }
    ITTicketCategory.getItems().addAll("Wifi", "Email", "Login", "Kiosk", "Pager", "Other");
    statusChangeStatus.getItems().addAll("Ticket Sent", "In Progress", "Complete");
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));

    ITTicketLocation.setItems(allNodeList);

    // Set up table
    tblViewITTicket =
        new SimpleTableView<>(
            new ITTicket(new Timestamp(System.currentTimeMillis()), "", "", "", "", "", ""), 80.0);
    ticketTablePane.getChildren().add(tblViewITTicket);

    updateTable();
  }

  public void deleteRequest(ActionEvent actionEvent) {}

  public void changeStatus(ActionEvent actionEvent) {}

  public void submitTicket(ActionEvent actionEvent) {
    Node locationSelection = ITTicketLocation.getSelectionModel().getSelectedItem();
    String location = "";
    if (locationSelection != null && !ITTicketLocation.getValue().equals("")) {
      location = locationSelection.getNodeID();
    }
  }

  public void updateTable() {
    try {
      tblViewITTicket.clear();

      tblViewITTicket.add(itTicketDatabase.ITTicketObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Failed to update IT Ticket Table");
    }
  }
}
