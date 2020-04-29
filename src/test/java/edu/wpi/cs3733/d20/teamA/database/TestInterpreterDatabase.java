package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.graph.NodeType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestInterpreterDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  InterpreterDatabase iDB;
  GraphDatabase gDB;

  public TestInterpreterDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    iDB = new InterpreterDatabase(conn);
  }

  @AfterEach
  public void teardown() {
    try {
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  @Test
  public void testTables() {
    gDB.createTables();
    iDB.dropTables();

    boolean dropTables = iDB.dropTables();
    Assertions.assertFalse(dropTables);

    boolean makeTables = iDB.createTables();
    Assertions.assertTrue(makeTables);

    boolean dropTables2 = iDB.dropTables();
    Assertions.assertTrue(dropTables2);
    iDB.createTables();
  }

  @Test
  public void testAddInterpreter() {
    iDB.dropTables();
    iDB.createTables();

    boolean a = iDB.addInterpreter("Yash", "Spanish");
    Assertions.assertTrue(a);

    boolean d = iDB.addInterpreter("Yash2", "French");
    Assertions.assertTrue(d);

    boolean b = iDB.addInterpreter("Yash3", "Italian");
    Assertions.assertTrue(b);

    boolean e = iDB.addInterpreter("Yash4", "German");
    Assertions.assertTrue(e);

    boolean f = iDB.addInterpreter("Yash", "Latin");
    Assertions.assertFalse(f);

    Assertions.assertEquals(4, iDB.getSizeInterpreters());
    iDB.dropTables();
  }

  @Test
  public void testAddRequest() {
    gDB.createTables();
    gDB.removeAll();
    gDB.addNode("cookie", 5, 5, 1, "Main", NodeType.HALL.name(), "", "", "");

    iDB.dropTables();
    iDB.createTables();
    iDB.addInterpreter("Yash", "French");

    int a = iDB.addRequest("Yash", "French", "cookie", "Submitted");
    Assertions.assertTrue(a > 0);

    int b = iDB.addRequest("Ysah", "French", "cookie", "Submitted");
    Assertions.assertEquals(0, b);

    int c = iDB.addRequest("Yash", "French", "biscuit", "Submitted");
    Assertions.assertEquals(0, c);
    iDB.dropTables();
    gDB.dropTables();
  }

  @Test
  public void testChangeRequestStatus() {
    gDB.createTables();
    gDB.removeAll();
    gDB.addNode("cookie", 5, 5, 1, "Main", NodeType.HALL.name(), "", "", "");

    iDB.dropTables();
    iDB.createTables();
    iDB.addInterpreter("Yash", "Spanish");

    int id = iDB.addRequest("Yash", "Spanish", "cookie", "Submitted");

    boolean success = iDB.updateRequestStatus(id, "Completed");
    Assertions.assertTrue(success);
    iDB.dropTables();
    gDB.dropTables();
  }
}
