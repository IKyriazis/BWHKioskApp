package edu.wpi.cs3733.d20.teamA.database;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;

public class EmployeesDatabase extends Database {

  int employeeID;
  private int numIterations = 16; // 2 ^ 16 = 65536 iterations

  public EmployeesDatabase(Connection connection) {

    super(connection);

    if (doesTableNotExist("EMPLOYEES")) {
      createTables();
    }

    employeeID = getSizeEmployees() + 1;
  }

  /**
   * Drop the 'Employees' table
   *
   * @return Success / Failure
   */
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
        "CREATE TABLE Employees (employeeID INTEGER PRIMARY KEY, nameFirst Varchar(25), nameLast Varchar(25), username Varchar(25) UNIQUE NOT NULL, password Varchar(60) NOT NULL, title Varchar(50))");
  }

  /**
   * @param nameFirst nameFirst
   * @param nameLast last name
   * @return returns true if the employee is added
   */
  public boolean addEmployee(
      int employeeID,
      String nameFirst,
      String nameLast,
      String username,
      String password,
      String title) {
    String storedPassword =
        BCrypt.withDefaults().hashToString(numIterations, password.toCharArray());

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Employees (employeeID, nameFirst, nameLast, username, password, title) VALUES (?, ?, ?, ?, ?, ?)");
      pstmt.setInt(1, employeeID);
      pstmt.setString(2, nameFirst);
      pstmt.setString(3, nameLast);
      pstmt.setString(4, username);
      pstmt.setString(5, storedPassword);
      pstmt.setString(6, title);
      pstmt.executeUpdate();
      pstmt.close();
      this.employeeID++;
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // returns true if the username isn't in the database
  public boolean uNameExists(String uName) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Employees WHERE username = ?");
      pstmt.setString(1, uName);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      String uNameFromTable;
      uNameFromTable = rset.getString("username");
      pstmt.close();
      return uName.equals(uNameFromTable);
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean addEmployee(
      String nameFirst, String nameLast, String username, String password, String title) {
    return checkSecurePass(password)
        && addEmployee(employeeID, nameFirst, nameLast, username, password, title);
  }

  /**
   * Removes a janitor of empID from the Janitor's table
   *
   * @return true if the Janitor was successfully deleted
   */
  public boolean deleteEmployee(String username) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Employees Where username = ?");
      pstmt.setString(1, username);
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
   * @param newTitle newTitle
   * @return true if the title is changed
   */
  public boolean editTitle(String username, String newTitle) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newTitle
                      + "' WHERE username = '"
                      + username
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean editNameFirst(String username, String newFirst) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newFirst
                      + "' WHERE username = '"
                      + username
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean editNameLast(String username, String newLast) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newLast
                      + "' WHERE username = '"
                      + username
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean logIn(String username, String enteredPass) {
    String pass = null;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "Select password From Employees Where username = '" + username + "'");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        pass = rset.getString("password");
      }
      rset.close();
      pstmt.close();
      BCrypt.Result result = BCrypt.verifyer().verify(enteredPass.toCharArray(), pass);
      return (pass != null) && result.verified;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean changePassword(String username, String oldPass, String newPass) {

    if (logIn(username, oldPass) && checkSecurePass(newPass)) {
      String storedPass = BCrypt.withDefaults().hashToString(numIterations, newPass.toCharArray());
      try {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE Employees SET password = '"
                        + storedPass
                        + "' WHERE username = '"
                        + username
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
    if (password.length() > 72) {
      return false;
    }
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
  public boolean removeAllEmployees() {
    return helperPrepared("DELETE From Employees");
  }

  public void readEmployeeCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/Employees.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String nameFirst, nameLast, username, password, title;
        int employeeID;
        employeeID = Integer.parseInt(data.get(i)[0]);
        nameFirst = data.get(i)[1];
        nameLast = data.get(i)[2];
        username = data.get(i)[3];
        password = data.get(i)[4];
        title = data.get(i)[5];
        addEmployee(employeeID, nameFirst, nameLast, username, password, title);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }
}
