package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public abstract class Database {
  /*
   Database service class. This class will be loaded as a Singleton by Guice.
  */

  private final Connection connection;

  public Database(Connection connection) {
    this.connection = connection;
    // makeDatabase();
  }

  public Connection getConnection() {
    return connection;
  }

  /**
   * makes connection
   *
   * @param str the sql statement in a string
   * @return false if anything goes wrong
   */
  public boolean helperPrepared(String str) {

    try {

      PreparedStatement stmt = connection.prepareStatement(str);

      stmt.executeUpdate();
      stmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getSize(String tableName) {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From " + tableName);
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        count++;
      }
      rset.close();
      pstmt.close();
      return count;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
