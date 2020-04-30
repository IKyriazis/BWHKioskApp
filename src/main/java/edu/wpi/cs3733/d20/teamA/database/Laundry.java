package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Laundry implements ITableable<Laundry> {
  @Setter private SimpleStringProperty requestNum;
  @Setter private SimpleStringProperty employeeEntered;
  @Setter private SimpleStringProperty location;
  @Setter private SimpleStringProperty progress;
  @Setter private SimpleStringProperty employeeWash;
  @Setter private SimpleStringProperty timeRequested;

  public Laundry(
      String requestNum,
      String employeeEntered,
      String location,
      String progress,
      String employeeWash,
      Timestamp timeRequested) {
    this.requestNum = new SimpleStringProperty(requestNum);
    this.employeeEntered = new SimpleStringProperty(employeeEntered);
    this.location = new SimpleStringProperty(location);
    this.progress = new SimpleStringProperty(progress);
    this.employeeWash = new SimpleStringProperty(employeeWash);
    this.timeRequested = new SimpleStringProperty(timeRequested.toString());
  }

  public SimpleStringProperty requestNumProperty() {
    return requestNum;
  }

  public SimpleStringProperty employeeEnteredProperty() {
    return employeeEntered;
  }

  public SimpleStringProperty locationProperty() {
    return location;
  }

  public SimpleStringProperty progressProperty() {
    return progress;
  }

  public SimpleStringProperty employeeWashProperty() {
    return employeeWash;
  }

  public SimpleStringProperty timeRequestedProperty() {
    return timeRequested;
  }

  public String getRequestNum() {
    return requestNum.get();
  }

  public String getEmployeeEntered() {
    return employeeEntered.get();
  }

  public String getLocation() {
    return location.get();
  }

  public String getProgress() {
    return progress.get();
  }

  public String getEmployeeWash() {
    return employeeWash.get();
  }

  public String getTimeRequested() {
    return timeRequested.get();
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Laundry, ?>> getColumns() {
    JFXTreeTableColumn<Laundry, String> column1 = new JFXTreeTableColumn<>("Request #");
    column1.setCellValueFactory(param -> param.getValue().getValue().requestNumProperty());

    JFXTreeTableColumn<Laundry, String> column2 = new JFXTreeTableColumn<>("Requested By");
    column2.setCellValueFactory(param -> param.getValue().getValue().employeeEnteredProperty());

    JFXTreeTableColumn<Laundry, String> column3 = new JFXTreeTableColumn<>("Location");
    column3.setCellValueFactory(param -> param.getValue().getValue().locationProperty());

    JFXTreeTableColumn<Laundry, String> column4 = new JFXTreeTableColumn<>("Progress");
    column4.setCellValueFactory(param -> param.getValue().getValue().progressProperty());

    JFXTreeTableColumn<Laundry, String> column5 = new JFXTreeTableColumn<>("Cleaner");
    column5.setCellValueFactory(param -> param.getValue().getValue().employeeWash);

    JFXTreeTableColumn<Laundry, String> column6 = new JFXTreeTableColumn<>("Request Time");
    column6.setCellValueFactory(param -> param.getValue().getValue().timeRequestedProperty());

    return new ArrayList<>(List.of(column1, column2, column3, column4, column5, column6));
  }
}
