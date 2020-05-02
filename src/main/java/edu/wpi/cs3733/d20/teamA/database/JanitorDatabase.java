package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JanitorDatabase extends Database {
  private int requestCount;

  /**
   * Sets the connection to the database
   *
   * @param connection connection
   */
  public JanitorDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("JANITORREQUEST")) {
      createTables();
    }

    requestCount = getRandomInt();
  }

  /**
   * Drops the janitor tables so we can start fresh
   *
   * @return false if the tables don't exist, true if table is dropped correctly
   */
  public boolean dropTables() {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    if (!(helperPrepared("ALTER TABLE JanitorRequest DROP CONSTRAINT FK_L"))) {

      return false;
    }
    // Drop the tables
    return helperPrepared("DROP TABLE JanitorRequest");
  }

  /**
   * Creates janitor tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the janitorrequest table
    return helperPrepared(
        "CREATE TABLE JanitorRequest (requestNumber INTEGER PRIMARY KEY, time TIMESTAMP NOT NULL, location Varchar(10) NOT NULL, longName Varchar(100), previousNodeCost INTEGER, employeeName Varchar(30), progress Varchar(19) NOT NULL, priority Varchar(10) NOT NULL, CONSTRAINT FK_L FOREIGN KEY (location) REFERENCES Node(nodeID), CONSTRAINT CHK_PRIO CHECK (priority in ('Low', 'Medium', 'High')), CONSTRAINT CHK_PROG CHECK (progress in ('Reported', 'Dispatched', 'Done')))");
  }

  /**
   * Sets the requestCount to 0
   *
   * @return true if everything could be deleted
   */
  public boolean removeAll() {
    requestCount = 0;
    return helperPrepared("DELETE From JanitorRequest");
  }

  /**
   * Adds janitor service request given location, and priority
   *
   * @return False if request couldn't be added
   */
  public int addRequest(String location, String priority, String employeeName, String longName) {

    // creates a timestamp of the time that the function is called
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    // default status is reported
    String progress = "Reported";
    requestCount = getRandomInt();
    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO JanitorRequest (time, location, longName, employeeName, progress, priority, requestNumber) VALUES (?, ?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setTimestamp(1, timestamp);
      pstmt.setString(2, location);
      pstmt.setString(3, longName);
      pstmt.setString(4, employeeName);
      pstmt.setString(5, progress);
      pstmt.setString(6, priority);

      // first request starts at 1 and increments every time a new request is added
      pstmt.setInt(7, requestCount);
      pstmt.executeUpdate();
      pstmt.close();
      // return true if the request is added
      return requestCount;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * @param rn request number
   * @return true if the request was deleted
   */
  public boolean deleteRequest(int rn) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From JanitorRequest Where requestNumber = ?");
      pstmt.setInt(1, rn);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Deletes all the progress that have been completed
   *
   * @return true if requests were deleted
   */
  public boolean deleteDoneRequests() {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From JanitorRequest Where progress = 'Done'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Updates the rn with a certain progress
   *
   * @param rn request number
   * @param longName name
   * @param progress progress
   * @return true if the progress has been updated
   */
  public boolean updateRequest(int rn, String longName, String progress, String employeeName) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE JanitorRequest SET longName = ?, progress = ?, employeeName = ? Where requestNumber = ?");
      pstmt.setString(1, longName);
      pstmt.setString(2, progress);
      pstmt.setInt(4, rn);
      pstmt.setString(3, employeeName);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  //  public boolean updateEmployeeName(int rn, String employeeName) {
  //    try {
  //      PreparedStatement pstmt =
  //          getConnection()
  //              .prepareStatement("SELECT * FROM JanitorRequest WHERE requestNumber = " + rn);
  //      pstmt.executeUpdate();
  //      ResultSet rset = pstmt.executeQuery();
  //      rset.next();
  //      String name = rset.getString("name");
  //      String progress = rset.getString("progress");
  //      rset.close();
  //      pstmt.close();
  //      updateRequest(rn, name, progress, employeeName);
  //      return true;
  //    } catch (SQLException e) {
  //      e.printStackTrace();
  //      return false;
  //    }
  //  }

  /** Prints out the table */
  public void printTable() {
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * FROM JanitorRequest ");
      ResultSet rset = pstmt.executeQuery();
      String request;
      while (rset.next()) {
        request =
            "requestNumber: "
                + rset.getInt("requestNumber")
                + ", location: "
                + rset.getString("location")
                + ", time: "
                + rset.getTimestamp("time")
                + ", name: "
                + rset.getString("employeeName")
                + ", progress: "
                + rset.getString("progress")
                + ", priority: "
                + rset.getString("priority");
        System.out.println(request);
      }
      rset.close();
      pstmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String getRequest(int rn) {
    String request = "";
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM JanitorRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      request =
          "location: "
              + rset.getString("location")
              + ", employeeName: "
              + rset.getString("employeeName")
              + ", progress: "
              + rset.getString("progress")
              + ", priority: "
              + rset.getString("priority");
      pstmt.close();
      return request;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public int getRequestSize() {
    return getSize("JanitorRequest");
  }

  public Timestamp getTimestamp(int rn) {
    Timestamp ts;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("SELECT time FROM JanitorRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      ts = rset.getTimestamp("time");
      pstmt.close();
      return ts;
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
              .prepareStatement("SELECT name FROM JanitorRequest WHERE requestNumber = ?");
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

  public String getLocation(int rn) {
    String n;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("SELECT location FROM JanitorRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      n = rset.getString("location");
      pstmt.close();
      return n;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getProgress(int rn) {
    String n;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("SELECT progress FROM JanitorRequest WHERE requestNumber = ?");
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

  public String getPriority(int rn) {
    String n;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("SELECT priority FROM JanitorRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      n = rset.getString("priority");
      pstmt.close();
      return n;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getEmployeeName(int rn) {
    String n;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("SELECT priority FROM JanitorRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      n = rset.getString("employeeName");
      pstmt.close();
      return n;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public ObservableList<JanitorService> janitor01() {
    ObservableList<JanitorService> oList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM JanitorRequest");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String location = rset.getString("location");
        String priority = rset.getString("priority");
        String status = rset.getString("progress");
        String employeeName = rset.getString("employeeName");
        int requestNumber = rset.getInt("requestNumber");
        String longName = rset.getString("longName");

        JanitorService node =
            new JanitorService(
                location, priority, status, employeeName, "" + requestNumber, longName);
        oList.add(node);
      }
      rset.close();
      pstmt.close();
      conn.close();
      return oList;
    } catch (SQLException e) {
      e.printStackTrace();
      return oList;
    }
  }
}
