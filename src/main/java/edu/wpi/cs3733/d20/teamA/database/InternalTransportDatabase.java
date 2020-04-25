package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class InternalTransportDatabase extends Database {

  private int requestCount = 0;

  public InternalTransportDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("InternalTransportRequest")) {
      createTables();
    }
  }

  /**
   * Creates internal transport tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the janitorrequest table
    return helperPrepared(
        "CREATE TABLE InternalTransportRequest (requestNumber INTEGER PRIMARY KEY, time TIMESTAMP NOT NULL, start Varchar(10) NOT NULL, destination Varchar(10) NOT NULL, name Varchar(15), progress Varchar(19) NOT NULL, CONSTRAINT FK_S FOREIGN KEY (start) REFERENCES Node(nodeID), CONSTRAINT FK_D FOREIGN KEY (destination) REFERENCES Node(nodeID), CONSTRAINT CHK_PROG CHECK (progress in ('Reported', 'Dispatched', 'Done')))");
  }

  /**
   * Drops the internal transport request tables so we can start fresh
   *
   * @return false if the tables don't exist, true if table is dropped correctly
   */
  public boolean dropTables() {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    if (!(helperPrepared("ALTER TABLE InternalTransportRequest DROP CONSTRAINT FK_S")
        && helperPrepared("ALTER TABLE InternalTransportRequest DROP CONSTRAINT FK_D"))) {

      return false;
    }
    // Drop the tables
    return helperPrepared("DROP TABLE InternalTransportRequest");
  }

  /**
   * Sets the requestCount to 0
   *
   * @return true if everything could be deleted
   */
  public boolean removeAll() {
    requestCount = 0;
    return helperPrepared("DELETE From InternalTransportRequest");
  }

  public boolean addRequest(String start, String destination) {
    // creates a timestamp of the time that the function is called
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    // default status is reported
    String progress = "Reported";
    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO InternalTransportRequest (time, start, progress, destination, requestNumber) VALUES (?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setTimestamp(1, timestamp);
      pstmt.setString(2, start);
      pstmt.setString(3, progress);
      pstmt.setString(4, destination);
      // first request starts at 1 and increments every time a new request is added
      pstmt.setInt(5, ++requestCount);
      pstmt.executeUpdate();
      pstmt.close();
      // return true if the request is added
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getRequestSize() {
    int count = 0;
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("Select * From InternalTransportRequest ");
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
}
