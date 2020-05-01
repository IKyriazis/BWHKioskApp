package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class InternalTransportRequest implements ITableable<InternalTransportRequest> {

  private final SimpleStringProperty requestNumber;
  private final SimpleStringProperty start;
  private final SimpleStringProperty destination;
  private final SimpleStringProperty time;
  private final SimpleStringProperty progress;
  private final SimpleStringProperty name;

  public InternalTransportRequest(
      String requestNumber,
      String start,
      String destination,
      Timestamp time,
      String progress,
      String name) {
    this.requestNumber = new SimpleStringProperty(requestNumber);
    this.start = new SimpleStringProperty(start);
    this.destination = new SimpleStringProperty(destination);
    this.time = new SimpleStringProperty(time.toString());
    this.progress = new SimpleStringProperty(progress);
    this.name = new SimpleStringProperty(name);
  }

  public String getRequestNumber() {
    return requestNumber.get();
  }

  public SimpleStringProperty requestNumberProperty() {
    return requestNumber;
  }

  public String getStart() {
    return start.get();
  }

  public SimpleStringProperty startProperty() {
    return start;
  }

  public String getDestination() {
    return destination.get();
  }

  public SimpleStringProperty destinationProperty() {
    return destination;
  }

  public String getTime() {
    return time.get();
  }

  public SimpleStringProperty timeProperty() {
    return time;
  }

  public String getProgress() {
    return progress.get();
  }

  public SimpleStringProperty progressProperty() {
    return progress;
  }

  public String getName() {
    return name.get();
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  @Override
  public ArrayList<JFXTreeTableColumn<InternalTransportRequest, ?>> getColumns() {
    JFXTreeTableColumn<InternalTransportRequest, String> col1 =
        new JFXTreeTableColumn<>("Request #");
    col1.setCellValueFactory(param -> param.getValue().getValue().requestNumberProperty());

    JFXTreeTableColumn<InternalTransportRequest, String> col2 = new JFXTreeTableColumn<>("Start");
    col2.setCellValueFactory(param -> param.getValue().getValue().startProperty());

    JFXTreeTableColumn<InternalTransportRequest, String> col3 =
        new JFXTreeTableColumn<>("Destination");
    col3.setCellValueFactory(param -> param.getValue().getValue().destinationProperty());

    JFXTreeTableColumn<InternalTransportRequest, String> col4 = new JFXTreeTableColumn<>("Time");
    col4.setCellValueFactory(param -> param.getValue().getValue().timeProperty());

    JFXTreeTableColumn<InternalTransportRequest, String> col5 =
        new JFXTreeTableColumn<>("Progress");
    col5.setCellValueFactory(param -> param.getValue().getValue().progressProperty());

    JFXTreeTableColumn<InternalTransportRequest, String> col6 = new JFXTreeTableColumn<>("Name");
    col6.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

    return new ArrayList<>(List.of(col1, col2, col3, col4, col5, col6));
  }
}
