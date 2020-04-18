package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import java.util.Date;

public class JanitorDatabase extends Database {
  /**
   * Drops the janitor tables so we can start fresh
   *
   * @return false if the tables don't exist, true if
   *     table is dropped correctly
   * @throws SQLException
   */
  public boolean dropTables() throws SQLException {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    //        if (!(helperPrepared("ALTER TABLE Orders DROP CONSTRAINT FK_fT"))) {
    //
    //            return false;
    //        }
    // Drop the tables
    if (!(helperPrepared("DROP TABLE JanitorRequest"))) {
      return false;
    }

    return true;
  }

  /**
   * Creates janitor tables
   *
   * @return False if tables couldn't be created
   * @throws SQLException
   */
  public boolean createTables() throws SQLException {

    // Create the janitorrequest table
    boolean a =
        helperPrepared(
            "CREATE TABLE JanitorRequest (time TIMESTAMP NOT NULL, location Varchar(15) NOT NULL, name Varchar(15), progress Varchar(19) NOT NULL, priority Varchar(6) NOT NULL, CONSTRAINT CHK_PRIO CHECK (priority in ('Low', 'Medium', 'High')), CONSTRAINT CHK_PROG CHECK (progress in ('Reported', 'Dispatched', 'Done')), CONSTRAINT PK_J PRIMARY KEY (time, location))");
    if (a) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Adds janitor service request given location, and priority
   *
   * @return False if request couldn't be added
   * @throws SQLException
   */
  public boolean addRequest(String location, String priority) throws SQLException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String progress = "Reported";
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
              conn.prepareStatement(
                      "INSERT INTO JanitorRequest (time, location, progress, priority) VALUES (?, ?, ?, ?)");
      pstmt.setTimestamp(1, timestamp);
      pstmt.setString(2, location);
      pstmt.setString(3, progress);
      pstmt.setString(4, priority);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }

  }


  public boolean deleteRequest(Timestamp time, String location) throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("DELETE From JanitorRequest Where time = ? AND location = ?");
      pstmt.setTimestamp(1, time);
      pstmt.setString(2, location);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

}
