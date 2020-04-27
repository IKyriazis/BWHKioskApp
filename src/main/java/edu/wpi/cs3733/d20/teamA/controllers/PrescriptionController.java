package edu.wpi.cs3733.d20.teamA.controllers;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PrescriptionController extends AbstractController {

  @FXML private Label prescriptionTblLbl;

  public void initialize() {
    if (prescriptionDatabase.getSizePrescription() == -1) {
      prescriptionDatabase.dropTables();
      prescriptionDatabase.createTables();
      prescriptionDatabase.readPrescriptionCSV();
    } else if (prescriptionDatabase.getSizePrescription() == 0) {
      prescriptionDatabase.removeAllPrescriptions();
      prescriptionDatabase.readPrescriptionCSV();
    }

    prescriptionTblLbl.setGraphic(new MaterialIconView(MaterialIcon.LOCAL_PHARMACY));
  }
}
