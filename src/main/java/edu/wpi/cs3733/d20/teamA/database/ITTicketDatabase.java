package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class ITTicketDatabase extends Database {
    private int ticketNum = 0;

    public ITTicketDatabase(Connection connection) {
        super(connection);
        if (doesTableNotExist("ITTICKETS")) {
            createTables();
        }
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
                        + "timestamp VarChar(30) NOT NULL, "
                        + "status VarChar(50) NOT NULL, "
                        + "category Varchar(50) NOT NULL, "
                        + "location Varchar(10) NOT NULL, "
                        + "name Varchar(10) NOT NULL, "
                        + "completedBy Varchar(10) NOT NULL, "
                        + "description Varchar(200) NOT NULL, "
                        + "CONSTRAINT ITLOCATION FOREIGN KEY (location) REFERENCES Node(nodeID), "
                        + "CONSTRAINT ITStatus CHECK (status in ('Ticket Sent', 'In Progress', 'Complete')))");
    }

    public boolean addTicket(String category, String location, String name, String description) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String initStatus = "Ticket Sent";
        String initCompletedBy = "";
        try {
            // creates the prepared statement that will be sent to the database
            PreparedStatement pstmt =
                    getConnection()
                            .prepareStatement(
                                    "INSERT INTO ITTickets (ticketNum, timestamp , status, category, location, name, completedBy, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            // sets all the parameters of the prepared statement string
            pstmt.setInt(1, ticketNum);
            pstmt.setTimestamp(2, timestamp);
            pstmt.setString(3, initStatus);
            pstmt.setString(4, category);
            pstmt.setString(5, location);
            pstmt.setString(6, name);
            pstmt.setString(7, initCompletedBy);
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

    //  public ObservableList<ITTicket> ITTicketObervableList() {
    //    ObservableList<ITTicket> obervableITTicketList = FXCollections.observableArrayList();
    //    try {
    //      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
    //      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Orders");
    //      ResultSet rset = pstmt.executeQuery();
    //      while (rset.next()) {
    //        int orderNumber = rset.getInt("orderNumber");
    //        int numFlowers = rset.getInt("numFlowers");
    //        String flowerType = rset.getString("flowerType");
    //        String flowerColor = rset.getString("flowerColor");
    //        double price = rset.getDouble("price");
    //        String status = rset.getString("status");
    //        String location = rset.getString("location");
    //
    //        Order node =
    //                new Order(orderNumber, numFlowers, flowerType, flowerColor, price, status,
    // location);
    //        obervableITTicketList.add(node);
    //      }
    //      rset.close();
    //      pstmt.close();
    //      conn.close();
    //      return obervableITTicketList;
    //    } catch (SQLException e) {
    //      e.printStackTrace();
    //      return obervableITTicketList;
    //    }
    //  }
}
