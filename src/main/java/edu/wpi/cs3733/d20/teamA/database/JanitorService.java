package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class JanitorService extends RecursiveTreeObject<JanitorService>
    implements ITableable<JanitorService> {
  @Setter private SimpleStringProperty location;
  @Setter private SimpleStringProperty typeOfJanitorService;
  @Setter private SimpleStringProperty status;

  public JanitorService(String location, String typeOfJanitorService, String status) {
    this.location = new SimpleStringProperty(location);
    this.typeOfJanitorService = new SimpleStringProperty(typeOfJanitorService);
    this.status = new SimpleStringProperty(status);
  }

  public SimpleStringProperty location() {
    return location;
  }

  public SimpleStringProperty typeOfJanitorService() {
    return typeOfJanitorService;
  }

  public SimpleStringProperty status() {
    return status;
  }

  public String getServiceType() {
    return typeOfJanitorService.get();
  }

  public String getLocation() {
    return location.get();
  }

  public String getQty() {
    return status.get();
  }

  @Override
  public ArrayList<JFXTreeTableColumn<JanitorService, ?>> getColumns() {
    JFXTreeTableColumn<JanitorService, String> column1 = new JFXTreeTableColumn<>("Location");
    column1.setCellValueFactory(param -> param.getValue().getValue().location);

    JFXTreeTableColumn<JanitorService, String> column2 =
        new JFXTreeTableColumn<>("Type of Janitor Service");
    column2.setCellValueFactory(param -> param.getValue().getValue().typeOfJanitorService);

    JFXTreeTableColumn<JanitorService, String> column3 = new JFXTreeTableColumn<>("Priority");
    column3.setCellValueFactory(param -> param.getValue().getValue().status);

    return new ArrayList<>(List.of(column1, column2, column3));
  }
}
