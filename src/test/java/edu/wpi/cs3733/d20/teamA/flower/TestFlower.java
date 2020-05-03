package edu.wpi.cs3733.d20.teamA.flower;

import edu.wpi.cs3733.d20.teamA.database.flower.Flower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFlower {
  @Test
  public void testFlower() {
    Flower f = new Flower(12, "Name", "Blue", 4, 3.12);
    Assertions.assertEquals(f.getQty(), 4);
    Assertions.assertEquals(f.getPricePer(), 3.12);
    Assertions.assertEquals(f.getColor(), "Blue");
    Assertions.assertEquals("Name", f.getTypeFlower());
    Assertions.assertEquals(0, f.getQuantitySelected());
  }
}
