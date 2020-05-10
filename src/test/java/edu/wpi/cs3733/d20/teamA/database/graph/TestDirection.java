package edu.wpi.cs3733.d20.teamA.database.graph;

import edu.wpi.cs3733.d20.teamA.graph.Direction;
import org.junit.jupiter.api.Test;

public class TestDirection {

  @Test
  public void testDirections() {
    Direction d = Direction.DOWN;
    d = Direction.LEFT;
    d = Direction.RIGHT;
    d = Direction.SLIGHTLEFT;
  }
}
