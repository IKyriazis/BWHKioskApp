package edu.wpi.cs3733.d20.teamA.database.service;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import edu.wpi.cs3733.d20.teamA.database.Database;
import edu.wpi.cs3733.d20.teamA.database.IDatabase;
import edu.wpi.cs3733.d20.teamA.database.TableItemFactory;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServiceDatabase extends Database implements IDatabase<ITableable> {

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
   * Creates Service Request table
   *
   * @return False if table couldn't be created
   */
  public synchronized boolean createTables() {

    if (doesTableNotExist("SERVICEREQ")) {
      return helperPrepared(
          "CREATE TABLE SERVICEREQ (servType Varchar(10), reqID Varchar(6) PRIMARY KEY, didReqName Varchar(25), madeReqName Varchar(25), timeOfReq Timestamp, status Varchar(20), location Varchar(200), description Varchar(100), additional Varchar(2000), CONSTRAINT CK_TYPE CHECK (servType in ('janitor', 'medicine', 'equipreq', 'laundry', 'ittix', 'intrntrans', 'interpret', 'rxreq')), CONSTRAINT FK_Location FOREIGN KEY (location) REFERENCES Node(longName), CONSTRAINT CK_STAT CHECK (status in ('Request Made', 'In Progress', 'Completed', 'Prescribed')))");
    }
    return false;
  }

  public synchronized String addServiceReq(
      ServiceType servType, String location, String description, String additional) {

    String reqID = getRandomString();
    boolean c = checkIfExistsString("ServiceReq", "reqID", reqID);
    while (c) {
      reqID = getRandomString();
      c = checkIfExistsString("ServiceReq", "reqID", reqID);
    }
    String madeReqName = getLoggedIn().getUsername();
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

  public synchronized String addServiceReq(
      ServiceType servType,
      String location,
      String didReqName,
      String description,
      String additional) {

    String reqID = getRandomString();
    boolean c = checkIfExistsString("ServiceReq", "reqID", reqID);
    while (c) {
      reqID = getRandomString();
      c = checkIfExistsString("ServiceReq", "reqID", reqID);
    }
    String madeReqName = getLoggedIn().getUsername();
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
      pstmt.setString(7, (location == null || location.isEmpty()) ? null : location);
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

  public synchronized String addServiceReq(
      ServiceType servType, Timestamp t, String location, String description, String additional) {

    String reqID = getRandomString();
    boolean c = checkIfExistsString("ServiceReq", "reqID", reqID);
    while (c) {
      reqID = getRandomString();
      c = checkIfExistsString("ServiceReq", "reqID", reqID);
    }
    String madeReqName = getLoggedIn().getUsername();
    String didReqName = null;
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
      pstmt.setTimestamp(5, t);
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

  public synchronized boolean deleteServReq(String reqID) {
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

  public synchronized int getSize() {
    return getSize("SERVICEREQ");
  }

  public synchronized int getSize(ServiceType service) {
    String serviceString = service.toString();
    int count = 0;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "SELECT * FROM SERVICEREQ WHERE servType = '" + serviceString + "'");
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

  public synchronized boolean removeAll() {
    return helperPrepared("DELETE From SERVICEREQ");
  }

  public synchronized boolean removeAll(ServiceType service) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("DELETE FROM SERVICEREQ WHERE servType = '" + service + "'");
      pstmt.executeQuery();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
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
      String user = getLoggedIn().getUsername();
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

  public synchronized String helperGetString(String s, String col) {
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

  public synchronized String getStatus(String reqID) {
    return helperGetString(reqID, "status");
  }

  public synchronized String getDidReqName(String reqID) {
    return helperGetString(reqID, "didReqName");
  }

  public String getAdditional(String reqID) {
    return helperGetString(reqID, "additional");
  }
  // Setters for various fields
  public synchronized boolean setAssignedEmployee(String IDString, String didReqName) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET didReqName = '"
                      + didReqName
                      + "' WHERE reqID = '"
                      + IDString
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean setRequestingEmployee(String IDString, String madeReqName) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET madeReqName = '"
                      + madeReqName
                      + "' WHERE reqID = '"
                      + IDString
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean setStatus(String IDString, String status) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET status = '"
                      + status
                      + "' WHERE reqID = '"
                      + IDString
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean setAdditional(String IDString, String add) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET additional = '"
                      + add
                      + "' WHERE reqID = '"
                      + IDString
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean setDescription(String IDString, String desc) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE SERVICEREQ SET description = '"
                      + desc
                      + "' WHERE reqID = '"
                      + IDString
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized ObservableList<ITableable> getObservableList() {
    ObservableList<ITableable> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM SERVICEREQ");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String servType = rset.getString("servType");
        String id = rset.getString("reqID");
        String didReqName = rset.getString("didReqName");
        String madeReqName = rset.getString("madeReqName");
        Timestamp timeOfReq = rset.getTimestamp("timeOfReq");
        String status = rset.getString("status");
        String location = rset.getString("location");
        String description = rset.getString("description");
        String additional = rset.getString("additional");
        ITableable item =
            TableItemFactory.getService(
                servType,
                id,
                didReqName,
                madeReqName,
                timeOfReq,
                status,
                location,
                description,
                additional);
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

  public synchronized ObservableList<ServiceRequest> getGenericObservableList() {
    ObservableList<ServiceRequest> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM SERVICEREQ");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        ServiceType servType = ServiceType.getServiceType(rset.getString("servType"));
        String id = rset.getString("reqID");
        String didReqName = rset.getString("didReqName");
        String madeReqName = rset.getString("madeReqName");
        Timestamp timeOfReq = rset.getTimestamp("timeOfReq");
        String status = rset.getString("status");
        String location = rset.getString("location");
        String description = rset.getString("description");
        String additional = rset.getString("additional");
        ServiceRequest request =
            new ServiceRequest(
                servType,
                id,
                didReqName,
                madeReqName,
                timeOfReq.toString(),
                status,
                location,
                description,
                additional);
        observableList.add(request);
      }
      rset.close();
      pstmt.close();
      return observableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public synchronized ObservableList<ITableable> getObservableListService(ServiceType type) {
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
            TableItemFactory.getService(
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
