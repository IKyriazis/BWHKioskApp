package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestFlowerDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  FlowerDatabase fDB;

  public TestFlowerDatabase() throws SQLException {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    fDB = new FlowerDatabase(conn);
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
    fDB.dropTables();
    boolean dropTables = fDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = fDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = fDB.dropTables();
    Assertions.assertTrue(dropTables2);
    fDB.createTables();
  }
}
