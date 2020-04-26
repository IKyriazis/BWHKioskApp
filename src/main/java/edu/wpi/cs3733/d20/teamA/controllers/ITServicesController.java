package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ITServicesController extends AbstractController {

  @FXML private JFXTextField ITTicketName;
  @FXML private JFXComboBox ITTicketLocation;
  @FXML private JFXComboBox ITTicketCategory;
  @FXML private JFXTextArea ITTicketDescription;
  @FXML private GridPane ticketTablePane;
  @FXML private HBox changeStatusOptions;
  @FXML private JFXComboBox statusChangeStatus;
  @FXML private JFXTextField statusChangeName;

  public void initialize() {
    if (itTicketDatabase.getSizeITTickets() == -1) {
      itTicketDatabase.dropTables();
      itTicketDatabase.createTables();
      itTicketDatabase.readITTicketsCSV();
    } else if (itTicketDatabase.getSizeITTickets() == 0) {
      flDatabase.removeAllOrders();
      flDatabase.removeAllFlowers();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    }
  }

  public void deleteRequest(ActionEvent actionEvent) {}

  public void changeStatus(ActionEvent actionEvent) {}

  public void submitTicket(ActionEvent actionEvent) {
    //    Node node = roomList.getSelectionModel().getSelectedItem();
    //    String loc = "";
    //    if (node != null && !comboboxPriority.getValue().equals("")) {
    //      loc = node.getNodeID();
    //      janitorDatabase.addRequest(loc, comboboxPriority.getValue());
    //      statusHash.put(loc, "Not Started");
    //      comboboxPriority.getSelectionModel().clearSelection();
    //      labelSubmitRequest.setText("Request Submitted Successfully");
    //    } else if (comboboxPriority.getValue() != null && node == null) {
    //      labelSubmitRequest.setText("Please enter a location");
    //    } else if (comboboxPriority.getValue() == null && node != null) {
    //      labelSubmitRequest.setText("Please enter a priority");
    //    } else if (comboboxPriority.getValue() == null && node == null) {
    //      labelSubmitRequest.setText("Please enter data");
    //    }
    //    refreshActiveRequests();
  }
}
