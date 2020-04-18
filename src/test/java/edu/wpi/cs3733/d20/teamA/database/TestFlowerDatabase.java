package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFlowerDatabase {
  FlowerDatabase fDB = new FlowerDatabase();

  @Test
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
    boolean w = fDB.helperEmpty("SELECT * FROM Flowers");
    Assertions.assertTrue(w);
    fDB.removeAllFlowers();
  }

  @Test
  public void testUpdatePrice() throws SQLException {
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
    fDB.removeAllFlowers();
    fDB.addFlower("Rose", "Yellow", 6, 2.45);
    fDB.deleteFlower("Rose", "Yellow");
    boolean w = fDB.helperEmpty("SELECT * FROM Flowers");
    Assertions.assertFalse(w);
    fDB.removeAllFlowers();
  }

  @Test
  public void testAddOrder() throws SQLException {
    fDB.removeAllOrders();
    fDB.addFlower("Daisy", "Blue", 10, 1.20);
    fDB.addOrder(5, "Daisy", "Blue");
    fDB.addOrder(-1, "Daisy", "Blue");
    boolean e = fDB.helperEmpty("SELECT * FROM Orders");
    Assertions.assertTrue(e);
    fDB.removeAllOrders();
  }

  @Test
  public void testDeleteOrder() throws SQLException {

    fDB.removeAllOrders();
    fDB.addFlower("Daisy", "Blue", 20, 1.20);
    fDB.addOrder(5, "Daisy", "Blue");
    boolean a = fDB.deleteOrder(1);
    Assertions.assertTrue(a);
    boolean b = fDB.helperEmpty("SELECT * FROM Orders");
    Assertions.assertFalse(b);
    fDB.addOrder(5, "Daisy", "Blue");
    fDB.addOrder(5, "Daisy", "Blue");
    boolean r = fDB.deleteOrder(2);
    Assertions.assertTrue(r);
    boolean s = fDB.helperEmpty("SELECT * FROM Orders");
    Assertions.assertTrue(s);
    fDB.removeAllOrders();
  }

  @Test
  public void testUpdateStatus() throws SQLException {
    fDB.removeAllOrders();
    fDB.addOrder(5, "Daisy", "Blue");
    boolean a = fDB.changeOrderStatus(5, "Order Received");
    Assertions.assertTrue(a);
    fDB.removeAllOrders();
  }
}
