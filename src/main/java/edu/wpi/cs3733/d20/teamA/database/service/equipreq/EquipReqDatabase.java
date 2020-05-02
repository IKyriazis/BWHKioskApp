package edu.wpi.cs3733.d20.teamA.database.service.equipreq;

import edu.wpi.cs3733.d20.teamA.database.Database;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EquipReqDatabase extends Database {

  public EquipReqDatabase(Connection connection) {

    super(connection);

    if (doesTableNotExist("EQUIPREQ")) {
      createTables();
    }
  }

  /**
   * Drop the 'EquipReq' table
   *
   * @return Success / Failure
   */
  public synchronized boolean dropTables() {

    // Drop the tables
    if (!(helperPrepared("ALTER TABLE EquipReq DROP CONSTRAINT FK_EID"))) {

      return false;
    }

    // Drop the tables
    if (!(helperPrepared("DROP TABLE EquipReq"))) {
      return false;
    }

    return true;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   */
  public synchronized boolean createTables() {

    // Create the graph tables

    return helperPrepared(
        "CREATE TABLE EquipReq (username Varchar(25), timeOf TIMESTAMP, item Varchar(75) NOT NULL, qty INTEGER, location Varchar(10) NOT NULL, priority Varchar(7) NOT NULL, CONSTRAINT CK_Q CHECK (qty >= 0), CONSTRAINT FK_EID FOREIGN KEY (username) REFERENCES Employees(username), CONSTRAINT PK_ET PRIMARY KEY(username, timeOf), CONSTRAINT CHK_PRI CHECK (priority in ('High', 'Medium', 'Low')), CONSTRAINT FK_NLOC FOREIGN KEY (location) REFERENCES NODE(nodeID))");
  }

  public boolean addReq(String item, int qty, String location, String priority) {
    String username = getLoggedIn();
    Timestamp timeOf = new Timestamp(System.currentTimeMillis());
    if (username == null) {
      return false;
    }
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO EquipReq (username, timeOf, item, qty, location, priority) VALUES (?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, username);
      pstmt.setTimestamp(2, timeOf);
      pstmt.setString(3, item);
      pstmt.setInt(4, qty);
      pstmt.setString(5, location);
      pstmt.setString(6, priority);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deleteReq(String username, String timeOf) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "DELETE From EquipReq Where username = '"
                      + username
                      + "' AND timeOf = '"
                      + timeOf
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getSizeReq() {
    return getSize("EquipReq");
  }

  public boolean removeAllReqs() {
    return helperPrepared("DELETE From EquipReq");
  }

  public String getTime(String username) {
    Timestamp timeOf;
    try {
      Statement priceStmt = getConnection().createStatement();
      ResultSet rst =
          priceStmt.executeQuery("SELECT * FROM EquipReq WHERE username = '" + username + "'");
      ;
      rst.next();
      timeOf = rst.getTimestamp("timeOf");
      return timeOf.toString();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public ObservableList<EquipRequest> ReqOl() {
    ObservableList<EquipRequest> rList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM EquipReq");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String name = getNamefromUser(rset.getString("username"));
        Timestamp timeOf = rset.getTimestamp("timeOF");
        String item = rset.getString("item");
        int qty = rset.getInt("qty");
        String location = rset.getString("location");
        String priority = rset.getString("priority");
        String username = rset.getString("username");

        EquipRequest node = new EquipRequest(name, item, null, null, "");

        rList.add(node);
      }
      rset.close();
      pstmt.close();
      conn.close();
      return rList;
    } catch (SQLException e) {
      e.printStackTrace();
      return rList;
    }
  }
}
