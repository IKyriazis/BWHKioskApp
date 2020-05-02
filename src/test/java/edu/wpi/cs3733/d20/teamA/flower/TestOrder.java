package edu.wpi.cs3733.d20.teamA.flower;

import edu.wpi.cs3733.d20.teamA.database.flower.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestOrder {

  @Test
  public void testOrder() {
    Order o = new Order(4, 5, "1/15|", 45, "Status", "location", "Hi mom", -1);
    Assertions.assertEquals(
        o.getOrderNumber(), 4); // Order is a very simple class, comprehensive tests not required
  }
}
