package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class InterpreterRequest implements ITableable<InterpreterRequest> {
  SimpleStringProperty requestID;
  SimpleStringProperty name;
  SimpleStringProperty language;
  SimpleStringProperty location;
  SimpleStringProperty status;

  public InterpreterRequest(
      String id, String name, String language, String location, String status) {
    this.requestID = new SimpleStringProperty(id);
    this.name = new SimpleStringProperty(name);
    this.language = new SimpleStringProperty(language);
    this.location = new SimpleStringProperty(location);
    this.status = new SimpleStringProperty(status);
  }

  public String getRequestID() {
    return requestID.get();
  }

  public SimpleStringProperty requestIDProperty() {
    return requestID;
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

  public String getLanguage() {
    return language.get();
  }

  public SimpleStringProperty languageProperty() {
    return language;
  }

  public String getLocation() {
    return location.get();
  }

  public SimpleStringProperty locationProperty() {
    return location;
  }

  public void setLocation(String location) {
    this.location.set(location);
  }

  public String getStatus() {
    return status.get();
  }

  public SimpleStringProperty statusProperty() {
    return status;
  }

  public void setStatus(String status) {
    this.status.set(status);
  }

  @Override
  public ArrayList<JFXTreeTableColumn<InterpreterRequest, ?>> getColumns() {
    JFXTreeTableColumn<InterpreterRequest, String> col0 = new JFXTreeTableColumn<>("Number");
    col0.setCellValueFactory(param -> param.getValue().getValue().requestIDProperty());

    JFXTreeTableColumn<InterpreterRequest, String> col1 = new JFXTreeTableColumn<>("Name");
    col1.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

    JFXTreeTableColumn<InterpreterRequest, String> col2 = new JFXTreeTableColumn<>("Language");
    col2.setCellValueFactory(param -> param.getValue().getValue().languageProperty());

    JFXTreeTableColumn<InterpreterRequest, String> col3 = new JFXTreeTableColumn<>("Location");
    col3.setCellValueFactory(param -> param.getValue().getValue().locationProperty());

    JFXTreeTableColumn<InterpreterRequest, String> col4 = new JFXTreeTableColumn<>("Status");
    col4.setCellValueFactory(param -> param.getValue().getValue().statusProperty());

    return new ArrayList<>(List.of(col0, col1, col2, col3, col4));
  }
}
