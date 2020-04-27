package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerEditController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.EquipRequest;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.Order;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.util.Comparator;
import java.util.stream.Collectors;

public class EquipReqController extends AbstractController{

    @FXML JFXButton addReqbttn;
    @FXML JFXButton delReqbttn;
    @FXML JFXComboBox priCombo;
    @FXML JFXComboBox locCombo;
    @FXML JFXTextField itemField;
    @FXML JFXTextField qtyField;

    private EquipRequest selected;
    private SimpleTableView<EquipRequest> tblEquipView;

    public void initialize(){

        if(eDB.getSizeReq() == -1){
            eDB.dropTables();
            eDB.createTables();
        }else if(eDB.getSizeReq() == 0){
            eDB.removeAllReqs();
        }

        //initialize choices for combo
        priCombo.getItems().addAll("High", "Medium", "Low");
        ObservableList<Node> allNodeList =
                FXCollections.observableArrayList(
                        Graph.getInstance().getNodes().values().stream()
                        .filter(node -> node.getFloor() == 1)
                        .collect(Collectors.toList()));
        allNodeList.sort(Comparator.comparing(Node::getLongName));
        locCombo.setItems(allNodeList);

        tblEquipView =
                new SimpleTableView<>(
                        new EquipRequest("", "", 0, "", "", ""), 80.0);



    }


}
