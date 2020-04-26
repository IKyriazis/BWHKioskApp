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
  // @Setter private SimpleStringProperty name;

  public JanitorService(String location, String typeOfJanitorService, String status) {
    this.location = new SimpleStringProperty(location);
    this.typeOfJanitorService = new SimpleStringProperty(typeOfJanitorService);
    this.status = new SimpleStringProperty(status);
    // this.time = new SimpleStringProperty(time);
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

  //  public SimpleStringProperty time() {
  //    return time;
  //  }

  public String getTypeOfJanitorService() {
    return typeOfJanitorService.get();
  }

  public String getLocation() {
    return location.get();
  }

  public String getStatus() {
    return status.get();
  }

  //  public String getTime() {
  //    return status.get();
  //  }

  @Override
  public ArrayList<JFXTreeTableColumn<JanitorService, ?>> getColumns() {
    JFXTreeTableColumn<JanitorService, String> column1 = new JFXTreeTableColumn<>("Location");
    column1.setCellValueFactory(param -> param.getValue().getValue().location);

    JFXTreeTableColumn<JanitorService, String> column2 = new JFXTreeTableColumn<>("Priority");
    column2.setCellValueFactory(param -> param.getValue().getValue().typeOfJanitorService);

    JFXTreeTableColumn<JanitorService, String> column3 = new JFXTreeTableColumn<>("Status");
    column3.setCellValueFactory(param -> param.getValue().getValue().status);

    JFXTreeTableColumn<JanitorService, String> column4 = new JFXTreeTableColumn<>("Status");
    column3.setCellValueFactory(param -> param.getValue().getValue().status);

    return new ArrayList<>(List.of(column1, column2, column3));
  }
}
