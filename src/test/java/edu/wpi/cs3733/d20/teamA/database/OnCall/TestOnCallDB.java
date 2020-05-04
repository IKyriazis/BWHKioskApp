package edu.wpi.cs3733.d20.teamA.database.OnCall;

import edu.wpi.cs3733.d20.teamA.database.OnCallDatabase;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestOnCallDB {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  EmployeesDatabase eDB;
  GraphDatabase DB;
  OnCallDatabase ocDB;

  public TestOnCallDB() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    DB = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    ocDB = new OnCallDatabase(conn);
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
    ocDB.dropTables();
    boolean dropTables = ocDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = ocDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = ocDB.dropTables();
    Assertions.assertTrue(dropTables2);
    ocDB.createTables();
  }

  @Test
  public void testAddOnCall() {
    eDB.removeAll();
    ocDB.removeAll();
    String a =
        eDB.addEmployee("abc", "brad", "bad", "passwordH2", EmployeeTitle.JANITOR, 8884449302l);
    ocDB.signOntoShift("bad");
    Assertions.assertEquals(ocDB.getStatus("bad"), "Available");
    Assertions.assertEquals(ocDB.getSize(), 1);
    ocDB.removeAll();
    eDB.removeAll();
  }

  @Test
  public void testDeleteOnCall() {
    eDB.removeAll();
    ocDB.removeAll();
    eDB.addEmployee("abc", "brad", "bad", "passwordA2", EmployeeTitle.JANITOR, 5553335553l);
    boolean b = ocDB.signOntoShift("bad");
    eDB.addEmployee("bad", "brad", "abc", "passwordA2", EmployeeTitle.JANITOR, 2255225523l);
    boolean c = ocDB.signOntoShift("abc");
    boolean d = ocDB.signOffShift("bad");
    Assertions.assertEquals(ocDB.getSize(), 1);
  }

  @Test
  public void testChangeStat() {
    eDB.removeAll();
    ocDB.removeAll();
    eDB.addEmployee("abc", "brad", "bad", "passwordA2", EmployeeTitle.JANITOR, 5553335553l);
    boolean b = ocDB.signOntoShift("bad");
    eDB.addEmployee("bad", "brad", "abc", "passwordA2", EmployeeTitle.JANITOR, 2255225523l);
    boolean c = ocDB.signOntoShift("abc");
    boolean d = ocDB.updateStatus("abc", "Out");
    Assertions.assertEquals(ocDB.getStatus("abc"), "Out");
  }
}
