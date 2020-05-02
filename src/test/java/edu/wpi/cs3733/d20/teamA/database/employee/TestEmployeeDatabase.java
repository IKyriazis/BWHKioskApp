package edu.wpi.cs3733.d20.teamA.database.employee;

import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
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
  EmployeesDatabase employeeDatabase;
  GraphDatabase graphDatabase;

  public TestEmployeeDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    graphDatabase = new GraphDatabase(conn);
    employeeDatabase = new EmployeesDatabase(conn);
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
    employeeDatabase.dropTables();
    boolean dropTables = employeeDatabase.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = employeeDatabase.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = employeeDatabase.dropTables();
    Assertions.assertTrue(dropTables2);
    employeeDatabase.createTables();
  }

  @Test
  public void testAddEmployee() {
    employeeDatabase.removeAllEmployees();
    String a = employeeDatabase.addEmployee("abc", "brad", "bad", "passwordH2", "Janitor");
    Assertions.assertEquals(a, employeeDatabase.getEmployeeID("bad"));
    Assertions.assertEquals(1, employeeDatabase.getSize());

    String d = employeeDatabase.addEmployee("abcd", "chad", "lad", "passwordH2", "Manager");
    Assertions.assertEquals(d, employeeDatabase.getEmployeeID("lad"));
    Assertions.assertEquals(2, employeeDatabase.getSize());

    String b = employeeDatabase.addEmployee("ab", "kassy", "lassy", "passwordH2", "Surgeon");
    Assertions.assertEquals(b, employeeDatabase.getEmployeeID("lassy"));
    Assertions.assertEquals(3, employeeDatabase.getSize());

    String e = employeeDatabase.addEmployee("bacd", "ray", "jay", "passwordH2", "Intern");
    Assertions.assertEquals(e, employeeDatabase.getEmployeeID("jay"));
    Assertions.assertEquals(4, employeeDatabase.getSize());
    String f = employeeDatabase.addEmployee("bacd", "ray", "jay", "a".repeat(90), "Intern");
    Assertions.assertEquals("", f);
    Assertions.assertEquals(4, employeeDatabase.getSize());
    employeeDatabase.removeAllEmployees();
  }

  @Test
  public void testDeleteEmployee() {
    employeeDatabase.removeAllEmployees();
    String a = employeeDatabase.addEmployee("abc", "brad", "bad", "passwordA2", "Nurse");
    boolean b = employeeDatabase.deleteEmployee("bad");
    Assertions.assertTrue(b);
    Assertions.assertEquals(0, employeeDatabase.getSize());
    employeeDatabase.addEmployee("bad", "brad", "abc", "passwordA2", "Nurse");
    employeeDatabase.addEmployee("bad", "brad", "dyi", "passwordA2", "Doctor Sleep");
    Assertions.assertEquals(2, employeeDatabase.getSize());
    boolean e = employeeDatabase.deleteEmployee("bad");
    Assertions.assertTrue(e);
    Assertions.assertEquals(2, employeeDatabase.getSize());
    employeeDatabase.addEmployee("bad", "brad", "abc", "password", "Nurse");
    Assertions.assertEquals(2, employeeDatabase.getSize());
    boolean d = employeeDatabase.removeAllEmployees();
    Assertions.assertTrue(d);
    Assertions.assertEquals(0, employeeDatabase.getSize());
  }

  @Test
  public void testEditTitle() {
    employeeDatabase.removeAllEmployees();
    employeeDatabase.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = employeeDatabase.editTitle("bacd", "Doctor");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditFName() {
    employeeDatabase.removeAllEmployees();
    employeeDatabase.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = employeeDatabase.editNameFirst("bacd", "cray");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditLName() {
    employeeDatabase.removeAllEmployees();
    employeeDatabase.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = employeeDatabase.editNameLast("bacd", "kay");
    Assertions.assertTrue(a);
  }

  @Test
  public void testChangePass() {
    employeeDatabase.removeAllEmployees();
    employeeDatabase.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = employeeDatabase.changePassword("jay", "passwordA2", "Is3");
    Assertions.assertTrue(a);
    boolean b = employeeDatabase.changePassword("jay", "Is33", "happy");
    Assertions.assertFalse(b);
    boolean c = employeeDatabase.changePassword("jay", "Is3", "IskkIg3");
    Assertions.assertTrue(c);
  }

  @Test
  public void testAddLog() {
    employeeDatabase.removeAllLogs();
    graphDatabase.removeAll();
    Assertions.assertEquals(0, employeeDatabase.getSizeLog());
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    employeeDatabase.addEmployee("bacd", "ray", "jay", "Password69", "Intern");
    employeeDatabase.addLog("jay");
    Assertions.assertEquals(1, employeeDatabase.getSizeLog());
    employeeDatabase.removeAllLogs();
    graphDatabase.removeAll();
  }

  @Test
  public void testChangeFlag() {
    employeeDatabase.removeAllLogs();
    graphDatabase.removeAll();
    Assertions.assertEquals(0, employeeDatabase.getSizeLog());
    graphDatabase.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    employeeDatabase.addEmployee("bacd", "ray", "jay", "Password87", "Intern");
    employeeDatabase.addLog("jay");
    Assertions.assertEquals(1, employeeDatabase.getSizeLog());
    boolean b = employeeDatabase.isOnline("jay");
    Assertions.assertTrue(b);
    employeeDatabase.changeFlag();
    boolean a = employeeDatabase.isOnline("jay");
    Assertions.assertFalse(a);
    employeeDatabase.removeAllLogs();
    graphDatabase.removeAll();
  }

  @Test
  public void testUNameExists() {
    employeeDatabase.createTables();
    employeeDatabase.removeAllEmployees();
    employeeDatabase.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    Assertions.assertTrue(employeeDatabase.uNameExists("jay"));
    employeeDatabase.addEmployee("bacd", "ray", "play", "passwordA2", "Intern");
    Assertions.assertTrue(employeeDatabase.uNameExists("play"));
    employeeDatabase.addEmployee("bacd", "ray", "cray", "passwordA2", "Intern");
    Assertions.assertTrue(employeeDatabase.uNameExists("cray"));
    Assertions.assertFalse(employeeDatabase.uNameExists("asdfj"));
    Assertions.assertFalse(employeeDatabase.uNameExists("askldjf"));
  }

  @Test
  public void testLogin() {
    employeeDatabase.createTables();
    employeeDatabase.removeAllEmployees();
    employeeDatabase.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    employeeDatabase.addEmployee("bacd", "ray", "play", "passwordA2", "Intern");
    employeeDatabase.addEmployee("bacd", "ray", "cray", "passwordA2", "Intern");
    Assertions.assertTrue(employeeDatabase.logIn("jay", "passwordA2"));
    Assertions.assertTrue(employeeDatabase.logIn("play", "passwordA2"));
    Assertions.assertTrue(employeeDatabase.logIn("play", "passwordA2"));
    Assertions.assertFalse(employeeDatabase.logIn("play", "passworasdfdA2"));
    Assertions.assertFalse(employeeDatabase.logIn("plaasdfsdafy", "passwordA2"));
  }
}
