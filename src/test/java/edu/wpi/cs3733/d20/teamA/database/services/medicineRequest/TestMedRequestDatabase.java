package edu.wpi.cs3733.d20.teamA.database.services.medicineRequest;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.database.service.medicine.MedRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMedRequestDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  ServiceDatabase serviceDatabase;
  EmployeesDatabase employeesDatabase;
  GraphDatabase graphDatabase;

  public TestMedRequestDatabase() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      graphDatabase = new GraphDatabase(conn);
      graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
      employeesDatabase = new EmployeesDatabase(conn);
      employeesDatabase.addEmployee("Yash", "Patel", "yppatel", "YashPatel1", EmployeeTitle.ADMIN, 8837726619l);
      employeesDatabase.logIn("yppatel", "YashPatel1");
      serviceDatabase = new ServiceDatabase(conn);
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
  public void testAddRequest() {
    serviceDatabase.createTables();
    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(
            ServiceType.MEDICINE, "balogna", "212", "Schmoe|Dr. Phil|Xanax");
    Assertions.assertEquals(1, serviceDatabase.getSize(ServiceType.MEDICINE));
    String d =
        serviceDatabase.addServiceReq(
            ServiceType.MEDICINE, "balogna", "200", "Tom|Hank|Dr. Bob|Zoloft");
    Assertions.assertEquals(2, serviceDatabase.getSize(ServiceType.MEDICINE));
    serviceDatabase.removeAll();
  }

  @Test
  public void testUpdateMedicine() {
    serviceDatabase.createTables();
    serviceDatabase.removeAll();

    String d =
        serviceDatabase.addServiceReq(
            ServiceType.MEDICINE, "washing hall", "212", "Joe|Schmoe|Dr. Phil|Xanax");
    boolean a = serviceDatabase.setAdditional(d, "Joe|Schmoe|Dr. Phil|help");
    Assertions.assertTrue(a);
    serviceDatabase.removeAll();
  }

  @Test
  public void testUpdateProgress() {
    serviceDatabase.createTables();
    serviceDatabase.removeAll();

    String a =
        serviceDatabase.addServiceReq(
            ServiceType.MEDICINE, "washing hall", "212", "Schmoe|Dr. Phil|Xanax");
    boolean b = serviceDatabase.setStatus(a, "Done");
    Assertions.assertTrue(b);

    serviceDatabase.removeAll();
  }

  @Test
  public void testMedRequest() {
    MedRequest request =
        new MedRequest(
            "1", "Done", "FIRSTNAME_LASTNAME|DOCTOR|MEDICATION", "211", new Timestamp(0), "joe");
    Assertions.assertEquals("DOCTOR", request.getDoctor());
    Assertions.assertEquals("MEDICATION", request.getMedicine());
    Assertions.assertEquals(211, request.getRoomNum());
    Assertions.assertEquals("Done", request.getProgress());
    Assertions.assertEquals("joe", request.getFulfilledBy());
  }
}
