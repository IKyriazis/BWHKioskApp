package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
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
  GraphDatabase DB;

  public TestEmployeeDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    DB = new GraphDatabase(conn);
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
    eDB.removeAllEmployees();
    boolean a = eDB.addEmployee("abc", "brad", "bad", "passwordH2", "Janitor");
    Assertions.assertTrue(a);
    boolean d = eDB.addEmployee("abcd", "chad", "lad", "passwordH2", "Manager");
    Assertions.assertTrue(d);
    boolean b = eDB.addEmployee("ab", "kassy", "lassy", "passwordH2", "Surgeon");
    Assertions.assertTrue(b);
    boolean e = eDB.addEmployee("bacd", "ray", "jay", "passwordH2", "Intern");
    Assertions.assertTrue(e);
    boolean f = eDB.addEmployee("bacd", "ray", "jay", "a".repeat(90), "Intern");
    Assertions.assertFalse(f);
    Assertions.assertEquals(4, eDB.getSizeEmployees());
    eDB.removeAllEmployees();
  }

  @Test
  public void testDeleteEmployee() {
    eDB.removeAllEmployees();
    boolean a = eDB.addEmployee("abc", "brad", "bad", "passwordA2", "Nurse");
    Assertions.assertTrue(a);
    boolean b = eDB.deleteEmployee("bad");
    Assertions.assertTrue(b);
    Assertions.assertEquals(0, eDB.getSizeEmployees());
    eDB.addEmployee("bad", "brad", "abc", "passwordA2", "Nurse");
    eDB.addEmployee("bad", "brad", "dyi", "passwordA2", "Doctor Sleep");
    Assertions.assertEquals(2, eDB.getSizeEmployees());
    boolean e = eDB.deleteEmployee("bad");
    Assertions.assertTrue(e);
    Assertions.assertEquals(2, eDB.getSizeEmployees());
    eDB.addEmployee("bad", "brad", "abc", "password", "Nurse");
    Assertions.assertEquals(2, eDB.getSizeEmployees());
    boolean d = eDB.removeAllEmployees();
    Assertions.assertTrue(d);
    Assertions.assertEquals(0, eDB.getSizeEmployees());
  }

  @Test
  public void testEditTitle() {
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = eDB.editTitle("bacd", "Doctor");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditFName() {
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = eDB.editNameFirst("bacd", "cray");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditLName() {
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = eDB.editNameLast("bacd", "kay");
    Assertions.assertTrue(a);
  }

  @Test
  public void testChangePass() {
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    boolean a = eDB.changePassword("jay", "passwordA2", "Is3");
    Assertions.assertTrue(a);
    boolean b = eDB.changePassword("jay", "Is33", "happy");
    Assertions.assertFalse(b);
    boolean c = eDB.changePassword("jay", "Is3", "IskkIg3");
    Assertions.assertTrue(c);
  }

  @Test
  public void testAddLog() {
    eDB.removeAllLogs();
    DB.removeAll();
    Assertions.assertEquals(0, eDB.getSizeLog());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password69", "Intern");
    eDB.addLog("jay");
    Assertions.assertEquals(1, eDB.getSizeLog());
    eDB.removeAllLogs();
    DB.removeAll();
  }

  @Test
  public void testChangeFlag() {
    eDB.removeAllLogs();
    DB.removeAll();
    Assertions.assertEquals(0, eDB.getSizeLog());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    eDB.addEmployee("bacd", "ray", "jay", "Password87", "Intern");
    eDB.addLog("jay");
    Assertions.assertEquals(1, eDB.getSizeLog());
    boolean b = eDB.isOnline("jay");
    Assertions.assertTrue(b);
    eDB.changeFlag();
    boolean a = eDB.isOnline("jay");
    Assertions.assertFalse(a);
    eDB.removeAllLogs();
    DB.removeAll();
  }

  @Test
  public void testUNameExists() {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    Assertions.assertTrue(eDB.uNameExists("jay"));
    eDB.addEmployee("bacd", "ray", "play", "passwordA2", "Intern");
    Assertions.assertTrue(eDB.uNameExists("play"));
    eDB.addEmployee("bacd", "ray", "cray", "passwordA2", "Intern");
    Assertions.assertTrue(eDB.uNameExists("cray"));
    Assertions.assertFalse(eDB.uNameExists("asdfj"));
    Assertions.assertFalse(eDB.uNameExists("askldjf"));
  }

  @Test
  public void testLogin() {
    eDB.createTables();
    eDB.removeAllEmployees();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", "Intern");
    eDB.addEmployee("bacd", "ray", "play", "passwordA2", "Intern");
    eDB.addEmployee("bacd", "ray", "cray", "passwordA2", "Intern");
    Assertions.assertTrue(eDB.logIn("jay", "passwordA2"));
    Assertions.assertTrue(eDB.logIn("play", "passwordA2"));
    Assertions.assertTrue(eDB.logIn("play", "passwordA2"));
    Assertions.assertFalse(eDB.logIn("play", "passworasdfdA2"));
    Assertions.assertFalse(eDB.logIn("plaasdfsdafy", "passwordA2"));
  }
}
