package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class EmployeesDatabase extends Database {

  public EmployeesDatabase(Connection connection) {
    super(connection);
  }

  /** @return */
  public boolean dropTables() {

    // Drop the tables
    return helperPrepared("DROP TABLE Employees");
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the graph tables

    return helperPrepared(
        "CREATE TABLE Employees (employeeID Varchar(50) PRIMARY KEY, nameFirst Varchar(25), nameLast Varchar(25), password Varchar(10), title Varchar(50))");
  }

  /**
   * @param empID employee ID
   * @param nameFirst nameFirst
   * @param nameLast last name
   * @return returns true if the employee is added
   */
  public boolean addEmployee(String empID, String nameFirst, String nameLast, String title) {

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
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Removes a janitor of empID from the Janitor's table
   *
   * @param empID employee ID
   * @return true if the Janitor was successfully deleted
   */
  public boolean deleteEmployee(String empID) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Employees Where employeeID = ?");
      pstmt.setString(1, empID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /** @return returns the size of the table */
  public int getSizeEmployees() {
    return getSize("Employees");
  }

  /**
   * @param empID employee ID
   * @param newTitle newTitle
   * @return true if the title is changed
   */
  public boolean editTitle(String empID, String newTitle) {
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
      e.printStackTrace();
      return false;
    }
  }

  public boolean editNameFirst(String empID, String newFirst) {
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
      e.printStackTrace();
      return false;
    }
  }

  public boolean editNameLast(String empID, String newLast) {
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
      e.printStackTrace();
      return false;
    }
  }

  public boolean logIn(String empID, String enteredPass) {
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
      e.printStackTrace();
      return false;
    }
  }

  public boolean changePassword(String empID, String oldPass, String newPass) {

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
        e.printStackTrace();
        return false;
      }
    }

    return false;
  }

  /**
   * @param password password
   * @return true if the there is a scure pass
   */
  public boolean checkSecurePass(String password) {
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

  /** @return true if all all employee are removed */
  public boolean removeAllEmployees() throws SQLException {
    return helperPrepared("DELETE From Employees");
  }
}
