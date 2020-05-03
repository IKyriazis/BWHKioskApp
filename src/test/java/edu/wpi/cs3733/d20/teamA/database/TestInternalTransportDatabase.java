package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestInternalTransportDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  GraphDatabase gDB;

  ServiceDatabase serviceDatabase;

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      gDB = new GraphDatabase(conn);
      serviceDatabase = new ServiceDatabase(conn);
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
  public void testAddRequest() {

    gDB.removeAllNodes();
    // need nodeID "biscuit" in node table so addrequest works
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("gravy", 2, 4, 2, "White House", "CONF", "basket", "b", "Team A");
    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", "basket", null);
    Assertions.assertTrue(serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", a));
    Assertions.assertEquals(1, serviceDatabase.getSize());
    String b =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", "yolk", null);
    Assertions.assertNull(b);
    Assertions.assertEquals(1, serviceDatabase.getSize());
    String c =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", "balogna", null);
    Assertions.assertEquals(c, serviceDatabase.checkIfExistsString("SERVICEREQ", "reqID", c));
    Assertions.assertEquals(2, serviceDatabase.getSize());

    serviceDatabase.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testUpdateRequest() {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("gravy", 2, 4, 2, "White House", "CONF", "basket", "b", "Team A");
    gDB.addNode("help", 2, 4, 2, "White House", "CONF", "water", "b", "Team A");
    String a =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", "basket", null);
    boolean b = serviceDatabase.setDescription(a, "water");
    Assertions.assertTrue(b);

    serviceDatabase.removeAll();
    gDB.removeAllNodes();
  }

  @Test
  public void testGetRequestStatus() {
    gDB.removeAllNodes();
    gDB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    gDB.addNode("help", 2, 4, 2, "White House", "CONF", "water", "b", "Team A");
    serviceDatabase.removeAll();
    String a =
        serviceDatabase.addServiceReq(ServiceType.INTERNAL_TRANSPORT, "balogna", "water", null);
    boolean b = serviceDatabase.setStatus(a, "In Progress");
    Assertions.assertTrue(
        serviceDatabase.checkIfExistsString("SERVICEREQ", "status", "In Progress"));
    Assertions.assertTrue(b);

    serviceDatabase.removeAll();
    gDB.removeAllNodes();
  }
}
