package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestEmployeeDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  EmployeesDatabase eDB;

  public TestEmployeeDatabase() throws SQLException {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    eDB = new EmployeesDatabase(conn);
  }

  @AfterEach
  public void teardown() {
    try {
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  // @Test
  public void testDB() throws SQLException {
    boolean test = false;
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      test = true;
    } catch (SQLException e) {

    }
    Assertions.assertTrue(test);
  }

  @Test
  public void testTables() throws SQLException {
    eDB.dropTables();
    boolean dropTables = eDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = eDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = eDB.dropTables();
    Assertions.assertTrue(dropTables2);
    eDB.createTables();
  }

  @Test
  public void testAddEmployee() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    boolean a = eDB.addEmployee(1, "abc", "brad", "bad", "123", "Janitor");
    Assertions.assertTrue(a);
    boolean d = eDB.addEmployee(2, "abcd", "chad", "lad", "456", "Manager");
    Assertions.assertTrue(d);
    boolean b = eDB.addEmployee(3, "ab", "kassy", "lassy", "789", "Surgeon");
    Assertions.assertTrue(b);
    boolean e = eDB.addEmployee(4, "bacd", "ray", "jay", "banana", "Intern");
    Assertions.assertTrue(e);
    boolean c = eDB.addEmployee(2, "abcd", "bailey", "kaylee", "blah", "Nurse");
    Assertions.assertFalse(c);
    Assertions.assertEquals(4, eDB.getSizeEmployees());
    eDB.removeAllEmployees();
  }

  @Test
  public void testDeleteEmployee() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    boolean a = eDB.addEmployee(1, "abc", "brad", "bad", "123", "Janitor");
    Assertions.assertTrue(a);
    boolean b = eDB.deleteEmployee("bad");
    Assertions.assertTrue(b);
    Assertions.assertEquals(0, eDB.getSizeEmployees());
    eDB.addEmployee(1, "abc", "brad", "bad", "123", "Janitor");
    eDB.addEmployee(2, "abcd", "chad", "lad", "456", "Manager");
    Assertions.assertEquals(2, eDB.getSizeEmployees());
    boolean e = eDB.deleteEmployee("bad");
    Assertions.assertTrue(e);
    Assertions.assertEquals(1, eDB.getSizeEmployees());
  }

  @Test
  public void testEditTitle() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee(3, "ab", "kassy", "lassy", "789", "Intern");
    boolean a = eDB.editTitle("lassy", "Doctor");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditFName() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee(3, "ab", "kassy", "lassy", "789", "Intern");
    boolean a = eDB.editNameFirst("lassy", "cray");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditLName() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee(3, "ab", "kassy", "lassy", "789", "Intern");
    boolean a = eDB.editNameLast("lassy", "kay");
    Assertions.assertTrue(a);
  }

  @Test
  public void testChangePass() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee(3, "ab", "kassy", "lassy", "789", "Intern");
    boolean a = eDB.changePassword("lassy", "789", "Is3");
    Assertions.assertTrue(a);
    boolean b = eDB.changePassword("lassy", "Is3", "happy");
    Assertions.assertFalse(b);
    boolean c = eDB.changePassword("lassy", "Is3", "IskkIg3");
    Assertions.assertTrue(c);
  }
}
