package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import java.util.Random;

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

  /**
   * Finds out how many rows of data are in the given table
   *
   * @param tableName - The name of the table
   * @return The size of the table
   */
  public int getSize(String tableName) {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From " + tableName);
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        System.out.println(rset.toString());
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

  /**
   * @param table name of the table
   * @return true if the table does not exist
   */
  public boolean doesTableNotExist(String table) {
    try {
      DatabaseMetaData dbm = connection.getMetaData();
      ResultSet tables = dbm.getTables(null, null, table, null);
      // If table doesn't exist create them
      if (!(tables.next())) {
        return true;
      }

      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find out if a specific string from the given table exists
   *
   * @param tableName - The name of the table
   * @param col - The column to search in
   * @param value - The value to search for
   * @return True, if the string exists
   */
  public boolean checkIfExistsString(String tableName, String col, String value) {
    boolean a = false;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "SELECT * FROM " + tableName + " WHERE " + col + " = '" + value + "'");
      ResultSet rset = pstmt.executeQuery();
      if (rset.next()) {
        a = true;
      }
      rset.close();
      pstmt.close();
      return a;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find out if a specific int from the given table exists
   *
   * @param tableName - The name of the table
   * @param col - The column to search in
   * @param value - The value to search for
   * @return True, if the int exists
   */
  public boolean checkIfExistsInt(String tableName, String col, int value) {
    boolean a = false;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("SELECT * FROM " + tableName + " WHERE " + col + " = " + value);
      ResultSet rset = pstmt.executeQuery();
      if (rset.next()) {
        a = true;
      }
      rset.close();
      pstmt.close();
      return a;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * get's the username of whoever is currently logged in
   *
   * @return the username
   */
  public String getLoggedIn() {
    String username = null;
    try {
      PreparedStatement pstm =
          getConnection().prepareStatement("Select username From LoggedIn Where flag = true");
      ResultSet rset = pstm.executeQuery();
      while (rset.next()) {
        username = rset.getString("username");
      }
      rset.close();
      pstm.close();
      if (username != null) {
        return username;
      }
      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public int getRandomNumber() {
    return new Random().nextInt((999999999 - 100000000) + 1) + 100000000;
  }
}
