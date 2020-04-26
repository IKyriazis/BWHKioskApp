package edu.wpi.cs3733.d20.teamA.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ITTicketDatabase extends Database {
  private int ticketNum;

  public ITTicketDatabase(Connection connection) {
    super(connection);
    if (doesTableNotExist("ITTICKETS")) {
      createTables();
    }
    ticketNum = getSizeITTickets() + 1;
  }

  public boolean dropTables() {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    if (!(helperPrepared("ALTER TABLE ITTickets DROP CONSTRAINT FK_L"))) {

      return false;
    }
    // Drop the tables
    return helperPrepared("DROP TABLE ITTickets");
  }

  public boolean createTables() {
    // Create the graph tables
    return helperPrepared(
        "CREATE TABLE ITTickets (ticketNum INTEGER PRIMARY KEY, "
            + "ticketTime TIMESTAMP NOT NULL, "
            + "status VarChar(50) NOT NULL, "
            + "category Varchar(50) NOT NULL, "
            + "location Varchar(10) NOT NULL, "
            + "requesterName Varchar(25) NOT NULL, "
            + "completedBy Varchar(25) NOT NULL, "
            + "description Varchar(200) NOT NULL, "
            + "CONSTRAINT ITLocation FOREIGN KEY (location) REFERENCES Node(nodeID), "
            + "CONSTRAINT ITStatus CHECK (status in ('Ticket Sent', 'In Progress', 'Complete')))");
  }

  public boolean addTicket(
      Timestamp ticketTime,
      String status,
      String category,
      String location,
      String requesterName,
      String completedBy,
      String description) {
    System.out.println(completedBy);
    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO ITTickets (ticketNum, ticketTime , status, category, location, requesterName, completedBy, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setInt(1, ticketNum);
      pstmt.setTimestamp(2, ticketTime);
      pstmt.setString(3, status);
      pstmt.setString(4, category);
      pstmt.setString(5, location);
      pstmt.setString(6, requesterName);
      pstmt.setString(7, completedBy);
      pstmt.setString(8, description);
      pstmt.executeUpdate();
      pstmt.close();
      ticketNum++;
      // return true if the request is added
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getSizeITTickets() {
    return getSize("ITTickets");
  }

  public boolean changeStatus(int ticketNumber, String newStatus) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE ITTickets Set status = '"
                      + newStatus
                      + "' WHERE orderNumber = "
                      + ticketNumber);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public ObservableList<ITTicket> ITTicketObservableList() {
    ObservableList<ITTicket> ITTicketObservableList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ITTickets");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        int ticketNum = rset.getInt("ticketNum");
        Timestamp ticketTime = rset.getTimestamp("ticketTime");
        String status = rset.getString("status");
        String category = rset.getString("category");
        String location = rset.getString("location");
        String requesterName = rset.getString("requesterName");
        String completedBy = rset.getString("completedBy");
        String description = rset.getString("description");

        ITTicket node =
            new ITTicket(
                ticketTime, status, category, location, requesterName, completedBy, description);
        ITTicketObservableList.add(node);
      }
      rset.close();
      pstmt.close();
      conn.close();
      return ITTicketObservableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return ITTicketObservableList;
    }
  }

  public void readITTicketsCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/ITTicketsCSV.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      // Timestamp ticketTime = new Timestamp(System.currentTimeMillis());
      for (int i = 1; i < data.size(); i++) {
        Timestamp ticketTime;
        String status, category, location, requesterName, completedBy, description;
        ticketTime = Timestamp.valueOf(data.get(i)[0]);
        status = data.get(i)[1];
        category = data.get(i)[2];
        location = data.get(i)[3];
        requesterName = data.get(i)[4];
        completedBy = data.get(i)[5];
        description = data.get(i)[6];
        addTicket(ticketTime, status, category, location, requesterName, completedBy, description);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }
}
