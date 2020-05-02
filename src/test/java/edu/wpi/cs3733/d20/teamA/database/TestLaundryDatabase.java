package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.laundry.LaundryDatabase;
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
    eDB.addEmployee("Bob", "Roberts", "brob", "AbCd1234", "desk clerk");
    eDB.addEmployee("Rob", "Boberts", "rbob", "1234aBcD", "cleaner");
    gDB.addNode("AWASH00101", 123, 456, 1, "main", "HALL", "washing hall", "WH", "TeamA");
  }

  @AfterEach
  public void teardown() {
    try {
      gDB.removeAll();
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
    lDB.addLaundry("brob", "washing hall");
    Assertions.assertEquals(1, lDB.getSizeLaundry());
  }

  @Test
  public void testAddLaundry() {
    lDB.createTables();
    lDB.removeAll();
    int a = lDB.addLaundry("brob", "washing hall");
    Assertions.assertTrue(lDB.checkIfExistsInt("Laundry", "requestNum", a));
    int b = lDB.addLaundry("rbob", "washing hall");
    Assertions.assertTrue(lDB.checkIfExistsInt("Laundry", "requestNum", b));
    int c = lDB.addLaundry("steve", "washing hall");
    Assertions.assertFalse(lDB.checkIfExistsInt("Laundry", "requestNum", c));
    int d = lDB.addLaundry("rbob", "notanode");
    Assertions.assertFalse(lDB.checkIfExistsInt("Laundry", "requestNum", d));
  }

  @Test
  public void testDeleteLaundry() {
    lDB.createTables();
    lDB.removeAll();
    int l = lDB.addLaundry("brob", "washing hall");
    boolean a = lDB.deleteLaundry(l);
    Assertions.assertTrue(a);
    boolean b = lDB.deleteLaundry(l);
    Assertions.assertFalse(b);
  }

  @Test
  public void testGetters() {
    lDB.createTables();
    lDB.removeAll();
    int a = lDB.addLaundry("brob", "washing hall");
    Assertions.assertEquals("brob", lDB.getEmpE(a));
    Assertions.assertEquals("washing hall", lDB.getLoc(a));
    Assertions.assertEquals("Requested", lDB.getProg(a));
    Assertions.assertNull(lDB.getEmpW(a));
    Assertions.assertNotNull(lDB.getTimeRequested(a));
    Assertions.assertNull(lDB.getEmpE(0));
  }

  @Test
  public void testSetters() {
    lDB.createTables();
    lDB.removeAll();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    int a = lDB.addLaundry("brob", "washing hall");
    Assertions.assertTrue(lDB.setEmpE(a, "rbob"));
    Assertions.assertFalse(lDB.setEmpE(a, "steve"));
    Assertions.assertTrue(lDB.setLoc(a, "washing hall"));
    Assertions.assertFalse(lDB.setLoc(a, "notanode"));
    Assertions.assertTrue(lDB.setProg(a, "Collected"));
    Assertions.assertFalse(lDB.setProg(a, "notaprog"));
    Assertions.assertTrue(lDB.setEmpW(a, "rbob"));
    Assertions.assertFalse(lDB.setEmpW(a, "steve"));
    Assertions.assertTrue(lDB.setTimestamp(a, timestamp));
    Assertions.assertFalse(lDB.setEmpE(0, "rbob"));
  }
}
