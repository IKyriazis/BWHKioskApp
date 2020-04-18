package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestGraphDatabase {
  GraphDatabase DB;

  public TestGraphDatabase() throws SQLException {
    DB = new GraphDatabase();
  }

  @Test
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
    DB.removeAllNodes();
    Assertions.assertEquals(0, DB.getSizeNode());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertEquals(1, DB.getSizeNode());
    DB.removeAllNodes();
  }

  @Test
  public void testGetSizeEdge() throws SQLException {
    DB.removeAll();
    Assertions.assertEquals(0, DB.getSizeEdge());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertEquals(1, DB.getSizeEdge());
    DB.removeAll();
  }

  @Test
  public void testAddNode() throws SQLException {
    DB.removeAllNodes();
    boolean a = DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertTrue(a);
    boolean b = DB.addNode("nugget", 0, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertFalse(b);
    boolean c = DB.addNode("mashedP", 2, -1, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertFalse(c);
    boolean d = DB.addNode("BBQSauce", 2, 5, 0, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertFalse(d);
    boolean e = DB.addNode("banana", 2, 5, 11, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertFalse(e);
    boolean f =
        DB.addNode("banana", 2, 5, 1, "White House", "abacabadabacaba", "balogna", "b", "Team A");
    Assertions.assertFalse(f);
    DB.removeAllNodes();
  }

  @Test
  public void testAddEdge() throws SQLException {
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna", "b", "Team A");
    boolean a = DB.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertTrue(a);
    boolean b = DB.addEdge("biscuit_mashedP", "biscuit", "mashedP");
    Assertions.assertFalse(b);
    DB.removeAll();
  }

  @Test
  public void testDeleteEdge() throws SQLException {
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addEdge("biscuit_nugget", "biscuit", "nugget");
    Assertions.assertEquals(1, DB.getSizeEdge());
    boolean a = DB.deleteEdge("biscuit_nugget");
    Assertions.assertTrue(a);
    Assertions.assertEquals(0, DB.getSizeEdge());
    DB.removeAll();
  }

  @Test
  public void testDeleteNode() throws SQLException {
    DB.removeAllNodes();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    DB.addNode("nugget", 4, 10, 2, "White House", "CONF", "balogna", "b", "Team A");
    Assertions.assertEquals(2, DB.getSizeNode());
    boolean a = DB.deleteNode("nugget");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, DB.getSizeNode());
    DB.removeAllNodes();
  }
}
