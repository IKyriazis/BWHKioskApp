package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import java.sql.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGraphDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase DB;

  public TestGraphDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    DB = new GraphDatabase(conn);
  }

  @AfterEach
  public void teardown() {
    try {
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  // @Test
  public void testDB() throws SQLException {
    boolean test = false;
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      test = true;
    } catch (SQLException e) {

    }
    Assertions.assertTrue(test);
  }

  @Test
  public void testTables() throws SQLException {
    DB.dropTables();
    boolean dropTables = DB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = DB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = DB.dropTables();
    Assertions.assertTrue(dropTables2);
    DB.createTables();
  }

  @Test
  public void testGetSizeNode() throws SQLException {
    DB.createTables();
    DB.removeAllNodes();
    Assertions.assertEquals(0, DB.getSizeNode());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertEquals(1, DB.getSizeNode());
    DB.removeAllNodes();
  }

  @Test
  public void testGetSizeEdge() throws SQLException {
    DB.createTables();
    DB.removeAll();
    Assertions.assertEquals(0, DB.getSizeEdge());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    DB.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertEquals(1, DB.getSizeEdge());
    DB.removeAll();
  }

  @Test
  public void testAddNode() throws SQLException {
    DB.createTables();
    DB.removeAllNodes();
    boolean a = DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertTrue(a);
    boolean b = DB.addNode("nugget", 0, 0, 2, "White House", "CONF", "balogna2", "b", "Team A");
    Assertions.assertTrue(b);
    boolean c = DB.addNode("mashedP", 2, -1, 2, "White House", "CONF", "balogna3", "b", "Team A");
    Assertions.assertFalse(c);
    boolean d = DB.addNode("BBQSauce", 2, 5, 0, "White House", "CONF", "balogna4", "b", "Team A");
    Assertions.assertFalse(d);
    boolean e = DB.addNode("banana", 2, 5, 11, "White House", "CONF", "balogna5", "b", "Team A");
    Assertions.assertFalse(e);
    boolean f =
        DB.addNode("banana", 2, 5, 1, "White House", "abacabadabacaba", "balogna6", "b", "Team A");
    Assertions.assertFalse(f);
    DB.removeAllNodes();
  }

  @Test
  public void testAddEdge() throws SQLException {
    DB.createTables();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    boolean a = DB.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertTrue(a);
    boolean b = DB.addEdge("biscuit_mashedP", "biscuit", "mashedP");
    Assertions.assertFalse(b);
    DB.removeAll();
  }

  @Test
  public void testDeleteEdge() throws SQLException {
    DB.createTables();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    DB.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertEquals(1, DB.getSizeEdge());
    boolean a = DB.deleteEdge("biscuit_nugget");
    Assertions.assertTrue(a);
    Assertions.assertEquals(0, DB.getSizeEdge());
    DB.removeAll();
  }

  @Test
  public void testDeleteNode() throws SQLException {
    DB.createTables();
    DB.removeAllNodes();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna2", "b", "Team A");
    Assertions.assertEquals(2, DB.getSizeNode());
    boolean a = DB.deleteNode("nugget");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, DB.getSizeNode());
    DB.removeAllNodes();
  }

  @Test
  public void testEditNode() throws SQLException {
    DB.createTables();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.editNode("biscuit", 2, 7, 3, "White Hose", "CONF", "balogna2", "b", "Team A");
    Assertions.assertEquals("White Hose", DB.getBuilding("biscuit"));
    DB.removeAll();
  }

  @Test
  public void testGetters() throws SQLException {
    DB.createTables();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertEquals(2, DB.getX("biscuit"));
    Assertions.assertEquals(5, DB.getY("biscuit"));
    Assertions.assertEquals(2, DB.getFloor("biscuit"));
    Assertions.assertEquals("White House", DB.getBuilding("biscuit"));
    Assertions.assertEquals("CONF", DB.getNodeType("biscuit"));
    Assertions.assertEquals("balogna", DB.getLongName("biscuit"));
    Assertions.assertEquals("b", DB.getShortName("biscuit"));
    Assertions.assertEquals("Team A", DB.getTeamAssigned("biscuit"));
    DB.removeAll();
  }

  @Test
  public void testSetters() throws SQLException {
    DB.createTables();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertTrue(DB.setX("biscuit", 3));
    Assertions.assertTrue(DB.setY("biscuit", 6));
    Assertions.assertTrue(DB.setFloor("biscuit", 3));
    Assertions.assertTrue(DB.setBuilding("biscuit", "White Hose"));
    Assertions.assertTrue(DB.setNodeType("biscuit", "HALL"));
    Assertions.assertTrue(DB.setLongName("biscuit", "ham"));
    Assertions.assertTrue(DB.setShortName("biscuit", "h"));
    Assertions.assertTrue(DB.setTeamAssigned("biscuit", "Asgardians"));
    DB.removeAll();
  }
}
