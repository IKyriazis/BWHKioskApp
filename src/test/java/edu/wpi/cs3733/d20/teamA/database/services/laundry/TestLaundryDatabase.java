package edu.wpi.cs3733.d20.teamA.database.services.laundry;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import java.sql.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLaundryDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  ServiceDatabase serviceDatabase;
  GraphDatabase gDB;
  EmployeesDatabase eDB;

  public TestLaundryDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    serviceDatabase = new ServiceDatabase(conn);
    eDB.addEmployee("Bob", "Roberts", "brob", "AbCd1234", EmployeeTitle.ADMIN);
    eDB.addEmployee("Rob", "Boberts", "rbob", "1234aBcD", EmployeeTitle.ADMIN);
    eDB.addEmployee("Yash", "Patel", "yppatel", "YashPatel1", EmployeeTitle.ADMIN);
    eDB.logIn("yppatel", "YashPatel1");
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
    serviceDatabase.dropTables();
    boolean dropTables = serviceDatabase.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = serviceDatabase.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = serviceDatabase.dropTables();
    Assertions.assertTrue(dropTables2);
    serviceDatabase.createTables();
  }

  @Test
  public void testGetSize() {
    serviceDatabase.createTables();
    serviceDatabase.getSize(ServiceType.LAUNDRY);
    Assertions.assertEquals(0, serviceDatabase.getSize(ServiceType.LAUNDRY));
    serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "brob", "");
    Assertions.assertEquals(1, serviceDatabase.getSize(ServiceType.LAUNDRY));
  }

  @Test
  public void testAddLaundry() {
    serviceDatabase.removeAll();
    String a = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "brob", "");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", a));
    String b = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "rbob", "");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", b));
    /*
    int c = lDB.addLaundry("steve", "washing hall");
    Assertions.assertFalse(lDB.checkIfExistsInt("Laundry", "requestNum", c));
    int d = lDB.addLaundry("rbob", "notanode");
    Assertions.assertFalse(lDB.checkIfExistsInt("Laundry", "requestNum", d));

     */
  }

  @Test
  public void testDeleteLaundry() {
    serviceDatabase.removeAll();
    String a = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "brob", "");
    boolean b = serviceDatabase.deleteServReq(a);
    Assertions.assertTrue(b);
    serviceDatabase.removeAll();
  }
}
