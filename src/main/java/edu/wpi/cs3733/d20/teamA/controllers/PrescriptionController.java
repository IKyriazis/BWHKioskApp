package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Prescription;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class PrescriptionController extends AbstractController {

  @FXML private JFXButton addPrescriptionBtn;
  @FXML private JFXButton editPrescriptionBtn;
  @FXML private JFXButton deletePrescriptionBtn;
  @FXML private JFXButton infoPrescriptionBtn;

  @FXML private GridPane prescriptionTablePane;
  @FXML private StackPane prescriptionStackPane;

  private Prescription selected;

  private SimpleTableView<Prescription> tblViewPrescription;

  public void initialize() {
    // Set up the database
    if (prescriptionDatabase.getSizePrescription() == -1) {
      prescriptionDatabase.dropTables();
      prescriptionDatabase.createTables();
      prescriptionDatabase.readPrescriptionCSV();
    } else if (prescriptionDatabase.getSizePrescription() == 0) {
      prescriptionDatabase.removeAllPrescriptions();
      prescriptionDatabase.readPrescriptionCSV();
    }

    // prescriptionTblLbl.setGraphic(new MaterialIconView(MaterialIcon.LOCAL_PHARMACY));

    // Setup table
    tblViewPrescription =
        new SimpleTableView<>(new Prescription(0, "", "", "", "", 0, "", ""), 80.0);
    prescriptionTablePane.getChildren().addAll(tblViewPrescription);

    // Track when the mouse has clicked the table
    tblViewPrescription.setOnMouseClicked(
        event -> {
          selected = tblViewPrescription.getSelected();
          if (selected != null) {
            editPrescriptionBtn.disableProperty().setValue(false);
            deletePrescriptionBtn.disableProperty().setValue(false);
            infoPrescriptionBtn.disableProperty().setValue(false);
          }
        });

    updateTable();
  }

  /** Updates the table with the items in the ITTicket database. */
  public void updateTable() {
    try {
      tblViewPrescription.clear();

      tblViewPrescription.add(prescriptionDatabase.prescriptionObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          prescriptionStackPane, "Error", "Failed to update Prescription Table");
    }
  }

  /**
   * Called when the mouse clicks on anything that isn't the table, or buttons it is disabling.
   *
   * @param mouseEvent mouseClicked
   */
  public void disableBtns(MouseEvent mouseEvent) {
    editPrescriptionBtn.disableProperty().setValue(true);
    deletePrescriptionBtn.disableProperty().setValue(true);
    infoPrescriptionBtn.disableProperty().setValue(true);
  }

  public void openAddDialog(ActionEvent e) {
    DialogUtil.complexDialog(
        prescriptionStackPane,
        "Add Prescription",
        "views/PrescriptionServiceAddDialog.fxml",
        false,
        event -> updateTable(),
        new PrescriptionDialogController());
  }

  public void openEditDialog(ActionEvent e) {
    DialogUtil.complexDialog(
        prescriptionStackPane,
        "Edit Prescription",
        "views/PrescriptionServiceAddDialog.fxml",
        false,
        event -> updateTable(),
        new PrescriptionDialogController());
  }

  public void openDeleteDialog(ActionEvent e) {}

  public void openInfoDialog(ActionEvent e) {
    DialogUtil.complexDialog(
        prescriptionStackPane,
        "Info Prescription",
        "views/PrescriptionServiceAddDialog.fxml",
        false,
        event -> updateTable(),
        new PrescriptionDialogController());
  }
}
