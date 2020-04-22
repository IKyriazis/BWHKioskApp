package edu.wpi.cs3733.d20.teamA.database;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Flower {

  @Setter private SimpleStringProperty typeFlower;
  @Setter private SimpleStringProperty color;
  @Setter private SimpleIntegerProperty qty;
  @Setter private SimpleDoubleProperty pricePer;

  public Flower(String typeFlower, String color, int qty, double pricePer) {
    this.typeFlower = new SimpleStringProperty(typeFlower);
    this.color = new SimpleStringProperty(color);
    this.qty = new SimpleIntegerProperty(qty);
    this.pricePer = new SimpleDoubleProperty(pricePer);
  }

  public SimpleStringProperty typeFlowerProperty() {
    return typeFlower;
  }

  public SimpleStringProperty colorProperty() {
    return color;
  }

  public SimpleIntegerProperty qtyProperty() {
    return qty;
  }

  public SimpleDoubleProperty pricePerProperty() {
    return pricePer;
  }

  public String getTypeFlower() {
    return typeFlower.get();
  }

  public String getColor() {
    return color.get();
  }

  public int getQty() {
    return qty.get();
  }

  public double getPricePer() {
    return pricePer.get();
  }
}
