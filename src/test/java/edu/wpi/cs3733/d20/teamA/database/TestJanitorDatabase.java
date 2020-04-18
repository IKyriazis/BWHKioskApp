package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestJanitorDatabase {
  JanitorDatabase jDB = new JanitorDatabase();

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
    jDB.dropTables();
    boolean dropTables = jDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = jDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = jDB.dropTables();
    Assertions.assertTrue(dropTables2);
    jDB.createTables();
  }
}
