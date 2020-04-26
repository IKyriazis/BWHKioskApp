package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class LaundryController extends AbstractController {

    @FXML
    private JFXButton addRequestButton;
    @FXML
    private JFXButton removeRequestButton;
    @FXML
    private JFXButton seeCompletedButton;
    @FXML
    private JFXButton updateCleanerButton;
    @FXML
    private JFXButton updateProgressButton;

    @FXML
    private JFXComboBox<String> cleanerComboBox;
    @FXML
    private JFXComboBox<String> progressComboBox;
    @FXML
    private JFXComboBox<Node> roomList;

    @FXML private GridPane orderTablePane;

    private SimpleTableView<Flower> tblLaundryView;
}
