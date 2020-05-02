package edu.wpi.cs3733.d20.teamA.database.inventory;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import edu.wpi.cs3733.d20.teamA.database.Database;
import edu.wpi.cs3733.d20.teamA.database.IDatabase;
import edu.wpi.cs3733.d20.teamA.database.TableItemFactory;
import edu.wpi.cs3733.d20.teamA.database.service.interpreter.Interpreter;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InventoryDatabase extends Database implements IDatabase {

  public InventoryDatabase(Connection connection) {
    super(connection);
    createTables();
  }

  public synchronized boolean dropTables() {
    if (!doesTableNotExist("INVENTORY")) {
      return helperPrepared("DROP TABLE Inventory");
    }
    return false;
  }

  public synchronized boolean createTables() {
    if (doesTableNotExist("INVENTORY")) {
      return helperPrepared(
          "CREATE TABLE Inventory (itemID Varchar(6) PRIMARY KEY, "
              + "itemType Varchar(20) NOT NULL, itemName Varchar(50) UNIQUE NOT NULL, "
              + "quantity INTEGER, unitPrice REAL, description Varchar(100), additional Varchar(2000), "
              + "CONSTRAINT Check_IType CHECK (itemType in ('flower','interpreter')))");
    }
    return false;
  }

  public synchronized String addItem(
      ItemType type,
      String name,
      int quantity,
      double unitPrice,
      String description,
      String additional) {
    boolean a = checkIfExistsString("Inventory", "itemName", name);
    String itemID = getRandomString();
    boolean b = checkIfExistsString("Inventory", "itemID", itemID);
    while (b) {
      itemID = getRandomString();
      b = checkIfExistsString("Inventory", "itemID", itemID);
    }
    try {
      if (!a) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "INSERT INTO Inventory (itemID, itemType, itemName, quantity, unitPrice, description, additional) VALUES (?, ?, ?, ?, ?, ?, ?)");
        pstmt.setString(1, itemID);
        pstmt.setString(2, type.toString());
        pstmt.setString(3, name);
        pstmt.setInt(4, quantity);
        pstmt.setDouble(5, unitPrice);
        pstmt.setString(6, description);
        pstmt.setString(7, additional);
        pstmt.executeUpdate();
        pstmt.close();
        return itemID;
      }
      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public synchronized boolean removeItem(String itemID) {
    try {
      if (checkIfExistsString("Inventory", "itemID", itemID)) {
        PreparedStatement pstmt =
            getConnection().prepareStatement("DELETE FROM Inventory WHERE itemID = ?");
        pstmt.setString(1, itemID);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized int getSize() {
    return getSize("Inventory");
  }

  public synchronized boolean removeAll() {
    return helperPrepared("DELETE FROM Inventory");
  }

  public synchronized ObservableList<ITableable> getObservableList() {
    ObservableList<ITableable> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Inventory");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String itemID = rset.getString("itemID");
        String itemType = rset.getString("itemType");
        String itemName = rset.getString("itemName");
        int quantity = rset.getInt("quantity");
        double unitPrice = rset.getDouble("unitPrice");
        String description = rset.getString("description");
        String additional = rset.getString("additional");

        ITableable item =
            TableItemFactory.getInventory(
                itemID, itemType, itemName, quantity, unitPrice, description, additional);
        observableList.add(item);
      }
      rset.close();
      pstmt.close();
      return observableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public synchronized ObservableList<ITableable> getObservableListItem(ItemType item) {
    ObservableList<ITableable> observableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Inventory");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String itemID = rset.getString("itemID");
        String itemType = rset.getString("itemType");
        String itemName = rset.getString("itemName");
        int quantity = rset.getInt("quantity");
        double unitPrice = rset.getDouble("unitPrice");
        String description = rset.getString("description");
        String additional = rset.getString("additional");

        ITableable listItem = null;
        if (item == ItemType.INTERPRETER) {
          listItem = new Interpreter(itemID, itemName, additional);
        }
        /*ITableable item =
        TableItemFactory.getInventory(
            itemID, itemType, itemName, quantity, unitPrice, description, additional);*/
        observableList.add(listItem);
      }
      rset.close();
      pstmt.close();
      return observableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
