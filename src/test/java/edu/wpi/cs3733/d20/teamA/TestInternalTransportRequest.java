package edu.wpi.cs3733.d20.teamA;

import edu.wpi.cs3733.d20.teamA.database.service.internaltransport.InternalTransportRequest;
import java.sql.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestInternalTransportRequest {
  @Test
  public void testGetRequestNumber() {
    InternalTransportRequest itr =
        new InternalTransportRequest(
            "AAAAAA",
            "biscuit",
            "torn",
            new Timestamp(System.currentTimeMillis()),
            "done",
            "harry");
    Assertions.assertEquals("AAAAAA", itr.getRequestNumber());
  }

  @Test
  public void testGetStart() {
    InternalTransportRequest itr =
        new InternalTransportRequest(
            "BBBBBB",
            "biscuit",
            "torn",
            new Timestamp(System.currentTimeMillis()),
            "done",
            "harry");
    Assertions.assertEquals("biscuit", itr.getStart());
  }

  @Test
  public void testGetDestination() {
    InternalTransportRequest itr =
        new InternalTransportRequest(
            "CCCCCC",
            "biscuit",
            "torn",
            new Timestamp(System.currentTimeMillis()),
            "done",
            "harry");
    Assertions.assertEquals("torn", itr.getDestination());
  }

  @Test
  public void testGetProgress() {
    InternalTransportRequest itr =
        new InternalTransportRequest(
            "CCCCCC",
            "biscuit",
            "torn",
            new Timestamp(System.currentTimeMillis()),
            "done",
            "harry");
    Assertions.assertEquals("done", itr.getProgress());
  }
}
