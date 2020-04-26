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
    employeesDatabase.addEmployee("Yash", "Patel", "yppatel", "1234", "employee");
    prescriptionDatabase.removeAllPrescriptions();
    prescriptionDatabase.addPrescription(
        1, "Yash", "Ketamin", "CVS", "2 pills", 3, "WEEK", "yppatel", "Don't Die");
    Assertions.assertEquals(1, prescriptionDatabase.getSizePrescription());
  }
}
