package edu.wpi.cs3733.d20.teamA.database;

import java.math.BigInteger;
import java.sql.*;
import java.util.Random;
import java.lang.Math;

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
      return username;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getNamefromUser(String username) {
    String last;
    try {
      Statement priceStmt = getConnection().createStatement();
      ResultSet rst =
          priceStmt.executeQuery("SELECT * FROM Employees WHERE username = '" + username + "'");
      ;
      rst.next();
      last = rst.getString("nameLast");
      return last;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public String getNamefromUserF(String username) {
    String first;
    try {
      Statement priceStmt = getConnection().createStatement();
      ResultSet rst =
              priceStmt.executeQuery("SELECT * FROM Employees WHERE username = '" + username + "'");
      ;
      rst.next();
      first = rst.getString("nameFirst");
      return first;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public synchronized String getTitle(String username){

    try {
      PreparedStatement pstmt =
              getConnection().prepareStatement("Select * From Employees Where username = '" + username + "'");
      ResultSet rset = pstmt.executeQuery();
      String title = "Not found";
      if (rset.next()) {
        title = rset.getString("title");
      }
      rset.close();
      pstmt.close();
      return title;
    } catch (SQLException e) {
      e.printStackTrace();
      return "Not found";
    }

  }

  public synchronized Long getPager(String username){

    try {
      PreparedStatement pstmt =
              getConnection().prepareStatement("Select * From Employees Where username = '" + username + "'");
      ResultSet rset = pstmt.executeQuery();
      Long page = null;
      if (rset.next()) {
        page = rset.getLong("pagerNum");
      }
      rset.close();
      pstmt.close();
      return page;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }

  }

  public long getRandomNumber() {
    double lo = 2176782335L;
    long result = (long) (new Random().nextLong() * (lo / (double) Long.MAX_VALUE));
    result = Math.min(result, 2176782335L);
    return Math.abs(result);
  }

  public String getRandomString() {
    long l = getRandomNumber();
    String reqID = Long.toString(l, 36);
    reqID = ("000000" + reqID).substring(reqID.length());
    return reqID.toUpperCase();
  }

  public int getRandomInt() {
    return new Random().nextInt((999999 - 100000) + 1) + 100000;
  }
}
