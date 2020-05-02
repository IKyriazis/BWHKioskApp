package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.janitor.JanitorDatabase;
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
    int a = jDB.addRequest("biscuit", "High", "", "");
    Assertions.assertNotEquals(1, a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    int b = jDB.addRequest("biscuit", "Extra Large", "", "");
    Assertions.assertNotEquals(1, b);
    Assertions.assertEquals(1, jDB.getRequestSize());
    int c = jDB.addRequest("yoyoyo", "Medium", "", "");
    Assertions.assertNotEquals(1, c);
    Assertions.assertEquals(1, jDB.getRequestSize());
    int d = jDB.addRequest("biscuit", "Medium", "", "");
    Assertions.assertNotEquals(1, d);
    Assertions.assertEquals(2, jDB.getRequestSize());

    jDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testUpdateRequest() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.removeAll();
    int a = jDB.addRequest("biscuit", "Medium", "", "");
    boolean b = jDB.updateRequest(a, "Harry", "Dispatched", "");
    Assertions.assertTrue(b);
    boolean c = jDB.updateRequest(a, "Harry", "Ert", "");
    Assertions.assertFalse(c);

    jDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testDeleteRequest() throws SQLException {
    gDB.removeAllNodes();

    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.removeAll();
    int test = jDB.addRequest("biscuit", "Medium", "", "");

    boolean a = jDB.deleteRequest(test);
    Assertions.assertTrue(a);

    jDB.removeAll();
    gDB.removeAllNodes();
  }

  //  @Test
  //  public void testDeleteDoneRequests() throws SQLException {
  //    gDB.removeAllNodes();
  //    gDB.addNode("biscuit", 1, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
  //    gDB.addNode("yoyoyo", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
  //    gDB.addNode("hihihi", 3, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
  //
  //    jDB.removeAll();
  //    int a = jDB.addRequest("biscuit", "Medium", "John", "beep");
  //    int b = jDB.addRequest("yoyoyo", "Medium", "Henry", "boop");
  //    int c = jDB.addRequest("hihihi", "Medium", "Sid", "bop");
  //    Assertions.assertEquals(3, jDB.getRequestSize());
  //
  //    jDB.updateRequest(a, "harry", "Done", "");
  //    jDB.updateRequest(b, "harry", "Done", "");
  //    jDB.updateRequest(c, "harry", "Done", "");
  //
  //    boolean test = jDB.deleteDoneRequests();
  //    Assertions.assertEquals(0, jDB.getRequestSize());
  //    Assertions.assertTrue(test);
  //
  //    jDB.removeAll();
  //    gDB.removeAllNodes();
  //  }

  @Test
  public void testGetRequest() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    int a = jDB.addRequest("biscuit", "Medium", "", "");
    Assertions.assertNotEquals(1, a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(a, "harry", "Dispatched", "harry");
    jDB.printTable();
    Assertions.assertEquals(1, jDB.getRequestSize());
    Assertions.assertTrue(b);
    Assertions.assertEquals(
        "location: biscuit, employeeName: harry, progress: Dispatched, priority: Medium",
        jDB.getRequest(a));

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
    int a = jDB.addRequest("biscuit", "Medium", "", "");
    Assertions.assertNotEquals(1, a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched", "");

    Assertions.assertNotNull(jDB.getTimestamp(a));
    Assertions.assertNull(jDB.getTimestamp(2));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }

  //  @Test
  //  public void testGetEmployeeName() throws SQLException {
  //    gDB.removeAllNodes();
  //    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
  //
  //    jDB.createTables();
  //    jDB.removeAll();
  //    int a = jDB.addRequest("biscuit", "Medium", "", "");
  //    Assertions.assertNotEquals(1, a);
  //    Assertions.assertEquals(1, jDB.getRequestSize());
  //    boolean b = jDB.updateRequest(a, "harry", "Dispatched", "harry");
  //
  //    Assertions.assertEquals("harry", jDB.getEmployeeName(a));
  //
  //    int c = jDB.addRequest("biscuit", "Medium", "", "");
  //    Assertions.assertTrue(jDB.updateRequest(c, "boris", "Dispatched", "boris"));
  //    Assertions.assertEquals("boris", jDB.getEmployeeName(c));
  //
  //    jDB.removeAll();
  //    jDB.dropTables();
  //    gDB.removeAllNodes();
  //  }

  @Test
  public void testGetLocation() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    jDB.createTables();
    jDB.removeAll();
    int a = jDB.addRequest("biscuit", "Medium", "", "");
    Assertions.assertNotEquals(1, a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(1, "harry", "Dispatched", "");

    Assertions.assertEquals("biscuit", jDB.getLocation(a));
    Assertions.assertNull(jDB.getLocation(2));

    Assertions.assertNotEquals(1, jDB.addRequest("biscuit", "Medium", "", ""));
    Assertions.assertTrue(jDB.updateRequest(2, "boris", "Dispatched", ""));
    Assertions.assertEquals("biscuit", jDB.getLocation(a));

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
    int a = jDB.addRequest("biscuit", "Medium", "", "");
    Assertions.assertNotEquals(1, a);
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(a, "harry", "Dispatched", "");

    Assertions.assertEquals("Dispatched", jDB.getProgress(a));

    int c = jDB.addRequest("biscuit", "Medium", "", "");
    Assertions.assertTrue(jDB.updateRequest(c, "boris", "Done", ""));
    Assertions.assertEquals("Done", jDB.getProgress(c));

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

    int a = jDB.addRequest("biscuit", "Medium", "", "");
    Assertions.assertEquals(1, jDB.getRequestSize());
    boolean b = jDB.updateRequest(a, "harry", "Dispatched", "");

    Assertions.assertEquals("Medium", jDB.getPriority(a));

    int c = jDB.addRequest("biscuit", "High", "", "");
    Assertions.assertTrue(jDB.updateRequest(c, "boris", "Done", ""));
    Assertions.assertEquals("High", jDB.getPriority(c));

    jDB.removeAll();
    jDB.dropTables();
    gDB.removeAllNodes();
  }
}
