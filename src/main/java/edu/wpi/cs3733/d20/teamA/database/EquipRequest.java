package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EquipRequest implements ITableable<EquipRequest> {

  private final SimpleStringProperty name;
  private final SimpleStringProperty item;
  private final SimpleIntegerProperty qty;
  private final SimpleStringProperty location;
  private final SimpleStringProperty priority;
  private final SimpleStringProperty time;
  private final SimpleStringProperty username;

  public EquipRequest(
      String name,
      String item,
      int qty,
      String location,
      String priority,
      Timestamp time,
      String username) {
    this.username = new SimpleStringProperty(username);
    this.name = new SimpleStringProperty(name);
    this.item = new SimpleStringProperty(item);
    this.qty = new SimpleIntegerProperty(qty);
    this.location = new SimpleStringProperty(location);
    this.priority = new SimpleStringProperty(priority);
    this.time = new SimpleStringProperty(time.toString());
  }

  public String getUsername() {
    return username.get();
  }

  public SimpleStringProperty usernameProperty() {
    return username;
  }

  public String getName() {
    return name.get();
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  public String getItem() {
    return item.get();
  }

  public SimpleStringProperty itemProperty() {
    return item;
  }

  public int getQty() {
    return qty.get();
  }

  public SimpleIntegerProperty qtyProperty() {
    return qty;
  }

  public String getLocation() {
    return location.get();
  }

  public SimpleStringProperty locationProperty() {
    return location;
  }

  public String getPriority() {
    return priority.get();
  }

  public SimpleStringProperty priorityProperty() {
    return priority;
  }

  public String getTime() {
    return time.get();
  }

  public SimpleStringProperty timeProperty() {
    return time;
  }

  @Override
  public ArrayList<JFXTreeTableColumn<EquipRequest, ?>> getColumns() {
    JFXTreeTableColumn<EquipRequest, String> col1 = new JFXTreeTableColumn<>("Name");
    col1.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

    JFXTreeTableColumn<EquipRequest, String> col2 = new JFXTreeTableColumn<>("Item");
    col2.setCellValueFactory(param -> param.getValue().getValue().itemProperty());

    JFXTreeTableColumn<EquipRequest, Integer> col3 = new JFXTreeTableColumn<>("Qty");
    col3.setCellValueFactory(param -> param.getValue().getValue().qtyProperty().asObject());

    JFXTreeTableColumn<EquipRequest, String> col4 = new JFXTreeTableColumn<>("Where");
    col4.setCellValueFactory(param -> param.getValue().getValue().locationProperty());

    JFXTreeTableColumn<EquipRequest, String> col5 = new JFXTreeTableColumn<>("Priority");
    col5.setCellValueFactory(param -> param.getValue().getValue().priorityProperty());

    JFXTreeTableColumn<EquipRequest, String> col6 = new JFXTreeTableColumn<>("Time");
    col6.setCellValueFactory(param -> param.getValue().getValue().timeProperty());

    return new ArrayList<>(List.of(col1, col2, col3, col4, col5, col6));
  }
}
