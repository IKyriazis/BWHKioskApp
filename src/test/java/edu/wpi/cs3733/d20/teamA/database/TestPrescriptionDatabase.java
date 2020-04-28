package edu.wpi.cs3733.d20.teamA.database;

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
  PrescriptionDatabase prescriptionDatabase;

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      employeesDatabase = new EmployeesDatabase(conn);
      prescriptionDatabase = new PrescriptionDatabase(conn);
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
  public void testTables() {
    prescriptionDatabase.dropTables();
    boolean dropTables = prescriptionDatabase.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = prescriptionDatabase.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = prescriptionDatabase.dropTables();
    Assertions.assertTrue(dropTables2);
    prescriptionDatabase.createTables();
  }

  @Test
  public void testAddPrescription() {
    employeesDatabase.removeAllEmployees();
    employeesDatabase.addEmployee("Yash", "Patel", "yppatel", "Passwords123", "employee");
    prescriptionDatabase.removeAllPrescriptions();
    prescriptionDatabase.addPrescription(
        1, "Yash", "Ketamin", "CVS", "2 pills", 3, "yppatel", "Don't Die");
    Assertions.assertEquals(1, prescriptionDatabase.getSizePrescription());
    Assertions.assertEquals("Ketamin", prescriptionDatabase.getPrescription("Yash"));
    Assertions.assertEquals("CVS", prescriptionDatabase.getPharmacy("Yash"));
    Assertions.assertEquals("2 pills", prescriptionDatabase.getDosage("Yash"));
    Assertions.assertEquals(3, prescriptionDatabase.getNumRefills("Yash"));
    Assertions.assertEquals("Don't Die", prescriptionDatabase.getNotes("Yash"));
    Assertions.assertEquals("Yash Patel", prescriptionDatabase.getDoctor("Yash"));
  }

  @Test
  public void modifyPrescription() {
    employeesDatabase.removeAllEmployees();
    employeesDatabase.readEmployeeCSV();
    prescriptionDatabase.removeAllPrescriptions();
    Assertions.assertTrue(prescriptionDatabase.readPrescriptionCSV());
    Assertions.assertTrue(prescriptionDatabase.setPatient(1, "Jacob White"));
    Assertions.assertEquals("Jacob White", prescriptionDatabase.getPatient(1));
    Assertions.assertTrue(prescriptionDatabase.setPrescription("Jacob White", "Bengay"));
    Assertions.assertEquals("Bengay", prescriptionDatabase.getPrescription("Jacob White"));
    Assertions.assertTrue(prescriptionDatabase.setPharmacy("Jacob White", "Walgreens"));
    Assertions.assertEquals("Walgreens", prescriptionDatabase.getPharmacy("Jacob White"));
    Assertions.assertTrue(prescriptionDatabase.setDosage("Jacob White", "80 pills"));
    Assertions.assertEquals("80 pills", prescriptionDatabase.getDosage("Jacob White"));
    Assertions.assertTrue(prescriptionDatabase.setNumberOfRefills("Jacob White", 7));
    Assertions.assertEquals(7, prescriptionDatabase.getNumRefills("Jacob White"));
    Assertions.assertTrue(prescriptionDatabase.setDoctorUsername("Jacob White", "staff"));
    Assertions.assertEquals("Yash Patel", prescriptionDatabase.getDoctor("Jacob White"));
    Assertions.assertTrue(prescriptionDatabase.setNotes("Jacob White", "UGHH"));
    Assertions.assertEquals("UGHH", prescriptionDatabase.getNotes("Jacob White"));
  }

  @Test
  public void createPrescriptionObject() {
    Prescription p =
        new Prescription(1, "Yash", "Ketamin", "CVS", "2 pills", 3, "yppatel", "Don't Die");
    Assertions.assertEquals(1, p.getPrescriptionID());
    Assertions.assertEquals("Yash", p.getPatientName());
    Assertions.assertEquals("Ketamin", p.getPrescription());
    Assertions.assertEquals("CVS", p.getPharmacy());
    Assertions.assertEquals("2 pills", p.getDosage());
    Assertions.assertEquals(3, p.getNumberOfRefills());
    Assertions.assertEquals("yppatel", p.getDoctorName());
    Assertions.assertEquals("Don't Die", p.getNotes());
  }
}
