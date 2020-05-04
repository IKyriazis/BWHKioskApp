package edu.wpi.cs3733.d20.teamA.flower;

import edu.wpi.cs3733.d20.teamA.database.flower.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFlowerOrder {

  @Test
  public void testOrder() {
    Order o = new Order(4, 5, "1/15", 45, "Status", "location", "Hi mom", -1);
    Assertions.assertEquals(
        o.getOrderNumber(), 4); // Order is a very simple class, comprehensive tests not required
    Assertions.assertEquals("1/15", o.getFlowerString());
    Assertions.assertEquals("1/15", o.flowerStringProperty().get());
    Assertions.assertEquals("location", o.getLocation());
    Assertions.assertEquals("Hi mom", o.getMessage());
    Assertions.assertFalse(o.employeeAssigned());
  }
}
