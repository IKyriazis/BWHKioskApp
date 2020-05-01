package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class JanitorService extends RecursiveTreeObject<JanitorService>
    implements ITableable<JanitorService> {
  @Setter private SimpleStringProperty location;
  @Setter private SimpleStringProperty priority;
  @Setter private SimpleStringProperty status;
  @Setter private SimpleStringProperty employeeName;
  private SimpleStringProperty index;
  @Setter private SimpleStringProperty longName;

  public JanitorService(
      String location,
      String priority,
      String status,
      String employeeName,
      String id,
      String longName) {
    this.location = new SimpleStringProperty(location);
    this.priority = new SimpleStringProperty(priority);
    this.status = new SimpleStringProperty(status);
    this.employeeName = new SimpleStringProperty(employeeName);
    this.index = new SimpleStringProperty(id);
    this.longName = new SimpleStringProperty(longName);
  }

  public SimpleStringProperty location() {
    return location;
  }

  public SimpleStringProperty priority() {
    return priority;
  }

  public SimpleStringProperty status() {
    return status;
  }

  public SimpleStringProperty employeeName() {
    return employeeName;
  }

  public SimpleStringProperty index() {
    return index;
  }

  public String getPriority() {
    return priority.get();
  }

  public String getLocation() {
    return location.get();
  }

  public String getStatus() {
    return status.get();
  }

  public String getEmployeeName() {
    return employeeName.get();
  }

  public String getIndex() {
    return index.get();
  }

  public String getLongName() {
    return longName.get();
  }

  @Override
  public ArrayList<JFXTreeTableColumn<JanitorService, ?>> getColumns() {
    JFXTreeTableColumn<JanitorService, String> column1 = new JFXTreeTableColumn<>("Location");
    column1.setCellValueFactory(param -> param.getValue().getValue().location);

    JFXTreeTableColumn<JanitorService, String> column5 =
        new JFXTreeTableColumn<>("Assigned employee");
    column5.setCellValueFactory(param -> param.getValue().getValue().longName);

    JFXTreeTableColumn<JanitorService, String> column2 = new JFXTreeTableColumn<>("Priority");
    column2.setCellValueFactory(param -> param.getValue().getValue().priority);

    JFXTreeTableColumn<JanitorService, String> column3 = new JFXTreeTableColumn<>("Status");
    column3.setCellValueFactory(param -> param.getValue().getValue().status);

    JFXTreeTableColumn<JanitorService, String> column4 = new JFXTreeTableColumn<>("Created by");
    column4.setCellValueFactory(param -> param.getValue().getValue().employeeName);

    return new ArrayList<>(List.of(column1, column2, column3, column5, column4));
  }
}
