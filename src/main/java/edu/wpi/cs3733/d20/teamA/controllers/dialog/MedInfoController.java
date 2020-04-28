package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.MedRequest;
import javafx.fxml.FXML;

public class MedInfoController extends AbstractController implements IDialogController {
  private JFXDialog dialog;
  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField doctor;
  @FXML private JFXTextField medicine;
  @FXML private JFXTextField roomNum;
  @FXML private JFXTextField fBy;
  @FXML private JFXTextField status;
  @FXML private JFXTextField adminTime;
  private MedRequest request;

  public MedInfoController() {
    super();
  }

  public MedInfoController(MedRequest r) {
    super();
    this.request = r;
  }

  public void initialize() {
    fName.setText(request.getFirstName());
    lName.setText(request.getLastName());
    doctor.setText(request.getDoctor());
    medicine.setText(request.getMedicine());
    fBy.setText(request.getFulfilledBy());
    roomNum.setText(request.getRoomNum() + "");
    status.setText(request.getProgress());

    try {
      request.getTime();
      adminTime.setText(request.getTime().toString());
    } catch (NullPointerException np) {
      // do nothing
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
