package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.employee.Employee;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import java.sql.*;
import java.util.Random;

public abstract class Database {
  /*
   Database service class. This class will be loaded as a Singleton by Guice.
  */

  private final Connection connection;
  public static String loggedIn = "";

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

  public synchronized EmployeeTitle getTitle(String username) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("Select * From Employees Where username = '" + username + "'");
      ResultSet rset = pstmt.executeQuery();
      EmployeeTitle title = null;
      String titleString = null;
      if (rset.next()) {
        titleString = rset.getString("title");
        title = EmployeeTitle.valueOf(titleString.toUpperCase());
      }
      rset.close();
      pstmt.close();
      return title;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public synchronized long getPager(String username) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("Select * From Employees Where username = '" + username + "'");
      ResultSet rset = pstmt.executeQuery();
      long page = 0;
      if (rset.next()) {
        page = rset.getLong("pagerNum");
      }
      rset.close();
      pstmt.close();
      return page;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
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

  /**
   * Makes an employee object for the person who is logged in currently
   *
   * @return The employee object
   */
  public Employee getLoggedIn() {
    try {
      if (checkIfExistsString("Employees", "username", loggedIn)) {
        PreparedStatement pstmt =
            getConnection().prepareStatement("SELECT * FROM Employees WHERE username = ?");
        pstmt.setString(1, loggedIn);
        ResultSet rset = pstmt.executeQuery();
        rset.next();
        String ID = rset.getString("employeeID");
        String fName = rset.getString("nameFirst");
        String lName = rset.getString("nameLast");
        String title = rset.getString("title");
        return new Employee(ID, fName, lName, EmployeeTitle.valueOf(title.toUpperCase()), loggedIn);
      }
      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
