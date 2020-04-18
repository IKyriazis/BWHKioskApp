package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class FlowerModController {
  private FlowerDatabase d;

  private boolean modify;

  @FXML private JFXTextField txtName;
  @FXML private JFXTextField txtColor;
  @FXML private JFXTextField txtQty;
  @FXML private JFXTextField txtCost;

  private Flower myFlower;

  public FlowerModController(FlowerDatabase database) {
    System.out.println("Constructor for modify window");
    d = database;
    modify = false;
  }

  /* public FlowerModController(ModifyController ctrl) {
      modify = false;

      prevControl = ctrl;
  }

  public NodesPopUpController(ModifyController ctrl, ProtoNode n) {
      modify = true;

      prevControl = ctrl;

      myNode = n;
  }*/

  public void initialize() {
    /*nodeType.getItems().addAll(types);
    nodeType.getSelectionModel().select(0);
    if (modify) {
        nodeID.setText(myNode.getId());

        nodeType.getSelectionModel().select(myNode.getNodeType());

        nodeID.setText(myNode.getId());
        floor.setText("" + myNode.getFloor());
        building.setText(myNode.getBuilding());
        longName.setText(myNode.getLongName());
        shortName.setText(myNode.getShortName());
        xCoord.setText("" + myNode.getXcoord());
        yCoord.setText("" + myNode.getYcoord());

        nodeID.setEditable(false);

        done.setDisable(false);
    }*/
  }

  // Scene switch & database addNode
  @FXML
  private void isDone(ActionEvent e) throws SQLException {
    String name = txtName.getText();
    String color = txtColor.getText();
    int qty = Integer.parseInt(txtQty.getText());
    double price = Double.parseDouble(txtCost.getText());

    if (!modify) d.addFlower(name, color, qty, price);
    // MODIFY FLOWER else d.c(name,color,qty,price);
    Stage stage;
    stage =
        (Stage) this.txtColor.getScene().getWindow(); // Get the stage from an arbitrary component
    stage.close();
  }
}
