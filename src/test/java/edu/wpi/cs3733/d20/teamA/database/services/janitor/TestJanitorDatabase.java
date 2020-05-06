package edu.wpi.cs3733.d20.teamA.database.services.janitor;

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

public class TestJanitorDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase graphDatabase;
  ServiceDatabase serviceDatabase;
  EmployeesDatabase employeeDatabase;

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    graphDatabase = new GraphDatabase(conn);
    employeeDatabase = new EmployeesDatabase(conn);
    serviceDatabase = new ServiceDatabase(conn);
  }

  @AfterEach
  public void teardown() {
    try {
      graphDatabase.dropTables();
      serviceDatabase.dropTables();
      employeeDatabase.dropTables();
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  @Test
  public void testAddRequest() throws SQLException {

    graphDatabase.removeAllNodes(Campus.FAULKNER);
    employeeDatabase.removeAll();
    // need nodeID "biscuit" in node table so addrequest works
    graphDatabase.addNode(
        "biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    employeeDatabase.addEmployee(
        "Yash", "Patel", "yppatel", "Superman1", EmployeeTitle.ADMIN, 7736499283l);
    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(
            ServiceType.JANITOR, "balogna", "yppatel", "Request Made", "");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", a));

    serviceDatabase.removeAll();
    graphDatabase.removeAllNodes(Campus.FAULKNER);
  }

  @Test
  public void testUpdateRequest() {

    graphDatabase.removeAllNodes(Campus.FAULKNER);
    graphDatabase.addNode(
        "biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A", Campus.FAULKNER);
    employeeDatabase.addEmployee(
        "Yash", "Patel", "yppatel", "Superman1", EmployeeTitle.ADMIN, 8847736283l);
    employeeDatabase.addEmployee(
        "Nisha", "Goel", "ngoel", "SweetGirl2", EmployeeTitle.ADMIN, 6635266683l);

    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(
            ServiceType.JANITOR, "balogna", "yppatel", "Request Made", "");
    boolean b = serviceDatabase.setAssignedEmployee(a, "ngoel");
    Assertions.assertTrue(b);
    String c = serviceDatabase.getStatus(a);
    Assertions.assertEquals(c, "Request Made");
    Assertions.assertEquals(1, serviceDatabase.getSize());
    serviceDatabase.deleteServReq(a);
    Assertions.assertEquals(0, serviceDatabase.getSize());

    serviceDatabase.removeAll();
    graphDatabase.removeAllNodes(Campus.FAULKNER);
  }
}
