package edu.wpi.cs3733.d20.teamA.database;

import java.sql.SQLException;

public class FlowerDatabase extends Database {

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
    if (!(helperPrepared("ALTER TABLE Orders DROP CONSTRAINT FK_fT")
        && helperPrepared("ALTER TABLE Orders DROP CONSTRAINT FK_fc"))) {

      return false;
    }
    // Drop the tables
    if (!(helperPrepared("DROP TABLE Orders") && helperPrepared("DROP TABLE Flowers"))) {
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
            "CREATE TABLE Flowers (type Varchar(15), qty INTEGER NOT NULL, color Varchar(15), pricePer DECIMAL(5,2) NOT NULL, CONSTRAINT pk PRIMARY KEY (type, color))");

    boolean b =
        helperPrepared(
            "CREATE TABLE Orders (orderNumber INTEGER PRIMARY KEY, numFlowers INTEGER NOT NULL, flowerType Varchar(15) NOT NULL, flowerColor Varchar(15) NOT NULL, price DECIMAL(5,2) NOT NULL, CONSTRAINT FK_fT FOREIGN KEY(flowerType) REFERENCES Flowers(type), CONSTRAINT FK_fc FOREIGN KEY(flowerColor) REFERENCES Flowers(color))");

    System.out.println("A: " + a + " B: " + b);
    if (a && b) {
      return true;
    } else {
      return false;
    }
  }
}
