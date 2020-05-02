package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestServiceDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  EmployeesDatabase eDB;
  GraphDatabase DB;
  ServiceDatabase sDB;

  public TestServiceDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    DB = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    sDB = new ServiceDatabase(conn);
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
    sDB.dropTables();
    boolean dropTables = sDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = sDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = sDB.dropTables();
    Assertions.assertTrue(dropTables2);
    sDB.createTables();
  }

  @Test
  public void testAddReq() {
    sDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
    Assertions.assertEquals(0, sDB.getSizeReq());
    Assertions.assertEquals(0, eDB.getSizeLog());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password56", "Intern");
    eDB.addLog("jay");
    sDB.addServiceReq(
        "janitor", "balogna", "Janitor needed at balogna", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    Assertions.assertEquals(1, sDB.getSizeReq());
    sDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
  }

  @Test
  public void testDelReq() {
    sDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
    Assertions.assertEquals(0, sDB.getSizeReq());
    Assertions.assertEquals(0, eDB.getSizeLog());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password54", "Intern");
    eDB.addLog("jay");
    Assertions.assertEquals(1, eDB.getSizeLog());
    String req =
        sDB.addServiceReq(
            "janitor", "balogna", "Janitor needed at balogna", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    Assertions.assertEquals(1, sDB.getSizeReq());
    sDB.deleteServReq(req);
    Assertions.assertEquals(0, sDB.getSizeReq());
    sDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
  }

  @Test
  public void testEditStatus() {
    sDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password56", "Intern");
    eDB.addLog("jay");
    String req =
        sDB.addServiceReq(
            "janitor", "balogna", "Janitor needed at balogna", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    sDB.editStatus(req, "Completed");
    Assertions.assertEquals("Completed", sDB.getStatus(req));
    sDB.removeAllReqs();
    eDB.removeAllLogs();
    DB.removeAll();
  }
}
