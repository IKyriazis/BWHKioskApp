package edu.wpi.cs3733.d20.teamA.database;

import com.sun.source.tree.AssertTree;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
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
  GraphDatabase graphDatabase;

  public TestITTicketDatabse() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      graphDatabase = new GraphDatabase(conn);
      serviceDatabase = new ServiceDatabase(conn);
      graphDatabase.addNode("MDEPT00325", 1, 1, 1, "Main", "DEPT", "LongName", "ShortName", "Team A");
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
  public void testAddITTicket(){
    serviceDatabase.removeAll();
    String a = serviceDatabase.addServiceReq(ServiceType.IT_TICKET, "LongName", "Machine Broke", "WIFI");
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", a));
    Assertions.assertEquals(1, serviceDatabase.getSize());
  }

  @Test
  public void testUpdateStatus() {
    serviceDatabase.removeAll();
    Timestamp ticketTime = new Timestamp(System.currentTimeMillis());
    String a = serviceDatabase.addServiceReq(ServiceType.IT_TICKET, "LongName", "Machine Broke", "WIFI");
    boolean b = serviceDatabase.setStatus(a, "Completed");
    Assertions.assertTrue(b);
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "status", "Completed"));
  }
}
