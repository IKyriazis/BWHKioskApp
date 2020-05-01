package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.PrescriptionDialogController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Prescription;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class PrescriptionController extends AbstractController {
  @FXML private Label lblTitle;

  @FXML private JFXButton addPrescriptionBtn;
  @FXML private JFXButton editPrescriptionBtn;
  @FXML private JFXButton deletePrescriptionBtn;
  @FXML private JFXButton infoPrescriptionBtn;

  @FXML private GridPane prescriptionTablePane;
  @FXML private StackPane prescriptionStackPane;

  private Prescription selected;

  private SimpleTableView tblViewPrescription;

  public void initialize() {
    // Set up icons for buttons and label
    lblTitle.setGraphic(new MaterialIconView(MaterialIcon.LOCAL_PHARMACY));
    addPrescriptionBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE));
    editPrescriptionBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE));
    deletePrescriptionBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE));
    infoPrescriptionBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QUESTION));

    // Setup table
    tblViewPrescription = new SimpleTableView<>(new Prescription(null, null, null), 80.0);
    prescriptionTablePane.getChildren().addAll(tblViewPrescription);
    // Double click a row in the prescription table to bring up the dialog for that order
    tblViewPrescription.setRowFactory(
        tv -> {
          TreeTableRow<Prescription> row = new TreeTableRow<>();
          row.setOnMouseClicked(
              event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                  openEditDialog();
                }
              });
          return row;
        });

    // Track when the mouse has clicked the table
    tblViewPrescription.setOnMouseClicked(
        event -> {
          selected = (Prescription) tblViewPrescription.getSelected();
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

      tblViewPrescription.add(serviceDatabase.observableList(ServiceType.PRESCRIPTION));
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          prescriptionStackPane, "Error", "Failed to update Prescription Table");
    }
  }

  /** Called when the mouse clicks on anything that isn't the table, or buttons it is disabling. */
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

  public void openEditDialog() {
    selected = (Prescription) tblViewPrescription.getSelected();
    DialogUtil.complexDialog(
        prescriptionStackPane,
        "Edit Prescription",
        "views/PrescriptionServiceAddDialog.fxml",
        false,
        event -> updateTable(),
        new PrescriptionDialogController(selected, true, false));
  }

  public void openDeleteDialog(ActionEvent e) {
    selected = (Prescription) tblViewPrescription.getSelected();
    if (selected != null) {
      serviceDatabase.deleteServReq(selected.getPrescriptionID());
      updateTable();
    }
  }

  public void openInfoDialog(ActionEvent e) {
    DialogUtil.complexDialog(
        prescriptionStackPane,
        "Info Prescription",
        "views/PrescriptionServiceAddDialog.fxml",
        false,
        event -> updateTable(),
        new PrescriptionDialogController(selected, true, true));
  }
}
