package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.controllers.dialog.AddMedRequestController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.MedRequest;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class MedicineDeliveryController extends AbstractController {

  @FXML private GridPane medList;
  @FXML private AnchorPane medPane;
  @FXML private StackPane dialogStackPane;
  private SimpleTableView<MedRequest> tblMedReq;

  public void initialize() {
    if (medicineRequestDatabase.getRequestSize() == -1) {
      medicineRequestDatabase.dropTables();
      medicineRequestDatabase.createTables();
      medicineRequestDatabase.readFromCSV();
    } else if (medicineRequestDatabase.getRequestSize() == 0) {
      medicineRequestDatabase.removeAll();
      medicineRequestDatabase.readFromCSV();
    }

    medPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    System.out.print(medicineRequestDatabase.getRequestSize());
    tblMedReq = new SimpleTableView<>(new MedRequest(0, "", "", "", "", 0, "", "", ""), 150.0);
    medList.getChildren().add(tblMedReq);

    update();
  }

  public void update() {
    try {
      tblMedReq.clear();
      tblMedReq.add(medicineRequestDatabase.requests());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void addRequest() {
    DialogUtil.complexDialog(
        dialogStackPane,
        "Add New Request",
        "views/AddMedRequestPopup.fxml",
        false,
        event -> update(),
        new AddMedRequestController());
  }
}
