package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServiceDatabase extends Database {

  public ServiceDatabase(Connection connection) {
    super(connection);
    createTables();
  }

  public synchronized boolean dropTables() {

    // Drop the tables
    if (!doesTableNotExist("SERVICEREQ")) {
      return (helperPrepared("DROP TABLE SERVICEREQ"));
    }

    return false;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   */
  public synchronized boolean createTables() {

    if (doesTableNotExist("SERVICEREQ")) {
      return helperPrepared(
          "CREATE TABLE SERVICEREQ (servType Varchar(10), reqID Varchar(6) PRIMARY KEY, didReqName Varchar(25), madeReqName Varchar(25), timeOfReq Timestamp, status Varchar(20), location Varchar(200), description Varchar(100), additional Varchar(2000), CONSTRAINT CK_TYPE CHECK (servType in ('janitor', 'medicine', 'equipreq', 'laundry', 'ittix', 'intrntrans', 'interpret', 'rxreq')), CONSTRAINT FK_REQEMP FOREIGN KEY (didReqName) REFERENCES EMPLOYEES(username), CONSTRAINT FK_EmpUse FOREIGN KEY (madeReqName) REFERENCES EMPLOYEES(username), CONSTRAINT FK_Location FOREIGN KEY (location) REFERENCES Node(longName), CONSTRAINT CK_STAT CHECK (status in ('Request Made', 'In Progress', 'Completed')))");
    }
    return false;
  }

  public synchronized String addServiceReq(
      ServiceType servType, String location, String description, String additional) {

    long l = getRandomNumber();
    String reqID = Long.toString(l, 36);
    boolean c = checkIfExistsString("ServiceReq", "reqID", reqID);
    while (c) {
      l = getRandomNumber();
      reqID = Long.toString(l, 36);
      reqID = ("000000" + reqID).substring(reqID.length() - 1);
      c = checkIfExistsString("ServiceReq", "reqID", reqID);
    }
    String madeReqName = getLoggedIn();
    String didReqName = null;
    Timestamp timeOf = new Timestamp(System.currentTimeMillis());
    String status = "Request Made";

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO SERVICEREQ (servType, reqID, didReqName, madeReqName, timeOfReq, status, location, description, additional) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, servType.toString());
      pstmt.setString(2, reqID);
      pstmt.setString(3, didReqName);
      pstmt.setString(4, madeReqName);
      pstmt.setTimestamp(5, timeOf);
      pstmt.setString(6, status);
      pstmt.setString(7, location);
      pstmt.setString(8, description);
      pstmt.setString(9, additional);
      pstmt.executeUpdate();
      pstmt.close();
      return reqID;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean deleteServReq(String reqID) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From SERVICEREQ Where reqID = '" + reqID + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getSizeReq() {
    return getSize("SERVICEREQ");
  }

  public boolean removeAllReqs() {
    return helperPrepared("DELETE From SERVICEREQ");
  }

  public synchronized boolean editStatus(String reqID, String newStatus) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET status = '"
                      + newStatus
                      + "' WHERE reqID = '"
                      + reqID
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      String user = getLoggedIn();
      PreparedStatement pstmt2 =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET didReqName = '"
                      + user
                      + "' WHERE reqID = '"
                      + reqID
                      + "'");
      pstmt2.executeUpdate();
      pstmt2.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public String helperGetString(String s, String col) {
    try {
      boolean a = checkIfExistsString("SERVICEREQ", "reqID", s);
      if (a) {
        PreparedStatement pstmt =
            getConnection().prepareStatement("SELECT * FROM SERVICEREQ WHERE reqID = ?");
        pstmt.setString(1, s);
        ResultSet rset = pstmt.executeQuery();
        rset.next();
        String stat = rset.getString(col);
        rset.close();
        pstmt.close();
        return stat;
      } else {
        return null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getStatus(String reqID) {
    return helperGetString(reqID, "status");
  }
  // Setters for various fields

  public boolean setAssignedEmployee(String IDString, String madeReqName) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET madeReqName = '"
                      + madeReqName
                      + "' WHERE reqID = '"
                      + IDString
                      + "''");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setStatus(String IDString, String status) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET status = '"
                      + status
                      + "' WHERE reqID = '"
                      + IDString
                      + "''");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized ObservableList<ITableable> observableList(ServiceType type) {
    ObservableList<ITableable> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "SELECT * FROM SERVICEREQ WHERE servType = '" + type.toString() + "'");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String id = rset.getString("reqID");
        String didReq = rset.getString("didReqName");
        String madeReq = rset.getString("madeReqName");
        Timestamp t = rset.getTimestamp("timeOfReq");
        String stat = rset.getString("status");
        String loc = rset.getString("location");
        String desc = rset.getString("description");
        String additional = rset.getString("additional");

        ITableable item =
            TableItemFactory.get(
                type.toString(), id, didReq, madeReq, t, stat, loc, desc, additional);
        observableList.add(item);
      }
      rset.close();
      pstmt.close();
      return observableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
