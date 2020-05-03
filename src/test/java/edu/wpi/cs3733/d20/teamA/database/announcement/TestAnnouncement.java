package edu.wpi.cs3733.d20.teamA.database.announcement;

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
    Assertions.assertEquals("AAAAAA", announcement.getAnnouncementIDProperty().get());
    Assertions.assertEquals("New Announcement", announcement.getAnnouncementProperty().get());
  }
}
