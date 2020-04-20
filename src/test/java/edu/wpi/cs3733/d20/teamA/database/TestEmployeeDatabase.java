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
    boolean a = eDB.addEmployee("abc", "brad", "bad", "Janitor");
    Assertions.assertTrue(a);
    boolean d = eDB.addEmployee("abcd", "chad", "lad", "Manager");
    Assertions.assertTrue(d);
    boolean b = eDB.addEmployee("ab", "kassy", "lassy", "Surgeon");
    Assertions.assertTrue(b);
    boolean e = eDB.addEmployee("bacd", "ray", "jay", "Intern");
    Assertions.assertTrue(e);
    boolean c = eDB.addEmployee("abcd", "bailey", "kaylee", "Nurse");
    Assertions.assertFalse(c);
    Assertions.assertEquals(4, eDB.getSizeEmployees());
    eDB.removeAllEmployees();
  }

  @Test
  public void testDeleteEmployee() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    boolean a = eDB.addEmployee("abc", "brad", "bad", "Nurse");
    Assertions.assertTrue(a);
    boolean b = eDB.deleteEmployee("abc");
    Assertions.assertTrue(b);
    Assertions.assertEquals(0, eDB.getSizeEmployees());
    eDB.addEmployee("abc", "brad", "bad", "Nurse");
    eDB.addEmployee("dyi", "brad", "bad", "Doctor Sleep");
    Assertions.assertEquals(2, eDB.getSizeEmployees());
    boolean c = eDB.deleteEmployee("abc");
    Assertions.assertTrue(c);
    Assertions.assertEquals(1, eDB.getSizeEmployees());
  }

  @Test
  public void testEditTitle() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "Intern");
    boolean a = eDB.editTitle("bacd", "Doctor");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditFName() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "Intern");
    boolean a = eDB.editNameFirst("bacd", "cray");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditLName() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "Intern");
    boolean a = eDB.editNameLast("bacd", "kay");
    Assertions.assertTrue(a);
  }

  @Test
  public void testChangePass() throws SQLException {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "Intern");
    boolean a = eDB.changePassword("bacd", "bacd", "Is3");
    Assertions.assertTrue(a);
    boolean b = eDB.changePassword("bacd", "Is3", "happy");
    Assertions.assertFalse(b);
    boolean c = eDB.changePassword("bacd", "Is3", "IskkIg3");
    Assertions.assertTrue(c);
  }
}
