package edu.wpi.cs3733.d20.teamA.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.sql.*;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FlowerDatabase extends Database {
  private int orderNum;

  /**
   * Creates the Flower database with given connection
   *
   * @param connection connection
   */
  public FlowerDatabase(Connection connection) {

    super(connection);

    if (doesTableNotExist("FLOWERS") && doesTableNotExist("ORDERS")) {
      createTables();
    }
    orderNum = getSizeOrders() + 1;
  }

  /**
   * Drops the tables so we can start fresh
   *
   * @return false if the tables don't exist and CONSTRAINT can't be dropped, true if CONSTRAINT and
   *     tables are dropped correctly
   */
  public boolean dropTables() {

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
   * Creates tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the graph tables
    boolean a =
        helperPrepared(
            "CREATE TABLE Flowers (typeFlower Varchar(15), color Varchar(15), qty INTEGER NOT NULL, pricePer DOUBLE NOT NULL, CONSTRAINT PK_fl PRIMARY KEY (typeFlower, color), CONSTRAINT CHK_NUMFL CHECK (qty >= 0))");

    boolean b =
        helperPrepared(
            "CREATE TABLE Orders (orderNumber INTEGER PRIMARY KEY, numFlowers INTEGER NOT NULL, flowerType Varchar(15) NOT NULL, flowerColor Varchar(15) NOT NULL, price DOUBLE NOT NULL, status Varchar(50) NOT NULL, location Varchar(10) NOT NULL, CONSTRAINT FK_NID FOREIGN KEY (location) REFERENCES Node(nodeID), CONSTRAINT CHK_STAT CHECK (status in ('Order Sent', 'Order Received', 'Flowers Sent', 'Flowers Delivered')), CONSTRAINT FK_fT FOREIGN KEY (flowerType, flowerColor) REFERENCES Flowers(typeFlower, color), CONSTRAINT CHK_FLNUM CHECK (numFlowers > 0))");

    return a && b;
  }

  /**
   * Adds a flower to the flower table
   *
   * @param type type of flower
   * @param color color
   * @param qty how many are available in inventory
   * @param pricePer price per flower
   * @return boolean for test purposes. True is everything goes through without an SQL exception
   */
  public boolean addFlower(String type, String color, int qty, double pricePer) {

    String text = Double.toString(Math.abs(pricePer));
    int integerPlaces = text.indexOf('.');
    int decimalPlaces = text.length() - integerPlaces - 1;

    if (decimalPlaces > 2) {
      return false;
    }

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Flowers (typeFlower, color, qty, pricePer) VALUES (?, ?, ?, ?)");
      pstmt.setString(1, type);
      pstmt.setString(2, color);
      pstmt.setInt(3, qty);
      pstmt.setDouble(4, pricePer);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Takes in the color and type of flower and updates to a given price
   *
   * @param type flowerType
   * @param color color
   * @param newPrice new price
   * @return true if the price is updated, false otherwise
   */
  public boolean updatePrice(String type, String color, double newPrice) {

    String text = Double.toString(Math.abs(newPrice));
    int integerPlaces = text.indexOf('.');
    int decimalPlaces = text.length() - integerPlaces - 1;

    if (decimalPlaces != 2) {
      return false;
    }

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Flowers SET pricePer = "
                      + newPrice
                      + " WHERE typeFlower = '"
                      + type
                      + "' AND color = '"
                      + color
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Takes in the type and color of the flower and updates the inventory qty
   *
   * @param type typeFlower
   * @param color color
   * @param newNum number of flowers
   * @return true if the quantity updated, false otherwise
   */
  public boolean updateQTY(String type, String color, int newNum) {

    if (newNum < 0) {
      return false;
    }

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Flowers SET qty = "
                      + newNum
                      + " WHERE typeFlower = '"
                      + type
                      + "' AND color = '"
                      + color
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Deletes a flower from the flower table
   *
   * @param type, color
   * @return true if the flower was deleted, false otherwise
   */
  public boolean deleteFlower(String type, String color) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "DELETE From Flowers WHERE typeFlower = '"
                      + type
                      + "' AND color = '"
                      + color
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * adds an order to the order table
   *
   * @return the customer's order number
   */
  public int addOrder(int numFlowers, String flowerType, String flowerColor, String location) {
    try {
      double total;
      double pricePer = getFlowerPricePer(flowerType, flowerColor);
      total = pricePer * numFlowers;

      total = Math.round(total * 100.0) / 100.0;

      String status = "Order Sent";

      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Orders (orderNumber, numFlowers, flowerType, flowerColor, price, status, location) VALUES (?, ?, ?, ?, ?, ?, ?)");
      pstmt.setInt(1, orderNum);
      pstmt.setInt(2, numFlowers);
      pstmt.setString(3, flowerType);
      pstmt.setString(4, flowerColor);
      pstmt.setDouble(5, total);
      pstmt.setString(6, status);
      pstmt.setString(7, location);
      pstmt.executeUpdate();
      pstmt.close();
      int quantity = getFlowerQuantity(flowerType, flowerColor);
      updateQTY(flowerType, flowerColor, (quantity - numFlowers));
      orderNum++;
      return orderNum - 1;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * Changes the order status of a selected order
   *
   * @param orderNum order Number
   * @param newStat new Status
   * @return true if the order status was changed, false otherwise
   */
  public boolean changeOrderStatus(int orderNum, String newStat) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Orders Set status = '" + newStat + "' WHERE orderNumber = " + orderNum);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Deletes a certain order from the table
   *
   * @param orderNumber orderNumber
   * @return true if the order was deleted, false otherwise
   */
  public boolean deleteOrder(int orderNumber) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Orders Where orderNumber = ?");
      pstmt.setInt(1, orderNumber);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Gets how many entries are in the flower table
   *
   * @return the number of entries in the flower table
   */
  public int getSizeFlowers() {
    return getSize("Flowers");
  }

  /**
   * Gets how many entries in the order table
   *
   * @return the number of entries in the order table
   */
  public int getSizeOrders() {
    return getSize("Orders");
  }

  /**
   * removes all flowers from the table
   *
   * @return true if all the flowers were removed, false otherwise
   */
  public boolean removeAllFlowers() {
    return helperPrepared("DELETE From Flowers");
  }

  /**
   * Removes all orders from the table
   *
   * @return true if all the orders were removed, false otherwise
   */
  public boolean removeAllOrders() {
    orderNum = 1;
    return helperPrepared("DELETE From Orders");
  }

  /**
   * Gets all flowers in the flower table in an observable list
   *
   * @return an observable list containing all the flowers in the table
   */
  public ObservableList<Flower> flowerOl() {
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

  /**
   * Gets all orders in the table
   *
   * @return an observable list containing all orders in the table
   */
  public ObservableList<Order> orderOl() {
    ObservableList<Order> oList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Orders");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        int orderNumber = rset.getInt("orderNumber");
        int numFlowers = rset.getInt("numFlowers");
        String flowerType = rset.getString("flowerType");
        String flowerColor = rset.getString("flowerColor");
        double price = rset.getDouble("price");
        String status = rset.getString("status");
        String location = rset.getString("location");

        Order node =
            new Order(orderNumber, numFlowers, flowerType, flowerColor, price, status, location);
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

  /**
   * Get individual flower price for a given flower
   *
   * @param type flowerType
   * @param color flowerColor
   * @return the price of a specified flower in a specified color
   */
  public double getFlowerPricePer(String type, String color) {
    double price = -1;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "Select pricePer From Flowers Where typeFlower = '"
                      + type
                      + "' AND color = '"
                      + color
                      + "'");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        price = rset.getDouble("pricePer");
      }
      rset.close();
      pstmt.close();
      return price;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }
  /**
   * Get the number of a given flower available
   *
   * @param typeFlower typeFlower
   * @param flowerColor flowerColor
   * @return the number of flowers in stock of a specified type and color
   */
  public int getFlowerQuantity(String typeFlower, String flowerColor) throws SQLException {
    int quantity;
    Statement priceStmt = getConnection().createStatement();
    ResultSet rst =
        priceStmt.executeQuery(
            "SELECT * FROM Flowers WHERE typeFlower = '"
                + typeFlower
                + "' AND color = '"
                + flowerColor
                + "'");
    ;
    rst.next();
    quantity = rst.getInt("qty");
    return quantity;
  }

  /**
   * gets the order status for a chosen order
   *
   * @param orderNum orderNum
   * @return the status of a specified order number as a string
   */
  public String getOrderStatus(int orderNum) {
    String status = null;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("Select status From Orders Where orderNumber = " + orderNum);
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        status = rset.getString("status");
      }
      rset.close();
      pstmt.close();
      return status;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /** Reads the flower csv file into the database */
  public void readFlowersCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/FlowersCSV.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String typeFlower, color;
        int qty;
        double pricePer;
        typeFlower = data.get(i)[0];
        color = data.get(i)[1];
        qty = Integer.parseInt(data.get(i)[2]);
        pricePer = Double.parseDouble(data.get(i)[3]);
        addFlower(typeFlower, color, qty, pricePer);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }

  /** Reads the flower order csv file into the order database */
  public void readFlowerOrderCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/FlowerOrderCSV.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String flowerType, flowerColor, location;
        int numFlowers;
        numFlowers = Integer.parseInt(data.get(i)[0]);
        flowerType = data.get(i)[1];
        flowerColor = data.get(i)[2];
        location = data.get(i)[3];
        addOrder(numFlowers, flowerType, flowerColor, location);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }
}
