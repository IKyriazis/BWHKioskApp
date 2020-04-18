package edu.wpi.cs3733.d20.teamA.database;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;

public class Flower {

  @Getter @Setter private SimpleStringProperty typeFlower;
  @Getter @Setter private SimpleStringProperty color;
  @Getter @Setter private SimpleIntegerProperty qty;
  @Getter @Setter private SimpleDoubleProperty pricePer;

  public Flower(String typeFlower, String color, int qty, double pricePer) {
    this.typeFlower = new SimpleStringProperty(typeFlower);
    this.color = new SimpleStringProperty(color);
    this.qty = new SimpleIntegerProperty(qty);
    this.pricePer = new SimpleDoubleProperty(pricePer);
  }
}
