package edu.wpi.cs3733.d20.teamA.database.graph;

import java.sql.*;
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
      graphDatabase.removeAll();
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
    graphDatabase.removeAllNodes();
    Assertions.assertEquals(0, graphDatabase.getSizeNode());
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertEquals(1, graphDatabase.getSizeNode());
    graphDatabase.removeAllNodes();
  }

  @Test
  public void testGetSizeEdge() {
    graphDatabase.createTables();
    graphDatabase.removeAll();
    Assertions.assertEquals(0, graphDatabase.getSizeEdge());
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    graphDatabase.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertEquals(1, graphDatabase.getSizeEdge());
    graphDatabase.removeAll();
  }

  @Test
  public void testAddNode() {
    graphDatabase.createTables();
    graphDatabase.removeAllNodes();
    boolean a =
        graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertTrue(a);
    boolean b =
        graphDatabase.addNode("nugget", 0, 0, 2, "White House", "CONF", "balogna2", "b", "Team A");
    Assertions.assertTrue(b);
    boolean c =
        graphDatabase.addNode(
            "mashedP", 2, -1, 2, "White House", "CONF", "balogna3", "b", "Team A");
    Assertions.assertFalse(c);
    boolean d =
        graphDatabase.addNode(
            "BBQSauce", 2, 5, 0, "White House", "CONF", "balogna4", "b", "Team A");
    Assertions.assertFalse(d);
    boolean e =
        graphDatabase.addNode("banana", 2, 5, 11, "White House", "CONF", "balogna5", "b", "Team A");
    Assertions.assertFalse(e);
    boolean f =
        graphDatabase.addNode(
            "banana", 2, 5, 1, "White House", "abacabadabacaba", "balogna6", "b", "Team A");
    Assertions.assertFalse(f);
    graphDatabase.removeAllNodes();
  }

  @Test
  public void testAddEdge() {
    graphDatabase.createTables();
    graphDatabase.removeAll();
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    boolean a = graphDatabase.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertTrue(a);
    boolean b = graphDatabase.addEdge("biscuit_mashedP", "biscuit", "mashedP");
    Assertions.assertFalse(b);
    graphDatabase.removeAll();
  }

  @Test
  public void testDeleteEdge() {
    graphDatabase.createTables();
    graphDatabase.removeAll();
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    graphDatabase.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertEquals(1, graphDatabase.getSizeEdge());
    boolean a = graphDatabase.deleteEdge("biscuit_nugget");
    Assertions.assertTrue(a);
    Assertions.assertEquals(0, graphDatabase.getSizeEdge());
    graphDatabase.removeAll();
  }

  @Test
  public void testDeleteNode() {
    graphDatabase.createTables();
    graphDatabase.removeAllNodes();
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    graphDatabase.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    Assertions.assertEquals(2, graphDatabase.getSizeNode());
    boolean a = graphDatabase.deleteNode("nugget");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, graphDatabase.getSizeNode());
    graphDatabase.removeAllNodes();
  }

  @Test
  public void testEditNode() {
    graphDatabase.createTables();
    graphDatabase.removeAll();
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    graphDatabase.editNode("biscuit", 2, 7, 3, "White Hose", "CONF", "balogna2", "b", "Team A");
    Assertions.assertEquals("White Hose", graphDatabase.getBuilding("biscuit"));
    graphDatabase.removeAll();
  }

  @Test
  public void testGetters() {
    graphDatabase.createTables();
    graphDatabase.removeAll();
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertEquals(2, graphDatabase.getX("biscuit"));
    Assertions.assertEquals(5, graphDatabase.getY("biscuit"));
    Assertions.assertEquals(2, graphDatabase.getFloor("biscuit"));
    Assertions.assertEquals("White House", graphDatabase.getBuilding("biscuit"));
    Assertions.assertEquals("CONF", graphDatabase.getNodeType("biscuit"));
    Assertions.assertEquals("balogna", graphDatabase.getLongName("biscuit"));
    Assertions.assertEquals("b", graphDatabase.getShortName("biscuit"));
    Assertions.assertEquals("Team A", graphDatabase.getTeamAssigned("biscuit"));
    graphDatabase.removeAll();
  }

  @Test
  public void testSetters() {
    graphDatabase.createTables();
    graphDatabase.removeAll();
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertTrue(graphDatabase.setX("biscuit", 3));
    Assertions.assertTrue(graphDatabase.setY("biscuit", 6));
    Assertions.assertTrue(graphDatabase.setFloor("biscuit", 3));
    Assertions.assertTrue(graphDatabase.setBuilding("biscuit", "White Hose"));
    Assertions.assertTrue(graphDatabase.setNodeType("biscuit", "HALL"));
    Assertions.assertTrue(graphDatabase.setLongName("biscuit", "ham"));
    Assertions.assertTrue(graphDatabase.setShortName("biscuit", "h"));
    Assertions.assertTrue(graphDatabase.setTeamAssigned("biscuit", "Asgardians"));
    graphDatabase.removeAll();
  }
}
