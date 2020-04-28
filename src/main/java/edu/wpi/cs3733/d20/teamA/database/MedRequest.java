package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class MedRequest implements ITableable<MedRequest> {
  private SimpleStringProperty orderNum;
  private SimpleStringProperty firstName;
  private SimpleStringProperty lastName;
  private SimpleStringProperty name;
  private SimpleStringProperty doctor;
  private SimpleStringProperty medicine;
  private SimpleIntegerProperty roomNum;
  private SimpleStringProperty progress;
  // private SimpleStringProperty time;
  private SimpleObjectProperty<LocalTime> time;
  private SimpleStringProperty fulfilledBy;

  public MedRequest(
      String orderNum,
      String firstName,
      String lastName,
      String doctor,
      String medicine,
      int roomNum,
      String progress,
      int hour,
      int minute,
      String fulfilledBy) {

    this.orderNum = new SimpleStringProperty(orderNum);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.name = new SimpleStringProperty(firstName + " " + lastName);
    this.doctor = new SimpleStringProperty(doctor);
    this.medicine = new SimpleStringProperty(medicine);
    this.roomNum = new SimpleIntegerProperty(roomNum);
    this.progress = new SimpleStringProperty(progress);
    this.time = new SimpleObjectProperty<LocalTime>();

    if (hour >= 0 && minute >= 0) {
      time.set(LocalTime.of(hour, minute));
    }

    this.fulfilledBy = new SimpleStringProperty(fulfilledBy);
  }

  public void setOrderNum(String orderNum) {
    this.orderNum.set(orderNum);
  }

  public void setFirstName(String firstName) {
    this.firstName.set(firstName);
  }

  public void setLastName(String lastName) {
    this.lastName.set(lastName);
  }

  public void setDoctor(String doctor) {
    this.doctor.set(doctor);
  }

  public void setMedicine(String medicine) {
    this.medicine.set(medicine);
  }

  public void setRoomNum(int roomNum) {
    this.roomNum.set(roomNum);
  }

  public void setProgress(String progress) {
    this.progress.set(progress);
  }

  public void setFulfilledBy(String fulfilledBy) {
    this.fulfilledBy.set(fulfilledBy);
  }

  public String getOrderNum() {
    return orderNum.get();
  }

  public SimpleStringProperty orderNumProperty() {
    return orderNum;
  }

  public String getFirstName() {
    return firstName.get();
  }

  public SimpleStringProperty firstNameProperty() {
    return firstName;
  }

  public String getLastName() {
    return lastName.get();
  }

  public SimpleStringProperty lastNameProperty() {
    return lastName;
  }

  public String getDoctor() {
    return doctor.get();
  }

  public SimpleStringProperty doctorProperty() {
    return doctor;
  }

  public String getMedicine() {
    return medicine.get();
  }

  public SimpleStringProperty medicineProperty() {
    return medicine;
  }

  public int getRoomNum() {
    return roomNum.get();
  }

  public SimpleIntegerProperty roomNumProperty() {
    return roomNum;
  }

  public String getProgress() {
    return progress.get();
  }

  public SimpleStringProperty progressProperty() {
    return progress;
  }

  public LocalTime getTime() {
    return time.get();
  }

  public SimpleObjectProperty<LocalTime> timeProperty() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time.set(time);
  }

  public String getFulfilledBy() {
    return fulfilledBy.get();
  }

  public SimpleStringProperty fulfilledByProperty() {
    return fulfilledBy;
  }

  public SimpleStringProperty timeStProperty() {
    try {
      SimpleStringProperty s = new SimpleStringProperty(this.time.get().toString());
      return s;
    } catch (NullPointerException ex) {
      return new SimpleStringProperty();
    }
  }

  public String getName() {
    return name.get();
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  public void setName(String name) {
    this.name.set(name);
  }

  @Override
  public ArrayList<JFXTreeTableColumn<MedRequest, ?>> getColumns() {
    /*
    JFXTreeTableColumn<MedRequest, String> column1 = new JFXTreeTableColumn<>("Request ID");
    column1.setCellValueFactory(param -> param.getValue().getValue().orderNumProperty());


     */
    /*
    JFXTreeTableColumn<MedRequest, String> column2 = new JFXTreeTableColumn<>("First Name");
    column2.setCellValueFactory(param -> param.getValue().getValue().firstNameProperty());

    JFXTreeTableColumn<MedRequest, String> column3 = new JFXTreeTableColumn<>("Last Name");
    column3.setCellValueFactory(param -> param.getValue().getValue().lastNameProperty());


     */

    JFXTreeTableColumn<MedRequest, String> column3 = new JFXTreeTableColumn<>("Name");
    column3.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

    /*
    JFXTreeTableColumn<MedRequest, String> column4 = new JFXTreeTableColumn<>("Doctor");
    column4.setCellValueFactory(param -> param.getValue().getValue().doctorProperty());


     */
    JFXTreeTableColumn<MedRequest, String> column5 = new JFXTreeTableColumn<>("Medicine");
    column5.setCellValueFactory(param -> param.getValue().getValue().medicineProperty());

    JFXTreeTableColumn<MedRequest, Integer> column6 = new JFXTreeTableColumn<>("Room Number");
    column6.setCellValueFactory(param -> param.getValue().getValue().roomNumProperty().asObject());

    JFXTreeTableColumn<MedRequest, String> column7 = new JFXTreeTableColumn<>("Progress");
    column7.setCellValueFactory(param -> param.getValue().getValue().progressProperty());

    /*
    JFXTreeTableColumn<MedRequest, String> column8 = new JFXTreeTableColumn<>("Time Administered");
    column8.setCellValueFactory(param -> param.getValue().getValue().timeStProperty());


     */
    JFXTreeTableColumn<MedRequest, String> column9 = new JFXTreeTableColumn<>("Fulfilled By");
    column9.setCellValueFactory(param -> param.getValue().getValue().fulfilledByProperty());
    return new ArrayList<>(List.of(column3, column5, column6, column7, column9));
  }
}
