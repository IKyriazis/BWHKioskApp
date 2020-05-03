package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.itticket.ITTicketDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestITTicketDatabse {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  ServiceDatabase serviceDatabase;
  GraphDatabase DB;

  public TestITTicketDatabse() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      DB = new GraphDatabase(conn);
      tDB = new ITTicketDatabase(conn);
      DB.addNode("MDEPT00325", 1, 1, 1, "Main", "DEPT", "LongName", "ShortName", "Team A");
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
  public void testTable() {
    tDB.dropTables();
    boolean dropTables = tDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = tDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = tDB.dropTables();
    Assertions.assertTrue(dropTables2);
    tDB.createTables();
  }

  @Test
  public void testAddITTicket() throws SQLException {
    tDB.removeAllITTickets();
    boolean a =
        tDB.addTicket(
            new Timestamp(System.currentTimeMillis()),
            "In Progress",
            "Email",
            "MDEPT00325",
            "Luke",
            "Adam",
            "this is a test");
    Assertions.assertTrue(a);
    boolean d =
        tDB.addTicket(
            new Timestamp(System.currentTimeMillis()),
            "Ticket Sent",
            "Wifi",
            "MDEPT00325",
            "Sam",
            "Peter",
            "this is a test");
    Assertions.assertTrue(d);
    boolean b =
        tDB.addTicket(
            new Timestamp(System.currentTimeMillis()),
            "Sent",
            "Wifi",
            "MDEPT00325",
            "Sam",
            "Peter",
            "this is a test");
    Assertions.assertFalse(b);
    boolean c =
        tDB.addTicket(
            new Timestamp(System.currentTimeMillis()),
            "Ticket Sent",
            "Wifi",
            "t",
            "Sam",
            "Peter",
            "this is a test");
    Assertions.assertFalse(c);
    Assertions.assertEquals(2, tDB.getSizeITTickets());
  }

  @Test
  public void testUpdateStatus() throws SQLException {
    tDB.removeAllITTickets();
    Timestamp ticketTime = new Timestamp(System.currentTimeMillis());
    tDB.addTicket(
        ticketTime, "In Progress", "Email", "MDEPT00325", "Luke", "Adam", "this is a test");

    Assertions.assertTrue(tDB.changeStatus(ticketTime, "Ticket Sent", "Joe"));
    tDB.removeAllITTickets();
  }
}
