package edu.wpi.cs3733.d20.teamA.database.reservation;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Reservation implements ITableable<Reservation> {
  @Setter private SimpleStringProperty requestedBy;
  @Setter private SimpleStringProperty startTime;
  @Setter private SimpleStringProperty endTime;
  @Setter private SimpleStringProperty areaReserved;

  public Reservation(
      String requestedBy, Calendar startTime, Calendar endTime, String areaReserved) {
    this.requestedBy = new SimpleStringProperty(requestedBy);
    this.startTime = new SimpleStringProperty(startTime.getTime().toString());
    this.endTime = new SimpleStringProperty(endTime.getTime().toString());
    this.areaReserved = new SimpleStringProperty(areaReserved);
  }

  public SimpleStringProperty requestedByProperty() {
    return requestedBy;
  }

  public SimpleStringProperty startTimeProperty() {
    return startTime;
  }

  public SimpleStringProperty endTimeProperty() {
    return endTime;
  }

  public SimpleStringProperty areaReservedProperty() {
    return areaReserved;
  }

  public String getRequestedBy() {
    return requestedBy.get();
  }

  public String getStartTime() {
    return startTime.get();
  }

  public String getEndTime() {
    return endTime.get();
  }

  public String getAreaReserved() {
    return areaReserved.get();
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Reservation, ?>> getColumns() {
    JFXTreeTableColumn<Reservation, String> column1 = new JFXTreeTableColumn<>("Area Reserved");
    column1.setCellValueFactory(param -> param.getValue().getValue().areaReservedProperty());

    JFXTreeTableColumn<Reservation, String> column2 = new JFXTreeTableColumn<>("Start Time");
    column2.setCellValueFactory(param -> param.getValue().getValue().startTimeProperty());

    JFXTreeTableColumn<Reservation, String> column3 = new JFXTreeTableColumn<>("End Time");
    column3.setCellValueFactory(param -> param.getValue().getValue().endTimeProperty());

    return new ArrayList<>(List.of(column1, column2, column3));
  }
}
