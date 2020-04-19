package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestJanitorDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase gDB;

  JanitorDatabase jDB;

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    jDB = new JanitorDatabase(conn);
    gDB.createTables();
    jDB.createTables();
  }

  @AfterEach
  public void teardown() {
    try {
      gDB.dropTables();
      jDB.dropTables();
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  //  @Test
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
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.addRequest("biscuit", "Extra Large");
    Assertions.assertFalse(b);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean c = jDB.addRequest("yoyoyo", "Medium");
    Assertions.assertFalse(c);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean d = jDB.addRequest("biscuit", "Medium");
    Assertions.assertTrue(d);
    Assertions.assertEquals(2, jDB.getRequestSize());

    jDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testUpdateRequest() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.removeAll();
    jDB.addRequest("biscuit", "Medium");
    boolean a = jDB.updateRequest(1, "Harry", "Dispatched");
    Assertions.assertTrue(a);
    boolean b = jDB.updateRequest(1, "Harry", "Ert");
    Assertions.assertFalse(b);

    jDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testDeleteRequest() throws SQLException {
    gDB.removeAllNodes();

    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.removeAll();
    jDB.addRequest("biscuit", "Medium");

    boolean a = jDB.deleteRequest(1);
    Assertions.assertTrue(a);

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
    Assertions.assertEquals(3, jDB.getRequestSize());

    jDB.updateRequest(1, "harry", "Done");
    jDB.updateRequest(2, "harry", "Done");
    jDB.updateRequest(3, "harry", "Done");

    boolean b = jDB.deleteDoneRequests();
    Assertions.assertEquals(0, jDB.getRequestSize());
    Assertions.assertTrue(b);

    jDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetRequest() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    boolean a = jDB.addRequest("biscuit", "Medium");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched");
    jDB.printTable();
    Assertions.assertEquals(1, jDB.getRequestSize());
    Assertions.assertTrue(b);
    Assertions.assertEquals(
        "requestNumber: 1, location: biscuit, name: harry, progress: Dispatched, priority: Medium",
        jDB.getRequest(1));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetTimestamp() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    boolean a = jDB.addRequest("biscuit", "Medium");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched");

    Assertions.assertNotNull(jDB.getTimestamp(1));
    Assertions.assertNull(jDB.getTimestamp(2));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetName() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    boolean a = jDB.addRequest("biscuit", "Medium");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched");

    Assertions.assertEquals("harry", jDB.getName(1));
    Assertions.assertNull(jDB.getName(2));

    Assertions.assertTrue(jDB.addRequest("biscuit", "Medium"));
    Assertions.assertTrue(jDB.updateRequest(2, "boris", "Dispatched"));
    Assertions.assertEquals("boris", jDB.getName(2));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetLocation() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    boolean a = jDB.addRequest("biscuit", "Medium");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched");

    Assertions.assertEquals("biscuit", jDB.getLocation(1));
    Assertions.assertNull(jDB.getLocation(2));

    Assertions.assertTrue(jDB.addRequest("biscuit", "Medium"));
    Assertions.assertTrue(jDB.updateRequest(2, "boris", "Dispatched"));
    Assertions.assertEquals("biscuit", jDB.getLocation(2));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetProgress() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    boolean a = jDB.addRequest("biscuit", "Medium");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched");

    Assertions.assertEquals("Dispatched", jDB.getProgress(1));
    Assertions.assertNull(jDB.getProgress(2));

    Assertions.assertTrue(jDB.addRequest("biscuit", "Medium"));
    Assertions.assertTrue(jDB.updateRequest(2, "boris", "Done"));
    Assertions.assertEquals("Done", jDB.getProgress(2));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetPriority() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    boolean a = jDB.addRequest("biscuit", "Medium");
    Assertions.assertTrue(a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched");

    Assertions.assertEquals("Medium", jDB.getPriority(1));
    Assertions.assertNull(jDB.getPriority(2));

    Assertions.assertTrue(jDB.addRequest("biscuit", "High"));
    Assertions.assertTrue(jDB.updateRequest(2, "boris", "Done"));
    Assertions.assertEquals("High", jDB.getPriority(2));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }
}
