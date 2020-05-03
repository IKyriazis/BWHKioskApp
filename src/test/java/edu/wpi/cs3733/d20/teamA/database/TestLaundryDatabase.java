package edu.wpi.cs3733.d20.teamA.database;

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
    serviceDatabase.dropTables();
    boolean dropTables = serviceDatabase.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = serviceDatabase.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = serviceDatabase.dropTables();
    Assertions.assertTrue(dropTables2);
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
    serviceDatabase.createTables();
    serviceDatabase.removeAll();
    String a = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "brob", "");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("Laundry", "reqID", a));
    String b = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "rbob", "");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("Laundry", "reqID", b));
    /*
    int c = lDB.addLaundry("steve", "washing hall");
    Assertions.assertFalse(lDB.checkIfExistsInt("Laundry", "requestNum", c));
    int d = lDB.addLaundry("rbob", "notanode");
    Assertions.assertFalse(lDB.checkIfExistsInt("Laundry", "requestNum", d));

     */
  }

  @Test
  public void testDeleteLaundry() {
    serviceDatabase.createTables();
    serviceDatabase.removeAll();
    String a = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "brob", "");
    boolean b = serviceDatabase.deleteServReq(a);
    Assertions.assertTrue(b);
  }
  /*
   @Test
   public void testGetters() {
     serviceDatabase.createTables();
     serviceDatabase.removeAll();
     String a = serviceDatabase.addServiceReq(ServiceType.LAUNDRY, "washing hall", "brob", "");

     Assertions.assertEquals("brob", serviceDatabase.get);
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

  */
}
