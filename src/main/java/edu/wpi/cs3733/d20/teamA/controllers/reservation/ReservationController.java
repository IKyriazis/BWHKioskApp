package edu.wpi.cs3733.d20.teamA.controllers.reservation;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.reservation.Reservation;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

public class ReservationController extends AbstractController {
    @FXML
    private JFXButton reservationButton;
    @FXML
    private JFXCheckBox myReservationsCheck;
    @FXML
    private GridPane requestTablePane;
    @FXML
    private Label headerLabel;
    @FXML
    private Label promptLabel;
    @FXML
    private StackPane dialogStackPane;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private JFXTimePicker startPicker;
    @FXML
    private JFXTimePicker endPicker;
    @FXML
    private JFXComboBox<String> roomBox;


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

        roomBox.getItems().addAll("On-Call Bed 1", "On-Call Bed 2", "On-Call Bed 3", "On-Call Bed 4", "On-Call Bed 5", "On-Call Bed 6", "On-Call Bed 7", "Reflection Room 1", "Reflection Room 2", "Reflection Room 3");

        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void addReservation() {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        LocalDate date = datePicker.getValue();
        LocalTime start = startPicker.getValue();
        LocalTime end = endPicker.getValue();
        String room = roomBox.getValue();
        if(date == null || start == null || end == null || room == null) {
            DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select all fields");
            return;
        }
        calendar1.set(Calendar.YEAR, date.getYear());
        calendar1.set(Calendar.MONTH, date.getMonthValue());
        calendar1.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        calendar1.set(Calendar.HOUR, start.getHour());
        calendar1.set(Calendar.MINUTE, start.getMinute());
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        calendar2.set(Calendar.YEAR, date.getYear());
        calendar2.set(Calendar.MONTH, date.getMonthValue());
        calendar2.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        calendar2.set(Calendar.HOUR, end.getHour());
        calendar2.set(Calendar.MINUTE, end.getMinute());
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        if(calendar1.compareTo(calendar2) == 0) {
            DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Please select different times");
            return;
        } else if (calendar1.compareTo(calendar2) == 1) {
            calendar2.add(Calendar.DAY_OF_MONTH, 1);
        }
        if(calendar2.getTimeInMillis() - calendar1.getTimeInMillis() > 43200000) {
            DialogUtil.simpleErrorDialog(dialogStackPane, "Error", "Time frame too large, maximum of 12 hours");
            return;
        } else {
            reservationDatabase.addRes(calendar1, calendar2, room);
            update();
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
