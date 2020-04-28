package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestAnnouncementDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  AnnouncementDatabase announcementDatabase;

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      announcementDatabase = new AnnouncementDatabase(conn);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  public void teardown() {
    try {
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  @Test
  public void testTables() {
    announcementDatabase.dropTables();
    boolean dropTables = announcementDatabase.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = announcementDatabase.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = announcementDatabase.dropTables();
    Assertions.assertTrue(dropTables2);
    announcementDatabase.createTables();
  }
}
