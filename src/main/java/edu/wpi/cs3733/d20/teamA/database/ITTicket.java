package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class ITTicket implements ITableable<ITTicket> {

  @Setter private SimpleIntegerProperty ticketNum;
  @Setter private SimpleStringProperty ticketTime;
  @Setter private SimpleStringProperty name;
  @Setter private SimpleStringProperty location;
  @Setter private SimpleStringProperty category;
  @Setter private SimpleStringProperty description;
  @Setter private SimpleStringProperty status;
  @Setter private SimpleStringProperty completedBy;

  public ITTicket(
      int ticketNum,
      String name,
      Timestamp ticketTime,
      String location,
      String category,
      String description,
      String status,
      String completedBy) {
    this.ticketNum = new SimpleIntegerProperty(ticketNum);
    this.ticketTime = new SimpleStringProperty(ticketTime.toString());
    this.name = new SimpleStringProperty(name);
    this.location = new SimpleStringProperty(location);
    this.category = new SimpleStringProperty(category);
    this.description = new SimpleStringProperty(description);
    this.status = new SimpleStringProperty(status);
    this.completedBy = new SimpleStringProperty(completedBy);
  }

  public String getticketTime() {
    return ticketTime.get();
  }

  public SimpleStringProperty ticketTimeProperty() {
    return ticketTime;
  }

  public int getTicketNum() {
    return ticketNum.get();
  }

  public SimpleIntegerProperty ticketNumProperty() {
    return ticketNum;
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
    JFXTreeTableColumn<ITTicket, Integer> column1 = new JFXTreeTableColumn<>("Ticket #");
    column1.setCellValueFactory(
        param -> param.getValue().getValue().ticketNumProperty().asObject());

    JFXTreeTableColumn<ITTicket, String> column2 = new JFXTreeTableColumn<>("Time");
    column2.setCellValueFactory(param -> param.getValue().getValue().ticketTimeProperty());

    JFXTreeTableColumn<ITTicket, String> column3 = new JFXTreeTableColumn<>("Status");
    column3.setCellValueFactory(param -> param.getValue().getValue().statusProperty());

    JFXTreeTableColumn<ITTicket, String> column4 = new JFXTreeTableColumn<>("Category");
    column4.setCellValueFactory(param -> param.getValue().getValue().categoryProperty());

    JFXTreeTableColumn<ITTicket, String> column5 = new JFXTreeTableColumn<>("Location");
    column5.setCellValueFactory(param -> param.getValue().getValue().locationProperty());

    JFXTreeTableColumn<ITTicket, String> column6 = new JFXTreeTableColumn<>("Requester Name");
    column6.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

    JFXTreeTableColumn<ITTicket, String> column7 = new JFXTreeTableColumn<>("Employee Name");
    column7.setCellValueFactory(param -> param.getValue().getValue().completedByProperty());

    return new ArrayList<>(List.of(column1, column2, column3, column4, column5, column6, column7));
  }
}
