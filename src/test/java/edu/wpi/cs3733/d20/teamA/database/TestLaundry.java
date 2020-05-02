package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Timestamp;

import edu.wpi.cs3733.d20.teamA.database.service.laundry.Laundry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestLaundry {

  public TestLaundry() {}

  @Test
  public void testGetters() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Laundry laundry = new Laundry(1, "admin", "Admitting", "Requested", "staff", timestamp);
    Assertions.assertEquals(1, laundry.getRequestNum());
    Assertions.assertEquals("admin", laundry.getEmployeeEntered());
    Assertions.assertEquals("Admitting", laundry.getLocation());
    Assertions.assertEquals("Requested", laundry.getProgress());
    Assertions.assertEquals("staff", laundry.getEmployeeWash());
    Assertions.assertNotNull(laundry.getTimeRequested());
  }
}
