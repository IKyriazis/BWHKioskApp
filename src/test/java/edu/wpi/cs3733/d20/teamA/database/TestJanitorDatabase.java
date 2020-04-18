package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestJanitorDatabase {
  JanitorDatabase jDB = new JanitorDatabase();
  GraphDatabase gDB = new GraphDatabase();

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
    jDB.dropTables();
    boolean dropTables = jDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = jDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = jDB.dropTables();
    Assertions.assertTrue(dropTables2);
    jDB.createTables();
  }

  @Test
  public void testAddRequest() throws SQLException {

    gDB.removeAllNodes();
    // need nodeID "biscuit" in node table so addrequest works
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    jDB.removeAll();
    boolean a = jDB.addRequest("biscuit", "High");
    Assertions.assertTrue(a);
    boolean b = jDB.addRequest("biscuit", "Extra Large");
    Assertions.assertFalse(b);
    boolean c = jDB.addRequest("yoyoyo", "Medium");
    Assertions.assertFalse(c);
    jDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testDeleteDoneRequests() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("yoyoyo", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("hihihi", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.removeAll();
    jDB.addRequest("biscuit", "Medium");
    jDB.addRequest("yoyoyo", "Medium");
    jDB.addRequest("hihihi", "Medium");
    jDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetTimestamp() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("yoyoyo", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("hihihi", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.removeAll();
    jDB.addRequest("biscuit", "Medium");
    jDB.addRequest("yoyoyo", "Medium");
    jDB.addRequest("hihihi", "Medium");

    Assertions.assertNotNull(jDB.getTimestamp("biscuit"));
    System.out.println(jDB.getTimestamp("biscuit"));
    jDB.removeAll();
    gDB.removeAllNodes();
  }

  //  Unsure on how to test deleteRequest() because we need the request timestamp to delete it but
  // that is stored in
  //  the database
  //  @Test
  //  public void testDeleteRequest() throws SQLException {
  //    jDB.removeAll();
  //    boolean a = jDB.addRequest("Lobby", "High");
  //    boolean b = jDB.addRequest("Main A", "Medium");
  //    boolean c = jDB.deleteRequest("Main A", )
  //  }
}
