package edu.wpi.cs3733.d20.teamA.database.graph;

import java.sql.*;

import edu.wpi.cs3733.d20.teamA.graph.Campus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGraphDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase graphDatabase;

  public TestGraphDatabase() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      graphDatabase = new GraphDatabase(conn);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  public void teardown() {
    try {
      graphDatabase.removeAll(Campus.FAULKNER);
      graphDatabase.dropTables();
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  @Test
  public void testTables() {
    graphDatabase.dropTables();
    boolean dropTables = graphDatabase.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = graphDatabase.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = graphDatabase.dropTables();
    Assertions.assertTrue(dropTables2);
    graphDatabase.createTables();
  }

  @Test
  public void testGetSizeNode() {
    graphDatabase.createTables();
    graphDatabase.removeAllNodes(Campus.FAULKNER);
    Assertions.assertEquals(0, graphDatabase.getSizeNode(Campus.FAULKNER));
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    Assertions.assertEquals(1, graphDatabase.getSizeNode(Campus.FAULKNER));
    graphDatabase.removeAllNodes(Campus.FAULKNER);
  }

  @Test
  public void testGetSizeEdge() {
    graphDatabase.createTables();
    graphDatabase.removeAll(Campus.FAULKNER);
    Assertions.assertEquals(0, graphDatabase.getSizeEdge(Campus.FAULKNER));
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A", Campus.FAULKNER);
    graphDatabase.addEdge("biscuit_nugget", "biscuit", "nugget", Campus.FAULKNER);
    Assertions.assertEquals(1, graphDatabase.getSizeEdge(Campus.FAULKNER));
    graphDatabase.removeAll(Campus.FAULKNER);
  }

  @Test
  public void testAddNode() {
    graphDatabase.createTables();
    graphDatabase.removeAllNodes(Campus.FAULKNER);
    boolean a =
        graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    Assertions.assertTrue(a);
    boolean b =
        graphDatabase.addNode("nugget", 0, 0, 2, "White House", "CONF", "balogna2", "b", "Team A", Campus.FAULKNER);
    Assertions.assertTrue(b);
    boolean c =
        graphDatabase.addNode(
            "mashedP", 2, -1, 2, "White House", "CONF", "balogna3", "b", "Team A", Campus.FAULKNER);
    Assertions.assertFalse(c);
    boolean d =
        graphDatabase.addNode(
            "BBQSauce", 2, 5, 0, "White House", "CONF", "balogna4", "b", "Team A", Campus.FAULKNER);
    Assertions.assertFalse(d);
    boolean e =
        graphDatabase.addNode("banana", 2, 5, 11, "White House", "CONF", "balogna5", "b", "Team A", Campus.FAULKNER);
    Assertions.assertFalse(e);
    boolean f =
        graphDatabase.addNode(
            "banana", 2, 5, 1, "White House", "abacabadabacaba", "balogna6", "b", "Team A", Campus.FAULKNER);
    Assertions.assertFalse(f);
    graphDatabase.removeAllNodes(Campus.FAULKNER);
  }

  @Test
  public void testAddEdge() {
    graphDatabase.createTables();
    graphDatabase.removeAll(Campus.FAULKNER);
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A", Campus.FAULKNER);
    boolean a = graphDatabase.addEdge("biscuit_nugget", "biscuit", "nugget", Campus.FAULKNER);
    Assertions.assertTrue(a);
    boolean b = graphDatabase.addEdge("biscuit_mashedP", "biscuit", "mashedP", Campus.FAULKNER);
    Assertions.assertFalse(b);
    graphDatabase.removeAll(Campus.FAULKNER);
  }

  @Test
  public void testDeleteEdge() {
    graphDatabase.createTables();
    graphDatabase.removeAll(Campus.FAULKNER);
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A", Campus.FAULKNER);
    graphDatabase.addEdge("biscuit_nugget", "biscuit", "nugget", Campus.FAULKNER);
    Assertions.assertEquals(1, graphDatabase.getSizeEdge(Campus.FAULKNER));
    boolean a = graphDatabase.deleteEdge("biscuit_nugget", Campus.FAULKNER);
    Assertions.assertTrue(a);
    Assertions.assertEquals(0, graphDatabase.getSizeEdge(Campus.FAULKNER));
    graphDatabase.removeAll(Campus.FAULKNER);
  }

  @Test
  public void testDeleteNode() {
    graphDatabase.createTables();
    graphDatabase.removeAllNodes(Campus.FAULKNER);
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A", Campus.FAULKNER);
    Assertions.assertEquals(2, graphDatabase.getSizeNode(Campus.FAULKNER));
    boolean a = graphDatabase.deleteNode("nugget", Campus.FAULKNER);
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, graphDatabase.getSizeNode(Campus.FAULKNER));
    graphDatabase.removeAllNodes(Campus.FAULKNER);
  }

  @Test
  public void testEditNode() {
    graphDatabase.createTables();
    graphDatabase.removeAll(Campus.FAULKNER);
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    graphDatabase.editNode("biscuit", 2, 7, 3, "White Hose", "CONF", "balogna2", "b", "Team A", Campus.FAULKNER);
    Assertions.assertEquals("White Hose", graphDatabase.getBuilding("biscuit", Campus.FAULKNER));
    graphDatabase.removeAll(Campus.FAULKNER);
  }

  @Test
  public void testGetters() {
    graphDatabase.createTables();
    graphDatabase.removeAll(Campus.FAULKNER);
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    Assertions.assertEquals(2, graphDatabase.getX("biscuit", Campus.FAULKNER));
    Assertions.assertEquals(5, graphDatabase.getY("biscuit", Campus.FAULKNER));
    Assertions.assertEquals(2, graphDatabase.getFloor("biscuit", Campus.FAULKNER));
    Assertions.assertEquals("White House", graphDatabase.getBuilding("biscuit", Campus.FAULKNER));
    Assertions.assertEquals("CONF", graphDatabase.getNodeType("biscuit", Campus.FAULKNER));
    Assertions.assertEquals("balogna", graphDatabase.getLongName("biscuit", Campus.FAULKNER));
    Assertions.assertEquals("b", graphDatabase.getShortName("biscuit", Campus.FAULKNER));
    Assertions.assertEquals("Team A", graphDatabase.getTeamAssigned("biscuit", Campus.FAULKNER));
    graphDatabase.removeAll(Campus.FAULKNER);
  }

  @Test
  public void testSetters() {
    graphDatabase.createTables();
    graphDatabase.removeAll(Campus.FAULKNER);
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    Assertions.assertTrue(graphDatabase.setX("biscuit", 3, Campus.FAULKNER));
    Assertions.assertTrue(graphDatabase.setY("biscuit", 6, Campus.FAULKNER));
    Assertions.assertTrue(graphDatabase.setFloor("biscuit", 3, Campus.FAULKNER));
    Assertions.assertTrue(graphDatabase.setBuilding("biscuit", "White Hose", Campus.FAULKNER));
    Assertions.assertTrue(graphDatabase.setNodeType("biscuit", "HALL", Campus.FAULKNER));
    Assertions.assertTrue(graphDatabase.setLongName("biscuit", "ham", Campus.FAULKNER));
    Assertions.assertTrue(graphDatabase.setShortName("biscuit", "h", Campus.FAULKNER));
    Assertions.assertTrue(graphDatabase.setTeamAssigned("biscuit", "Asgardians", Campus.FAULKNER));
    graphDatabase.removeAll(Campus.FAULKNER);
  }
}
