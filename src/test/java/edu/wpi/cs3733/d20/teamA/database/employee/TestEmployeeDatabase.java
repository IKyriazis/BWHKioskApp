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
    eDB.removeAll();
    String a = eDB.addEmployee("abc", "brad", "bad", "passwordH2", EmployeeTitle.JANITOR);
    Assertions.assertEquals(a, eDB.getEmployeeID("bad"));
    String d = eDB.addEmployee("abcd", "chad", "lad", "passwordH2", EmployeeTitle.JANITOR);
    Assertions.assertEquals(d, eDB.getEmployeeID("lad"));
    String b = eDB.addEmployee("ab", "kassy", "lassy", "passwordH2", EmployeeTitle.JANITOR);
    Assertions.assertEquals(b, eDB.getEmployeeID("lassy"));
    String e = eDB.addEmployee("bacd", "ray", "jay", "passwordH2", EmployeeTitle.JANITOR);
    Assertions.assertEquals(e, eDB.getEmployeeID("jay"));
    String f = eDB.addEmployee("bacd", "ray", "jay", "a".repeat(90), EmployeeTitle.JANITOR);
    Assertions.assertEquals(f, "");
    Assertions.assertEquals(4, eDB.getSize());
    eDB.removeAll();
  }

  @Test
  public void testDeleteEmployee() {
    eDB.removeAll();
    eDB.addEmployee("abc", "brad", "bad", "passwordA2", EmployeeTitle.JANITOR);
    boolean b = eDB.deleteEmployee("bad");
    Assertions.assertTrue(b);
    Assertions.assertEquals(0, eDB.getSize());
    eDB.addEmployee("bad", "brad", "abc", "passwordA2", EmployeeTitle.JANITOR);
    eDB.addEmployee("bad", "brad", "dyi", "passwordA2", EmployeeTitle.JANITOR);
    Assertions.assertEquals(2, eDB.getSize());
    boolean e = eDB.deleteEmployee("bad");
    Assertions.assertTrue(e);
    Assertions.assertEquals(2, eDB.getSize());
    eDB.addEmployee("bad", "brad", "abc", "password", EmployeeTitle.JANITOR);
    Assertions.assertEquals(2, eDB.getSize());
    boolean d = eDB.removeAll();
    Assertions.assertTrue(d);
    Assertions.assertEquals(0, eDB.getSize());
  }

  @Test
  public void testEditTitle() {
    eDB.removeAll();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", EmployeeTitle.JANITOR);
    boolean a = eDB.editTitle("bacd", "Doctor");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditFName() {
    eDB.removeAll();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", EmployeeTitle.JANITOR);
    boolean a = eDB.editNameFirst("bacd", "cray");
    Assertions.assertTrue(a);
  }

  @Test
  public void testEditLName() {
    eDB.removeAll();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", EmployeeTitle.JANITOR);
    boolean a = eDB.editNameLast("bacd", "kay");
    Assertions.assertTrue(a);
  }

  @Test
  public void testChangePass() {
    eDB.removeAll();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", EmployeeTitle.JANITOR);
    boolean a = eDB.changePassword("jay", "passwordA2", "Is3");
    Assertions.assertTrue(a);
    boolean b = eDB.changePassword("jay", "Is33", "happy");
    Assertions.assertFalse(b);
    boolean c = eDB.changePassword("jay", "Is3", "IskkIg3");
    Assertions.assertTrue(c);
  }

  @Test
  public void testUNameExists() {
    eDB.createTables();
    eDB.removeAll();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", EmployeeTitle.JANITOR);
    Assertions.assertTrue(eDB.uNameExists("jay"));
    eDB.addEmployee("bacd", "ray", "play", "passwordA2", EmployeeTitle.JANITOR);
    Assertions.assertTrue(eDB.uNameExists("play"));
    eDB.addEmployee("bacd", "ray", "cray", "passwordA2", EmployeeTitle.JANITOR);
    Assertions.assertTrue(eDB.uNameExists("cray"));
    Assertions.assertFalse(eDB.uNameExists("asdfj"));
    Assertions.assertFalse(eDB.uNameExists("askldjf"));
  }

  @Test
  public void testLogin() {
    eDB.createTables();
    eDB.removeAll();
    eDB.addEmployee("bacd", "ray", "jay", "passwordA2", EmployeeTitle.RECEPTIONIST);
    eDB.addEmployee("bacd", "ray", "play", "passwordA2", EmployeeTitle.RECEPTIONIST);
    eDB.addEmployee("bacd", "ray", "cray", "passwordA2", EmployeeTitle.RECEPTIONIST);
    Assertions.assertTrue(eDB.logIn("jay", "passwordA2"));
    Assertions.assertTrue(eDB.logIn("play", "passwordA2"));
    Assertions.assertTrue(eDB.logIn("play", "passwordA2"));
    Assertions.assertFalse(eDB.logIn("play", "passworasdfdA2"));
    Assertions.assertFalse(eDB.logIn("plaasdfsdafy", "passwordA2"));
  }
}