package edu.wpi.cs3733.d20.teamA.database.services.service;

import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestServiceType {

  @Test
  public void testType() {
    ServiceType type = ServiceType.getServiceType("laundry");
    Assertions.assertEquals("laundry", type.toString());
    ServiceType type1 = ServiceType.getServiceType("interpret");
    Assertions.assertEquals("interpret", type1.toString());
    ServiceType type2 = ServiceType.getServiceType("medicine");
    Assertions.assertEquals("medicine", type2.toString());
    ServiceType type3 = ServiceType.getServiceType("equipreq");
    Assertions.assertEquals("equipreq", type3.toString());
    ServiceType type4 = ServiceType.getServiceType("intrntrans");
    Assertions.assertEquals("intrntrans", type4.toString());
  }
}
