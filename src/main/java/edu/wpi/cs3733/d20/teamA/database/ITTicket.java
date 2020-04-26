package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class ITTicket implements ITableable<ITTicket> {

  @Setter private SimpleStringProperty ticketTime;
  @Setter private SimpleStringProperty name;
  @Setter private SimpleStringProperty location;
  @Setter private SimpleStringProperty category;
  @Setter private SimpleStringProperty description;
  @Setter private SimpleStringProperty status;
  @Setter private SimpleStringProperty completedBy;

  public ITTicket(
      Timestamp ticketTime,
      String status,
      String category,
      String location,
      String name,
      String completedBy,
      String description) {
    this.ticketTime = new SimpleStringProperty(ticketTime.toString());
    this.name = new SimpleStringProperty(name);
    this.location = new SimpleStringProperty(location);
    this.category = new SimpleStringProperty(category);
    this.description = new SimpleStringProperty(description);
    this.status = new SimpleStringProperty(status);
    this.completedBy = new SimpleStringProperty(completedBy);
  }

  public String getTicketTime() {
    return ticketTime.get();
  }

  public SimpleStringProperty ticketTimeProperty() {
    return ticketTime;
  }

  public String getName() {
    return name.get();
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  public String getLocation() {
    return location.get();
  }

  public SimpleStringProperty locationProperty() {
    return location;
  }

  public String getCategory() {
    return category.get();
  }

  public SimpleStringProperty categoryProperty() {
    return category;
  }

  public String getDescription() {
    return description.get();
  }

  public SimpleStringProperty descriptionProperty() {
    return description;
  }

  public String getStatus() {
    return status.get();
  }

  public SimpleStringProperty statusProperty() {
    return status;
  }

  public String getCompletedBy() {
    return completedBy.get();
  }

  public SimpleStringProperty completedByProperty() {
    return completedBy;
  }

  @Override
  public ArrayList<JFXTreeTableColumn<ITTicket, ?>> getColumns() {

    JFXTreeTableColumn<ITTicket, String> column1 = new JFXTreeTableColumn<>("Time");
    column1.setCellValueFactory(param -> param.getValue().getValue().ticketTimeProperty());

    JFXTreeTableColumn<ITTicket, String> column2 = new JFXTreeTableColumn<>("Status");
    column2.setCellValueFactory(param -> param.getValue().getValue().statusProperty());

    JFXTreeTableColumn<ITTicket, String> column3 = new JFXTreeTableColumn<>("Category");
    column3.setCellValueFactory(param -> param.getValue().getValue().categoryProperty());

    JFXTreeTableColumn<ITTicket, String> column4 = new JFXTreeTableColumn<>("Location");
    column4.setCellValueFactory(param -> param.getValue().getValue().locationProperty());

    JFXTreeTableColumn<ITTicket, String> column5 = new JFXTreeTableColumn<>("Requester Name");
    column5.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

    JFXTreeTableColumn<ITTicket, String> column6 = new JFXTreeTableColumn<>("Employee Name");
    column6.setCellValueFactory(param -> param.getValue().getValue().completedByProperty());

    return new ArrayList<>(List.of(column1, column2, column3, column4, column5, column6));
  }
}
