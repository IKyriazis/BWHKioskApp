package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class JanitorDatabase extends Database {
  private int requestCount = 0;
  /**
   * Drops the janitor tables so we can start fresh
   *
   * @return false if the tables don't exist, true if table is dropped correctly
   * @throws SQLException
   */
  public boolean dropTables() throws SQLException {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    if (!(helperPrepared("ALTER TABLE JanitorRequest DROP CONSTRAINT FK_L"))) {

      return false;
    }
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
            "CREATE TABLE JanitorRequest (requestNumber INTEGER PRIMARY KEY, time TIMESTAMP NOT NULL, location Varchar(10) NOT NULL, name Varchar(15), progress Varchar(19) NOT NULL, priority Varchar(6) NOT NULL, CONSTRAINT FK_L FOREIGN KEY (location) REFERENCES Node(nodeID), CONSTRAINT CHK_PRIO CHECK (priority in ('Low', 'Medium', 'High')), CONSTRAINT CHK_PROG CHECK (progress in ('Reported', 'Dispatched', 'Done')))");
    if (a) {
      return true;
    } else {
      return false;
    }
  }

  public boolean removeAll() throws SQLException {
    return helperPrepared("DELETE From JanitorRequest");
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
              "INSERT INTO JanitorRequest (time, location, progress, priority, requestNumber) VALUES (?, ?, ?, ?, ?)");
      pstmt.setTimestamp(1, timestamp);
      pstmt.setString(2, location);
      pstmt.setString(3, progress);
      pstmt.setString(4, priority);
      pstmt.setInt(5, ++requestCount);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
//      System.out.println("Add request failed");
//      System.out.println(e.getMessage());
      return false;
    }
  }

  public boolean deleteRequest(int rn) throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement("DELETE From JanitorRequest Where requestNumber = ?");
      pstmt.setInt(1, rn);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean deleteDoneRequests() throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement("DELETE From JanitorRequest Where progress = 'Done'");
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean updateRequest(int rn, String name, String progress) throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement(
              "UPDATE JanitorRequest SET name = ?, progress = ? Where requestNumber = ?");
      pstmt.setString(1, name);
      pstmt.setString(2, progress);
      pstmt.setInt(3, rn);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  // this function is for testing purposes,
  // since we need to use a timestamp to use the deleterequest method
  // and update request method
  //  public Timestamp getTimestamp(String location) throws SQLException {
  //    try {
  //      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
  //      PreparedStatement pstmt =
  //          conn.prepareStatement("SELECT time FROM JanitorRequest WHERE location = ?");
  //      pstmt.setString(1, location);
  //      ResultSet rset = pstmt.executeQuery();
  //      Timestamp time = rset.getTimestamp("time");
  //      pstmt.close();
  //      conn.close();
  //      return time;
  //    } catch (SQLException e) {
  //      System.out.println(e.getMessage());
  //      return null;
  //    }
  //  }
}
