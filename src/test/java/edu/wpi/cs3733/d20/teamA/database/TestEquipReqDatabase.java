package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.equipreq.EquipReqDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.equipreq.EquipRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestEquipReqDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  EmployeesDatabase eDB;
  GraphDatabase DB;
  EquipReqDatabase erDB;

  public TestEquipReqDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    DB = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    erDB = new EquipReqDatabase(conn);
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
    erDB.dropTables();
    boolean dropTables = erDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = erDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = erDB.dropTables();
    Assertions.assertTrue(dropTables2);
    erDB.createTables();
  }

  @Test
  public void testAddReq() {
    erDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
    Assertions.assertEquals(0, erDB.getSizeReq());
    Assertions.assertEquals(0, eDB.getSizeLog());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password56", "Intern");
    eDB.addLog("jay");
    erDB.addReq("mask", 2, "biscuit", "High");
    Assertions.assertEquals(1, erDB.getSizeReq());
    erDB.removeAllReqs();
    eDB.removeAllLogs();
  }

  @Test
  public void testDelReq() {
    erDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
    Assertions.assertEquals(0, erDB.getSizeReq());
    Assertions.assertEquals(0, eDB.getSizeLog());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password54", "Intern");
    eDB.addLog("jay");
    Assertions.assertEquals(1, eDB.getSizeLog());
    erDB.addReq("mask", 2, "biscuit", "High");
    Assertions.assertEquals(1, erDB.getSizeReq());
    erDB.deleteReq("jay", erDB.getTime("jay"));
    Assertions.assertEquals(0, erDB.getSizeReq());
    erDB.removeAllReqs();
    eDB.removeAllLogs();
  }

  @Test
  public void testEquipObject() {
    EquipRequest eq =
        new EquipRequest(
            "Daisy", "Blue", 9, " ", " ", new Timestamp(System.currentTimeMillis()), " ");
    Assertions.assertEquals("Daisy", eq.getName());
    Assertions.assertEquals("Blue", eq.getItem());
    Assertions.assertEquals(9, eq.getQty());
    Assertions.assertEquals(" ", eq.getLocation());
  }
}
