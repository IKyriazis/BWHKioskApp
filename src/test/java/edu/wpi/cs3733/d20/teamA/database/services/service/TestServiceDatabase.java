package edu.wpi.cs3733.d20.teamA.database.services.service;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
    eDB.addEmployee("Yash", "Patel", "yppatel", "YashPatel1", EmployeeTitle.ADMIN);
    eDB.logIn("yppatel", "YashPatel1");
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
    sDB.removeAll();
    DB.removeAll();
    Assertions.assertEquals(0, sDB.getSize());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password56", EmployeeTitle.INTERPRETER);
    sDB.addServiceReq(
        ServiceType.JANITOR,
        "balogna",
        "Janitor needed at balogna",
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    Assertions.assertEquals(1, sDB.getSize());
    sDB.removeAll();
    DB.removeAll();
  }

  @Test
  public void testDelReq() {
    sDB.removeAll();
    DB.removeAll();
    Assertions.assertEquals(0, sDB.getSize());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password54", EmployeeTitle.NURSE);
    String req =
        sDB.addServiceReq(
            ServiceType.JANITOR,
            "balogna",
            "Janitor needed at balogna",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    Assertions.assertEquals(1, sDB.getSize());
    sDB.deleteServReq(req);
    Assertions.assertEquals(0, sDB.getSize());
    sDB.removeAll();
    DB.removeAll();
  }

  @Test
  public void testEditStatus() {
    sDB.removeAll();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password56", EmployeeTitle.ADMIN);
    String req =
        sDB.addServiceReq(
            ServiceType.JANITOR,
            "balogna",
            "Janitor needed at balogna",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    sDB.editStatus(req, "Completed");
    Assertions.assertEquals("Completed", sDB.getStatus(req));
    sDB.removeAll();
    DB.removeAll();
  }
}
