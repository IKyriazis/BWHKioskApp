package edu.wpi.cs3733.d20.teamA.database.services;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.database.service.equipreq.EquipRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestEquipReqDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  EmployeesDatabase employeeDatabase;
  GraphDatabase graphDatabase;
  ServiceDatabase serviceDatabase;

  public TestEquipReqDatabase() {}

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
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  @Test
  public void testAddReq() {
    serviceDatabase.removeAll();
    employeeDatabase.removeAllLogs();
    graphDatabase.removeAll();
    Assertions.assertEquals(0, serviceDatabase.getSize());
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    employeeDatabase.addEmployee("bacd", "ray", "jay", "Password56", "Intern");
    serviceDatabase.addServiceReq(ServiceType.EQUIPMENT, "balogna", null, "item|2|High");
    Assertions.assertEquals(1, serviceDatabase.getSize());
    serviceDatabase.removeAll();
    employeeDatabase.removeAllLogs();
  }

  @Test
  public void testDelReq() {
    serviceDatabase.removeAll();
    graphDatabase.removeAll();
    Assertions.assertEquals(0, serviceDatabase.getSize());
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    employeeDatabase.addEmployee("bacd", "ray", "jay", "Password54", "Intern");
    employeeDatabase.addLog("jay");
    String a = serviceDatabase.addServiceReq(ServiceType.EQUIPMENT, "balogna", null, "item|2|High");
    Assertions.assertEquals(1, serviceDatabase.getSize());
    serviceDatabase.deleteServReq(a);
    Assertions.assertEquals(0, serviceDatabase.getSize());
    serviceDatabase.removeAll();
    employeeDatabase.removeAllLogs();
  }

  @Test
  public void testEquipObject() {
    EquipRequest eq =
        new EquipRequest(
            "Spider", "location", "Ave", new Timestamp(System.currentTimeMillis()), "Mask|2|Low");
    Assertions.assertEquals("Spider", eq.getID());
    Assertions.assertEquals("location", eq.getLocation());
    Assertions.assertEquals("Ave", eq.getName());
    Assertions.assertEquals("Low", eq.getPriority());
    Assertions.assertEquals(2, eq.getQty());
    Assertions.assertEquals("Mask", eq.getItem());
    Assertions.assertEquals(new SimpleStringProperty("location"), eq.locationProperty());
    Assertions.assertEquals(new SimpleStringProperty("Low"), eq.priorityProperty());
    Assertions.assertEquals(new SimpleStringProperty("Mask"), eq.itemProperty());
    Assertions.assertEquals(new SimpleStringProperty("Ave"), eq.nameProperty());
    Assertions.assertEquals(new SimpleIntegerProperty(2), eq.qtyProperty());
  }
}
