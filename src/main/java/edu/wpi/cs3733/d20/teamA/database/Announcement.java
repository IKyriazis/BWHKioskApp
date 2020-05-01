package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

public class Announcement implements ITableable<Announcement> {

  @Setter private SimpleStringProperty announcementID;
  @Setter private SimpleStringProperty announcement;

  public Announcement(String announcementID, String announcement) {
    this.announcementID = new SimpleStringProperty(announcementID);
    this.announcement = new SimpleStringProperty(announcement);
  }

  // Getters
  public SimpleStringProperty getAnnouncementIDProperty() {
    return this.announcementID;
  }

  public SimpleStringProperty getAnnouncementProperty() {
    return this.announcement;
  }

  public String getAnnouncementID() {
    return announcementID.get();
  };

  public String getAnnouncement() {
    return announcement.get();
  };

  @Override
  public ArrayList<JFXTreeTableColumn<Announcement, ?>> getColumns() {

    JFXTreeTableColumn<Announcement, String> column2 = new JFXTreeTableColumn<>("Announcement");
    column2.setCellValueFactory(param -> param.getValue().getValue().getAnnouncementProperty());

    return new ArrayList<>(List.of(column2));
  }
}
