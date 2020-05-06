package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import edu.wpi.cs3733.d20.teamA.controllers.PatientInfoController;
import edu.wpi.cs3733.d20.teamA.controllers.ViewEmployeesController;
import edu.wpi.cs3733.d20.teamA.database.patient.Patient;
import edu.wpi.cs3733.d20.teamA.graph.Campus;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.map.MapCanvas;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.scene.layout.StackPane;

public class DialogMaker {
  private IDialogController editMed;
  private IDialogController employeeEdit;
  private IDialogController node;
  private IDialogController notif;
  private IDialogController patient;
  private IDialogController QR;

  public DialogMaker() {
    editMed = new EditMedRequestController();
    employeeEdit = new EmployeeEditController();
    notif = new NotificationController();
    patient = new PatientEditController();
  }

  public void makePatientDialog(PatientInfoController controller) {
    DialogUtil.complexDialog(
        "Add Patient",
        "views/AddPatientPopup.fxml",
        true,
        event -> controller.update(),
        this.patient);
  }

  public void makePatientDialog(PatientInfoController controller, Patient p) {
    this.patient = new PatientEditController(p);
    DialogUtil.complexDialog(
        "Add Patient",
        "views/AddPatientPopup.fxml",
        true,
        event -> controller.update(),
        this.patient);
  }

  public void makeEmployeeDialog(ViewEmployeesController controller) {
    DialogUtil.complexDialog(
        "Add Employee",
        "views/AddEmployeePopup.fxml",
        true,
        event -> controller.update(),
        this.employeeEdit);
  }

  public void makeQRDialog(String lastDirs, StackPane dialogPane) {
    QR = new QRDialogController(lastDirs);

    DialogUtil.complexDialog(
        dialogPane, "Direction QR Code", "views/QRCodePopup.fxml", true, null, this.QR);
  }

  public void makeNodeDialog(
      Campus campus,
      Node node,
      int x,
      int y,
      int floor,
      MapCanvas currCanvas,
      StackPane dialogPane) {
    this.node = new NodeDialogController(campus, node, x, y, floor);
    String heading = (node == null) ? "Add Node" : "Edit Node";
    DialogUtil.complexDialog(
        dialogPane,
        heading,
        "views/NodeModifyPopup.fxml",
        false,
        event -> currCanvas.draw(floor),
        this.node);
  }
}
