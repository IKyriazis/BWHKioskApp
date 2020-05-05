package edu.wpi.cs3733.d20.teamA.controllers.reservation;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.reservation.Reservation;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ReservationController extends AbstractController {
  @FXML private JFXButton reservationButton;
  @FXML private JFXCheckBox myReservationsCheck;
  @FXML private GridPane requestTablePane;
  @FXML private Label headerLabel;
  @FXML private Label promptLabel;
  @FXML private StackPane dialogStackPane;
  @FXML private JFXDatePicker datePicker;
  @FXML private JFXTimePicker startPicker;
  @FXML private JFXTimePicker endPicker;
  @FXML private JFXComboBox<String> roomBox;

  private SimpleTableView<Reservation> tblView;

  public void initialize() {
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.CALENDAR_ALT));
    reservationButton.setGraphic(new FontIcon(FontAwesomeSolid.CALENDAR_PLUS));
    myReservationsCheck.setGraphic(new FontIcon(FontAwesomeSolid.CALENDAR_CHECK));
    promptLabel.setGraphic(new FontIcon(FontAwesomeSolid.BARS));

    tblView =
        new SimpleTableView<>(
            new Reservation("", Calendar.getInstance(), Calendar.getInstance(), ""), 80.0);
    requestTablePane.getChildren().add(tblView);

    update();

    roomBox
        .getItems()
        .addAll(
            "On-Call Bed 1",
            "On-Call Bed 2",
            "On-Call Bed 3",
            "On-Call Bed 4",
            "On-Call Bed 5",
            "On-Call Bed 6",
            "On-Call Bed 7",
            "Reflection Room 1",
            "Reflection Room 2",
            "Reflection Room 3");

    datePicker.setValue(LocalDate.now());
  }

  private Calendar makeCalendar(LocalDate date, LocalTime time) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, date.getYear());
    calendar.set(Calendar.MONTH, date.getMonthValue() - 1);
    calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
    calendar.set(Calendar.HOUR, time.getHour());
    calendar.set(Calendar.MINUTE, time.getMinute());
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar;
  }

  @FXML
  private void addReservation() {
    LocalDate date = datePicker.getValue();
    LocalTime start = startPicker.getValue();
    LocalTime end = endPicker.getValue();
    String room = roomBox.getValue();
    if (date == null || start == null || end == null || room == null) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select all fields");
      return;
    }
    Calendar calendar1 = makeCalendar(date, start);

    Calendar calendar2 = makeCalendar(date, end);

    Calendar check = Calendar.getInstance();
    if (calendar1.compareTo(calendar2) == 0) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select different times");
      return;
    } else if (calendar1.compareTo(calendar2) == 1) {
      calendar2.add(Calendar.DAY_OF_MONTH, 1);
    }
    if (calendar2.getTimeInMillis() - calendar1.getTimeInMillis() > 43200000) {
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Time frame too large, maximum of 12 hours");
      return;
    }
    if (check.compareTo(calendar1) == 1) {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a future time");
      return;
    }
    if (reservationDatabase.addRes(calendar1, calendar2, room)) {
      update();
      DialogUtil.simpleInfoDialog(dialogStackPane, "Thank You", "Request Made");
    } else {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Cannot submit request");
    }
  }

  public void update() {
    try {
      tblView.clear();

      tblView.add(reservationDatabase.getObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update room scheduler table");
    }
  }
}
