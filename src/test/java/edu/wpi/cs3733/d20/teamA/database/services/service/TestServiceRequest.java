package edu.wpi.cs3733.d20.teamA.database.services.service;

import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestServiceRequest {

  @Test
  public void createServiceRequest() {
    ServiceRequest serv =
        new ServiceRequest(
            ServiceType.INTERNAL_TRANSPORT,
            "TURTLE",
            "Yash",
            "Maddie",
            "Time",
            "Low",
            "location",
            "New description",
            "additional");
    Assertions.assertNotNull(serv);
  }

  @Test
  public void testGetters() {
    ServiceRequest serv =
        new ServiceRequest(
            ServiceType.INTERNAL_TRANSPORT,
            "TURTLE",
            "Yash",
            "Maddie",
            "Time",
            "Low",
            "location",
            "New description",
            "additional");
    Assertions.assertEquals("TURTLE", serv.getReqID());
    Assertions.assertEquals("Yash", serv.getDidReqName());
    Assertions.assertEquals("Maddie", serv.getMadeReqName());
    Assertions.assertEquals("Time", serv.getTimestamp());
    Assertions.assertEquals("Low", serv.getStatus());
    Assertions.assertEquals("location", serv.getLocation());
    Assertions.assertEquals("New description", serv.getDescription());
    Assertions.assertEquals("additional", serv.getAdditional());
  }
}
