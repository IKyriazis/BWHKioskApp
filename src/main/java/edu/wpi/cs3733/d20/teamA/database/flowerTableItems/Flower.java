package edu.wpi.cs3733.d20.teamA.database.flowerTableItems;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Flower extends RecursiveTreeObject<Flower> implements ITableable<Flower> {
  @Setter protected SimpleStringProperty typeFlower;
  @Setter protected SimpleStringProperty color;
  @Setter protected SimpleIntegerProperty qty;
  @Setter protected SimpleDoubleProperty pricePer;
  @Setter protected SimpleIntegerProperty flowerID;
  private SimpleIntegerProperty quantitySelected;

  public Flower(String typeFlower, String color, int qty, double pricePer, int flowerID) {
    this.typeFlower = new SimpleStringProperty(typeFlower);
    this.color = new SimpleStringProperty(color);
    this.qty = new SimpleIntegerProperty(qty);
    this.pricePer = new SimpleDoubleProperty(pricePer);
    this.flowerID = new SimpleIntegerProperty(flowerID);
    quantitySelected = new SimpleIntegerProperty(0);
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

  public SimpleStringProperty numProperty() {
    return new SimpleStringProperty("" + quantitySelected.get());
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

  public int getFlowerID() {
    return flowerID.get();
  }

  public SimpleIntegerProperty flowerIDProperty() {
    return flowerID;
  }

  public SimpleIntegerProperty getQuantitySelectedProp() {
    return quantitySelected;
  }

  public int getQuantitySelected() {
    return quantitySelected.get();
  }

  public void setQuantitySelected(int i) {
    quantitySelected = new SimpleIntegerProperty(i);
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
