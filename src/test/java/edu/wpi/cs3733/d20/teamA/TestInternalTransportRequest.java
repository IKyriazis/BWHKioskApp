package edu.wpi.cs3733.d20.teamA;

import edu.wpi.cs3733.d20.teamA.database.InternalTransportRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestInternalTransportRequest {
  @Test
  public void testGetRequestNumber() {
    InternalTransportRequest itr =
        new InternalTransportRequest(1, "biscuit", "torn", "time", "done", "harry");
    Assertions.assertEquals(1, itr.getRequestNumber());
  }

  @Test
  public void testGetStart() {
    InternalTransportRequest itr =
        new InternalTransportRequest(1, "biscuit", "torn", "time", "done", "harry");
    Assertions.assertEquals("biscuit", itr.getStart());
  }

  @Test
  public void testGetDestination() {
    InternalTransportRequest itr =
        new InternalTransportRequest(1, "biscuit", "torn", "time", "done", "harry");
    Assertions.assertEquals("torn", itr.getDestination());
  }

  @Test
  public void testGetProgressn() {
    InternalTransportRequest itr =
        new InternalTransportRequest(1, "biscuit", "torn", "time", "done", "harry");
    Assertions.assertEquals("done", itr.getProgress());
  }
}
