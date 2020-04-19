package edu.wpi.cs3733.d20.teamA.database;

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

  public TestFlowerDatabase() throws SQLException {}

  @BeforeEach
  public void init() throws SQLException {
    conn = DriverManager.getConnection(jdbcUrl);
    fDB = new FlowerDatabase(conn);
    DB = new GraphDatabase(conn);
  }

  @AfterEach
  public void teardown() {
    try {
      conn.close();
      DriverManager.getConnection(closeUrl);
    } catch (SQLException ignored) {
    }
  }

  // @Test
  public void testDB() throws SQLException {
    boolean test = false;
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      test = true;
    } catch (SQLException e) {

    }
    Assertions.assertTrue(test);
  }

  @Test
  public void testTables() throws SQLException {
    DB.createTables();
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
    fDB.addFlower("Daisy", "Blue", 10, 1.20);
    fDB.addOrder(5, "Daisy", "Blue", "biscuit");
    fDB.addOrder(-1, "Daisy", "Blue", "buiscuit");
    fDB.addOrder(1, "Daisy", "Blue", "butter");
    Assertions.assertEquals(1, fDB.getSizeOrders());
    fDB.removeAllOrders();
  }

  @Test
  public void testDeleteOrder() throws SQLException {
    DB.createTables();
    fDB.createTables();
    fDB.removeAllOrders();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    fDB.addFlower("Daisy", "Blue", 20, 1.20);
    fDB.addOrder(5, "Daisy", "Blue", "biscuit");
    boolean a = fDB.deleteOrder(1);
    Assertions.assertTrue(a);
    Assertions.assertEquals(0, fDB.getSizeOrders());
    fDB.addOrder(2, "Daisy", "Blue", "biscuit");
    fDB.addOrder(6, "Daisy", "Blue", "biscuit");
    boolean r = fDB.deleteOrder(2);
    Assertions.assertTrue(r);
    Assertions.assertEquals(1, fDB.getSizeOrders());
    fDB.removeAllOrders();
  }

  @Test
  public void testUpdateStatus() throws SQLException {
    DB.createTables();
    fDB.createTables();
    fDB.removeAllOrders();
    DB.removeAll();
    DB.addNode("biscuit", 2, 5, 2, "White House", "CONF", "balogna", "b", "Team A");
    fDB.addOrder(5, "Daisy", "Blue", "biscuit");
    boolean a = fDB.changeOrderStatus(5, "Order Received");
    Assertions.assertTrue(a);
    fDB.removeAllOrders();
  }
}
