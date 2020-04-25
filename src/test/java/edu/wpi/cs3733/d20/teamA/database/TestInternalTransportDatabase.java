package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestInternalTransportDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase gDB;

  InternalTransportDatabase itDB;

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    itDB = new InternalTransportDatabase(conn);
    gDB.createTables();
    itDB.createTables();
  }

  @AfterEach
  public void teardown() {
    try {
      gDB.dropTables();
      itDB.dropTables();
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  @Test
  public void testTables() throws SQLException {
    itDB.dropTables();
    boolean dropTables = itDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = itDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = itDB.dropTables();
    Assertions.assertTrue(dropTables2);
    itDB.createTables();
  }

  @Test
  public void testAddRequest() throws SQLException {

    gDB.removeAllNodes();
    // need nodeID "biscuit" in node table so addrequest works
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    itDB.removeAll();
    int a = itDB.addRequest("biscuit", "biscuit");
    Assertions.assertEquals(1, a);
    Assertions.assertEquals(1, itDB.getRequestSize());
    int b = itDB.addRequest("biscuit", "Extra Large");
    Assertions.assertEquals(-1, b);
    Assertions.assertEquals(1, itDB.getRequestSize());
    int c = itDB.addRequest("yoyoyo", "biscuit");
    Assertions.assertEquals(-1, c);
    Assertions.assertEquals(1, itDB.getRequestSize());
    int d = itDB.addRequest("biscuit", "biscuit");
    itDB.printTable();
    Assertions.assertEquals(2, d);
    Assertions.assertEquals(2, itDB.getRequestSize());

    itDB.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testUpdateRequest() throws SQLException {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");

    itDB.removeAll();
    itDB.addRequest("biscuit", "biscuit");
    boolean a = itDB.updateRequest(1, "Harry", "Dispatched");
    Assertions.assertTrue(a);
    boolean b = itDB.updateRequest(1, "Harry", "Ert");
    Assertions.assertFalse(b);

    itDB.removeAll();
    gDB.removeAllNodes();
  }
}
