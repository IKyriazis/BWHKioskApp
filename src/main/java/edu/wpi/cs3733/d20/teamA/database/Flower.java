package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Flower implements ITableable<Flower> {
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

  @Override
  public ArrayList<JFXTreeTableColumn<Flower, ?>> getColumns() {
    JFXTreeTableColumn<Flower, String> column1 = new JFXTreeTableColumn<>("Type");
    column1.setCellValueFactory(param -> param.getValue().getValue().typeFlowerProperty());

    JFXTreeTableColumn<Flower, String> column2 = new JFXTreeTableColumn<>("Color");
    column2.setCellValueFactory(param -> param.getValue().getValue().colorProperty());

    JFXTreeTableColumn<Flower, Integer> column3 = new JFXTreeTableColumn<>("Quantity");
    column3.setCellValueFactory(param -> param.getValue().getValue().qtyProperty().asObject());

    JFXTreeTableColumn<Flower, Double> column4 = new JFXTreeTableColumn<>("Unit Price");
    column4.setCellValueFactory(param -> param.getValue().getValue().pricePerProperty().asObject());

    return new ArrayList<>(List.of(column1, column2, column3, column4));
  }
}
