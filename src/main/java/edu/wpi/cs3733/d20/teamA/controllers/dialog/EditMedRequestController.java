package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.MedRequest;
import java.time.LocalTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class EditMedRequestController extends AbstractController implements IDialogController {
  private final boolean modify;
  private MedRequest request;
  private JFXDialog dialog;
  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField doctor;
  @FXML private JFXTextField medicine;
  @FXML private JFXTextField roomNum;
  @FXML private JFXTextField fBy;
  @FXML private JFXButton done;
  @FXML private JFXTimePicker pTime;

  public EditMedRequestController() {
    super();

    modify = false;
  }

  public EditMedRequestController(MedRequest r) {
    super();

    this.modify = true;
    this.request = r;
  }

  public void initialize() {
    if (modify) {
      fName.setText(request.getFirstName());
      lName.setText(request.getLastName());
      doctor.setText(request.getDoctor());
      medicine.setText(request.getMedicine());
      fBy.setText(request.getFulfilledBy());
      roomNum.setText(request.getRoomNum() + "");
      pTime.setEditable(true);

      try {
        request.getTime();
        pTime.setValue(
            LocalTime.of(request.getTime().getHour(), request.getTime().getMinute(), 0, 0));
      } catch (NullPointerException np) {
        // do nothing
      }
    }

    done.setOnAction(this::pressDone);
  }

  // Scene switch & database addNode
  @FXML
  public void pressDone(ActionEvent e) {
    if (fName.getText().isEmpty()
        || lName.getText().isEmpty()
        || doctor.getText().isEmpty()
        || medicine.getText().isEmpty()
        || roomNum.getText().isEmpty()) {
      return;
    }

    try {
      String fNameText = fName.getText();
      String lNameText = lName.getText();
      String doctorText = doctor.getText();
      String medicineText = medicine.getText();
      String fulfilledBy = fBy.getText();
      int rnum = Integer.parseInt(roomNum.getText());
      int hour = -1;
      int minute = -1;

      if (!modify) {
        try {
          pTime.getValue();
          hour = pTime.getValue().getHour();
          minute = pTime.getValue().getMinute();
          super.medicineRequestDatabase.addRequest(
              fNameText, lNameText, doctorText, medicineText, rnum, hour, minute);
        } catch (NullPointerException ex) {
          super.medicineRequestDatabase.addRequest(
              fNameText, lNameText, doctorText, medicineText, rnum);
        }
      } else {
        try {
          pTime.getValue();
          hour = pTime.getValue().getHour();
          minute = pTime.getValue().getMinute();
        } catch (NullPointerException ex) {
        }

        super.medicineRequestDatabase.updateMedicine(request.getOrderNum(), medicineText);
        super.medicineRequestDatabase.updateDoctor(request.getOrderNum(), doctorText);
        if (hour >= 0 && minute >= 0) {
          super.medicineRequestDatabase.updateHo(request.getOrderNum(), hour);
          super.medicineRequestDatabase.updateMins(request.getOrderNum(), minute);
        }

        super.medicineRequestDatabase.updateFulfilledBy(request.getOrderNum(), fulfilledBy);
      }

      dialog.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}
