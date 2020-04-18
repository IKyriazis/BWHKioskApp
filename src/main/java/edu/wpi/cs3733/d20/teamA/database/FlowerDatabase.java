package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FlowerDatabase extends Database {
  private int orderNum = 1;

  public FlowerDatabase() throws SQLException {}

  /**
   * Drops the graph tables so we can start fresh
   *
   * @return false if the tables don't exist and CONSTRAINT can't be dropped, true if CONSTRAINT and
   *     tables are dropped correctly
   * @throws SQLException
   */
  public boolean dropTables() throws SQLException {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    if (!(helperPrepared("ALTER TABLE Orders DROP CONSTRAINT FK_fT"))) {

      return false;
    }
    // Drop the tables
    if (!(helperPrepared("DROP TABLE Flowers") && helperPrepared("DROP TABLE Orders"))) {
      return false;
    }

    return true;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   * @throws SQLException
   */
  public boolean createTables() throws SQLException {

    // Create the graph tables
    boolean a =
        helperPrepared(
            "CREATE TABLE Flowers (typeFlower Varchar(15), color Varchar(15), qty INTEGER NOT NULL, pricePer DOUBLE NOT NULL, CONSTRAINT PK_fl PRIMARY KEY (typeFlower, color), CONSTRAINT CHK_NUMFL CHECK (qty >= 0))");

    boolean b =
        helperPrepared(
            "CREATE TABLE Orders (orderNumber INTEGER PRIMARY KEY, numFlowers INTEGER NOT NULL, flowerType Varchar(15) NOT NULL, flowerColor Varchar(15) NOT NULL, price DOUBLE NOT NULL, status Varchar(50), CONSTRAINT CHK_STAT CHECK (status in ('Order Sent', 'Order Received', 'Flowers Collected', 'Flowers Sent', 'Flowers Delivered')), CONSTRAINT FK_fT FOREIGN KEY (flowerType, flowerColor) REFERENCES Flowers(typeFlower, color), CONSTRAINT CHK_FLNUM CHECK (numFlowers > 0))");

    if (a && b) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @param type
   * @param color
   * @param qty
   * @param pricePer
   * @return
   */
  public boolean addFlower(String type, String color, int qty, double pricePer)
      throws SQLException {

    String text = Double.toString(Math.abs(pricePer));
    int integerPlaces = text.indexOf('.');
    int decimalPlaces = text.length() - integerPlaces - 1;

    if (decimalPlaces > 2) {
      return false;
    }

    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement(
              "INSERT INTO Flowers (typeFlower, color, qty, pricePer) VALUES (?, ?, ?, ?)");
      pstmt.setString(1, type);
      pstmt.setString(2, color);
      pstmt.setInt(3, qty);
      pstmt.setDouble(4, pricePer);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * @param type
   * @param color
   * @param newPrice
   * @return
   */
  public boolean updatePrice(String type, String color, double newPrice) throws SQLException {

    String text = Double.toString(Math.abs(newPrice));
    int integerPlaces = text.indexOf('.');
    int decimalPlaces = text.length() - integerPlaces - 1;

    if (decimalPlaces != 2) {
      return false;
    }

    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement(
              "UPDATE Flowers SET pricePer = "
                  + newPrice
                  + " WHERE typeFlower = '"
                  + type
                  + "' AND color = '"
                  + color
                  + "'");
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * @param type
   * @param color
   * @param newNum
   * @return
   */
  public boolean updateQTY(String type, String color, int newNum) throws SQLException {

    if (newNum < 0) {
      return false;
    }

    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement(
              "UPDATE Flowers SET qty = "
                  + newNum
                  + " WHERE typeFlower = '"
                  + type
                  + "' AND color = '"
                  + color
                  + "'");
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * Should only have to be used by employees if there was a typo??
   *
   * @param type, color
   * @return
   * @throws SQLException
   */
  public boolean deleteFlower(String type, String color) throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement(
              "DELETE From Flowers WHERE typeFlower = '" + type + "' AND color = '" + color + "'");
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  /** @return */
  public int addOrder(int numFlowers, String flowerType, String flowerColor) throws SQLException {

    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      double price;
      Statement priceStmt = conn.createStatement();
      ResultSet rst =
          priceStmt.executeQuery(
              "SELECT * FROM Flowers WHERE typeFlower = '"
                  + flowerType
                  + "' AND color = '"
                  + flowerColor
                  + "'");
      ;
      rst.next();
      price = rst.getDouble("pricePer") * numFlowers;

      price = Math.round(price * 100.0) / 100.0;

      String status = "Order Sent";

      PreparedStatement pstmt =
          conn.prepareStatement(
              "INSERT INTO Orders (orderNumber, numFlowers, flowerType, flowerColor, price, status) VALUES (?, ?, ?, ?, ?, ?)");
      pstmt.setInt(1, orderNum);
      pstmt.setInt(2, numFlowers);
      pstmt.setString(3, flowerType);
      pstmt.setString(4, flowerColor);
      pstmt.setDouble(5, price);
      pstmt.setString(6, status);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      orderNum++;
      return orderNum - 1;
    } catch (SQLException e) {
      return 0;
    }
  }

  public boolean changeOrderStatus(int orderNum, String newStat) {

    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement(
              "UPDATE Orders Set status = '" + newStat + "' WHERE orderNumber = " + orderNum);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * needs to end up being automated?
   *
   * @param orderNumber
   * @return
   * @throws SQLException
   */
  public boolean deleteOrder(int orderNumber) throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("DELETE From Orders Where orderNumber = ?");
      pstmt.setInt(1, orderNumber);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean removeAllFlowers() throws SQLException {
    return helperPrepared("DELETE From Flowers");
  }

  public boolean removeAllOrders() throws SQLException {
    return helperPrepared("DELETE From Orders");
  }

  public boolean removeAll() throws SQLException {
    return removeAllFlowers() && removeAllOrders();
  }

  public ObservableList<Flower> flowerOl() throws SQLException {
    ObservableList<Flower> oList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Flowers");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String typeFlower = rset.getString("typeFlower");
        String color = rset.getString("color");
        int qty = rset.getInt("qty");
        double pricePer = rset.getDouble("pricePer");

        Flower node = new Flower(typeFlower, color, qty, pricePer);
        oList.add(node);
      }
      rset.close();
      pstmt.close();
      conn.close();
      return oList;
    } catch (SQLException e) {
      e.printStackTrace();
      return oList;
    }
  }

  // public Flower getFlowerInfo(String type, String ){

  // }

}
