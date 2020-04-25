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
    boolean b = lDB.addLaundry("rbob", "AWASH00101");
    Assertions.assertTrue(b);
    boolean c = lDB.addLaundry("steve", "AWASH00101");
    Assertions.assertFalse(c);
    boolean d = lDB.addLaundry("rbob", "notanode");
    Assertions.assertFalse(d);
  }

  @Test
  public void testDeleteLaundry() {
    lDB.createTables();
    lDB.removeAll();
    lDB.addLaundry("brob", "AWASH00101");
    boolean a = lDB.deleteLaundry(1);
    Assertions.assertTrue(a);
    boolean b = lDB.deleteLaundry(1);
    Assertions.assertFalse(b);
  }

  @Test
  public void testGetters() {
    lDB.createTables();
    lDB.removeAll();
    lDB.addLaundry("brob", "AWASH00101");
    Assertions.assertEquals("brob", lDB.getEmpE(1));
    Assertions.assertEquals("AWASH00101", lDB.getLoc(1));
    Assertions.assertEquals("Request Sent", lDB.getProg(1));
    Assertions.assertNull(lDB.getEmpW(1));
    Assertions.assertNotNull(lDB.getTimeRequested(1));
    Assertions.assertNull(lDB.getEmpE(2));
  }

  @Test
  public void testSetters() {
    lDB.createTables();
    lDB.removeAll();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    lDB.addLaundry("brob", "AWASH00101");
    Assertions.assertTrue(lDB.setEmpE(1, "rbob"));
    Assertions.assertFalse(lDB.setEmpE(1, "steve"));
    Assertions.assertTrue(lDB.setLoc(1, "AWASH00101"));
    Assertions.assertFalse(lDB.setLoc(1, "notanode"));
    Assertions.assertTrue(lDB.setProg(1, "Clothes Collected"));
    Assertions.assertFalse(lDB.setProg(1, "notaprog"));
    Assertions.assertTrue(lDB.setEmpW(1, "rbob"));
    Assertions.assertFalse(lDB.setEmpW(1, "steve"));
    Assertions.assertTrue(lDB.setTimestamp(1, timestamp));
    Assertions.assertFalse(lDB.setEmpE(2, "rbob"));
  }
}
