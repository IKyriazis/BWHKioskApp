package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestITicket {
  @Test
  public void testITTicket() {
    ITTicket t =
        new ITTicket(
            new Timestamp(System.currentTimeMillis()),
            "In Progress",
            "Wifi",
            "ADEPT00101",
            "Jim",
            "James",
            "What is the wifi password?");
    Assertions.assertEquals(t.getCategory(), "Wifi");
    Assertions.assertEquals(t.getName(), "Jim");
    Assertions.assertEquals(t.getLocation(), "ADEPT00101");
    Assertions.assertEquals(t.getStatus(), "In Progress");
    Assertions.assertEquals(t.getCompletedBy(), "James");
    Assertions.assertEquals(t.getDescription(), "What is the wifi password?");
  }
}
