package edu.wpi.cs3733.d20.teamA.database.service.itticket;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;

import edu.wpi.cs3733.d20.teamA.database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ITTicketDatabase extends Database {

  public ITTicketDatabase(Connection connection) {
    super(connection);
    if (doesTableNotExist("ITTICKETS")) {
      createTables();
    }
  }

  public boolean dropTables() {

    if (!doesTableNotExist("ITTICKETS")) {
      // Drop the tables
      return helperPrepared("DROP TABLE ITTickets");
    }
    return false;
  }

  public boolean createTables() {
    // Create the graph tables
    if (doesTableNotExist("ITTICKETS")) {
      return helperPrepared(
          "CREATE TABLE ITTickets (ticketTime TIMESTAMP PRIMARY KEY, "
              + "status VarChar(15) NOT NULL, "
              + "category Varchar(15) NOT NULL, "
              + "location Varchar(10) NOT NULL, "
              + "requesterName Varchar(25) NOT NULL, "
              + "completedBy Varchar(25) NOT NULL, "
              + "description Varchar(200) NOT NULL, "
              + "CONSTRAINT ITLocation FOREIGN KEY (location) REFERENCES Node(nodeID), "
              + "CONSTRAINT ITStatus CHECK (status in ('Ticket Sent', 'In Progress', 'Complete')))");
    }
    return false;
  }

  public boolean addTicket(
      Timestamp ticketTime,
      String status,
      String category,
      String location,
      String requesterName,
      String completedBy,
      String description) {
    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO ITTickets (ticketTime , status, category, location, requesterName, completedBy, description) VALUES (?, ?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setTimestamp(1, ticketTime);
      pstmt.setString(2, status);
      pstmt.setString(3, category);
      pstmt.setString(4, location);
      pstmt.setString(5, requesterName);
      pstmt.setString(6, completedBy);
      pstmt.setString(7, description);
      pstmt.executeUpdate();
      pstmt.close();
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

  public boolean removeAllITTickets() {
    return helperPrepared("DELETE From ITTickets");
  }

  public boolean changeStatus(Timestamp statusTicketTime, String newStatus, String newCompletedBy) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE ITTickets Set status = '"
                      + newStatus
                      + "', completedBy = '"
                      + newCompletedBy
                      + "' WHERE ticketTime = '"
                      + statusTicketTime
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deleteTicket(Timestamp deleteTicketTime) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "DELETE From ITTickets WHERE ticketTime = '" + deleteTicketTime + "'");
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
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM ITTickets");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        Timestamp ticketTime = rset.getTimestamp("ticketTime");
        String status = rset.getString("status");
        String category = rset.getString("category");
        String location = rset.getString("location");
        String requesterName = rset.getString("requesterName");
        String completedBy = rset.getString("completedBy");
        String description = rset.getString("description");

        /*ITTicket node =
            new ITTicket
                ticketTime, status, category, location, requesterName, completedBy, description);
        ITTicketObservableList.add(node);*/
      }
      rset.close();
      pstmt.close();
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
