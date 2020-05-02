package edu.wpi.cs3733.d20.teamA.database.flower;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class FlowerEmployee extends RecursiveTreeObject<Flower>
    implements ITableable<FlowerEmployee> {
  private SimpleStringProperty fName;
  private SimpleStringProperty lName;

  public FlowerEmployee(String fName, String lName) {
    this.fName = new SimpleStringProperty(fName);
    this.lName = new SimpleStringProperty(lName);
  }

  public SimpleStringProperty getFirstNameProperty() {
    return fName;
  }

  public SimpleStringProperty getLastNameProperty() {
    return lName;
  }

  public String getFirstName() {
    return fName.get();
  }

  public String getLastName() {
    return lName.get();
  }

  public String toString() {
    return fName.get() + " " + lName.get();
  }

  @Override
  public ArrayList<JFXTreeTableColumn<FlowerEmployee, ?>> getColumns() {
    JFXTreeTableColumn<FlowerEmployee, String> column1 = new JFXTreeTableColumn<>("First Name");
    column1.setCellValueFactory(param -> param.getValue().getValue().fName);

    JFXTreeTableColumn<FlowerEmployee, String> column2 = new JFXTreeTableColumn<>("Last Name");
    column2.setCellValueFactory(param -> param.getValue().getValue().lName);

    return new ArrayList<>(List.of(column1, column2));
  }
}
