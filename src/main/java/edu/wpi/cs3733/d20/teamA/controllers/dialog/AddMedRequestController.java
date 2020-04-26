package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AddMedRequestController extends AbstractController implements IDialogController {
  private JFXDialog dialog;
  private final boolean modify;
  @FXML private JFXTextField fName;
  @FXML private JFXTextField lName;
  @FXML private JFXTextField doctor;
  @FXML private JFXTextField medicine;
  @FXML private JFXTextField roomNum;
  @FXML private JFXComboBox hourBox;
  @FXML private JFXComboBox minBox;

  public AddMedRequestController() {
    super();

    modify = false;
  }

  public void initialize() {
    ObservableList<String> minutes = FXCollections.observableArrayList();
    minutes.add("00");
    minutes.add("15");
    minutes.add("30");
    minutes.add("45");
    minBox.setItems(minutes);

    ObservableList<String> hours = FXCollections.observableArrayList();
    hours.add("00");
    hours.add("01");
    hours.add("02");
    hours.add("03");
    hours.add("04");
    hours.add("05");
    hours.add("06");
    hours.add("07");
    hours.add("08");
    hours.add("09");
    hours.add("10");
    hours.add("11");
    hours.add("12");
    hours.add("13");
    hours.add("14");
    hours.add("15");
    hours.add("16");
    hours.add("17");
    hours.add("18");
    hours.add("19");
    hours.add("20");
    hours.add("21");
    hours.add("22");
    hours.add("23");

    hourBox.setItems(hours);
  }

  // Scene switch & database addNode
  @FXML
  public void pressAdd(ActionEvent e) {
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
      int rnum = Integer.parseInt(roomNum.getText());

      if (hourBox.getSelectionModel().isEmpty()) {
        super.medicineRequestDatabase.addRequest(
            fNameText, lNameText, doctorText, medicineText, rnum);
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
