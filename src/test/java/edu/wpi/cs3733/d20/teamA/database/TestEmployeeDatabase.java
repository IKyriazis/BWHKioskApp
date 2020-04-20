package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestEmployeeDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  EmployeesDatabase eDB;

  public TestEmployeeDatabase() throws SQLException {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    eDB = new EmployeesDatabase(conn);
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
    eDB.dropTables();
    boolean dropTables = eDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = eDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = eDB.dropTables();
    Assertions.assertTrue(dropTables2);
    eDB.createTables();
  }

  @Test
  public void testAddJanitor() throws SQLException {
    eDB.createTables();
    eDB.removeAllJanitors();
    boolean a = eDB.addJanitor("abc", "brad", "bad");
    Assertions.assertTrue(a);
    boolean d = eDB.addJanitor("abcd", "chad", "lad");
    Assertions.assertTrue(d);
    boolean b = eDB.addJanitor("ab", "kassy", "lassy");
    Assertions.assertTrue(b);
    boolean e = eDB.addJanitor("bacd", "ray", "jay");
    Assertions.assertTrue(e);
    boolean c = eDB.addJanitor("abcd", "bailey", "kaylee");
    Assertions.assertFalse(c);
    Assertions.assertEquals(4, eDB.getSizeJanitors());
    eDB.removeAllJanitors();
  }

  @Test
  public void testDeleteJanitor() throws SQLException {
    eDB.createTables();
    eDB.removeAllJanitors();
    boolean a = eDB.addJanitor("abc", "brad", "bad");
    Assertions.assertTrue(a);
    boolean b = eDB.deleteJanitor("abc");
    Assertions.assertTrue(b);
    Assertions.assertEquals(0, eDB.getSizeJanitors());
    eDB.addJanitor("abc", "brad", "bad");
    eDB.addJanitor("dyi", "brad", "bad");
    Assertions.assertEquals(2, eDB.getSizeJanitors());
    boolean c = eDB.deleteJanitor("abc");
    Assertions.assertTrue(c);
    Assertions.assertEquals(1, eDB.getSizeJanitors());
  }
}
