package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public abstract class Database {
  /*
   Database service class. This class will be loaded as a Singleton by Guice.
  */

  private final Connection connection;

  public Database(Connection connection) throws SQLException {
    this.connection = connection;
    // makeDatabase();
  }

  // public static void makeDatabase() throws SQLException {
  //  try {
  //    Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase;create=true");
  //  } catch (SQLException e) {
  //    return;
  //  }
  // }

  public Connection getConnection() {
    return connection;
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

      PreparedStatement stmt = connection.prepareStatement(str);

      stmt.executeUpdate();
      stmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }
}
