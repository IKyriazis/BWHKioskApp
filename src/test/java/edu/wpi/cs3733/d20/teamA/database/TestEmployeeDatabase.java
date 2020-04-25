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

  public TestEmployeeDatabase() {}

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

  @Test
  public void testTables() {
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
  public void testAddEmployee() {
    eDB.createTables();
    eDB.removeAllEmployees();
    boolean a = eDB.addEmployee("abc", "brad", "bad", "password", "Janitor");
    Assertions.assertTrue(a);
    boolean d = eDB.addEmployee("abcd", "chad", "lad", "password", "Manager");
    Assertions.assertTrue(d);
    boolean b = eDB.addEmployee("ab", "kassy", "lassy", "password", "Surgeon");
    Assertions.assertTrue(b);
    boolean e = eDB.addEmployee("bacd", "ray", "jay", "password", "Intern");
    Assertions.assertTrue(e);
    Assertions.assertEquals(4, eDB.getSizeEmployees());
    eDB.removeAllEmployees();
  }

  @Test
  public void testDeleteEmployee() {
    eDB.createTables();
    eDB.removeAllEmployees();
    boolean a = eDB.addEmployee("abc", "brad", "bad", "password", "Nurse");
    Assertions.assertTrue(a);
    boolean b = eDB.deleteEmployee("bad");
    Assertions.assertTrue(b);
    Assertions.assertEquals(0, eDB.getSizeEmployees());
    eDB.addEmployee("bad", "brad", "abc", "password", "Nurse");
    eDB.addEmployee("bad", "brad", "dyi", "password", "Doctor Sleep");
    Assertions.assertEquals(2, eDB.getSizeEmployees());
    boolean c = eDB.deleteEmployee("abc");
    Assertions.assertTrue(c);
    Assertions.assertEquals(1, eDB.getSizeEmployees());
    eDB.addEmployee("bad", "brad", "abc", "password", "Nurse");
    Assertions.assertEquals(2, eDB.getSizeEmployees());
    boolean d = eDB.removeAllEmployees();
    Assertions.assertTrue(d);
    Assertions.assertEquals(0, eDB.getSizeEmployees());
  }

  @Test
  public void testEditTitle() {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "password", "Intern");
    boolean a = eDB.editTitle("bacd", "Doctor");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditFName() {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "password", "Intern");
    boolean a = eDB.editNameFirst("bacd", "cray");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditLName() {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "password", "Intern");
    boolean a = eDB.editNameLast("bacd", "kay");
    Assertions.assertTrue(a);
  }

  @Test
  public void testChangePass() {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "password", "Intern");
    boolean a = eDB.changePassword("jay", "password", "Is3");
    Assertions.assertTrue(a);
    boolean b = eDB.changePassword("jay", "Is33", "happy");
    Assertions.assertFalse(b);
    boolean c = eDB.changePassword("jay", "Is3", "IskkIg3");
    Assertions.assertTrue(c);
  }
}
