package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.database.service.medicine.MedRequest;
import edu.wpi.cs3733.d20.teamA.database.service.medicine.MedicineDeliveryDatabase;
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

  public TestMedRequestDatabase() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
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
  public void testAddRequest()  {
    serviceDatabase.createTables();
    serviceDatabase.removeAll();
    String a = serviceDatabase.addServiceReq(ServiceType.MEDICINE, "washing hall", "212", "Schmoe|Dr. Phil|Xanax");
    Assertions.assertEquals(1, serviceDatabase.getSize(ServiceType.MEDICINE));
    String d = serviceDatabase.addServiceReq(ServiceType.MEDICINE, "washing hall", "200", "Tom|Hank|Dr. Bob|Zoloft");
    Assertions.assertEquals(2, serviceDatabase.getSize(ServiceType.MEDICINE));
    serviceDatabase.removeAll();
  }

  @Test
  public void testUpdateMedicine() {
    serviceDatabase.createTables();
    serviceDatabase.removeAll();

    String d = serviceDatabase.addServiceReq(ServiceType.MEDICINE, "washing hall", "212", "Joe|Schmoe|Dr. Phil|Xanax");
    boolean a = serviceDatabase.setAdditional(d, "Joe|Schmoe|Dr. Phil|help");
    Assertions.assertTrue(a);
    serviceDatabase.removeAll();
  }


  @Test
  public void testUpdateProgress(){
    serviceDatabase.createTables();
    serviceDatabase.removeAll();

    String a = serviceDatabase.addServiceReq(ServiceType.MEDICINE, "washing hall", "212", "Schmoe|Dr. Phil|Xanax");
    boolean b = serviceDatabase.setStatus(a, "Done");
    Assertions.assertTrue(b);

    serviceDatabase.removeAll();
  }

  /*
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

   */

  @Test
  public void testMedRequest() {
    MedRequest request =
        new MedRequest(
            "1",
            "Done",
            "FIRST NAME|LASTNAME|DOCTOR|MEDICATION|ROOMNUM",
            "211",
            new Timestamp(0),
            "joe");
    Assertions.assertEquals("FIRST NAME", request.getFirstName());
    Assertions.assertEquals("LAST NAME", request.getLastName());
    Assertions.assertEquals("DOCTOR", request.getDoctor());
    Assertions.assertEquals("MEDICATION", request.getMedicine());
    Assertions.assertEquals(211, request.getRoomNum());
    Assertions.assertEquals("Done", request.getProgress());
    Assertions.assertEquals("joe", request.getFulfilledBy());
  }
}
