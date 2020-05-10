package edu.wpi.cs3733.d20.teamA.database.service.gift;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class Gift implements ITableable<Gift> {

  private SimpleStringProperty items;

  public Gift(String additional) {

    this.items = new SimpleStringProperty(additional);
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Gift, ?>> getColumns() {
    JFXTreeTableColumn<Gift, String> col1 = new JFXTreeTableColumn<>("Name");
    col1.setCellValueFactory(param -> param.getValue().getValue().items);

    return new ArrayList<>(List.of(col1));
  }
}
