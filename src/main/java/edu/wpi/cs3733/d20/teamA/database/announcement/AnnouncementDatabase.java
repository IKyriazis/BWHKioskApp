package edu.wpi.cs3733.d20.teamA.database.announcement;

import edu.wpi.cs3733.d20.teamA.database.Database;
import edu.wpi.cs3733.d20.teamA.database.IDatabase;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnnouncementDatabase extends Database implements IDatabase<Announcement> {

  public AnnouncementDatabase(Connection connection) {
    super(connection);
    createTables();
  }

  public boolean createTables() {
    if (doesTableNotExist("ANNOUNCEMENTS")) {
      return helperPrepared(
          "CREATE TABLE ANNOUNCEMENTS(announcementID VARCHAR(6) PRIMARY KEY, announcement VARCHAR(50))");
    }
    return false;
  }

  /**
   * Deletes the table from the database
   *
   * @return true if completed
   */
  public boolean dropTables() {

    return helperPrepared("DROP TABLE ANNOUNCEMENTS");
  }

  /**
   * Removes all info in the database
   *
   * @return
   */
  public boolean removeAll() {
    return helperPrepared("DELETE From ANNOUNCEMENTS");
  }

  @Override
  public ObservableList<Announcement> getObservableList() {
    ObservableList<Announcement> list = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Employees");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String id = rset.getString("announcementID");
        String announcement = rset.getString("announcement");
        Announcement e = new Announcement(id, announcement);
        list.add(e);
      }
      rset.close();
      pstmt.close();
      return list;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the size of the table
   *
   * @return int size
   */
  public int getSize() {
    return getSize("ANNOUNCEMENTS");
  }

  public String addAnnouncement(String announcementID, String announcement) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO ANNOUNCEMENTS (announcementID, announcement) VALUES (?, ?)");
      pstmt.setString(1, announcementID);
      pstmt.setString(2, announcement);
      pstmt.executeUpdate();
      pstmt.close();
      return announcementID;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public String addAnnouncement(String announcement) {
    return addAnnouncement(getRandomString(), announcement);
  }

  public String getAnnouncement(int id) {
    String announcement;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst = stmt.executeQuery("SELECT * FROM ANNOUNCEMENTS WHERE announcementID = " + id);
      rst.next();
      announcement = rst.getString("announcement");
      return announcement;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public boolean removeAnnouncement(String id) {
    return helperPrepared("DELETE From ANNOUNCEMENTS WHERE announcementID = '" + id + "'");
  }

  public ObservableList<Announcement> announcementObservableList() {
    ObservableList<Announcement> announcementObservableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM ANNOUNCEMENTS");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String announcementID = rset.getString("announcementID");
        String announcement = rset.getString("announcement");

        Announcement node = new Announcement(announcementID, announcement);
        announcementObservableList.add(node);
      }
      rset.close();
      pstmt.close();
      return announcementObservableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
