package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class JanitorialController {

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

//    private void addServiceRequest() {
//        addRequest(textfieldLocation.getText(), textfieldPriority.getText());
//
//    }

}
