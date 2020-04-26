package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MedRequest implements ITableable<MedRequest> {
  private SimpleIntegerProperty orderNum;
  private SimpleStringProperty firstName;
  private SimpleStringProperty lastName;
  private SimpleStringProperty doctor;
  private SimpleStringProperty medicine;
  private SimpleIntegerProperty roomNum;
  private SimpleStringProperty progress;
  private SimpleStringProperty time;
  private SimpleStringProperty fulfilledBy;

  public MedRequest(
      int orderNum,
      String firstName,
      String lastName,
      String doctor,
      String medicine,
      int roomNum,
      String progress,
      String time,
      String fulfilledBy) {

    this.orderNum = new SimpleIntegerProperty(orderNum);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.doctor = new SimpleStringProperty(doctor);
    this.medicine = new SimpleStringProperty(medicine);
    this.roomNum = new SimpleIntegerProperty(roomNum);
    this.progress = new SimpleStringProperty(progress);
    this.time = new SimpleStringProperty(time);
    this.fulfilledBy = new SimpleStringProperty(fulfilledBy);
  }

  public void setOrderNum(int orderNum) {
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

  public void setTime(String time) {
    this.time.set(time);
  }

  public void setFulfilledBy(String fulfilledBy) {
    this.fulfilledBy.set(fulfilledBy);
  }

  public int getOrderNum() {
    return orderNum.get();
  }

  public SimpleIntegerProperty orderNumProperty() {
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

  public String getTime() {
    return time.get();
  }

  public SimpleStringProperty timeProperty() {
    return time;
  }

  public String getFulfilledBy() {
    return fulfilledBy.get();
  }

  public SimpleStringProperty fulfilledByProperty() {
    return fulfilledBy;
  }

  @Override
  public ArrayList<JFXTreeTableColumn<MedRequest, ?>> getColumns() {
    JFXTreeTableColumn<MedRequest, Integer> column1 = new JFXTreeTableColumn<>("Request Number");
    column1.setCellValueFactory(param -> param.getValue().getValue().orderNumProperty().asObject());

    JFXTreeTableColumn<MedRequest, String> column2 = new JFXTreeTableColumn<>("First Name");
    column2.setCellValueFactory(param -> param.getValue().getValue().firstNameProperty());

    JFXTreeTableColumn<MedRequest, String> column3 = new JFXTreeTableColumn<>("Last Name");
    column3.setCellValueFactory(param -> param.getValue().getValue().lastNameProperty());

    JFXTreeTableColumn<MedRequest, String> column4 = new JFXTreeTableColumn<>("Doctor");
    column4.setCellValueFactory(param -> param.getValue().getValue().doctorProperty());

    JFXTreeTableColumn<MedRequest, String> column5 = new JFXTreeTableColumn<>("Medicine");
    column5.setCellValueFactory(param -> param.getValue().getValue().medicineProperty());

    JFXTreeTableColumn<MedRequest, Integer> column6 = new JFXTreeTableColumn<>("Room Number");
    column6.setCellValueFactory(param -> param.getValue().getValue().roomNumProperty().asObject());

    JFXTreeTableColumn<MedRequest, String> column7 = new JFXTreeTableColumn<>("Progress");
    column7.setCellValueFactory(param -> param.getValue().getValue().progressProperty());

    JFXTreeTableColumn<MedRequest, String> column8 = new JFXTreeTableColumn<>("Time Administered");
    column8.setCellValueFactory(param -> param.getValue().getValue().timeProperty());

    JFXTreeTableColumn<MedRequest, String> column9 = new JFXTreeTableColumn<>("Fulfilled By");
    column9.setCellValueFactory(param -> param.getValue().getValue().fulfilledByProperty());
    return new ArrayList<>(
        List.of(column1, column2, column3, column4, column5, column6, column7, column8, column9));
  }
}
