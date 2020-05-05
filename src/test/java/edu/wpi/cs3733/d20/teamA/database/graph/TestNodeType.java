package edu.wpi.cs3733.d20.teamA.database.graph;

import edu.wpi.cs3733.d20.teamA.graph.NodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestNodeType {

  @Test
  public void testNodeType() {
    NodeType elevator = NodeType.toNodeType("HALL");
    Assertions.assertNotNull(elevator);
    Assertions.assertEquals(elevator.name(), "HALL");
  }
}
