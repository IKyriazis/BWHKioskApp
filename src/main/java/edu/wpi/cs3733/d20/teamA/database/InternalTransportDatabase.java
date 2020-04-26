package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class InternalTransportDatabase extends Database {

  private int requestCount = getRequestSize();

  public InternalTransportDatabase(Connection connection) {
    super(connection);

    //    if (doesTableNotExist("InternalTransportRequest")) {
    //      createTables();
    //    }
  }

  /**
   * Creates internal transport tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the InternalTransportRequest table
    return helperPrepared(
        "CREATE TABLE InternalTransportRequest (requestNumber INTEGER PRIMARY KEY, time TIMESTAMP NOT NULL, start Varchar(10) NOT NULL, destination Varchar(10) NOT NULL, name Varchar(15), progress Varchar(19) NOT NULL, CONSTRAINT FK_START FOREIGN KEY (start) REFERENCES Node(nodeID), CONSTRAINT FK_DEST FOREIGN KEY (destination) REFERENCES Node(nodeID), CONSTRAINT CHK_INTTRANSPROG CHECK (progress in ('Reported', 'Dispatched', 'Done')))");
  }

  /**
   * Drops the internal transport request tables so we can start fresh
   *
   * @return false if the tables don't exist, true if table is dropped correctly
   */
  public boolean dropTables() {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    if (!(helperPrepared("ALTER TABLE InternalTransportRequest DROP CONSTRAINT FK_START")
        && helperPrepared("ALTER TABLE InternalTransportRequest DROP CONSTRAINT FK_DEST"))) {

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

  public int addRequest(String start, String destination) {
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
      pstmt.setInt(5, requestCount + 1);
      pstmt.executeUpdate();
      pstmt.close();
      // return requestCount if the request is added
      return ++requestCount;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
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

  /**
   * Updates the rn with a certain progress
   *
   * @param rn request number
   * @param name name
   * @param progress progress
   * @return true if the progress has been updated
   */
  public boolean updateRequest(int rn, String name, String progress) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE InternalTransportRequest SET name = ?, progress = ? Where requestNumber = ?");
      pstmt.setString(1, name);
      pstmt.setString(2, progress);
      pstmt.setInt(3, rn);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public String getRequestStatus(int rn) {
    String n;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "SELECT progress FROM InternalTransportRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      n = rset.getString("progress");
      pstmt.close();
      return n;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getName(int rn) {
    String n;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "SELECT name FROM InternalTransportRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      n = rset.getString("name");
      pstmt.close();
      return n;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /** Prints out the table */
  public void printTable() {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("Select * FROM InternalTransportRequest ");
      ResultSet rset = pstmt.executeQuery();
      String request;
      while (rset.next()) {
        request =
            "requestNumber: "
                + rset.getInt("requestNumber")
                + ", start: "
                + rset.getString("start")
                + ", time: "
                + rset.getTimestamp("time")
                + ", name: "
                + rset.getString("name")
                + ", progress: "
                + rset.getString("progress")
                + ", destination: "
                + rset.getString("destination");
        System.out.println(request);
      }
      rset.close();
      pstmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
