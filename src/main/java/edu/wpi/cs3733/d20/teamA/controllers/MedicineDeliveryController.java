package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.EditMedRequestController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.MedInfoController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.MedRequest;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class MedicineDeliveryController extends AbstractController {
  @FXML private GridPane medList;
  @FXML private AnchorPane medPane;
  @FXML private StackPane dialogStackPane;

  @FXML private JFXButton addBtn;
  @FXML private JFXButton editBtn;
  @FXML private JFXButton deleteBtn;
  @FXML private JFXButton infoBtn;
  @FXML private JFXComboBox<String> progBox;
  @FXML private JFXButton updateProgBtn;
  private SimpleTableView<MedRequest> tblMedReq;
  private MedRequest lastOrder;

  public void initialize() {
    if (medicineRequestDatabase.getRequestSize() == -1) {
      medicineRequestDatabase.dropTables();
      medicineRequestDatabase.createTables();
      medicineRequestDatabase.readFromCSV();
    } else if (medicineRequestDatabase.getRequestSize() == 0) {
      medicineRequestDatabase.removeAll();
      medicineRequestDatabase.readFromCSV();
    }
    // Set icon
    addBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE));
    editBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE));
    deleteBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE));
    infoBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QUESTION));

    medPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          update();
        });

    ObservableList<String> progs = FXCollections.observableArrayList();
    progs.add("Prescribed");
    progs.add("Dispatched");
    progs.add("Done");
    progBox.setItems(progs);

    tblMedReq = new SimpleTableView<>(new MedRequest("", "", "", "", "", 0, "", -1, -1, ""), 150.0);
    medList.getChildren().add(tblMedReq);

    // Set up table to open edit controller when double clicking row
    tblMedReq.setRowFactory(
        tv -> {
          TreeTableRow<MedRequest> row = new TreeTableRow<>();
          row.setOnMouseClicked(
              event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                  editRequest();
                }
              });
          return row;
        });

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
        new EditMedRequestController());
  }

  public void clickInfo() {
    MedRequest req = tblMedReq.getSelected();
    if (req != null) {
      // Figure out whether any outstanding orders depend on this flower type, in which case we
      // can't change the name / type

      MedInfoController controller = new MedInfoController(req);
      DialogUtil.complexDialog(
          dialogStackPane,
          "View Medicine Request",
          "views/MedRequestInfoPopup.fxml",
          false,
          event -> update(),
          controller);
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Request Selected",
          "Please select a medicine request by clicking a row in the table");
    }
  }

  public void editRequest() {
    MedRequest req = tblMedReq.getSelected();
    if (req != null) {
      // Figure out whether any outstanding orders depend on this flower type, in which case we
      // can't change the name / type

      EditMedRequestController controller = new EditMedRequestController(req);
      DialogUtil.complexDialog(
          dialogStackPane,
          "Edit Medicine Request",
          "views/AddMedRequestPopup.fxml",
          false,
          event -> update(),
          controller);
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Request Selected",
          "Please select a medicine request by clicking a row in the table");
    }
  }

  public void updateProg() {
    MedRequest selected = tblMedReq.getSelected();
    if (selected != null) {
      // track the last selected order
      lastOrder = selected;
    }
    if (lastOrder != null) {
      String s = progBox.getSelectionModel().getSelectedItem();
      super.medicineRequestDatabase.updateProgress(lastOrder.getOrderNum(), s);
      lastOrder = null;
      update();
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Order Selected",
          "Please select an order by clicking a row in the table");
    }
  }

  public void deleteRequest() {
    MedRequest req = tblMedReq.getSelected();
    if (req != null) {
      String num = req.getOrderNum();

      try {
        super.medicineRequestDatabase.deleteRequest(num);
      } catch (Exception e) {
        e.printStackTrace();
        DialogUtil.simpleErrorDialog(
            dialogStackPane, "Error Deleting Request", "Could not delete request: " + req);
      }

      update();
    } else {
      DialogUtil.simpleInfoDialog(
          dialogStackPane,
          "No Request Selected",
          "Please select a request by clicking a row in the table");
    }
  }
}
