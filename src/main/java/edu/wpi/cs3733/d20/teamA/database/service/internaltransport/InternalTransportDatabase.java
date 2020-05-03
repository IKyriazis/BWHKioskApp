package edu.wpi.cs3733.d20.teamA.database.service.internaltransport;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.database.Database;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InternalTransportDatabase extends Database {

  private int requestCount;

  public InternalTransportDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("INTERNALTRANSPORTREQUEST")) {
      createTables();
    }

    requestCount = getRandomInt();
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
    requestCount = getRandomInt();
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
      pstmt.setInt(5, requestCount);
      pstmt.executeUpdate();
      pstmt.close();
      // return requestCount if the request is added
      return requestCount;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public int getRequestSize() {
    return getSize("InternalTransportRequest");
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

  /**
   * Updates the rn with a certain progress
   *
   * @param rn request number
   * @param progress progress
   * @return true if the progress has been updated
   */
  public boolean updateRequest(int rn, String progress) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE InternalTransportRequest SET progress = ? Where requestNumber = ?");
      pstmt.setString(1, progress);
      pstmt.setInt(2, rn);
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

  private boolean addRequestFromCSV(
      int requestNumber,
      Timestamp time,
      String start,
      String destination,
      String name,
      String progress) {
    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO InternalTransportRequest (time, start, progress, destination, requestNumber, name) VALUES (?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setTimestamp(1, time);
      pstmt.setString(2, start);
      pstmt.setString(3, progress);
      pstmt.setString(4, destination);
      // first request starts at 1 and increments every time a new request is added
      pstmt.setInt(5, requestNumber);
      pstmt.setString(6, name);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /** Reads the internal transport csv file into the database */
  public void readInternalTransportCSV() {
    try {
      InputStream stream =
          getClass()
              .getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/InternalTransport.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
      for (int i = 1; i < data.size(); i++) {
        int requestNumber;
        String name, start, destination, progress;
        Timestamp time;
        requestNumber = Integer.parseInt(data.get(i)[0]);
        Date parsedDate = (Date) dateFormat.parse(data.get(i)[1]);
        time = new java.sql.Timestamp(parsedDate.getTime());
        start = data.get(i)[2];
        destination = data.get(i)[3];
        name = data.get(i)[4];
        progress = data.get(i)[5];
        addRequestFromCSV(requestNumber, time, start, destination, name, progress);
      }
    } catch (IOException | CsvException | ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets all requests in the table
   *
   * @return an observable list containing all orders in the table
   */
  public ObservableList<InternalTransportRequest> requestOl() {
    ObservableList<InternalTransportRequest> oList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM InternalTransportRequest");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        int orderNumber = rset.getInt("requestNumber");
        String start = rset.getString("start");
        String destination = rset.getString("destination");
        Timestamp time = rset.getTimestamp("time");
        String progress = rset.getString("progress");
        String name = rset.getString("name");

        /*  InternalTransportRequest node =
            new InternalTransportRequest(
                orderNumber, start, destination, time.toString(), progress, name);
        oList.add(node);*/
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
