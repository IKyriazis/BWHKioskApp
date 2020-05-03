package edu.wpi.cs3733.d20.teamA.database.flower;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// enum status {Status};

public class Order implements ITableable<Order> {

  private final SimpleIntegerProperty orderNumber;
  private final SimpleIntegerProperty numFlowers;
  private final SimpleStringProperty flowerString;
  private final SimpleDoubleProperty price;
  private final SimpleStringProperty status;
  private final SimpleStringProperty location;
  private final SimpleStringProperty message;

  private int id;

  public Order(
      int orderNumber,
      int numFlowers,
      String flowerString,
      double price,
      String status,
      String location,
      String message,
      int id) {
    this.orderNumber = new SimpleIntegerProperty(orderNumber);
    this.numFlowers = new SimpleIntegerProperty(numFlowers);
    this.flowerString = new SimpleStringProperty(flowerString);
    this.price = new SimpleDoubleProperty(price);
    this.status = new SimpleStringProperty(status);
    this.location = new SimpleStringProperty(location);
    this.message = new SimpleStringProperty(message);
    this.id = id;
  }

  public int getOrderNumber() {
    return orderNumber.get();
  }

  public SimpleIntegerProperty orderNumberProperty() {
    return orderNumber;
  }

  public int getNumFlowers() {
    return numFlowers.get();
  }

  public SimpleIntegerProperty numFlowersProperty() {
    return numFlowers;
  }

  public String getFlowerString() {
    return flowerString.get();
  }

  public SimpleStringProperty flowerStringProperty() {
    return flowerString;
  }

  public double getPrice() {
    return price.get();
  }

  public SimpleDoubleProperty priceProperty() {
    return price;
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

  public String getMessage() {
    return message.get();
  }

  public int getId() {
    return id;
  }

  public boolean employeeAssigned() {
    return id != -1;
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Order, ?>> getColumns() {
    JFXTreeTableColumn<Order, Integer> col1 = new JFXTreeTableColumn<>("Order #");
    col1.setCellValueFactory(param -> param.getValue().getValue().orderNumberProperty().asObject());

    JFXTreeTableColumn<Order, Integer> col2 = new JFXTreeTableColumn<>("Quantity");
    col2.setCellValueFactory(param -> param.getValue().getValue().numFlowersProperty().asObject());

    JFXTreeTableColumn<Order, Double> col5 = new JFXTreeTableColumn<>("Price");
    col5.setCellValueFactory(param -> param.getValue().getValue().priceProperty().asObject());

    JFXTreeTableColumn<Order, String> col6 = new JFXTreeTableColumn<>("Status");
    col6.setCellValueFactory(param -> param.getValue().getValue().statusProperty());

    JFXTreeTableColumn<Order, String> col7 = new JFXTreeTableColumn<>("Location");
    col7.setCellValueFactory(param -> param.getValue().getValue().locationProperty());

    return new ArrayList(List.of(col1, col2, col5, col6, col7));
  }
}
