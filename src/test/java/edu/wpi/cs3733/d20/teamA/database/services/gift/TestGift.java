package edu.wpi.cs3733.d20.teamA.database.services.gift;

import edu.wpi.cs3733.d20.teamA.database.service.gift.Gift;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestGift {

  @Test
  public void testGift() {
    Gift g = new Gift("Toy");
    Assertions.assertNotNull(g);
  }
}
