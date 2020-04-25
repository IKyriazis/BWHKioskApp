package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class LaundryDatabase extends Database {
  // private int requestNum = getSizeLaundry() + 1;

  public LaundryDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("LAUNDRY")) {
      createTables();
    }
  }

  /**
   * Drops the laundry table from the database
   *
   * @return True if successful drop
   */
  public boolean dropTables() {
    if (!doesTableNotExist("LAUNDRY")) {
      return helperPrepared("DROP TABLE Laundry");
    }
    return false;
  }

  /**
   * Creates the laundry table
   *
   * @return True if successful creation
   */
  public boolean createTables() {
    if (doesTableNotExist("LAUNDRY")) {
      boolean a =
          helperPrepared(
              "CREATE TABLE Laundry (requestNum INTEGER PRIMARY KEY, employeeEntered Varchar(25) NOT NULL, location Varchar(10), progress Varchar(50), employeeWash Varchar(25), timeRequested TIMESTAMP NOT NULL, CONSTRAINT FK_EMPE FOREIGN KEY (employeeEntered) REFERENCES Employees(username), CONSTRAINT FK_EMPW FOREIGN KEY (employeeWash) REFERENCES Employees(username), CONSTRAINT FK_LOC FOREIGN KEY (location) REFERENCES Node(nodeID), CONSTRAINT Check_Prog CHECK (progress in ('Request Sent', 'Clothes Collected', 'Clothes Washing', 'Clothes Drying', 'Clothes Returned')))");
      return a;
    }
    return false;
  }

  /**
   * Finds the number of entries in the laundry table
   *
   * @return The size of the laundry table
   */
  public int getSizeLaundry() {
    return getSize("Laundry");
  }

  /**
   * Removes all laundry requests (used primarily for testing purposes)
   *
   * @return True if the removal was successful
   */
  public boolean removeAll() {
    return helperPrepared("DELETE FROM Laundry");
  }

  /**
   * Adds a value to the laundry database
   *
   * @param emp - The employee who entered the request
   * @param loc - The location of the request (where to pick up/return the clothes)
   * @return True if a successful add
   */
  public boolean addLaundry(String emp, String loc) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    try {
      boolean a = checkIfExistsString("Employees", "username", emp);
      boolean b = checkIfExistsString("Node", "nodeID", loc);
      if (a && b) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "INSERT INTO Laundry (requestNum, employeeEntered, location, progress, timeRequested) VALUES (?, ?, ?, 'Request Sent', ?)");
        pstmt.setInt(1, getSizeLaundry() + 1);
        pstmt.setString(2, emp);
        pstmt.setString(3, loc);
        pstmt.setTimestamp(4, timestamp);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      } else {
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Finds a laundry request and makes edits to all its values
   *
   * @param num - The request number being edited
   * @param emp - The employee who made the request
   * @param loc - The location of the request
   * @param prog - The progress of the request
   * @param empW - The employee who is washing the clothes
   * @return True if a successful edit
   */
  public boolean editLaundry(
      int num, String emp, String loc, String prog, String empW, Timestamp timestamp) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Laundry SET employee = ?, location = ?, progress = ?, employeeWash = ?, timeRequested = ? WHERE requestNum = ?");
      pstmt.setString(1, emp);
      pstmt.setString(2, loc);
      pstmt.setString(3, prog);
      pstmt.setString(4, empW);
      pstmt.setTimestamp(5, timestamp);
      pstmt.setInt(6, num);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Removes a request from the laundry table
   *
   * @param num - The request number to be deleted
   * @return True if a successful deletion
   */
  public boolean deleteLaundry(int num) {
    try {
      boolean a = checkIfExistsInt("Laundry", "requestNum", num);
      if (a) {
        PreparedStatement pstmt =
            getConnection().prepareStatement("DELETE FROM Laundry WHERE requestNum = ?");
        pstmt.setInt(1, num);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      } else {
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Helps get the various string values from the Laundry table
   *
   * @param num - The request number
   * @param col - The column whose value we are looking for
   * @return The string from the given column
   */
  public String helperGetString(int num, String col) {
    try {
      boolean a = checkIfExistsInt("Laundry", "requestNum", num);
      if (a) {
        PreparedStatement pstmt =
            getConnection().prepareStatement("SELECT * FROM Laundry WHERE requestNum = ?");
        pstmt.setInt(1, num);
        ResultSet rset = pstmt.executeQuery();
        rset.next();
        String s = rset.getString(col);
        rset.close();
        pstmt.close();
        return s;
      } else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Gets the employeeEntered value from the laundry table
   *
   * @param num - The request number
   * @return The employee who entered the request
   */
  public String getEmpE(int num) {
    return helperGetString(num, "employeeEntered");
  }

  /**
   * Gets the location value from the laundry table
   *
   * @param num - The request number
   * @return The location where the request is taking place
   */
  public String getLoc(int num) {
    return helperGetString(num, "location");
  }

  /**
   * Gets the progress value from the laundry table
   *
   * @param num - The request number
   * @return The progress of the request
   */
  public String getProg(int num) {
    return helperGetString(num, "progress");
  }

  /**
   * Gets the employeeWash value from the laundry table
   *
   * @param num - The request number
   * @return The employee who is washing the clothes
   */
  public String getEmpW(int num) {
    return helperGetString(num, "employeeWash");
  }

  /**
   * Gets the timeRequested value from the laundry table
   *
   * @param num - The request number
   * @return The time the request was made
   */
  public Timestamp getTimeRequested(int num) {
    try {
      boolean a = checkIfExistsInt("Laundry", "requestNum", num);
      if (a) {
        PreparedStatement pstmt =
            getConnection().prepareStatement("SELECT * FROM Laundry WHERE requestNum = ?");
        pstmt.setInt(1, num);
        ResultSet rset = pstmt.executeQuery();
        rset.next();
        Timestamp ts = rset.getTimestamp("timeRequested");
        rset.close();
        pstmt.close();
        return ts;
      } else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Helps set the various String values in the laundry table
   *
   * @param num - The request number
   * @param col - The column that needs to be changed
   * @param s - The value to change it to
   * @return True if the change was successful
   */
  public boolean helperSetString(int num, String col, String s) {
    try {
      boolean a = checkIfExistsInt("Laundry", "requestNum", num);
      if (a) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE Laundry SET " + col + " = '" + s + "' WHERE requestNum = " + num);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      } else {
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Sets the employeeEntered field to the specified value at the specified request
   *
   * @param num - The request number
   * @param s - The value to change to
   * @return True if the change was successful
   */
  public boolean setEmpE(int num, String s) {
    boolean a = checkIfExistsString("Employees", "username", s);
    if (a) {
      return helperSetString(num, "employeeEntered", s);
    } else {
      return false;
    }
  }

  /**
   * Sets the location field to the specified value at the specified request
   *
   * @param num - The request number
   * @param s - The value to change to
   * @return True if the change was successful
   */
  public boolean setLoc(int num, String s) {
    boolean a = checkIfExistsString("Node", "nodeID", s);
    if (a) {
      return helperSetString(num, "location", s);
    } else {
      return false;
    }
  }

  /**
   * Sets the progress field to the specified value at the specified request
   *
   * @param num - The request number
   * @param s - The value to change to
   * @return True if the change was successful
   */
  public boolean setProg(int num, String s) {
    if (s.equals("Request Sent")
        || s.equals("Clothes Collected")
        || s.equals("Clothes Washing")
        || s.equals("Clothes Drying")
        || s.equals("Clothes Returned")) {
      return helperSetString(num, "progress", s);
    } else {
      return false;
    }
  }

  /**
   * Sets the employeeWash field to the specified value at the specified request
   *
   * @param num - The request number
   * @param s - The value to change to
   * @return True if the change was successful
   */
  public boolean setEmpW(int num, String s) {
    boolean a = checkIfExistsString("Employees", "username", s);
    if (a) {
      return helperSetString(num, "employeeWash", s);
    } else {
      return false;
    }
  }

  /**
   * Sets the timeRequested filed to the specified value at the specified request
   *
   * @param num - The request number
   * @param timestamp - The value to change to
   * @return True if the change was successful
   */
  public boolean setTimestamp(int num, Timestamp timestamp) {
    try {
      boolean a = checkIfExistsInt("Laundry", "requestNum", num);
      if (a) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE Laundry SET timeRequested = '"
                        + timestamp
                        + "' WHERE requestNum = "
                        + num);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      } else {
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
