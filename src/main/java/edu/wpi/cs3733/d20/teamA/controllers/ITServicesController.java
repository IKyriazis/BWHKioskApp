package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ITServicesController extends AbstractController {

    @FXML
    private JFXTextField ITTicketName;
    @FXML
    private JFXComboBox ITTicketLocation;
    @FXML
    private JFXComboBox ITTicketCategory;
    @FXML
    private JFXTextArea ITTicketDescription;
    @FXML
    private GridPane ticketTablePane;
    @FXML
    private HBox changeStatusOptions;
    @FXML
    private JFXComboBox statusChangeStatus;
    @FXML
    private JFXTextField statusChangeName;

    public void submitTicket(ActionEvent actionEvent) {
    }

    public void changeStatus(ActionEvent actionEvent) {
    }
}
