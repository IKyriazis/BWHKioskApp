package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class EmployeesDatabase extends Database {

  public EmployeesDatabase(Connection connection) throws SQLException {
    super(connection);
  }

  /**
   * @return
   * @throws SQLException
   */
  public boolean dropTables() throws SQLException {

    // Drop the tables
    if (!helperPrepared("DROP TABLE Employees")) {
      return false;
    }

    return true;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   * @throws SQLException
   */
  public boolean createTables() throws SQLException {

    // Create the graph tables
    boolean a =
        helperPrepared(
            "CREATE TABLE Employees (employeeID Varchar(50) PRIMARY KEY, nameFirst Varchar(25), nameLast Varchar(25), password Varchar(10), title Varchar(50))");

    if (a) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @param empID
   * @param nameFirst
   * @param nameLast
   * @return
   * @throws SQLException
   */
  public boolean addEmployee(String empID, String nameFirst, String nameLast, String title)
      throws SQLException {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Employees (employeeID, nameFirst, nameLast, password, title) VALUES (?, ?, ?, ?, ?)");
      pstmt.setString(1, empID);
      pstmt.setString(2, nameFirst);
      pstmt.setString(3, nameLast);
      pstmt.setString(4, empID);
      pstmt.setString(5, title);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * Removes a janitor of empID from the Janitor's table
   *
   * @param empID
   * @return true if the Janitor was successfully deleted
   * @throws SQLException
   */
  public boolean deleteEmployee(String empID) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Employees Where employeeID = ?");
      pstmt.setString(1, empID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * @return
   * @throws SQLException
   */
  public int getSizeEmployees() throws SQLException {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From Employees ");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        count++;
      }
      rset.close();
      pstmt.close();
      return count;
    } catch (SQLException e) {
      return -1;
    }
  }

  public boolean editTitle(String empID, String newTitle) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newTitle
                      + "' WHERE employeeID = '"
                      + empID
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean editNameFirst(String empID, String newFirst) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newFirst
                      + "' WHERE employeeID = '"
                      + empID
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean editNameLast(String empID, String newLast) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newLast
                      + "' WHERE employeeID = '"
                      + empID
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean logIn(String empID, String enteredPass) throws SQLException {
    String pass = null;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "Select password From Employees Where employeeID = '" + empID + "'");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        pass = rset.getString("password");
      }
      rset.close();
      pstmt.close();

      return (pass != null) && pass.equals(enteredPass);
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean changePassword(String empID, String oldPass, String newPass) throws SQLException {

    if (logIn(empID, oldPass) && checkSecurePass(newPass)) {
      try {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE Employees SET password = '"
                        + newPass
                        + "' WHERE employeeID = '"
                        + empID
                        + "'");
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      } catch (SQLException e) {
        return false;
      }
    }

    return false;
  }

  /**
   * @param password
   * @return
   */
  public boolean checkSecurePass(String password) throws SQLException {
    char ch;
    boolean capital = false;
    boolean lowercase = false;
    boolean number = false;
    for (int i = 0; i < password.length(); i++) {
      ch = password.charAt(i);
      if (Character.isDigit(ch)) {
        number = true;
      } else if (Character.isUpperCase(ch)) {
        capital = true;
      } else if (Character.isLowerCase(ch)) {
        lowercase = true;
      }

      if (number && capital && lowercase) return true;
    }
    return false;
  }

  /**
   * @return
   * @throws SQLException
   */
  public boolean removeAllEmployees() throws SQLException {
    return helperPrepared("DELETE From Employees");
  }
}
