package edu.wpi.cs3733.d20.teamA.database.services.itticket;

import edu.wpi.cs3733.d20.teamA.database.service.itticket.ITTicket;
import java.sql.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestITTicket {
  @Test
  public void testITTicket() {
    ITTicket t =
        new ITTicket(
            "SPIDER",
            new Timestamp(System.currentTimeMillis()),
            "In Progress",
            "WIFI",
            "ADEPT00101",
            "Harold",
            "James",
            "WIFI Broke");
    Assertions.assertEquals(t.getCategory(), "WIFI");
    Assertions.assertEquals(t.getName(), "Harold");
    Assertions.assertEquals(t.getLocation(), "ADEPT00101");
    Assertions.assertEquals(t.getStatus(), "In Progress");
    Assertions.assertEquals(t.getCompletedBy(), "James");
    Assertions.assertEquals(t.getDescription(), "WIFI Broke");
  }
}
