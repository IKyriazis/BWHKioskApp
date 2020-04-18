package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFlowerDatabase {
  FlowerDatabase fDB;

  public TestFlowerDatabase() throws SQLException {
    fDB = new FlowerDatabase();
  }

  @Test
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
