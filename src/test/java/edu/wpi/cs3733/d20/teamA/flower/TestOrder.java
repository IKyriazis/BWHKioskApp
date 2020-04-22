package edu.wpi.cs3733.d20.teamA.flower;

import edu.wpi.cs3733.d20.teamA.database.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestOrder {

  @Test
  public void testOrder() {
    Order o = new Order(4, 5, "type", "color", 3.45, "Status", "location");
    Assertions.assertEquals(
        o.getOrderNumber(), 4); // Order is a very simple class, comprehensive tests not required
  }
}
