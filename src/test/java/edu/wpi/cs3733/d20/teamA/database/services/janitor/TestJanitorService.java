package edu.wpi.cs3733.d20.teamA.database.services.janitor;

import edu.wpi.cs3733.d20.teamA.database.service.janitor.JanitorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestJanitorService {

  @Test
  public void testCreateJanitorService() {
    JanitorService janitorService =
        new JanitorService("IKEA", "HIGH", "In Progress", "Yash Patel", "SPIDER", "Clean up");
    Assertions.assertNotNull(janitorService);
  }

  @Test
  public void testGetters() {
    JanitorService janitorService =
        new JanitorService("IKEA", "HIGH", "In Progress", "Yash Patel", "SPIDER", "Clean up");
    Assertions.assertEquals("IKEA", janitorService.getLocation());
    Assertions.assertEquals("In Progress", janitorService.getStatus());
    Assertions.assertEquals("SPIDER", janitorService.getIndex());
    Assertions.assertEquals("Clean up", janitorService.getLongName());
    Assertions.assertEquals("Yash Patel", janitorService.getEmployeeName());
    Assertions.assertEquals("HIGH", janitorService.getPriority());
  }
}
