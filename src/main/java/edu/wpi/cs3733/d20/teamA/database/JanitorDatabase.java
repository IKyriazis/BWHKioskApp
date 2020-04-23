package edu.wpi.cs3733.d20.teamA.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;

public class JanitorDatabase extends Database {
  private int requestCount = 0;

  public JanitorDatabase(Connection connection) throws SQLException {
    super(connection);
  }

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

    try {
      // Create the janitorrequest table
      helperPrepared(
          "CREATE TABLE JanitorRequest (requestNumber INTEGER PRIMARY KEY, time TIMESTAMP NOT NULL, location Varchar(10) NOT NULL, name Varchar(15), progress Varchar(19) NOT NULL, priority Varchar(6) NOT NULL, CONSTRAINT FK_L FOREIGN KEY (location) REFERENCES Node(nodeID), CONSTRAINT CHK_PRIO CHECK (priority in ('Low', 'Medium', 'High')), CONSTRAINT CHK_PROG CHECK (progress in ('Reported', 'Dispatched', 'Done')))");
      return true;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  /**
   * Deletes everything from the JanitorRequest table
   *
   * @return false if the delete fails
   * @throws SQLException
   */
  public boolean removeAll() throws SQLException {
    requestCount = 0;
    return helperPrepared("DELETE From JanitorRequest");
  }

  /**
   * Adds janitor service request given location, and priority
   *
   * @return False if request couldn't be added
   * @throws SQLException
   */
  public boolean addRequest(String location, String priority) throws SQLException {
    // creates a timestamp of the time that the function is called
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    // default status is reported
    String progress = "Reported";
    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO JanitorRequest (time, location, progress, priority, requestNumber) VALUES (?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setTimestamp(1, timestamp);
      pstmt.setString(2, location);
      pstmt.setString(3, progress);
      pstmt.setString(4, priority);
      // first request starts at 1 and increments every time a new request is added
      pstmt.setInt(5, ++requestCount);
      pstmt.executeUpdate();
      pstmt.close();
      // return true if the request is added
      return true;
    } catch (SQLException e) {
      //      System.out.println("Add request failed");
      //      System.out.println(e.getMessage());
      // return false if the request couldn't be added
      return false;
    }
  }

  /**
   *
   * @param rn
   * @return
   * @throws SQLException
   */
  public boolean deleteRequest(int rn) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From JanitorRequest Where requestNumber = ?");
      pstmt.setInt(1, rn);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean deleteDoneRequests() throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From JanitorRequest Where progress = 'Done'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean updateRequest(int rn, String name, String progress) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE JanitorRequest SET name = ?, progress = ? Where requestNumber = ?");
      pstmt.setString(1, name);
      pstmt.setString(2, progress);
      pstmt.setInt(3, rn);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public void printTable() throws SQLException {
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
                + rset.getString("name")
                + ", progress: "
                + rset.getString("progress")
                + ", priority: "
                + rset.getString("priority");
        System.out.println(request);
      }
      rset.close();
      pstmt.close();
    } catch (SQLException e) {
    }
  }

  public String getRequest(int rn) throws SQLException {
    String request = "";
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM JanitorRequest WHERE requestNumber = ?");
      pstmt.setInt(1, rn);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      request =
          "requestNumber: "
              + rset.getInt("requestNumber")
              + ", location: "
              + rset.getString("location")
              + ", name: "
              + rset.getString("name")
              + ", progress: "
              + rset.getString("progress")
              + ", priority: "
              + rset.getString("priority");
      pstmt.close();
      return request;
    } catch (SQLException e) {
      return null;
    }
  }

  public int getRequestSize() throws SQLException {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From JanitorRequest ");
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

  public Timestamp getTimestamp(int rn) throws SQLException {
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
      return null;
    }
  }

  public String getName(int rn) throws SQLException {
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
      return null;
    }
  }

  public String getLocation(int rn) throws SQLException {
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
      return null;
    }
  }

  public String getProgress(int rn) throws SQLException {
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
      return null;
    }
  }

  public String getPriority(int rn) throws SQLException {
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
      return null;
    }
  }

  public void readFromCSV() throws IOException, CsvException, SQLException {
    InputStream stream =
        getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/JanitorRequest.csv");
    CSVReader reader = new CSVReader(new InputStreamReader(stream));
    List<String[]> data = reader.readAll();
    for (int i = 1; i < data.size(); i++) {
      String location, priority;
      location = data.get(i)[0];
      priority = data.get(i)[1];
      addRequest(location, priority);
    }
  }
}
