package edu.wpi.cs3733.d20.teamA.database.services.internalTransportRequest;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Campus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestInternalTransportDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase gDB;
  EmployeesDatabase employeesDatabase;
  ServiceDatabase serviceDatabase;

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      gDB = new GraphDatabase(conn);
      employeesDatabase = new EmployeesDatabase(conn);
      serviceDatabase = new ServiceDatabase(conn);
      employeesDatabase.addEmployee(
          "Yash", "Patel", "yppatel", "YashPatel1", EmployeeTitle.ADMIN, "7773326394l");
      employeesDatabase.logIn("yppatel", "YashPatel1");
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
    gDB.removeAllNodes(Campus.FAULKNER);
    // need nodeID "biscuit" in node table so addrequest works
    gDB.addNode(
        "biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    gDB.addNode("gravy", 2, 4, 2, "White House", "CONF", "basket", "b", "Team A", Campus.FAULKNER);
    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", null, "basket");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", a));
    Assertions.assertEquals(1, serviceDatabase.getSize());
    Assertions.assertEquals(1, serviceDatabase.getSize());
    String c =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", null, "balogna");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", c));
    Assertions.assertEquals(2, serviceDatabase.getSize());

    serviceDatabase.removeAll();
    gDB.removeAllNodes(Campus.FAULKNER);
  }

  @Test
  public void testUpdateRequest() {
    gDB.removeAllNodes(Campus.FAULKNER);
    gDB.addNode(
        "biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    gDB.addNode("gravy", 2, 4, 2, "White House", "CONF", "basket", "b", "Team A", Campus.FAULKNER);
    gDB.addNode("help", 2, 4, 2, "White House", "CONF", "water", "b", "Team A", Campus.FAULKNER);
    String a =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", null, "basket");
    boolean b = serviceDatabase.setDescription(a, "water");
    Assertions.assertTrue(b);

    serviceDatabase.removeAll();
    gDB.removeAllNodes(Campus.FAULKNER);
  }

  @Test
  public void testGetRequestStatus() {
    gDB.removeAllNodes(Campus.FAULKNER);
    gDB.addNode(
        "biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    gDB.addNode("help", 2, 4, 2, "White House", "CONF", "water", "b", "Team A", Campus.FAULKNER);
    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", null, "water");
    boolean b = serviceDatabase.setStatus(a, "In Progress");
    Assertions.assertTrue(
        serviceDatabase.checkIfExistsString("SERVICEREQ", "status", "In Progress"));
    Assertions.assertTrue(b);

    serviceDatabase.removeAll();
    gDB.removeAllNodes(Campus.FAULKNER);
  }
}
