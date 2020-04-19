package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.JanitorDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.derby.iapi.types.SqlXmlUtil;

import java.sql.*;
import java.util.LinkedList;


public class JanitorialController {

    private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
    private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
    private Connection conn;
    GraphDatabase gDB;
    JanitorDatabase jDB;

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
    @FXML
    private Label confirmationTextBox;

    public void init() throws SQLException {
        conn = DriverManager.getConnection(jdbcUrl);
        gDB = new GraphDatabase(conn);
        jDB = new JanitorDatabase(conn);
        gDB.createTables();
        jDB.createTables();
    }

    @FXML
    private void addServiceRequest() throws SQLException {
        jDB.addRequest(textfieldLocation.getText(), textfieldPriority.getText());
        confirmationTextBox = new Label("Request Submitted Successfully");
    }

    @FXML
    private void removeServiceRequest() throws SQLException {
//        jDB.deleteRequest();

    }

    @FXML
    private void refreshActiveRequests() throws SQLException {
        String Request;
        int size = jDB.getRequestSize();
        LinkedList<String> listOfActiveRequestNames = new LinkedList<String>();
        for (int i=0; i<size; i++){
//            Request = jDB.printRequest(i);
//            if (Request==null) {
//                continue;
//            }
        }
    }
}