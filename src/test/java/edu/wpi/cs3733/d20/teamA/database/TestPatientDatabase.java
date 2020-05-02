package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import edu.wpi.cs3733.d20.teamA.database.patient.Patient;
import edu.wpi.cs3733.d20.teamA.database.patient.PatientDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPatientDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  PatientDatabase pDB;

  public TestPatientDatabase() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      pDB = new PatientDatabase(conn);
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
    pDB.dropTables();
    boolean dropTables = pDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = pDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = pDB.dropTables();
    Assertions.assertTrue(dropTables2);
    pDB.createTables();
  }

  @Test
  public void testAddPatient() throws SQLException {
    pDB.removeAllPatients();
    int a = pDB.addPatient(0, "Tyler", "Looney", "Health Insurance", "01/01/2000");
    Assertions.assertEquals(0, a);
    int d = pDB.addPatient(1, "Yash", "Patel", "Yash Health", "01/02/2000");
    Assertions.assertEquals(1, d);
    int b = pDB.addPatient(0, "Eva", "Labbe", "Eva Health", "01/03/2000");
    Assertions.assertEquals(-1, b);
    int e = pDB.addPatient(2, "Brennan", "Aubuchon", "Brennan Insurance", "01/04/2000");
    Assertions.assertEquals(2, e);
    int c = pDB.addPatient(-1, "Lily", "Green", "Lily Insurance", "01/04/2000");
    Assertions.assertEquals(-1, c);
    Assertions.assertEquals(3, pDB.getSizePatients());
    pDB.removeAllPatients();
  }

  @Test
  public void testDeletePatient() throws SQLException {
    pDB.createTables();
    pDB.removeAllPatients();
    pDB.addPatient(1, "Rose", "Yellow", "Flower health", "01/04/2000");
    pDB.deletePatient(1);
    Assertions.assertEquals(0, pDB.getSizePatients());
    pDB.removeAllPatients();
  }

  @Test
  public void testPatient() {
    Patient patient = new Patient(1, "Daisy", "Blue", "health", "01/01/2000");
    Assertions.assertEquals(1, patient.getPatientID());
    Assertions.assertEquals("Daisy", patient.getFirstName());
    Assertions.assertEquals("Blue", patient.getLastName());
    Assertions.assertEquals("health", patient.getHealthInsurance());
    Assertions.assertEquals("01/01/2000", patient.getDateOfBirth());
  }
}
