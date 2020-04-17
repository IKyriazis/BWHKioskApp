package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestDatabase {

  GraphDatabase DB = new GraphDatabase();

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
    boolean dropTables = DB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = DB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = DB.dropTables();
    Assertions.assertTrue(dropTables2);
  }
}
