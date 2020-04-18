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

  @Test
  public void testAddRequest() throws SQLException {
    jDB.removeAll();
    boolean a = jDB.addRequest("ADEPT00101", "High");
    Assertions.assertTrue(a);
    boolean b = jDB.addRequest("ADEPT00101", "Extra Large");
    Assertions.assertFalse(b);
    jDB.removeAll();
  }

  //  Unsure on how to test deleteRequest() because we need the request timestamp to delete it but
  // that is stored in
  //  the database
  //  @Test
  //  public void testDeleteRequest() throws SQLException {
  //    jDB.removeAll();
  //    boolean a = jDB.addRequest("Lobby", "High");
  //    boolean b = jDB.addRequest("Main A", "Medium");
  //    boolean c = jDB.deleteRequest("Main A", )
  //  }
}
