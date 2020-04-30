package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnnouncementDatabase extends Database {

  int announcementID;

  public AnnouncementDatabase(Connection connection) {
    super(connection);
    createTables();
    announcementID = getRandomInt();
  }

  public boolean createTables() {
    if (doesTableNotExist("ANNOUNCEMENTS")) {
      return helperPrepared(
          "CREATE TABLE ANNOUNCEMENTS(announcementID INTEGER PRIMARY KEY, announcement VARCHAR(50))");
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
  public boolean removeAllAnnouncements() {
    return helperPrepared("DELETE From ANNOUNCEMENTS");
  }

  /**
   * Returns the size of the table
   *
   * @return int size
   */
  public int getSizeAnnouncements() {
    return getSize("ANNOUNCEMENTS");
  }

  public int addAnnouncement(int announcementID, String announcement) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO ANNOUNCEMENTS (announcementID, announcement) VALUES (?, ?)");
      pstmt.setInt(1, announcementID);
      pstmt.setString(2, announcement);
      pstmt.executeUpdate();
      pstmt.close();
      return announcementID;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public int addAnnouncement(String announcement) {
    this.announcementID = getRandomInt();
    return addAnnouncement(this.announcementID, announcement);
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

  public boolean removeAnnouncement(int id) {
    return helperPrepared("DELETE From ANNOUNCEMENTS WHERE announcementID = " + id);
  }

  public ObservableList<Announcement> announcementObservableList() {
    ObservableList<Announcement> announcementObservableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM ANNOUNCEMENTS");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        int announcementID = rset.getInt("announcementID");
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
