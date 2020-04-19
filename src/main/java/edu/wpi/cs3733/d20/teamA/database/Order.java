package edu.wpi.cs3733.d20.teamA.database;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Order {

  private final SimpleIntegerProperty orderNumber;
  private final SimpleIntegerProperty numFlowers;
  private final SimpleStringProperty flowerColor;
  private final SimpleDoubleProperty price;
  private final SimpleStringProperty status;
  private final SimpleStringProperty location;
  private final SimpleStringProperty flowerType;

  public Order(
      int orderNumber,
      int numFlowers,
      String flowerType,
      String flowerColor,
      double price,
      String status,
      String location) {
    this.orderNumber = new SimpleIntegerProperty(orderNumber);
    this.numFlowers = new SimpleIntegerProperty(numFlowers);
    this.flowerType = new SimpleStringProperty(flowerType);
    this.flowerColor = new SimpleStringProperty(flowerColor);
    this.price = new SimpleDoubleProperty(price);
    this.status = new SimpleStringProperty(status);
    this.location = new SimpleStringProperty(location);
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

  public String getFlowerColor() {
    return flowerColor.get();
  }

  public SimpleStringProperty flowerColorProperty() {
    return flowerColor;
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

  public String getFlowerType() {
    return flowerType.get();
  }

  public SimpleStringProperty flowerTypeProperty() {
    return flowerType;
  }
}
