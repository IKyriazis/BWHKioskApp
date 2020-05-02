package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.service.medicine.MedRequest;
import edu.wpi.cs3733.d20.teamA.database.service.medicine.MedicineDeliveryDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMedRequestDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  MedicineDeliveryDatabase mDB;

  public TestMedRequestDatabase() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      mDB = new MedicineDeliveryDatabase(conn);
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
    mDB.dropTables();
    boolean dropTables = mDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = mDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = mDB.dropTables();
    Assertions.assertTrue(dropTables2);
    mDB.createTables();
  }

  @Test
  public void testAddRequest() throws SQLException {
    mDB.createTables();
    mDB.removeAll();
    boolean a = mDB.addRequest("Joe", "Schmoe", "Dr. Phil", "Xanax", 212);
    Assertions.assertTrue(a);
    boolean d = mDB.addRequest("Tom", "Hank", "Dr. Bob", "Zoloft", 7, 7, 45);
    Assertions.assertTrue(d);
    boolean b = mDB.addRequest("Joe", "Steve", "Dr. Phil", "Xanax", 233, -1, -13);
    Assertions.assertFalse(b);

    Assertions.assertEquals(2, mDB.getRequestSize());
    mDB.removeAll();
  }

  @Test
  public void testUpdateMedicine() throws SQLException {
    mDB.createTables();
    mDB.removeAll();

    mDB.addRequest("Joe", "Schmoe", "Dr. Phil", "Xanax", 212);
    boolean a = mDB.updateMedicine("JoeSchmoeXanax212", "help");
    Assertions.assertTrue(a);
    mDB.removeAll();
  }

  @Test
  public void testUpdateDoctor() throws SQLException {
    mDB.createTables();
    mDB.removeAll();

    mDB.addRequest("Joe", "Schmoe", "Dr. Phil", "Xanax", 212);
    boolean a = mDB.updateDoctor("JoeSchmoeXanax212", "Dr. Brown");
    Assertions.assertTrue(a);
    mDB.removeAll();
  }

  @Test
  public void testUpdateProgress() throws SQLException {
    mDB.createTables();
    mDB.removeAll();

    mDB.addRequest("Joe", "Schmoe", "Dr. Phil", "Xanax", 212);
    boolean a = mDB.updateProgress("JoeSchmoeXanax212", "");
    Assertions.assertFalse(a);

    boolean b = mDB.updateProgress("JoeSchmoeXanax212", "Done");
    Assertions.assertTrue(b);

    mDB.removeAll();
  }

  @Test
  public void testUpdateTime() throws SQLException {
    mDB.createTables();
    mDB.removeAll();

    mDB.addRequest("Joe", "Schmoe", "Dr. Phil", "Xanax", 212);
    boolean a = mDB.updateHo("JoeSchmoeXanax212", -2);
    Assertions.assertFalse(a);

    boolean c = mDB.updateHo("JoeSchmoeXanax212", 3);
    Assertions.assertTrue(c);

    boolean b = mDB.updateMins("JoeSchmoeXanax212", -6);
    Assertions.assertFalse(b);

    boolean d = mDB.updateMins("JoeSchmoeXanax212", 22);
    Assertions.assertTrue(d);

    mDB.removeAll();
  }

  @Test
  public void testMedRequest() {
    MedRequest request =
        new MedRequest(
            "JoeSchmoeXanax211",
            "Joe",
            "Schmoe",
            "Dr.Phil",
            "Xanax",
            211,
            "Dispatched",
            5,
            25,
            "Tom");
    Assertions.assertEquals("Joe", request.getFirstName());
    Assertions.assertEquals("Schmoe", request.getLastName());
    Assertions.assertEquals("Dr.Phil", request.getDoctor());
    Assertions.assertEquals("Xanax", request.getMedicine());
    Assertions.assertEquals(211, request.getRoomNum());
    Assertions.assertEquals("Dispatched", request.getProgress());
    Assertions.assertEquals("Tom", request.getFulfilledBy());
  }
}
