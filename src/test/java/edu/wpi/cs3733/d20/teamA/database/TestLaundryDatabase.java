package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLaundryDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  LaundryDatabase lDB;
  GraphDatabase gDB;
  EmployeesDatabase eDB;

  public TestLaundryDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    lDB = new LaundryDatabase(conn);
    eDB.addEmployee("Bob", "Roberts", "brob", "abcd", "desk clerk");
    eDB.addEmployee("Rob", "Boberts", "rbob", "abcd", "cleaner");
    gDB.addNode("AWASH00101", 123, 456, 1, "main", "HALL", "washing hall", "WH", "TeamA");
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
    lDB.dropTables();
    boolean dropTables = lDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = lDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = lDB.dropTables();
    Assertions.assertTrue(dropTables2);
  }

  @Test
  public void testGetSize() {
    lDB.createTables();
    lDB.removeAll();
    Assertions.assertEquals(0, lDB.getSizeLaundry());
    lDB.addLaundry("brob", "AWASH00101");
    Assertions.assertEquals(1, lDB.getSizeLaundry());
  }

  @Test
  public void testAddLaundry() {
    lDB.createTables();
    lDB.removeAll();
    boolean a = lDB.addLaundry("brob", "AWASH00101");
    Assertions.assertTrue(a);
  }
}
