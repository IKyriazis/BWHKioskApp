package edu.wpi.cs3733.d20.teamA.flower;

import edu.wpi.cs3733.d20.teamA.database.flower.Flower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFlower {
  @Test
  public void testFlower() {
    Flower f = new Flower("Name", "Color", 4, 2.35, 1);
    Assertions.assertEquals(f.getQty(), 4);
    Assertions.assertEquals(f.getPricePer(), 2.35);
  }
}
