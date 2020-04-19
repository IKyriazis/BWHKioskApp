package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.JanitorDatabase;
import javafx.fxml.FXML;

import java.sql.*;


public class JanitorialController {

    JanitorDatabase Database;

    @FXML
    private JFXComboBox<String> comboboxActiveRequests;
    @FXML
    private JFXButton btnClearRequest;
    @FXML
    private JFXButton btnSubmitRequest;
    @FXML
    private JFXTextField textfieldLocation;
    @FXML
    private JFXTextField textfieldPriority;

    private void addServiceRequest() throws SQLException {
        Database.addRequest(textfieldLocation.getText(), textfieldPriority.getText());

    }

}
