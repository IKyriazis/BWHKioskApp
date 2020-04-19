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
    if (!helperPrepared("DROP TABLE Janitors")) {
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
            "CREATE TABLE Janitors (employeeID Varchar(50) PRIMARY KEY, nameFirst Varchar(25), nameLast Varchar(25)");

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
  public boolean addJanitor(String empID, String nameFirst, String nameLast) throws SQLException {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Flowers (employeeID, nameFirst, nameLast) VALUES (?, ?, ?)");
      pstmt.setString(1, empID);
      pstmt.setString(2, nameFirst);
      pstmt.setString(3, nameLast);
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
  public int getSizeJanitors() throws SQLException {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From Janitors ");
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

  /**
   * @return
   * @throws SQLException
   */
  public boolean removeAllJanitors() throws SQLException {
    return helperPrepared("DELETE From Janitors");
  }
}
