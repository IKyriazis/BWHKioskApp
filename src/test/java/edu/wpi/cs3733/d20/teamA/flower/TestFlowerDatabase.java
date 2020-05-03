package edu.wpi.cs3733.d20.teamA.flower;

import edu.wpi.cs3733.d20.teamA.database.flower.Flower;
import edu.wpi.cs3733.d20.teamA.database.flower.FlowerDatabase;
import edu.wpi.cs3733.d20.teamA.database.flower.FlowerEmployee;
import edu.wpi.cs3733.d20.teamA.database.flower.Order;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestFlowerDatabase {

  private static final String jdbcUrl = "jdbc:derby:memory:BWDatabase;create=true";
  private static final String closeUrl = "jdbc:derby:memory:BWDatabase;drop=true";
  private Connection conn;
  FlowerDatabase fDB;
  GraphDatabase DB;

  public TestFlowerDatabase() {}

  @BeforeEach
  public void init() {
    try {
      conn = DriverManager.getConnection(jdbcUrl);
      DB = new GraphDatabase(conn);
      fDB = new FlowerDatabase(conn);
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
    fDB.dropTables();
    boolean dropTables = fDB.dropTables();
    Assertions.assertFalse(dropTables);
    boolean makeTables = fDB.createTables();
    Assertions.assertTrue(makeTables);
    boolean dropTables2 = fDB.dropTables();
    Assertions.assertTrue(dropTables2);
    fDB.createTables();
  }

  @Test
  public void testAddFlower() throws SQLException {
    fDB.createTables();
    fDB.removeAllFlowers();
    boolean a = fDB.addFlower("Rose", "Yellow", 6, 2.45);
    Assertions.assertTrue(a);
    boolean d = fDB.addFlower("Rose", "Red", 6, 2.00);
    Assertions.assertTrue(d);
    boolean b = fDB.addFlower("Daisy", "Blue", -1, 3.45);
    Assertions.assertFalse(b);
    boolean e = fDB.addFlower("Daisy", "Purple", 7, 3);
    Assertions.assertTrue(e);
    boolean c = fDB.addFlower("Lily", "Green", 1, 567.323);
    Assertions.assertFalse(c);
    Assertions.assertEquals(3, fDB.getSizeFlowers());
    fDB.removeAllFlowers();
  }

  @Test
  public void testUpdatePrice() throws SQLException {
    fDB.createTables();
    fDB.removeAllFlowers();
    fDB.addFlower("Rose", "Yellow", 6, 2.45);
    boolean a = fDB.updatePrice("Rose", "Yellow", 2.88);
    Assertions.assertTrue(a);
    fDB.addFlower("Daisy", "Yellow", 6, 2.45);
    boolean b = fDB.updatePrice("Daisy", "Yellow", 3.996);
    Assertions.assertFalse(b);
    fDB.removeAllFlowers();
  }

  @Test
  public void testUpdateQty() throws SQLException {
    fDB.createTables();
    fDB.removeAllFlowers();
    fDB.addFlower("Rose", "Yellow", 6, 2.45);
    boolean a = fDB.updateQTY("Rose", "Yellow", 3);
    Assertions.assertTrue(a);
    fDB.addFlower("Daisy", "Yellow", 6, 2.45);
    boolean b = fDB.updateQTY("Daisy", "Yellow", -1);
    Assertions.assertFalse(b);
    fDB.removeAllFlowers();
  }

  @Test
  public void testDeleteFlower() throws SQLException {
    fDB.createTables();
    fDB.removeAllFlowers();
    fDB.addFlower("Rose", "Yellow", 6, 2.45);
    fDB.deleteFlower("Rose", "Yellow");
    Assertions.assertEquals(0, fDB.getSizeFlowers());
    fDB.removeAllFlowers();
  }

  @Test
  public void testAddOrder() throws SQLException {
    DB.createTables();
    fDB.createTables();
    fDB.removeAllOrders();
    DB.removeAll();
    Assertions.assertEquals(0, fDB.getSizeOrders());
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    fDB.addFlowerWithID(1, "Daisy", "Blue", 10, 1.20);
    fDB.addOrder(5, "1/5|", "biscuit", "stuff", 6);
    fDB.addOrder(-1, "1/-1|", "biscuit", "thing", 0);
    fDB.addOrder(1, "1/1|", "biscuit", "foo", 1.2);
    Assertions.assertEquals(2, fDB.getSizeOrders());
    // Assertions.assertEquals(5, fDB.getFlowerQuantity("Daisy", "Blue"));
    fDB.removeAllOrders();
    DB.removeAll();
  }

  @Test
  public void testDeleteOrder() throws SQLException {
    DB.createTables();
    fDB.createTables();
    fDB.removeAllOrders();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    fDB.addFlower("Daisy", "Blue", 20, 1.20);
    fDB.addOrder(5, "1/5|", "biscuit", "mess", 2);
    boolean a = fDB.deleteOrder(1);
    Assertions.assertTrue(a);
    Assertions.assertEquals(0, fDB.getSizeOrders());
    fDB.addOrder(2, "1/2|", "biscuit", "ms", 3);
    fDB.addOrder(6, "1/6|", "biscuit", "ms", 4);
    boolean r = fDB.deleteOrder(2);
    Assertions.assertTrue(r);
    Assertions.assertEquals(1, fDB.getSizeOrders());
    fDB.removeAllOrders();
    DB.removeAll();
  }

  @Test
  public void testUpdateStatus() throws SQLException {
    DB.createTables();
    fDB.createTables();
    fDB.removeAllOrders();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    fDB.addOrder(5, "1/5|", "biscuit", "Mess", 2.3);
    boolean a = fDB.changeOrderStatus(5, "Order Received");
    Assertions.assertTrue(a);
    fDB.removeAllOrders();
    DB.removeAll();
  }

  @Test
  public void testFlowers() {
    Flower flr = new Flower(1, "Daisy", "Blue", 9, 9.20);
    Assertions.assertEquals("Daisy", flr.getTypeFlower());
    Assertions.assertEquals("Blue", flr.getColor());
    Assertions.assertEquals(9, flr.getQty());
    Assertions.assertEquals(9.20, flr.getPricePer());
    Assertions.assertEquals(1, flr.getFlowerID());
  }

  @Test
  public void testOrders() {
    Order flr = new Order(1, 4, "1/4|", 9.88, "Order Sent", "dsss", "Hi", -1);
    Assertions.assertEquals(1, flr.getOrderNumber());
    Assertions.assertEquals(4, flr.getNumFlowers());
    Assertions.assertEquals("1/4|", flr.getFlowerString());
    Assertions.assertEquals(9.88, flr.getPrice());
    Assertions.assertEquals("Order Sent", flr.getStatus());
    Assertions.assertEquals("dsss", flr.getLocation());
    Assertions.assertEquals("Hi", flr.getMessage());
  }

  @Test
  public void testEmployees() {
    FlowerEmployee emp = new FlowerEmployee("Bob", "LastName");
    Assertions.assertEquals("Bob LastName", emp.toString());
  }
}
