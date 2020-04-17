package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public abstract class Database {

  public static void makeDatabase() throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase;create=true");
    } catch (SQLException e) {
      return;
    }
  }
}
