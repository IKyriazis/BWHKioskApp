package edu.wpi.cs3733.d20.teamA.database.services.interpreter;

import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.inventory.InventoryDatabase;
import edu.wpi.cs3733.d20.teamA.database.inventory.ItemType;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.graph.Campus;
import edu.wpi.cs3733.d20.teamA.graph.NodeType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestInterpreterDatabase {
  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  ServiceDatabase serviceDatabase;
  InventoryDatabase inventoryDatabase;
  EmployeesDatabase employeesDatabase;
  GraphDatabase gDB;

  public TestInterpreterDatabase() {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    gDB = new GraphDatabase(conn);
    employeesDatabase = new EmployeesDatabase(conn);
    inventoryDatabase = new InventoryDatabase(conn);
    serviceDatabase = new ServiceDatabase(conn);
    employeesDatabase.addEmployee(
        "Yash", "Patel", "yppatel", "YashPatel1", EmployeeTitle.ADMIN, "7738495743l");
    employeesDatabase.logIn("yppatel", "YashPatel1");
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
    gDB.createTables();
    serviceDatabase.dropTables();

    boolean dropTables = serviceDatabase.dropTables();
    Assertions.assertFalse(dropTables);

    boolean makeTables = serviceDatabase.createTables();
    Assertions.assertTrue(makeTables);

    boolean dropTables2 = serviceDatabase.dropTables();
    Assertions.assertTrue(dropTables2);
    serviceDatabase.createTables();
  }

  @Test
  public void testAddInterpreter() {
    serviceDatabase.removeAll();
    inventoryDatabase.removeAll();

    String a =
        inventoryDatabase.addItem(ItemType.INTERPRETER, "Sam Har", 1, 0.0, null, "Portugese");
    Assertions.assertEquals(a, inventoryDatabase.getID("Sam Har"));

    String d = inventoryDatabase.addItem(ItemType.INTERPRETER, "Yash2", 1, 0.0, null, "French");
    Assertions.assertEquals(d, inventoryDatabase.getID("Yash2"));

    String f = inventoryDatabase.addItem(ItemType.INTERPRETER, "Yash2", 1, 0.0, null, "Latin");
    Assertions.assertNull(f);

    Assertions.assertEquals(2, inventoryDatabase.getSize("INVENTORY"));
    inventoryDatabase.removeAll();
  }

  @Test
  public void testAddRequest() {
    gDB.removeAll(Campus.FAULKNER);
    gDB.addNode("cookie", 5, 5, 1, "Main", NodeType.HALL.name(), "", "", "", Campus.FAULKNER);

    inventoryDatabase.removeAll();
    serviceDatabase.removeAll();

    inventoryDatabase.addItem(ItemType.INTERPRETER, "Yash", 0, 0.0, null, "French");

    String a = serviceDatabase.addServiceReq(ServiceType.JANITOR, "", "Yash", "");
    Assertions.assertEquals(1, serviceDatabase.getSize());

    serviceDatabase.deleteServReq(a);
    Assertions.assertEquals(0, serviceDatabase.getSize());

    serviceDatabase.removeAll();
    inventoryDatabase.removeAll();
    gDB.removeAll(Campus.FAULKNER);
  }
}
