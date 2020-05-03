package edu.wpi.cs3733.d20.teamA.database.service;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class ServiceRequest implements ITableable<ServiceRequest> {
  private ServiceType serviceType;
  private SimpleStringProperty reqID;
  private SimpleStringProperty didReqName;
  private SimpleStringProperty madeReqName;
  private SimpleStringProperty timestamp;
  private SimpleStringProperty status;
  private SimpleStringProperty location;
  private SimpleStringProperty description;
  private SimpleStringProperty additional;

  public ServiceRequest(
      ServiceType serviceType,
      String reqID,
      String didReqName,
      String madeReqName,
      String timestamp,
      String status,
      String location,
      String description,
      String additional) {
    this.serviceType = serviceType;
    this.reqID = new SimpleStringProperty(reqID);
    this.didReqName = new SimpleStringProperty(didReqName);
    this.madeReqName = new SimpleStringProperty(madeReqName);
    this.timestamp = new SimpleStringProperty(timestamp);
    this.status = new SimpleStringProperty(status);
    this.location = new SimpleStringProperty(location);
    this.description = new SimpleStringProperty(description);
    this.additional = new SimpleStringProperty(additional);
  }

  @Override
  public ArrayList<JFXTreeTableColumn<ServiceRequest, ?>> getColumns() {
    JFXTreeTableColumn<ServiceRequest, String> col1 = new JFXTreeTableColumn<>("Type");
    col1.setCellValueFactory(
        param -> new SimpleStringProperty(param.getValue().getValue().serviceType.name()));

    JFXTreeTableColumn<ServiceRequest, String> col2 = new JFXTreeTableColumn<>("ID");
    col2.setCellValueFactory(param -> param.getValue().getValue().reqID);
    col2.setMinWidth(80.0);

    JFXTreeTableColumn<ServiceRequest, String> col3 = new JFXTreeTableColumn<>("User");
    col3.setCellValueFactory(param -> param.getValue().getValue().madeReqName);
    col3.setMinWidth(100);

    JFXTreeTableColumn<ServiceRequest, String> col4 = new JFXTreeTableColumn<>("Fulfiller");
    col4.setCellValueFactory(param -> param.getValue().getValue().didReqName);
    col4.setMinWidth(100);

    JFXTreeTableColumn<ServiceRequest, String> col5 = new JFXTreeTableColumn<>("Status");
    col5.setCellValueFactory(param -> param.getValue().getValue().status);
    col5.setMinWidth(150.0);

    JFXTreeTableColumn<ServiceRequest, String> col6 = new JFXTreeTableColumn<>("Location");
    col6.setCellValueFactory(param -> param.getValue().getValue().location);
    col6.setMinWidth(80.0);

    return new ArrayList<>(List.of(col1, col2, col3, col4, col5, col6));
  }

  public ServiceType getServiceType() {
    return serviceType;
  }

  public String getReqID() {
    return reqID.get();
  }

  public SimpleStringProperty reqIDProperty() {
    return reqID;
  }

  public String getDidReqName() {
    return didReqName.get();
  }

  public SimpleStringProperty didReqNameProperty() {
    return didReqName;
  }

  public String getMadeReqName() {
    return madeReqName.get();
  }

  public SimpleStringProperty madeReqNameProperty() {
    return madeReqName;
  }

  public String getTimestamp() {
    return timestamp.get();
  }

  public SimpleStringProperty timestampProperty() {
    return timestamp;
  }

  public String getStatus() {
    return status.get();
  }

  public SimpleStringProperty statusProperty() {
    return status;
  }

  public String getLocation() {
    return location.get();
  }

  public SimpleStringProperty locationProperty() {
    return location;
  }

  public String getDescription() {
    return description.get();
  }

  public SimpleStringProperty descriptionProperty() {
    return description;
  }

  public String getAdditional() {
    return additional.get();
  }

  public SimpleStringProperty additionalProperty() {
    return additional;
  }
}
