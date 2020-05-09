package edu.wpi.cs3733.d20.teamA.database.services.prescription;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.database.service.prescription.Prescription;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPrescriptionDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  EmployeesDatabase employeesDatabase;
  GraphDatabase graphDatabase;
  ServiceDatabase serviceDatabase;

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      graphDatabase = new GraphDatabase(conn);
      employeesDatabase = new EmployeesDatabase(conn);
      employeesDatabase.addEmployee(
          "Yash", "Patel", "yppatel", "YashPatel1", EmployeeTitle.ADMIN, "8887493372l");
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
  public void testAddPrescription() {
    serviceDatabase.removeAll();
    // Additional : patientName|prescription|pharmacy|dosage|numberOfRefills|notes
    String a =
        serviceDatabase.addServiceReq(
            ServiceType.PRESCRIPTION, null, "", "Yash|Ketamin|CVS|2 pills|3|Don't Die");
    Assertions.assertEquals(1, serviceDatabase.getSize());
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", a));
  }

  @Test
  public void modifyPrescription() {
    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(
            ServiceType.PRESCRIPTION, null, "", "Yash|Ketamin|CVS|2 pills|3|Don't Die");
    serviceDatabase.setAdditional(a, "Sarah|hummus|Walgreens|2 scoops|4|With Chips");
    Assertions.assertEquals(
        "Sarah|hummus|Walgreens|2 scoops|4|With Chips", serviceDatabase.getAdditional(a));
  }

  @Test
  public void createPrescriptionObject() {
    Prescription p = new Prescription("1", "yppatel", "Yash|Ketamin|CVS|2 pills|3|Don't Die");
    Assertions.assertEquals("1", p.getPrescriptionID());
    Assertions.assertEquals("Yash", p.getPatientName());
    Assertions.assertEquals("Ketamin", p.getPrescription());
    Assertions.assertEquals("CVS", p.getPharmacy());
    Assertions.assertEquals("2 pills", p.getDosage());
    Assertions.assertEquals(3, p.getNumberOfRefills());
    Assertions.assertEquals("yppatel", p.getDoctorName());
    Assertions.assertEquals("Don't Die", p.getNotes());
  }
}
