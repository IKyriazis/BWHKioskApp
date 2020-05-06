package edu.wpi.cs3733.d20.teamA.database.reservation;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestReservationDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  ReservationDatabase rDB;
  EmployeesDatabase eDB;

  public TestReservationDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    eDB = new EmployeesDatabase(conn);
    rDB = new ReservationDatabase(conn);
    eDB.addEmployee("Bob", "Roberts", "brob", "AbCd1234", EmployeeTitle.ADMIN);
    eDB.addEmployee("Rob", "Boberts", "rbob", "1234aBcD", EmployeeTitle.ADMIN);
    eDB.addEmployee("Yash", "Patel", "yppatel", "YashPatel1", EmployeeTitle.ADMIN);
    eDB.logIn("yppatel", "YashPatel1");
  }

  @AfterEach
  public void teardown() {
    try {
      rDB.dropTables();
      eDB.dropTables();
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  @Test
  public void testTables() {
    rDB.dropTables();
    boolean dropTables = rDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = rDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = rDB.dropTables();
    Assertions.assertTrue(dropTables2);
  }

  @Test
  public void testGetSize() {
    Assertions.assertEquals(0, rDB.getSize());
    rDB.addRes(Calendar.getInstance(), Calendar.getInstance(), "Bed2");
    Assertions.assertEquals(1, rDB.getSize());
  }

  @Test
  public void testAddRes() {
    Assertions.assertTrue(rDB.addRes(Calendar.getInstance(), Calendar.getInstance(), "Bed2"));
    Assertions.assertTrue(rDB.addRes(Calendar.getInstance(), Calendar.getInstance(), "Bed3"));
    Assertions.assertEquals(2, rDB.getSize());
  }

  @Test
  public void testDeleteRes() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2020);
    calendar.set(Calendar.MONTH, 2);
    calendar.set(Calendar.DAY_OF_MONTH, 15);
    calendar.set(Calendar.HOUR, 12);
    calendar.set(Calendar.MINUTE, 42);
    calendar.set(Calendar.SECOND, 30);
    calendar.set(Calendar.MILLISECOND, 0);
    rDB.addRes(calendar, Calendar.getInstance(), "Bed2");
    rDB.addRes(calendar, Calendar.getInstance(), "Bed3");
    Assertions.assertTrue(rDB.deleteRes(calendar, "Bed2"));
    Assertions.assertEquals(1, rDB.getSize());
  }

  @Test
  public void testGetters() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2020);
    calendar.set(Calendar.MONTH, 2);
    calendar.set(Calendar.DAY_OF_MONTH, 15);
    calendar.set(Calendar.HOUR, 12);
    calendar.set(Calendar.MINUTE, 42);
    calendar.set(Calendar.SECOND, 30);
    calendar.set(Calendar.MILLISECOND, 0);
    rDB.addRes(calendar, Calendar.getInstance(), "Bed2");
    Assertions.assertEquals("yppatel", rDB.getRequestedBy(calendar, "Bed2"));
    Assertions.assertNotNull(rDB.getEndTime(calendar, "Bed2"));
  }

  @Test
  public void testSetters() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2020);
    calendar.set(Calendar.MONTH, 2);
    calendar.set(Calendar.DAY_OF_MONTH, 15);
    calendar.set(Calendar.HOUR, 12);
    calendar.set(Calendar.MINUTE, 42);
    calendar.set(Calendar.SECOND, 30);
    calendar.set(Calendar.MILLISECOND, 0);
    rDB.addRes(calendar, Calendar.getInstance(), "Bed2");
    Assertions.assertTrue(rDB.setRequestedBy(calendar, "Bed2", "brob"));
    Assertions.assertTrue(rDB.setEndTime(calendar, "Bed2", Calendar.getInstance()));
  }
}
