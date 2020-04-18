package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public abstract class Database {

  public Database() throws SQLException {
    makeDatabase();
  }

  public static void makeDatabase() throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase;create=true");
    } catch (SQLException e) {
      return;
    }
  }

  /**
   * makes connection
   *
   * @param str the sql statement in a string
   * @return false if anything goes wrong
   * @throws SQLException
   */
  public boolean helperPrepared(String str) throws SQLException {

    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");

      PreparedStatement stmt = conn.prepareStatement(str);

      stmt.executeUpdate();
      stmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
