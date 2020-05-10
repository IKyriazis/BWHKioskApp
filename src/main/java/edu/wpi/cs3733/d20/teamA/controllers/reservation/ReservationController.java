package edu.wpi.cs3733.d20.teamA.controllers.reservation;

import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.reservation.Reservation;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    // requestTablePane.getChildren().add(tblView);

    update();

    ObservableList<String> roomList = FXCollections.observableArrayList();

    roomList.addAll(
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

    roomList.addAll(graphDatabase.getNodeByType("CONF"));

    roomBox.getItems().addAll(roomList);

    datePicker.setValue(LocalDate.now());

    myReservationsCheck.setSelected(false);

    CalendarView calendarView = new CalendarView();

    CalendarSource calendarSource = new CalendarSource("My Calendars");

    for (String s : roomList) {
      calendarSource.getCalendars().add(new com.calendarfx.model.Calendar(s));
    }

    calendarView.getCalendarSources().addAll(calendarSource);

    requestTablePane.getChildren().add(calendarView);
  }
  // Creates a calendar datatype from a date and a time
  private Calendar makeCalendar(LocalDate date, LocalTime time) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, date.getYear());
    calendar.set(Calendar.MONTH, date.getMonthValue() - 1);
    calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
    calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
    calendar.set(Calendar.MINUTE, time.getMinute());
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar;
  }

  // Ads a reservation if it m=meets all of the requirements
  @FXML
  private void addReservation() {
    LocalDate date = datePicker.getValue();
    LocalTime start = startPicker.getValue();
    LocalTime end = endPicker.getValue();
    String room = roomBox.getValue();
    if (date == null || start == null || end == null || room == null) { // If any data is missing
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select all fields");
      return;
    }
    Calendar calendar1 = makeCalendar(date, start);

    Calendar calendar2 = makeCalendar(date, end);

    Calendar check = Calendar.getInstance();
    if (calendar1.compareTo(calendar2) == 0) { // If the times selected are the same, that's illegal
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select different times");
      return;
    } else if (calendar1.compareTo(calendar2)
        == 1) { // If the start date is larger than the end date, add one day to the end date
      calendar2.add(Calendar.DAY_OF_MONTH, 1);
    }
    if (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()
        > 43200000) { // If the difference between the times is more than 12 hours
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Time frame too large, maximum of 12 hours");
      return;
    }
    if (check.compareTo(calendar1) == 1) { // If the start time already happened
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select a future time");
      return;
    }
    if (checkTimes(room, calendar1, calendar2)) { // If there are no conflicting times
      if (reservationDatabase.addRes(
          calendar1, calendar2, room)) { // If the add to the database was successful
        update();
        DialogUtil.simpleInfoDialog(dialogStackPane, "Thank You", "Request Made");
        datePicker.setValue(LocalDate.now());
        startPicker.setValue(null);
        endPicker.setValue(null);
        roomBox.setValue(null);
        return;
      } else {
        DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Cannot submit request");
        return;
      }
    } else {
      DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Overlapping Times");
      return;
    }
  }

  // Checks to make sure there are no time overlaps
  private boolean checkTimes(String room, Calendar start, Calendar end) {
    ObservableList<Reservation> theList = reservationDatabase.getObservableListByRoom(room);
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    try {
      for (int i = 0;
          i < theList.size();
          i++) { // Look through the list of reservations for the room
        calendar1.setTime(sdf.parse(theList.get(i).getStartTime()));
        calendar2.setTime(sdf.parse(theList.get(i).getEndTime()));
        long c1 = calendar1.getTimeInMillis();
        long c2 = calendar2.getTimeInMillis();
        long sMil = start.getTimeInMillis();
        long eMil = end.getTimeInMillis();
        if (c1 >= sMil - 43200000
            && c1 <= sMil) { // If the start time is within 12 hours of the new start but less than
          // the new start
          if (c2 >= sMil) { // If the end time is greater than the start time, there is an overlap
            return false;
          }
        } else if (c1 <= eMil
            && c1 >= sMil) { // If the start time is between the new start and new end, there is an
          // overlap
          return false;
        }
      }
      return true;
    } catch (ParseException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void update() {
    try {
      tblView.clear();
      if (myReservationsCheck.selectedProperty().get()) {
        tblView.add(reservationDatabase.getObservableListByUser());
      } else {
        tblView.add(reservationDatabase.getObservableList());
      }
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(
          dialogStackPane, "Error", "Failed to update room scheduler table");
    }
  }
}
