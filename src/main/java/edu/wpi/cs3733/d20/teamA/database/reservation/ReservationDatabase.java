package edu.wpi.cs3733.d20.teamA.database.reservation;

import edu.wpi.cs3733.d20.teamA.database.Database;
import edu.wpi.cs3733.d20.teamA.database.IDatabase;
import java.sql.*;
import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReservationDatabase extends Database implements IDatabase<Reservation> {

  public ReservationDatabase(Connection conn) {
    super(conn);
    createTables();
  }

  /**
   * Drops the reservation table
   *
   * @return True if the table was dropped
   */
  public boolean dropTables() {
    if (!doesTableNotExist("RESERVATION")) {
      return helperPrepared("DROP TABLE Reservation");
    }
    return false;
  }

  /**
   * Creates the reservation table
   *
   * @return True if the table was created
   */
  public boolean createTables() {
    if (doesTableNotExist("RESERVATION")) {
      return helperPrepared(
          "CREATE TABLE Reservation (requestedBy Varchar(25), startTime TIMESTAMP, endTime TIMESTAMP, areaReserved Varchar(50), CONSTRAINT FK_ResEmp FOREIGN KEY (requestedBy) REFERENCES Employees(username), CONSTRAINT PK_Res PRIMARY KEY (startTime, areaReserved))");
    }
    return false;
  }

  /**
   * Finds the number of entries in the table
   *
   * @return The size of the table
   */
  public int getSize() {
    return getSize("Reservation");
  }

  /**
   * Returns all the entries from the reservation table
   *
   * @return True if the removal was successful
   */
  public boolean removeAll() {
    return helperPrepared("DELETE FROM Reservation");
  }

  /**
   * Adds a reservation to the reservation table
   *
   * @param start - The starting time of the reservation
   * @param end - The ending time of the reservation
   * @param loc - Which room/bed is being reserved
   * @return True if it was added successfully
   */
  public boolean addRes(Calendar start, Calendar end, String loc) {
    Timestamp startT = new Timestamp(start.getTime().getTime());
    Timestamp endT = new Timestamp(end.getTime().getTime());
    boolean a = checkIfExistsTSandString("Reservation", "startTime", startT, "areaReserved", loc);
    try {
      if (!a) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "INSERT INTO Reservation (requestedBy, startTime, endTime, areaReserved) VALUES (?, ?, ?, ?)");
        pstmt.setString(1, getLoggedIn().getUsername());
        pstmt.setTimestamp(2, startT);
        pstmt.setTimestamp(3, endT);
        pstmt.setString(4, loc);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Removes a reservation from the reservation table
   *
   * @param start - The starting time of the reservation
   * @param loc - The location of the reservation
   * @return True if the reservation was deleted
   */
  public boolean deleteRes(Calendar start, String loc) {
    Timestamp startT = new Timestamp(start.getTime().getTime());
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("DELETE FROM Reservation WHERE startTime = ? AND areaReserved = ?");
      pstmt.setTimestamp(1, startT);
      pstmt.setString(2, loc);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Finds out who requested a specific reservation
   *
   * @param start - The start time of the reservation
   * @param loc - The location of the reservation
   * @return The username of the person who reserved it
   */
  public String getRequestedBy(Calendar start, String loc) {
    Timestamp startT = new Timestamp(start.getTime().getTime());
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "SELECT * FROM Reservation WHERE startTime = ? AND areaReserved = ?");
      pstmt.setTimestamp(1, startT);
      pstmt.setString(2, loc);
      ResultSet rset = pstmt.executeQuery();
      if (rset.next()) {
        String emp = rset.getString("requestedBy");
        rset.close();
        pstmt.close();
        return emp;
      }
      rset.close();
      pstmt.close();
      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Find out when a reservation ends
   *
   * @param start - The start time of the reservation
   * @param loc - The location of the reservation
   * @return The calendar value of the end time
   */
  public Calendar getEndTime(Calendar start, String loc) {
    Timestamp startT = new Timestamp(start.getTime().getTime());
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "SELECT * FROM Reservation WHERE startTime = ? AND areaReserved = ?");
      pstmt.setTimestamp(1, startT);
      pstmt.setString(2, loc);
      ResultSet rset = pstmt.executeQuery();
      if (rset.next()) {
        Timestamp ts = rset.getTimestamp("endTime");
        Calendar endT = Calendar.getInstance();
        endT.setTimeInMillis(ts.getTime());
        rset.close();
        pstmt.close();
        return endT;
      }
      rset.close();
      pstmt.close();
      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sets the person who requested the reservation to the given value
   *
   * @param start - The starting time of the reservation
   * @param loc - The location of the reservation
   * @param req - The requester to be set to
   * @return True if the update was made successfully
   */
  public boolean setRequestedBy(Calendar start, String loc, String req) {
    Timestamp startT = new Timestamp(start.getTime().getTime());
    boolean a = checkIfExistsTSandString("Reservation", "startTime", startT, "areaReserved", loc);
    try {
      if (a) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE Reservation SET requestedBy = ? WHERE startTime = ? AND areaReserved = ?");
        pstmt.setString(1, req);
        pstmt.setTimestamp(2, startT);
        pstmt.setString(3, loc);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Sets the ending time of the reservation to the given time
   *
   * @param start - The starting time of the request
   * @param loc - The location of the request
   * @param end - The new ending time
   * @return True if the update was made successfully
   */
  public boolean setEndTime(Calendar start, String loc, Calendar end) {
    Timestamp startT = new Timestamp(start.getTime().getTime());
    Timestamp endT = new Timestamp(end.getTime().getTime());
    boolean a = checkIfExistsTSandString("Reservation", "startTime", startT, "areaReserved", loc);
    try {
      if (a) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE Reservation SET endTime = ? WHERE startTime = ? AND areaReserved = ?");
        pstmt.setTimestamp(1, endT);
        pstmt.setTimestamp(2, startT);
        pstmt.setString(3, loc);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public ObservableList<Reservation> getObservableList() {
    ObservableList<Reservation> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Reservation");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String emp = rset.getString("requestedBy");
        Timestamp startT = rset.getTimestamp("startTime");
        Timestamp endT = rset.getTimestamp("endTime");
        String loc = rset.getString("areaReserved");
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startT.getTime());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endT.getTime());
        Calendar check = Calendar.getInstance();
        if (end.compareTo(check) >= 0) {
          observableList.add(new Reservation(emp, start, end, loc));
        } else {
          deleteRes(start, loc);
        }
      }
      rset.close();
      pstmt.close();
      return observableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public ObservableList<Reservation> getObservableListByRoom(String room) {
    ObservableList<Reservation> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Reservation WHERE areaReserved = ?");
      pstmt.setString(1, room);
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String emp = rset.getString("requestedBy");
        Timestamp startT = rset.getTimestamp("startTime");
        Timestamp endT = rset.getTimestamp("endTime");
        String loc = rset.getString("areaReserved");
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startT.getTime());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endT.getTime());
        Calendar check = Calendar.getInstance();
        if (end.compareTo(check) >= 0) {
          observableList.add(new Reservation(emp, start, end, loc));
        } else {
          deleteRes(start, loc);
        }
      }
      rset.close();
      pstmt.close();
      return observableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public ObservableList<Reservation> getObservableListByUser() {
    ObservableList<Reservation> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Reservation WHERE requestedBy = ?");
      pstmt.setString(1, getLoggedIn().getUsername());
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String emp = rset.getString("requestedBy");
        Timestamp startT = rset.getTimestamp("startTime");
        Timestamp endT = rset.getTimestamp("endTime");
        String loc = rset.getString("areaReserved");
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startT.getTime());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endT.getTime());
        Calendar check = Calendar.getInstance();
        if (end.compareTo(check) >= 0) {
          observableList.add(new Reservation(emp, start, end, loc));
        } else {
          deleteRes(start, loc);
        }
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
