package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestITTicketDatabse {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  ITTicketDatabase tDB;
  GraphDatabase DB;

  public TestITTicketDatabse() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      DB = new GraphDatabase(conn);
      tDB = new ITTicketDatabase(conn);
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
    tDB.dropTables();
    boolean dropTables = tDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = tDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = tDB.dropTables();
    Assertions.assertTrue(dropTables2);
    tDB.createTables();
  }
}
