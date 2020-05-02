package edu.wpi.cs3733.d20.teamA.database.announcement;

import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAnnouncement {

  @Test
  public void createAnnouncement() {
    Announcement announcement = new Announcement("AAAAAA", "New Announcement");
    Assertions.assertNotNull(announcement);
  }

  @Test
  public void testGetters() {
    Announcement announcement = new Announcement("AAAAAA", "New Announcement");
    Assertions.assertEquals("AAAAAA", announcement.getAnnouncementID());
    Assertions.assertEquals("New Announcement", announcement.getAnnouncement());
    Assertions.assertEquals(
        new SimpleStringProperty("AAAAAA"), announcement.getAnnouncementIDProperty());
    Assertions.assertEquals(
        new SimpleStringProperty("New Announcement"), announcement.getAnnouncementProperty());
  }
}
